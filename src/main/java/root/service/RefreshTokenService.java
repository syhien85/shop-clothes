package root.service;

import root.dto.RefreshTokenDTO;
import root.entity.RefreshToken;
import root.entity.User;
import root.exception.ForbiddenException;
import root.exception.ResourceNotFoundException;
import root.repository.RefreshTokenRepo;
import root.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import root.security.JwtTokenService;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.DAYS;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepo refreshTokenRepo;
    private final UserRepo userRepo;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;

    private static final long refreshExpiration = 30;

    public RefreshTokenDTO login(String username, String password) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );

        String accessToken = jwtTokenService.createToken(username);

        User user = userRepo.findByUsername(username)
            .orElseThrow(() ->
                new ResourceNotFoundException("Username is not in database!")
            );

        Optional<RefreshToken> currentRefreshToken = refreshTokenRepo.findByUserId(user.getId());

        RefreshToken newRefreshRefreshToken;
        // nếu refreshToken có trong DB
        if (currentRefreshToken.isPresent()) {
            newRefreshRefreshToken = currentRefreshToken.get();
            // nếu refreshToken trong DB hết hạn, xoá refreshToken, tạo mới refreshToken
            if (verifyExpiration(currentRefreshToken.get())) {
                newRefreshRefreshToken = createRefreshToken(user.getId());
            }
        } else {
            newRefreshRefreshToken = createRefreshToken(user.getId());
        }

        return new RefreshTokenDTO(accessToken, newRefreshRefreshToken.getToken());
    }

    public RefreshTokenDTO refreshToken(String refreshToken) {
        RefreshToken currentRefreshToken = refreshTokenRepo.findByToken(refreshToken)
            .orElseThrow(() ->
                new ResourceNotFoundException("Refresh token is not in database!")
            );
        // verify refreshToken, nếu hết hạn thì xoá trong DB và throw exception
        if (verifyExpiration(currentRefreshToken)) {
            throw new ForbiddenException(
                "Refresh token was expired. Please make a new sign in request!"
            );
        }

        String accessToken = jwtTokenService.createToken(currentRefreshToken.getUser().getUsername());

        return new RefreshTokenDTO(accessToken, refreshToken);
    }

    private RefreshToken createRefreshToken(Long userId) {
        User user = userRepo.findById(userId).orElseThrow(
            () -> new ResourceNotFoundException("user with id [" + userId + "] not found")
        );

        RefreshToken refreshToken = RefreshToken.builder()
            .user(user)
            .expired(Date.from(Instant.now().plus(refreshExpiration, DAYS)))
            .token(UUID.randomUUID().toString())
            .build();

        refreshToken = refreshTokenRepo.save(refreshToken);
        return refreshToken;
    }

    private boolean verifyExpiration(RefreshToken refreshToken) {
        if (refreshToken.getExpired().before(Date.from(Instant.now()))) {
            refreshTokenRepo.delete(refreshToken); // refreshToken hết hạn
            return true;
        }
        return false;
    }
}

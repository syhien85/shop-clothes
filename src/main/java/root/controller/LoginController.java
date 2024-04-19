package root.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import root.dto.RefreshTokenDTO;
import root.dto.ResponseDTO;
import root.dto.UserDTO;
import root.entity.User;
import root.repository.UserRepo;
import root.service.RefreshTokenService;

import java.security.Principal;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class LoginController {

    private final RefreshTokenService refreshTokenService;
    private final UserRepo userRepo;

    @PostMapping("/login")
    public ResponseDTO<RefreshTokenDTO> login(String username, String password) {
        return ResponseDTO.<RefreshTokenDTO>builder()
            .status(200).msg("OK")
            .data(refreshTokenService.login(username, password))
            .build();
    }

    @PostMapping("/refresh-token")
    public ResponseDTO<RefreshTokenDTO> refreshToken(String refreshToken) {
        return ResponseDTO.<RefreshTokenDTO>builder()
            .status(200).msg("OK")
            .data(refreshTokenService.refreshToken(refreshToken))
            .build();
    }

    // method security
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    /*public Principal me(Principal principal) {
        // principal:: current user đang đăng nhập
        // String username = principal.getName();
        return principal;
    }*/
    public UserDTO me(Principal principal) {
        Optional<User> user = userRepo.findByUsername(principal.getName());
        return user.map(this::convert).orElse(null);
    }

    private UserDTO convert(User user) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(user, UserDTO.class);
    }
}


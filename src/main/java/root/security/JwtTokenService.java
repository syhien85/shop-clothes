package root.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class JwtTokenService {

    @Value("${JWT_SECRET_KEY}")
    private String SECRET_KEY;
    //    private static final String SECRET_KEY = "IT_IS_NOT_LOOKING_GOOD_@#_IT_IS_NOT_LOOKING_GOOD";
    private static final long EXPIRED_TIME = 15;

    // để tạo một token cần: secret key và thời gian hết hạn
    public String createToken(String username) {
        Claims claims = Jwts.claims().setSubject(username);
//        Date exp = new Date(new Date().getTime() + EXPIRED_TIME * 60 * 1000);

        return Jwts
            .builder()
            .setClaims(claims)
//            .setIssuer("http://domain.com")
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(Date.from(Instant.now().plus(EXPIRED_TIME, DAYS)))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // đọc body của token (subject)
    public Claims getClaims(String token) {
        return Jwts
            .parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, String username) {
        String subject = getSubject(token);
        return subject.equals(username) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        Date today = Date.from(Instant.now());
        return getClaims(token).getExpiration().before(today);
    }
}

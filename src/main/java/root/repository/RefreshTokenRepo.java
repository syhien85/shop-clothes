package root.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import root.entity.RefreshToken;
import root.entity.User;

import java.util.Optional;

public interface RefreshTokenRepo extends CrudRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUserId(Long id);

    @Modifying
    int deleteByUser(User user);
}

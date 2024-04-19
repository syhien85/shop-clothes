package root.repository;

import root.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    @Query(
        "SELECT u " +
            "FROM User u " +
            "WHERE u.name LIKE :name"
    )
    Page<User> searchByName(@Param("name") String s, Pageable pageable);
    // for: jobschedule.JobSchedule

    @Query(
        "SELECT u " +
            "FROM User u " +
            "WHERE DAY(u.birthdate) = :date AND MONTH(u.birthdate) = :month"
    )
    List<User> searchByBirthdate(@Param("date") int date, @Param("month") int month);

    @Query(
        "SELECT u " +
            "FROM User u " +
            "WHERE u.username LIKE :usernameOrEmail OR u.email LIKE :usernameOrEmail"
    )
    Optional<User> findByUsernameOrEmail(@Param("usernameOrEmail") String s);
}

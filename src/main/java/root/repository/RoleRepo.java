package root.repository;

import root.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role, Integer> {
    @Query("SELECT r FROM Role r WHERE r.name LIKE :s")
    Page<Role> searchByName(@Param("s") String name, Pageable pageable);

    boolean existsByName(String name);

    Optional<Role> findByName(String name);
}

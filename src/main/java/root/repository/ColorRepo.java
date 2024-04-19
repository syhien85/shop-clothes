package root.repository;

import root.entity.Color;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ColorRepo extends JpaRepository<Color, Integer> {

    @Query("SELECT c FROM Color c WHERE c.name LIKE :s")
    Page<Color> searchByName(@Param("s") String name, Pageable pageable);

    boolean existsByName(String name);
}

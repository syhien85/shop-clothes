package root.repository;

import root.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepo extends JpaRepository<Category, Integer> {
    @Query("SELECT c FROM Category c WHERE c.name LIKE :s")
    Page<Category> searchByName(@Param("s") String name, Pageable pageable);

    boolean existsByName(String name);
}

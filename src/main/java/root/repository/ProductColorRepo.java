package root.repository;

import root.entity.ProductColor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductColorRepo extends JpaRepository<ProductColor, Integer> {
    @Query("SELECT p FROM ProductColor p WHERE p.color.name LIKE :s")
    Page<ProductColor> searchByColorName(String s, PageRequest pageRequest);

    Optional<ProductColor> findByProductIdAndColorId(Long productId, Integer colorId);
}

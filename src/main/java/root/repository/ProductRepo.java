package root.repository;

import root.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepo extends JpaRepository<Product, Long> {

    String C1 = "p.price >= :c1 ";
    String C2 = "p.price <= :c2 ";
    String C3 = "p.category.id >= :c3 ";
    String queryKeyword = "(p.name LIKE :s OR p.category.name LIKE :s)";

    @Query("SELECT p FROM Product p WHERE " + queryKeyword)
    Page<Product> searchBy(
        @Param("s") String s,
        Pageable pageable
    );

    @Query("SELECT p FROM Product p WHERE " + C1 + "AND " + queryKeyword)
    Page<Product> searchByC1(
        @Param("s") String s,
        @Param("c1") Double c1,
        Pageable pageable
    );

    @Query("SELECT p FROM Product p WHERE " + C2 + "AND " + queryKeyword)
    Page<Product> searchByC2(
        @Param("s") String s,
        @Param("c2") Double c2,
        Pageable pageable
    );

    @Query("SELECT p FROM Product p WHERE " + C3 + "AND " + queryKeyword)
    Page<Product> searchByC3(
        @Param("s") String s,
        @Param("c3") Integer c3,
        Pageable pageable
    );

    @Query("SELECT p FROM Product p WHERE " + C1 + "AND " + C2 + "AND " + queryKeyword)
    Page<Product> searchByC1AndC2(
        @Param("s") String s,
        @Param("c1") Double c1,
        @Param("c2") Double c2,
        Pageable pageable
    );

    @Query("SELECT p FROM Product p WHERE " + C1 + "AND " + C3 + "AND " + queryKeyword)
    Page<Product> searchByC1AndC3(
        @Param("s") String s,
        @Param("c1") Double c1,
        @Param("c3") Integer c3,
        Pageable pageable
    );

    @Query("SELECT p FROM Product p WHERE " + C2 + "AND " + C3 + "AND " + queryKeyword)
    Page<Product> searchByC2AndC3(
        @Param("s") String s,
        @Param("c2") Double c2,
        @Param("c3") Integer c3,
        Pageable pageable
    );

    @Query("SELECT p FROM Product p WHERE " + C1 + "AND " + C2 + "AND " + C3 + "AND " + queryKeyword)
    Page<Product> searchByC1AndC2AndC3(
        @Param("s") String s,
        @Param("c1") Double c1,
        @Param("c2") Double c2,
        @Param("c3") Integer c3,
        Pageable pageable
    );

    Double findTopByOrderByPriceAsc(); // min price
    Double findTopByOrderByPriceDesc(); // max price
}

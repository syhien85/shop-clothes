package root.repository;

import root.entity.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface ProductReviewRepo extends JpaRepository<ProductReview, Long> {
    String C1 = "p.product.id = :c1 ";
    String C2 = "p.user.id = :c2 ";
    String C3 = "p.createdAt >= :c3 ";
    String C4 = "p.createdAt <= :c4 ";
    String queryKeyword = "(p.product.name LIKE :s OR p.content LIKE :s)";

    @Query("SELECT p FROM ProductReview p WHERE " + queryKeyword)
    Page<ProductReview> searchBy(
        @Param("s") String s,
        Pageable pageable
    );

    @Query("SELECT p FROM ProductReview p WHERE " + C1 + "AND " + queryKeyword)
    Page<ProductReview> searchByC1(
        @Param("s") String s,
        @Param("c1") Long c1,
        Pageable pageable
    );

    @Query("SELECT p FROM ProductReview p WHERE " + C2 + "AND " + queryKeyword)
    Page<ProductReview> searchByC2(
        @Param("s") String s,
        @Param("c2") Long c2,
        Pageable pageable
    );

    @Query("SELECT p FROM ProductReview p WHERE " + C3 + "AND " + queryKeyword)
    Page<ProductReview> searchByC3(
        @Param("s") String s,
        @Param("c3") Date c3,
        Pageable pageable
    );

    @Query("SELECT p FROM ProductReview p WHERE " + C4 + "AND " + queryKeyword)
    Page<ProductReview> searchByC4(
        @Param("s") String s,
        @Param("c4") Date c4,
        Pageable pageable
    );

    @Query("SELECT p FROM ProductReview p WHERE " + C1 + "AND " + C2 + "AND " + queryKeyword)
    Page<ProductReview> searchByC1AndC2(
        @Param("s") String s,
        @Param("c1") Long c1,
        @Param("c2") Long c2,
        Pageable pageable
    );

    @Query("SELECT p FROM ProductReview p WHERE " + C1 + "AND " + C3 + "AND " + queryKeyword)
    Page<ProductReview> searchByC1AndC3(
        @Param("s") String s,
        @Param("c1") Long c1,
        @Param("c3") Date c3,
        Pageable pageable
    );

    @Query("SELECT p FROM ProductReview p WHERE " + C1 + "AND " + C4 + "AND " + queryKeyword)
    Page<ProductReview> searchByC1AndC4(
        @Param("s") String s,
        @Param("c1") Long c1,
        @Param("c4") Date c4,
        Pageable pageable
    );

    @Query("SELECT p FROM ProductReview p WHERE " + C2 + "AND " + C3 + "AND " + queryKeyword)
    Page<ProductReview> searchByC2AndC3(
        @Param("s") String s,
        @Param("c2") Long c2,
        @Param("c3") Date c3,
        Pageable pageable
    );

    @Query("SELECT p FROM ProductReview p WHERE " + C2 + "AND " + C4 + "AND " + queryKeyword)
    Page<ProductReview> searchByC2AndC4(
        @Param("s") String s,
        @Param("c2") Long c2,
        @Param("c4") Date c4,
        Pageable pageable
    );

    @Query("SELECT p FROM ProductReview p WHERE " + C3 + "AND " + C4 + "AND " + queryKeyword)
    Page<ProductReview> searchByC3AndC4(
        @Param("s") String s,
        @Param("c3") Date c3,
        @Param("c4") Date c4,
        Pageable pageable
    );

    @Query("SELECT p FROM ProductReview p WHERE " + C1 + "AND " + C2 + "AND " + C3 + "AND " + queryKeyword)
    Page<ProductReview> searchByC1AndC2AndC3(
        @Param("s") String s,
        @Param("c1") Long c1,
        @Param("c2") Long c2,
        @Param("c3") Date c3,
        Pageable pageable
    );

    @Query("SELECT p FROM ProductReview p WHERE " + C1 + "AND " + C2 + "AND " + C4 + "AND " + queryKeyword)
    Page<ProductReview> searchByC1AndC2AndC4(
        @Param("s") String s,
        @Param("c1") Long c1,
        @Param("c2") Long c2,
        @Param("c4") Date c4,
        Pageable pageable
    );

    @Query("SELECT p FROM ProductReview p WHERE " + C1 + "AND " + C3 + "AND " + C4 + "AND " + queryKeyword)
    Page<ProductReview> searchByC1AndC3AndC4(
        @Param("s") String s,
        @Param("c1") Long c1,
        @Param("c3") Date c3,
        @Param("c4") Date c4,
        Pageable pageable
    );

    @Query("SELECT p FROM ProductReview p WHERE " + C2 + "AND " + C3 + "AND " + C4 + "AND " + queryKeyword)
    Page<ProductReview> searchByC2AndC3AndC4(
        @Param("s") String s,
        @Param("c2") Long c2,
        @Param("c3") Date c3,
        @Param("c4") Date c4,
        Pageable pageable
    );

    @Query("SELECT p FROM ProductReview p WHERE " + C1 + "AND " + C2 + "AND " + C3 + "AND " + C4 + "AND " + queryKeyword)
    Page<ProductReview> searchByC1AndC2AndC3AndC4(
        @Param("s") String s,
        @Param("c1") Long c1,
        @Param("c2") Long c2,
        @Param("c3") Date c3,
        @Param("c4") Date c4,
        Pageable pageable
    );
}

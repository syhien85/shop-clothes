package root.repository;

import root.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface CommentRepo extends JpaRepository<Comment, Long> {
    String C1 = "c.product.id = :c1 ";
    String C2 = "c.user.id = :c2 ";
    String C3 = "c.createdAt >= :c3 ";
    String C4 = "c.createdAt <= :c4 ";
    String queryKeyword = "(c.product.name LIKE :s OR c.content LIKE :s)";

    @Query("SELECT c FROM Comment c WHERE " + queryKeyword)
    Page<Comment> searchBy(
        @Param("s") String s,
        Pageable pageable
    );

    @Query("SELECT c FROM Comment c WHERE " + C1 + "AND " + queryKeyword)
    Page<Comment> searchByC1(
        @Param("s") String s,
        @Param("c1") Long c1,
        Pageable pageable
    );

    @Query("SELECT c FROM Comment c WHERE " + C2 + "AND " + queryKeyword)
    Page<Comment> searchByC2(
        @Param("s") String s,
        @Param("c2") Long c2,
        Pageable pageable
    );

    @Query("SELECT c FROM Comment c WHERE " + C3 + "AND " + queryKeyword)
    Page<Comment> searchByC3(
        @Param("s") String s,
        @Param("c3") Date c3,
        Pageable pageable
    );

    @Query("SELECT c FROM Comment c WHERE " + C4 + "AND " + queryKeyword)
    Page<Comment> searchByC4(
        @Param("s") String s,
        @Param("c4") Date c4,
        Pageable pageable
    );

    @Query("SELECT c FROM Comment c WHERE " + C1 + "AND " + C2 + "AND " + queryKeyword)
    Page<Comment> searchByC1AndC2(
        @Param("s") String s,
        @Param("c1") Long c1,
        @Param("c2") Long c2,
        Pageable pageable
    );

    @Query("SELECT c FROM Comment c WHERE " + C1 + "AND " + C3 + "AND " + queryKeyword)
    Page<Comment> searchByC1AndC3(
        @Param("s") String s,
        @Param("c1") Long c1,
        @Param("c3") Date c3,
        Pageable pageable
    );

    @Query("SELECT c FROM Comment c WHERE " + C1 + "AND " + C4 + "AND " + queryKeyword)
    Page<Comment> searchByC1AndC4(
        @Param("s") String s,
        @Param("c1") Long c1,
        @Param("c4") Date c4,
        Pageable pageable
    );

    @Query("SELECT c FROM Comment c WHERE " + C2 + "AND " + C3 + "AND " + queryKeyword)
    Page<Comment> searchByC2AndC3(
        @Param("s") String s,
        @Param("c2") Long c2,
        @Param("c3") Date c3,
        Pageable pageable
    );

    @Query("SELECT c FROM Comment c WHERE " + C2 + "AND " + C4 + "AND " + queryKeyword)
    Page<Comment> searchByC2AndC4(
        @Param("s") String s,
        @Param("c2") Long c2,
        @Param("c4") Date c4,
        Pageable pageable
    );

    @Query("SELECT c FROM Comment c WHERE " + C3 + "AND " + C4 + "AND " + queryKeyword)
    Page<Comment> searchByC3AndC4(
        @Param("s") String s,
        @Param("c3") Date c3,
        @Param("c4") Date c4,
        Pageable pageable
    );

    @Query("SELECT c FROM Comment c WHERE " + C1 + "AND " + C2 + "AND " + C3 + "AND " + queryKeyword)
    Page<Comment> searchByC1AndC2AndC3(
        @Param("s") String s,
        @Param("c1") Long c1,
        @Param("c2") Long c2,
        @Param("c3") Date c3,
        Pageable pageable
    );

    @Query("SELECT c FROM Comment c WHERE " + C1 + "AND " + C2 + "AND " + C4 + "AND " + queryKeyword)
    Page<Comment> searchByC1AndC2AndC4(
        @Param("s") String s,
        @Param("c1") Long c1,
        @Param("c2") Long c2,
        @Param("c4") Date c4,
        Pageable pageable
    );

    @Query("SELECT c FROM Comment c WHERE " + C1 + "AND " + C3 + "AND " + C4 + "AND " + queryKeyword)
    Page<Comment> searchByC1AndC3AndC4(
        @Param("s") String s,
        @Param("c1") Long c1,
        @Param("c3") Date c3,
        @Param("c4") Date c4,
        Pageable pageable
    );

    @Query("SELECT c FROM Comment c WHERE " + C2 + "AND " + C3 + "AND " + C4 + "AND " + queryKeyword)
    Page<Comment> searchByC2AndC3AndC4(
        @Param("s") String s,
        @Param("c2") Long c2,
        @Param("c3") Date c3,
        @Param("c4") Date c4,
        Pageable pageable
    );

    @Query("SELECT c FROM Comment c WHERE " + C1 + "AND " + C2 + "AND " + C3 + "AND " + C4 + "AND " + queryKeyword)
    Page<Comment> searchByC1AndC2AndC3AndC4(
        @Param("s") String s,
        @Param("c1") Long c1,
        @Param("c2") Long c2,
        @Param("c3") Date c3,
        @Param("c4") Date c4,
        Pageable pageable
    );
}

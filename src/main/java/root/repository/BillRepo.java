package root.repository;

import root.dto.*;
import root.entity.Bill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface BillRepo extends JpaRepository<Bill, Long> {

//    String queryKeyword = "(b.user.name LIKE :s OR b.id = :s)";
    String queryKeyword = "(b.user.name LIKE :s)";

    @Query("SELECT b FROM Bill b " +
        "WHERE " + queryKeyword)
    Page<Bill> searchByDefault(@Param("s") String s, Pageable pageable);

    @Query("SELECT b FROM Bill b " +
        "WHERE b.user.id = :c1 AND " + queryKeyword)
    Page<Bill> searchByUserId(@Param("s") String s, @Param("c1") long c1, Pageable pageable);

    @Query("SELECT b FROM Bill b " +
        "WHERE b.createdAt >= :c2 AND " + queryKeyword)
    Page<Bill> searchByStart(@Param("s") String s, @Param("c2") Date c2, Pageable pageable);

    @Query("SELECT b FROM Bill b " +
        "WHERE b.createdAt <= :c3 AND " + queryKeyword)
    Page<Bill> searchByEnd(@Param("s") String s, @Param("c3") Date c3, Pageable pageable);

    @Query("SELECT b FROM Bill b " +
        "WHERE b.user.id = :c1 AND b.createdAt >= :c2 AND " + queryKeyword)
    Page<Bill> searchByUserIdAndStart(
        @Param("s") String s,
        @Param("c1") long c1,
        @Param("c2") Date c2,
        Pageable pageable
    );

    @Query("SELECT b FROM Bill b " +
        "WHERE b.user.id = :c1 AND b.createdAt <= :c3 AND " + queryKeyword)
    Page<Bill> searchByUserIdAndEnd(
        @Param("s") String s,
        @Param("c1") long c1,
        @Param("c3") Date c3,
        Pageable pageable
    );

    @Query("SELECT b FROM Bill b " +
        "WHERE b.createdAt >= :c2 AND b.createdAt <= :c3 AND " + queryKeyword)
    Page<Bill> searchByStartAndEnd(
        @Param("s") String s,
        @Param("c2") Date c2,
        @Param("c3") Date c3,
        Pageable pageable
    );

    @Query("SELECT b FROM Bill b " +
        "WHERE b.user.id = :c1 AND b.createdAt >= :c2 AND b.createdAt <= :c3 AND " + queryKeyword)
    Page<Bill> searchByUserIdAndStartAndEnd(
        @Param("s") String s,
        @Param("c1") long c1,
        @Param("c2") Date c2,
        @Param("c3") Date c3,
        Pageable pageable
    );

    /*@Query(
        "SELECT COUNT(b.id), MONTH(b.createdAt), YEAR(b.createdAt) " +
            "FROM Bill b " +
            "GROUP BY MONTH(b.createdAt), YEAR(b.createdAt)"
    )
    List<Objects[]> thongKeBill();*/

    @Query(
        "SELECT new root.dto.BillCountByMonthDTO(COUNT(b.id), MONTH(b.createdAt), YEAR(b.createdAt)) " +
            "FROM Bill b " +
            "GROUP BY MONTH(b.createdAt), YEAR(b.createdAt)"
    )
    Page<BillCountByMonthDTO[]> countBillByMonth(Pageable pageable);

    @Query(
        "SELECT COUNT(b.id) " +
            "FROM Bill b " +
            "WHERE b.createdAt >= :c1 AND b.createdAt <= :c2"
    )
    Long countBillByStartAndEnd(@Param("c1") Date c1, @Param("c2") Date c2);

    @Query(
        "SELECT new root.dto.BillCountByCouponCodeDTO(b.couponCode, COUNT(b.id)) " +
            "FROM Bill b " +
            "GROUP BY b.couponCode"
    )
    Page<BillCountByCouponCodeDTO> countBillByCouponCode(PageRequest pageRequest);

    @Query(
        "SELECT new root.dto.BillByUserTotalInvoiceDTO(b.user.name, COUNT(b.id), SUM(b.discountAmount)) " +
            "FROM Bill b " +
            "GROUP BY b.user.id"
    )
    Page<BillByUserTotalInvoiceDTO> billByUserTotalInvoice(PageRequest pageRequest);

    @Query(
        "SELECT new root.dto.BillByUserWithProductDTO(b.user.name, COUNT(DISTINCT b.id), SUM(b_i.quantity)) " +
            "FROM BillItem b_i " +
            "JOIN Bill b " +
                "ON b_i.bill.id = b.id " +
            "GROUP BY b.user.id"
    )
    Page<BillByUserWithProductDTO> billByUserWithProduct(PageRequest pageRequest);

    @Query(
        "SELECT new root.dto.BillCountByProductBestSellerDTO(p.id, p.product.name, p.color.name, SUM(b_i.quantity)) " +
            "FROM BillItem b_i " +
//            "JOIN Bill b " +
//                "ON b_i.bill.id = b.id " +
            "JOIN ProductColor p " +
                "ON b_i.productColor.id = p.id " +
            "GROUP BY p.id " +
            "ORDER BY SUM(b_i.quantity) DESC"
    )
    Page<BillCountByProductBestSellerDTO> billCountByProductBestSeller(PageRequest pageRequest);

    @Query("SELECT b FROM BillItem b_i " +
            "JOIN Bill b " +
                "ON b_i.bill.id = b.id " +
            "WHERE b.user.id = :c1 AND b_i.productColor.product.id = :c2"
    )
    List<Bill> searchByUserIdAndProductId(
        @Param("c1") long c1,
        @Param("c2") long c2
    );
}

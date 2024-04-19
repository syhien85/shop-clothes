package root.repository;

import root.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CouponRepo extends JpaRepository<Coupon, Integer> {

    boolean existsByCouponCode(String couponCode);

    Optional<Coupon> findByCouponCode(String couponCode);

    @Query("SELECT c FROM Coupon c WHERE c.couponCode LIKE :s")
    Page<Coupon> searchByCouponCode(@Param("s") String couponCode, Pageable pageable);
}

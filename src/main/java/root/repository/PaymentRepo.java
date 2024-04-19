package root.repository;

import root.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepo extends JpaRepository<Payment, Integer> {
    boolean existsByPaymentMethod(String name);
}

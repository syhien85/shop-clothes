package root.repository;

import root.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepo extends JpaRepository<Customer, Long> {
    @Query(
        "SELECT c " +
            "FROM Customer c " +
            "WHERE c.user.name LIKE :s"
    )
    Page<Customer> searchByName(@Param("s") String keyword, Pageable pageable);
}

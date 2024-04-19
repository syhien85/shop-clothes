package root.repository;

import root.entity.BillItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillItemRepo extends JpaRepository<BillItem, Long> {
}

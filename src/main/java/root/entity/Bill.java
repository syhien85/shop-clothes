package root.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Bill extends TimeAuditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String status; // PENDING, ACTIVE, SUCCESS
    private String couponCode;
    private Double discountAmount;

    @ManyToOne
    private User user;

    // 1 Bill ManyToMany Product, trung gian là bảng bill_item
    @OneToMany(
        mappedBy = "bill",
        cascade = CascadeType.ALL,
        fetch = FetchType.EAGER,
        orphanRemoval = true
    )
    private List<BillItem> billItems;

    @ManyToOne
    private Payment payment;
}

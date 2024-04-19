package root.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Coupon extends TimeAuditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true)
    private String couponCode;
    private Double discountAmount;
    @Temporal(TemporalType.DATE)
    private Date expiredDate;
}

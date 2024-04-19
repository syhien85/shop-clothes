package root.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BillCountByCouponCodeDTO {
    private String couponCode;
    private Long quantity;
}*/
public record BillCountByCouponCodeDTO(
    String couponCode,
    Long billQuantity
) {}

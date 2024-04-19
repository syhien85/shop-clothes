package root.dto;

/*@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BillCountByCouponCodeDTO {
    private String couponCode;
    private Long quantity;
}*/
public record BillByUserWithProductDTO(
    String name,
    Long billQuantity,
    Long sumProductQuantity
) {}

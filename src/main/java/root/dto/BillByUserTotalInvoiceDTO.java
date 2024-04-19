package root.dto;

/*@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BillCountByCouponCodeDTO {
    private String couponCode;
    private Long quantity;
}*/
public record BillByUserTotalInvoiceDTO(
    String name,
    Long quantity,
    Double discountAmount // totalBill mới đúng, tạm thời để discountAmount
) {}

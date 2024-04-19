package root.dto;

import lombok.*;

@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaymentDTO extends TimeAuditableDTO {
    private Integer id;
    private String paymentMethod;
//    private String status;
    private Long accountNumber;
    private String accountName;
    private String bankName;
}

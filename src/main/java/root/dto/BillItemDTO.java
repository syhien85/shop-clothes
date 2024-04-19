package root.dto;

import lombok.*;

@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BillItemDTO extends TimeAuditableDTO {
    private Long id;
    private ProductColorDTO productColor;
    private Integer quantity;
    private Double price;
}

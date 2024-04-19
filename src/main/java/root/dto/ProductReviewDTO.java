package root.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductReviewDTO extends TimeAuditableDTO {
    private Long id;
    private String status;
    private String content;
    @Min(1) @Max(5)
    private Integer rate;

    private UserDTO user;
    private ProductDTO product;
}

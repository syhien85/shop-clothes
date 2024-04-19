package root.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BillCountByProductBestSellerDTO {
    private Integer productColorId;
    private String productName;
    private String color;
    private Long quantity;
}

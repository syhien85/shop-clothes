package root.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
public class SearchProductDTO extends SearchDTO {
    @Min(0)
    private Double priceMin;
    @Min(0)
    private Double priceMax;
    @Min(1)
    private Integer categoryId;
//    private String isStock;
//    private double rate;

    public SearchProductDTO() {
        priceMin = null;
        priceMax = null;
        categoryId = null;
    }
}

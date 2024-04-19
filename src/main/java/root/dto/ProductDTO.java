package root.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductDTO extends TimeAuditableDTO {
    private Long id;

    @NotBlank
    private String name;
    private String image;
    private String description;
    @Min(0)
    private Double price;
    /*@Min(0)
    private Double priceSale;*/

    @JsonIgnore
    private MultipartFile file;

    private CategoryDTO category;
}

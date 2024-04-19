package root.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductColorDTO{
    private Integer id;

    @Min(0)
    private Integer quantity;
    private List<String> images;

    private ProductDTO product;
    private ColorDTO color;

    @JsonIgnore
    private MultipartFile[] files;
}

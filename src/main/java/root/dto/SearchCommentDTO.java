package root.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
public class SearchCommentDTO extends SearchDTO {
    @Min(1)
    private Long productId;
    @Min(1)
    private Long userId;
    @JsonFormat(pattern = "dd/MM/yyyy", timezone = "GMT+7")
    private Date start;
    @JsonFormat(pattern = "dd/MM/yyyy", timezone = "GMT+7")
    private Date end;

    public SearchCommentDTO() {
        productId = null;
        userId = null;
        start = null;
        end = null;
    }
}

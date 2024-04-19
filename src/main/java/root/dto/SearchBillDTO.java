package root.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
//@NoArgsConstructor
@AllArgsConstructor
@Data
public class SearchBillDTO extends SearchDTO {
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @JsonFormat(pattern = "dd/MM/yyyy", timezone = "GMT+7")
    private Date start;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @JsonFormat(pattern = "dd/MM/yyyy", timezone = "GMT+7")
    private Date end;
    private Long userId;

    public SearchBillDTO() {
        start = null;
        end = null;
        userId = null;
    }
}

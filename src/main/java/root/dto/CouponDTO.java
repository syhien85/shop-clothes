package root.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CouponDTO extends TimeAuditableDTO {
    private Integer id;
    private String couponCode;
    private Double discountAmount;
    @DateTimeFormat(pattern = "dd/MM/yyyy") //
    @JsonFormat(pattern = "dd/MM/yyyy", timezone = "GMT+7")
    private Date expiredDate;
}

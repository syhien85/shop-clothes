package root.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BillCountByMonthDTO {
    private Long billQuantity;
    private Integer month;
    private Integer year;
}

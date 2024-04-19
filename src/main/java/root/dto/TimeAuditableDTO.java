package root.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;

@Data
public abstract class TimeAuditableDTO {
//    @JsonIgnore // tạm thời tắt
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm", timezone = "GMT+7")
    private Date createdAt;
//    @JsonIgnore // tạm thời tắt
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm", timezone = "GMT+7")
    private Date updateAt;
}

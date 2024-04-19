package root.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ResponseDTO<T> {
    private int status;
    private String msg;

    /**
     * neu gia tri != null thi tra ve bien data, null thi ko tra ve bien data
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public ResponseDTO(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }
}

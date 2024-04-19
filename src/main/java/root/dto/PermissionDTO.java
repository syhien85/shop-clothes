package root.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PermissionDTO {
    private Integer id;
    private String path;
    private String method;
    private boolean visibility;

    private List<String> roles;
}

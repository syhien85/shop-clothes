package root.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoleDTO extends TimeAuditableDTO {
    private Integer id;

    @NotBlank
    private String name;
}

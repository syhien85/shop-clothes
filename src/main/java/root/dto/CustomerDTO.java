package root.dto;

import lombok.*;

@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CustomerDTO extends TimeAuditableDTO {
    private UserDTO user;
}

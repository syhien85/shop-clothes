package root.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDTO extends TimeAuditableDTO {
    private Long id;

    @NotBlank
    private String name;
    @Min(16)
    private Integer age;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private String homeAddress;
    private String avatarUrl;
    @NotBlank
    private String email;

    @JsonIgnore
    private MultipartFile file;

    @DateTimeFormat(pattern = "dd/MM/yyyy") //
    @JsonFormat(pattern = "dd/MM/yyyy", timezone = "GMT+7")
    private Date birthdate;

    private List<RoleDTO> roles;
}

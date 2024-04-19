package root.dto;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentDTO extends TimeAuditableDTO {
    private Long id;
    private String status;
    private String content;
    private Long parentId;
    private Integer rate;

    private UserDTO user;
    private ProductDTO product;
}

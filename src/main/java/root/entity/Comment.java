package root.entity;

import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Comment extends TimeAuditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String status; // hide, public
    private String content;
    private Long parentId;
    private Integer rate;

    @ManyToOne
    private User user;

    @ManyToOne
    private Product product;
}

package root.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String path;
    private String method;
    private boolean visibility;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "permission_roles",
        joinColumns = @JoinColumn(name = "permission_id")
    )
    @Column(name = "role")
    private List<String> roles;
}

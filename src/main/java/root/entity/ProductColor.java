package root.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(
    uniqueConstraints = {
        @UniqueConstraint(
            name = "UniqueProductIdAndColorId",
            columnNames = {"product_id", "color_id"}
        )
    }
) // mỗi cặp product_id và color_id là duy nhất
public class ProductColor extends TimeAuditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer quantity;

    @ManyToOne
    private Product product;

    @ManyToOne
    private Color color;

    // 1 product has many images
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "product_images",
        joinColumns = @JoinColumn(name = "product_id")
    )
    @Column(name = "image") // link image
    private List<String> images; // frontend: images[0,1,...]
}

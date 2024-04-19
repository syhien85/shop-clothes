package root.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Product extends TimeAuditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String image; // URL
    private String description;
    private Double price;

    // 1 product has many colors: Product ManyToMany Color
    // xoá product, xoá all color in table product_color
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductColor> productColors;

    @ManyToOne
    private Category category;
}

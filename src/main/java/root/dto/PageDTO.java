package root.dto;

import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PageDTO<T> {
    private Integer totalPage;
    private Long totalElements;
    private List<T> data;
}

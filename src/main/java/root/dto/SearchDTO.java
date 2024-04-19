package root.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class SearchDTO {
    private String keyword;
    private Integer currentPage;
    private Integer size;
    private String sortedField;

    public SearchDTO() {
        keyword = "";
        currentPage = 0;
        size = 10;
        sortedField = "id";
    }
}

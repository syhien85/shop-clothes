package root.controller;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import root.dto.ProductReviewDTO;
import root.dto.PageDTO;
import root.dto.ResponseDTO;
import root.dto.SearchCommentDTO;
import root.service.ProductReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/product-review")
public class ProductReviewController {

    private final ProductReviewService productReviewService;

    @CacheEvict(cacheNames = "product-review-search", allEntries = true)
    @PostMapping
    public ResponseDTO<Void> create(@RequestBody @Valid ProductReviewDTO productReviewDTO) {
        productReviewService.create(productReviewDTO);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
        //
    }

    @Caching(evict = {
        @CacheEvict(cacheNames = "product-review-search", allEntries = true),
        @CacheEvict(cacheNames = "product-review", key = "#productReviewDTO.id") // khi update delete cache
    })
    @PutMapping
    public ResponseDTO<Void> update(@RequestBody ProductReviewDTO productReviewDTO) {
        productReviewService.update(productReviewDTO);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @CacheEvict(cacheNames = "product-review", key = "#id")
    @DeleteMapping
    public ResponseDTO<Void> delete(@RequestParam("id") Long id) {
        productReviewService.delete(id);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @Cacheable(cacheNames = "product-review", key = "#id", unless = "#result == null")
    @GetMapping
    public ResponseDTO<ProductReviewDTO> getById(@RequestParam("id") Long id) {
        return ResponseDTO.<ProductReviewDTO>builder()
            .status(200).msg("OK")
            .data(productReviewService.getById(id))
            .build();
    }

    @Cacheable(cacheNames = "product-review-search")
    @PostMapping("/search")
    public ResponseDTO<PageDTO<ProductReviewDTO>> search(
        @RequestBody @Valid SearchCommentDTO searchDTO
    ) {
        return ResponseDTO.<PageDTO<ProductReviewDTO>>builder().status(200).msg("OK")
            .data(productReviewService.searchService(searchDTO))
            .build();
    }
}

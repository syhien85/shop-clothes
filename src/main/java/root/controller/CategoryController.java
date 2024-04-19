package root.controller;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import root.dto.CategoryDTO;
import root.dto.PageDTO;
import root.dto.ResponseDTO;
import root.dto.SearchDTO;
import root.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    @CacheEvict(cacheNames = "category-search", allEntries = true)
    @PostMapping
    public ResponseDTO<Void> create(@RequestBody @Valid CategoryDTO categoryDTO) {
        categoryService.create(categoryDTO);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @Caching(evict = {
        @CacheEvict(cacheNames = "category-search", allEntries = true),
        @CacheEvict(cacheNames = "category", key = "#productReviewDTO.id") // khi update delete cache
    })
    @PutMapping
    public ResponseDTO<Void> update(@RequestBody @Valid CategoryDTO categoryDTO) {
        categoryService.update(categoryDTO);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @CacheEvict(cacheNames = "category", key = "#id")
    @DeleteMapping
    public ResponseDTO<Void> delete(@RequestParam("id") Integer id) {
        categoryService.delete(id);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @Cacheable(cacheNames = "category", key = "#id", unless = "#result == null")
    @GetMapping
    public ResponseDTO<CategoryDTO> getById(@RequestParam("id") Integer id) {
        return ResponseDTO.<CategoryDTO>builder()
            .status(200).msg("OK")
            .data(categoryService.getById(id))
            .build();
    }

    @Cacheable(cacheNames = "category-search")
    @PostMapping("/search")
    public ResponseDTO<PageDTO<CategoryDTO>> search(@RequestBody @Valid SearchDTO searchDTO) {
        return ResponseDTO.<PageDTO<CategoryDTO>>builder().status(200).msg("OK")
            .data(categoryService.searchService(searchDTO))
            .build();
    }
}

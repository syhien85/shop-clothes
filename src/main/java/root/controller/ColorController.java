package root.controller;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import root.dto.ColorDTO;
import root.dto.PageDTO;
import root.dto.ResponseDTO;
import root.dto.SearchDTO;
import root.service.ColorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/color")
public class ColorController {

    private final ColorService colorService;

    @CacheEvict(cacheNames = "color-search", allEntries = true)
    @PostMapping
    public ResponseDTO<Void> create(@RequestBody @Valid ColorDTO colorDTO) {
        colorService.create(colorDTO);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @Caching(evict = {
        @CacheEvict(cacheNames = "color-search", allEntries = true),
        @CacheEvict(cacheNames = "color", key = "#colorDTO.id") // khi update delete cache
    })
    @PutMapping
    public ResponseDTO<Void> update(@RequestBody @Valid ColorDTO colorDTO) {
        colorService.update(colorDTO);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @CacheEvict(cacheNames = "color", key = "#id")
    @DeleteMapping
    public ResponseDTO<Void> delete(@RequestParam("id") Integer id) {
        colorService.delete(id);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @Cacheable(cacheNames = "color", key = "#id", unless = "#result == null")
    @GetMapping
    public ResponseDTO<ColorDTO> getById(@RequestParam("id") Integer id) {
        return ResponseDTO.<ColorDTO>builder()
            .status(200).msg("OK")
            .data(colorService.getById(id))
            .build();
    }

    @Cacheable(cacheNames = "color-search")
    @PostMapping("/search")
    public ResponseDTO<PageDTO<ColorDTO>> search(@RequestBody @Valid SearchDTO searchDTO) {
        return ResponseDTO.<PageDTO<ColorDTO>>builder().status(200).msg("OK")
            .data(colorService.searchService(searchDTO))
            .build();
    }
}

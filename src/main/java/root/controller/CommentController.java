package root.controller;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import root.dto.*;
import root.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    @CacheEvict(cacheNames = "comment-search", allEntries = true)
    @PostMapping
    public ResponseDTO<Void> create(@RequestBody @Valid CommentDTO commentDTO) {
        commentService.create(commentDTO);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @Caching(evict = {
        @CacheEvict(cacheNames = "comment-search", allEntries = true),
        @CacheEvict(cacheNames = "comment", key = "#commentDTO.id") // khi update delete cache
    })
    @PutMapping
    public ResponseDTO<Void> update(@RequestBody CommentDTO commentDTO) {
        commentService.update(commentDTO);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @CacheEvict(cacheNames = "comment", key = "#id")
    @DeleteMapping
    public ResponseDTO<Void> delete(@RequestParam("id") Long id) {
        commentService.delete(id);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @Cacheable(cacheNames = "comment", key = "#id", unless = "#result == null")
    @GetMapping
    public ResponseDTO<CommentDTO> getById(@RequestParam("id") Long id) {
        return ResponseDTO.<CommentDTO>builder()
            .status(200).msg("OK")
            .data(commentService.getById(id))
            .build();
    }

    @Cacheable(cacheNames = "comment-search")
    @PostMapping("/search")
    public ResponseDTO<PageDTO<CommentDTO>> search(@RequestBody @Valid SearchCommentDTO searchDTO) {
        return ResponseDTO.<PageDTO<CommentDTO>>builder().status(200).msg("OK")
            .data(commentService.searchService(searchDTO))
            .build();
    }
}

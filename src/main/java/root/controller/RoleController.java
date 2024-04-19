package root.controller;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import root.dto.PageDTO;
import root.dto.ResponseDTO;
import root.dto.RoleDTO;
import root.dto.SearchDTO;
import root.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/role")
public class RoleController {

    private final RoleService roleService;

    @CacheEvict(cacheNames = "role-search", allEntries = true)
    @PostMapping
    public ResponseDTO<Void> create(@RequestBody @Valid RoleDTO roleDTO) {
        roleService.create(roleDTO);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @Caching(evict = {
        @CacheEvict(cacheNames = "role-search", allEntries = true),
        @CacheEvict(cacheNames = "role", key = "#roleDTO.id") // khi update delete cache
    })
    @PutMapping
    public ResponseDTO<Void> update(@RequestBody @Valid RoleDTO roleDTO) {
        roleService.update(roleDTO);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @CacheEvict(cacheNames = "role", key = "#id")
    @DeleteMapping
    public ResponseDTO<Void> delete(@RequestParam("id") Integer id) {
        roleService.delete(id);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @Cacheable(cacheNames = "role", key = "#id", unless = "#result == null")
    @GetMapping
    public ResponseDTO<RoleDTO> getById(@RequestParam("id") Integer id) {
        return ResponseDTO.<RoleDTO>builder()
            .status(200).msg("OK")
            .data(roleService.getById(id))
            .build();
    }

    @Cacheable(cacheNames = "role-search")
    @PostMapping("/search")
    public ResponseDTO<PageDTO<RoleDTO>> search(@RequestBody @Valid SearchDTO searchDTO) {
        return ResponseDTO.<PageDTO<RoleDTO>>builder().status(200).msg("OK")
            .data(roleService.searchService(searchDTO))
            .build();
    }
}

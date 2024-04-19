package root.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import root.dto.PageDTO;
import root.dto.ResponseDTO;
import root.dto.SearchDTO;
import root.dto.UserDTO;
import root.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@Tag(name = "User Controller") // #swagger
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Value("${upload.folder}")
    private String UPLOAD_FOLDER;

    @Operation(
        // method = "POST",
        summary = "Add new user",
        description = "Send a request via this API to create new user"
    ) // #swagger
    @CacheEvict(cacheNames = "user-search", allEntries = true)
    @PostMapping
    public ResponseDTO<Void> create(@RequestBody @Valid UserDTO userDTO) {
        userService.create(userDTO);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @Caching(evict = {
        @CacheEvict(cacheNames = "comment-search", allEntries = true), //
        @CacheEvict(cacheNames = "comment", allEntries = true), //

        @CacheEvict(cacheNames = "product-review-search", allEntries = true), //
        @CacheEvict(cacheNames = "product-review", allEntries = true), //

        @CacheEvict(cacheNames = "bill-search", allEntries = true), //
        @CacheEvict(cacheNames = "bill", allEntries = true), //

        @CacheEvict(cacheNames = "user-search", allEntries = true),
        @CacheEvict(cacheNames = "user", key = "#userDTO.id") // khi update delete cache
    })
    @PutMapping
    public ResponseDTO<Void> update(@RequestBody UserDTO userDTO) {
        userService.update(userDTO);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @CacheEvict(cacheNames = "user", key = " #id")
    @DeleteMapping
    public ResponseDTO<Void> delete(@RequestParam("id") Long id) {
        userService.delete(id);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @Cacheable(cacheNames = "user", key = "#id", unless = "#result == null")
    @GetMapping
    public ResponseDTO<UserDTO> getById(@RequestParam("id") Long id) {
        return ResponseDTO.<UserDTO>builder()
            .status(200).msg("OK")
            .data(userService.getById(id))
            .build();
    }

    @Cacheable(cacheNames = "user-search")
    @PostMapping("/search")
    public ResponseDTO<PageDTO<UserDTO>> search(@RequestBody @Valid SearchDTO searchDTO) {
        return ResponseDTO.<PageDTO<UserDTO>>builder()
            .status(200).msg("OK")
            .data(userService.searchService(searchDTO))
            .build();
    }

    @Caching(evict = {
        @CacheEvict(cacheNames = "user-search", allEntries = true),
        @CacheEvict(cacheNames = "user", key = "#userDTO.id") // khi update delete cache
    })
    @PutMapping("/update-password")
    public ResponseDTO<Void> updatePassword(@RequestBody UserDTO userDTO) {
        userService.updatePassword(userDTO);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @Caching(evict = {
        @CacheEvict(cacheNames = "user-search", allEntries = true),
        @CacheEvict(cacheNames = "user-forgot-password", key = "#usernameOrEmail") // khi update delete cache
    })
    @PostMapping("/forgot-password")
    public ResponseDTO<Void> forgotPassword(@RequestParam("usernameOrEmail") String usernameOrEmail) {
        userService.forgotPassword(usernameOrEmail);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @PutMapping("/update-avatar")
    public ResponseDTO<Void> updateAvatar(@ModelAttribute("user") UserDTO userDTO)
        throws IOException {

        if (!userDTO.getFile().isEmpty()) {
            if (new File(UPLOAD_FOLDER).exists()) {
                new File(UPLOAD_FOLDER).mkdir();
            }
            String filename = userDTO.getFile().getOriginalFilename();
            // lấy định dạng file
            String extension = filename.substring((filename.lastIndexOf(".")));

            String newFilename = UUID.randomUUID().toString() + extension;
            File newFile = new File(UPLOAD_FOLDER + newFilename);
            userDTO.getFile().transferTo(newFile);

            userDTO.setAvatarUrl(newFilename);
        }

        userService.updateAvatar(userDTO);

        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @GetMapping("/download")
    public void download(@RequestParam("filename") String filename, HttpServletResponse resp)
        throws IOException {
        File file = new File(UPLOAD_FOLDER + filename);
        Files.copy(file.toPath(), resp.getOutputStream());
    }
}

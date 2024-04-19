package root.controller;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import root.dto.ProductColorDTO;
import root.dto.PageDTO;
import root.dto.ResponseDTO;
import root.dto.SearchDTO;
import root.service.ProductColorService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/product-color")
public class ProductColorController {

    private final ProductColorService productColorService;

    @Value("${upload.folder}")
    private String UPLOAD_FOLDER;

    @CacheEvict(cacheNames = "product-color-search", allEntries = true)
    @PostMapping
    public ResponseDTO<Void> create(
        @ModelAttribute("productColor") @Valid ProductColorDTO productColorDTO
    ) throws IOException {

        List<String> listImageUrl = new ArrayList<>();
        for (MultipartFile file : productColorDTO.getFiles()) {
            if (!file.isEmpty()) {
                if (new File(UPLOAD_FOLDER).exists()) {
                    new File(UPLOAD_FOLDER).mkdir();
                }
                String filename = file.getOriginalFilename();
                String extension = filename.substring((filename.lastIndexOf(".")));

                String newFilename = UUID.randomUUID().toString() + extension;
                File newFile = new File(UPLOAD_FOLDER + newFilename);
                file.transferTo(newFile);

                listImageUrl.add(newFilename);
            }
        }

        productColorDTO.setImages(listImageUrl);

        productColorService.create(productColorDTO);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @Caching(evict = {
        @CacheEvict(cacheNames = "product-color-search", allEntries = true),
        @CacheEvict(cacheNames = "product-color", key = "#productColorDTO.id") // khi update delete cache
    })
    @PutMapping
    public ResponseDTO<Void> update(
        @ModelAttribute("productColor") ProductColorDTO productColorDTO
    ) throws IOException {

        List<String> listImageUrl = new ArrayList<>();
        for (MultipartFile file : productColorDTO.getFiles()) {
            if (!file.isEmpty()) {
                if (new File(UPLOAD_FOLDER).exists()) {
                    new File(UPLOAD_FOLDER).mkdir();
                }
                String filename = file.getOriginalFilename();
                String extension = filename.substring((filename.lastIndexOf(".")));

                String newFilename = UUID.randomUUID().toString() + extension;
                File newFile = new File(UPLOAD_FOLDER + newFilename);
                file.transferTo(newFile);

                listImageUrl.add(newFilename);
            }
        }

        productColorDTO.setImages(listImageUrl);

        productColorService.update(productColorDTO);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    /**
     * thiếu: Update list images of Product Color
     */


    @CacheEvict(cacheNames = "product-color", key = "#id")
    @DeleteMapping
    public ResponseDTO<Void> delete(@RequestParam("id") Integer id) {
        productColorService.delete(id);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @Cacheable(cacheNames = "product-color", key = "#id", unless = "#result == null")
    @GetMapping
    public ResponseDTO<ProductColorDTO> getById(@RequestParam("id") Integer id) {
        return ResponseDTO.<ProductColorDTO>builder()
            .status(200).msg("OK")
            .data(productColorService.getById(id))
            .build();
    }

    @Cacheable(cacheNames = "product-color-search")
    @PostMapping("/search")
    public ResponseDTO<PageDTO<ProductColorDTO>> search(@RequestBody @Valid SearchDTO searchDTO) {
        return ResponseDTO.<PageDTO<ProductColorDTO>>builder().status(200).msg("OK")
            .data(productColorService.searchService(searchDTO))
            .build();
    }

    @GetMapping("/download")
    public void download(@RequestParam("filename") String filename, HttpServletResponse resp)
        throws IOException {
        File file = new File(UPLOAD_FOLDER + filename);
        Files.copy(file.toPath(), resp.getOutputStream());
    }
}

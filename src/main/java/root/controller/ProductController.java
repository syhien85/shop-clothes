package root.controller;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import root.dto.*;
import root.service.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @Value("${upload.folder}")
    private String UPLOAD_FOLDER;

    @CacheEvict(cacheNames = "product-search", allEntries = true)
    @PostMapping
    public ResponseDTO<Void> create(@ModelAttribute("product") @Valid ProductDTO productDTO)
        throws IOException {

        if (!productDTO.getFile().isEmpty()) {
            if (new File(UPLOAD_FOLDER).exists()) {
                new File(UPLOAD_FOLDER).mkdir();
            }
            String filename = productDTO.getFile().getOriginalFilename();
            // lấy định dạng file
            String extension = filename.substring((filename.lastIndexOf(".")));

            String newFilename = UUID.randomUUID().toString() + extension;
            File newFile = new File(UPLOAD_FOLDER + newFilename);
            productDTO.getFile().transferTo(newFile);

            productDTO.setImage(newFilename);
        }

        productService.create(productDTO);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @Caching(evict = {
        @CacheEvict(cacheNames = "product-search", allEntries = true),
        @CacheEvict(cacheNames = "product", key = "#productDTO.id") // khi update delete cache
    })
    @PutMapping
    public ResponseDTO<Void> update(@ModelAttribute("product") ProductDTO productDTO)
        throws IOException {

        if (!productDTO.getFile().isEmpty()) {
            if (new File(UPLOAD_FOLDER).exists()) {
                new File(UPLOAD_FOLDER).mkdir();
            }
            String filename = productDTO.getFile().getOriginalFilename();
            // lấy định dạng file
            String extension = filename.substring((filename.lastIndexOf(".")));

            String newFilename = UUID.randomUUID().toString() + extension;
            File newFile = new File(UPLOAD_FOLDER + newFilename);
            productDTO.getFile().transferTo(newFile);

            productDTO.setImage(newFilename);
        }

        productService.update(productDTO);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @CacheEvict(cacheNames = "product", key = "#id")
    @DeleteMapping
    public ResponseDTO<Void> delete(@RequestParam("id") Long id) {
        productService.delete(id);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @Cacheable(cacheNames = "product", key = "#id", unless = "#result == null")
    @GetMapping
    public ResponseDTO<ProductDTO> getById(@RequestParam("id") Long id) {
        return ResponseDTO.<ProductDTO>builder()
            .status(200).msg("OK")
            .data(productService.getById(id))
            .build();
    }

    @Cacheable(cacheNames = "product-search")
    @PostMapping("/search")
    public ResponseDTO<PageDTO<ProductDTO>> search(@RequestBody @Valid SearchProductDTO searchDTO) {
        return ResponseDTO.<PageDTO<ProductDTO>>builder().status(200).msg("OK")
            .data(productService.searchService(searchDTO))
            .build();
    }

    @GetMapping("/download")
    public void download(@RequestParam("filename") String filename, HttpServletResponse resp)
        throws IOException {
        File file = new File(UPLOAD_FOLDER + filename);
        Files.copy(file.toPath(), resp.getOutputStream());
    }
}

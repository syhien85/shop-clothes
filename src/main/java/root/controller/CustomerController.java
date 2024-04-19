package root.controller;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import root.dto.CustomerDTO;
import root.dto.PageDTO;
import root.dto.ResponseDTO;
import root.dto.SearchDTO;
import root.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;

    @CacheEvict(cacheNames = "customer-search", allEntries = true)
    @PostMapping
    public ResponseDTO<Void> create(@RequestBody @Valid CustomerDTO customerDTO) {
        customerService.create(customerDTO);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @Caching(evict = {
        @CacheEvict(cacheNames = "customer-search", allEntries = true),
        @CacheEvict(cacheNames = "customer", key = "#customerDTO.user.id") // khi update delete cache
    })
    @PutMapping
    public ResponseDTO<Void> update(@RequestBody CustomerDTO customerDTO) {
        customerService.update(customerDTO);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @CacheEvict(cacheNames = "customer", key = "#id")
    @DeleteMapping
    public ResponseDTO<Void> delete(@RequestParam("id") Long id) {
        customerService.delete(id);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @Cacheable(cacheNames = "customer", key = "#id", unless = "#result == null")
    @GetMapping
    public ResponseDTO<CustomerDTO> getById(@RequestParam("id") Long id) {
        return ResponseDTO.<CustomerDTO>builder().status(200).msg("OK")
            .data(customerService.getById(id))
            .build();
    }

    @Cacheable(cacheNames = "customer-search")
    @PostMapping("/search")
    public ResponseDTO<PageDTO<CustomerDTO>> search(@RequestBody @Valid SearchDTO searchDTO) {
        return ResponseDTO.<PageDTO<CustomerDTO>>builder().status(200).msg("OK")
            .data(customerService.searchName(searchDTO))
            .build();
    }

    @Caching(evict = {
        @CacheEvict(cacheNames = "customer-search", allEntries = true),
        @CacheEvict(cacheNames = "customer", key = "#customerDTO.user.id") // khi update delete cache
    })
    @PutMapping("/update-password")
    public ResponseDTO<Void> updatePassword(@RequestBody CustomerDTO customerDTO) {
        customerService.updatePassword(customerDTO);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }
}

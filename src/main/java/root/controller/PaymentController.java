package root.controller;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import root.dto.PaymentDTO;
import root.dto.ResponseDTO;
import root.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @CacheEvict(cacheNames = "payment-search", allEntries = true)
    @PostMapping
    public ResponseDTO<Void> create(@RequestBody @Valid PaymentDTO paymentDTO) {
        paymentService.create(paymentDTO);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @Caching(evict = {
        @CacheEvict(cacheNames = "payment-search", allEntries = true),
        @CacheEvict(cacheNames = "payment", key = "#paymentDTO.id") // khi update delete cache
    })
    @PutMapping
    public ResponseDTO<Void> update(@RequestBody PaymentDTO paymentDTO) {
        paymentService.update(paymentDTO);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @CacheEvict(cacheNames = "payment", key = "#id")
    @DeleteMapping
    public ResponseDTO<Void> delete(@RequestParam("id") Integer id) {
        paymentService.delete(id);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @Cacheable(cacheNames = "payment", key = "#id", unless = "#result == null")
    @GetMapping
    public ResponseDTO<PaymentDTO> getById(@RequestParam("id") Integer id) {
        return ResponseDTO.<PaymentDTO>builder()
            .status(200).msg("OK")
            .data(paymentService.getById(id))
            .build();
    }

    @Cacheable(cacheNames = "payment-search")
    @PostMapping("/list")
    public ResponseDTO<List<PaymentDTO>> listAll() {
        return ResponseDTO.<List<PaymentDTO>>builder().status(200).msg("OK")
            .data(paymentService.listAll())
            .build();
    }
}

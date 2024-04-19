package root.controller;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import root.dto.CouponDTO;
import root.dto.PageDTO;
import root.dto.ResponseDTO;
import root.dto.SearchDTO;
import root.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/coupon")
public class CouponController {

    private final CouponService couponService;

    @CacheEvict(cacheNames = "coupon-search", allEntries = true)
    @PostMapping
    public ResponseDTO<Void> create(@RequestBody @Valid CouponDTO couponDTO) {
        couponService.create(couponDTO);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @Caching(evict = {
        @CacheEvict(cacheNames = "coupon-search", allEntries = true),
        @CacheEvict(cacheNames = "coupon", key = "#couponDTO.id") // khi update delete cache
    })
    @PutMapping
    public ResponseDTO<Void> update(@RequestBody @Valid CouponDTO couponDTO) {
        couponService.update(couponDTO);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @CacheEvict(cacheNames = "coupon", key = "#id")
    @DeleteMapping
    public ResponseDTO<Void> delete(@RequestParam("id") Integer id) {
        couponService.delete(id);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @Cacheable(cacheNames = "coupon", key = "#id", unless = "#result == null")
    @GetMapping
    public ResponseDTO<CouponDTO> getById(@RequestParam("id") Integer id) {
        return ResponseDTO.<CouponDTO>builder()
            .status(200).msg("OK")
            .data(couponService.getById(id))
            .build();
    }

    @Cacheable(cacheNames = "coupon-search")
    @PostMapping("/search")
    public ResponseDTO<PageDTO<CouponDTO>> search(@RequestBody @Valid SearchDTO searchDTO) {
        return ResponseDTO.<PageDTO<CouponDTO>>builder().status(200).msg("OK")
            .data(couponService.searchService(searchDTO))
            .build();
    }
}

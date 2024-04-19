package root.controller;

import root.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@RestController
@RequestMapping("/cache")
public class CacheController {

    private final CacheManager cacheManager;

    @GetMapping("/")
    public ResponseDTO<List<String>> getCaches() {
//        return cacheManager.getCacheNames().stream().collect(Collectors.toList());
        return ResponseDTO.<List<String>>builder().status(200)
            .data(new ArrayList<>(cacheManager.getCacheNames()))
            .msg("OK").build();
    }

    @DeleteMapping("/")
    public ResponseDTO<Void> delete(@RequestParam("name") String name) {
        Cache cache = cacheManager.getCache(name);
        if (cache != null) {
            cache.invalidate();
        }
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @DeleteMapping("/clear-all")
    public ResponseDTO<Void> clearCache(){
        for(String name:cacheManager.getCacheNames()){
            Objects.requireNonNull(cacheManager.getCache(name)).clear();
        }
        return ResponseDTO.<Void>builder().status(200).msg("Clear all cache.").build();
    }
}

package root.service;

import root.dto.CouponDTO;
import root.dto.PageDTO;
import root.dto.SearchDTO;
import root.entity.Coupon;
import root.exception.DuplicateResourceException;
import root.exception.ResourceNotFoundException;
import root.repository.CouponRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CouponService {

    private final CouponRepo couponRepo;

    @Transactional
    public void create(CouponDTO couponDTO) {
        if (couponRepo.existsByCouponCode(couponDTO.getCouponCode())) {
            throw new DuplicateResourceException(
                "coupon with coupon code [%s] already taken".formatted(couponDTO.getCouponCode())
            );
        }
        Coupon coupon = new ModelMapper().map(couponDTO, Coupon.class);
        couponRepo.save(coupon);
    }

    @Transactional
    public void update(CouponDTO couponDTO) {
        Coupon currentCoupon = couponRepo.findById(couponDTO.getId()).orElseThrow(
            () -> new ResourceNotFoundException(
                "coupon with id [" + couponDTO.getId() + "] not found"
            )
        );

        if (couponDTO.getCouponCode() != null) {
            if (couponRepo.existsByCouponCode(couponDTO.getCouponCode())) {
                throw new DuplicateResourceException(
                    "coupon with coupon code [%s] already taken".formatted(couponDTO.getCouponCode())
                );
            }
            currentCoupon.setCouponCode(couponDTO.getCouponCode());
        }
        currentCoupon.setDiscountAmount(couponDTO.getDiscountAmount());
        currentCoupon.setExpiredDate(couponDTO.getExpiredDate());

        couponRepo.save(currentCoupon);
    }

    @Transactional
    public void delete(Integer id) {
        if (!couponRepo.existsById(id)) {
            throw new ResourceNotFoundException("coupon with id [" + id + "] not found");
        }
        couponRepo.deleteById(id);
    }

    public CouponDTO getById(Integer id) {
        return couponRepo.findById(id).map(this::convert).orElseThrow(
            () -> new ResourceNotFoundException("coupon with id [" + id + "] not found")
        );
    }

    public PageDTO<CouponDTO> searchService(SearchDTO searchDTO) {
        Sort sortBy = Sort.by("id").descending();

        if (searchDTO.getSortedField() != null && !searchDTO.getSortedField().isEmpty()) {
            sortBy = Sort.by(searchDTO.getSortedField()).ascending();
        }

        PageRequest pageRequest = PageRequest.of(
            searchDTO.getCurrentPage(), searchDTO.getSize(), sortBy
        );

        Page<Coupon> page = couponRepo.searchByCouponCode(
            "%" + searchDTO.getKeyword() + "%", pageRequest
        );

        return PageDTO.<CouponDTO>builder()
            .totalPage(page.getTotalPages())
            .totalElements(page.getTotalElements())
            .data(page.get().map(this::convert).toList())
            .build();
    }

    private CouponDTO convert(Coupon coupon) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(coupon, CouponDTO.class);
    }
}

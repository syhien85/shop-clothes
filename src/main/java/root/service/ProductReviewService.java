package root.service;

import root.dto.ProductReviewDTO;
import root.dto.PageDTO;
import root.dto.SearchCommentDTO;
import root.entity.Bill;
import root.entity.ProductReview;
import root.exception.RequestValidationException;
import root.exception.ResourceNotFoundException;
import root.repository.BillRepo;
import root.repository.ProductReviewRepo;
import root.repository.ProductRepo;
import root.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductReviewService {

    private final ProductReviewRepo productReviewRepo;
    private final ProductRepo productRepo;
    private final UserRepo userRepo;
    private final BillRepo billRepo;

    @Transactional
    public void create(ProductReviewDTO productReviewDTO) {
        if (!productRepo.existsById(productReviewDTO.getProduct().getId())) {
            throw new ResourceNotFoundException(
                "productReview with product id [%s] already taken".formatted(productReviewDTO.getProduct().getId())
            );
        }
        if (!userRepo.existsById(productReviewDTO.getUser().getId())) {
            throw new ResourceNotFoundException(
                "productReview with user id [%s] already taken".formatted(productReviewDTO.getUser().getId())
            );
        }

        Long userId = productReviewDTO.getUser().getId();
        Long productId = productReviewDTO.getProduct().getId();
        List<Bill> bills = billRepo.searchByUserIdAndProductId(userId, productId);
        if (bills.isEmpty()) {
            throw new RequestValidationException(
                "user with id [" + userId + "] have not purchased this product with id [" + productId + "] yet"
            );
        }

        ProductReview productReview = new ModelMapper().map(productReviewDTO, ProductReview.class);
        productReviewRepo.save(productReview);
    }

    @Transactional
    public void update(ProductReviewDTO productReviewDTO) {
        ProductReview currentProductReview = productReviewRepo.findById(productReviewDTO.getId()).orElseThrow(
            () -> new ResourceNotFoundException(
                "productReview with id [" + productReviewDTO.getId() + "] not found"
            )
        );

        currentProductReview.setStatus(productReviewDTO.getStatus());
        currentProductReview.setContent(productReviewDTO.getContent());
        currentProductReview.setRate(productReviewDTO.getRate());

        productReviewRepo.save(currentProductReview);
    }

    @Transactional
    public void delete(Long id) {
        if (!productReviewRepo.existsById(id)) {
            throw new ResourceNotFoundException("productReview with id [" + id + "] not found");
        }
        productReviewRepo.deleteById(id);
    }

    public ProductReviewDTO getById(Long id) {
        return productReviewRepo.findById(id).map(this::convert).orElseThrow(
            () -> new ResourceNotFoundException("productReview with id [" + id + "] not found")
        );
    }

    public PageDTO<ProductReviewDTO> searchService(SearchCommentDTO searchDTO) {
        Sort sortBy = Sort.by("id").descending();

        if (searchDTO.getSortedField() != null && !searchDTO.getSortedField().isEmpty()) {
            sortBy = Sort.by(searchDTO.getSortedField()).ascending();
        }

        PageRequest pageRequest = PageRequest.of(
            searchDTO.getCurrentPage(), searchDTO.getSize(), sortBy
        );

        String key = "%" + searchDTO.getKeyword() + "%";

        Long c1 = (searchDTO.getProductId() != null) ? searchDTO.getProductId() : null;
        Long c2 = searchDTO.getUserId();
        Date c3 = searchDTO.getStart();
        Date c4 = searchDTO.getEnd();

        Page<ProductReview> page = productReviewRepo.findAll(pageRequest);

        if (c1 == null && c2 == null && c3 == null && c4 == null) {
            page = productReviewRepo.searchBy(key, pageRequest);
        }
        if (c1 != null && c2 == null && c3 == null && c4 == null) {
            page = productReviewRepo.searchByC1(key, c1, pageRequest);
        }
        if (c1 == null && c2 != null && c3 == null && c4 == null) {
            page = productReviewRepo.searchByC2(key, c2, pageRequest);
        }
        if (c1 == null && c2 == null && c3 != null && c4 == null) {
            page = productReviewRepo.searchByC3(key, c3, pageRequest);
        }
        if (c1 == null && c2 == null && c3 == null && c4 != null) {
            page = productReviewRepo.searchByC4(key, c4, pageRequest);
        }
        if (c1 != null && c2 != null && c3 == null && c4 == null) {
            page = productReviewRepo.searchByC1AndC2(key, c1, c2, pageRequest);
        }
        if (c1 != null && c2 == null && c3 != null && c4 == null) {
            page = productReviewRepo.searchByC1AndC3(key, c1, c3, pageRequest);
        }
        if (c1 != null && c2 == null && c3 == null && c4 != null) {
            page = productReviewRepo.searchByC1AndC4(key, c1, c4, pageRequest);
        }
        if (c1 == null && c2 != null && c3 != null && c4 == null) {
            page = productReviewRepo.searchByC2AndC3(key, c2, c3, pageRequest);
        }
        if (c1 == null && c2 != null && c3 == null && c4 != null) {
            page = productReviewRepo.searchByC2AndC4(key, c2, c4, pageRequest);
        }
        if (c1 == null && c2 == null && c3 != null && c4 != null) {
            page = productReviewRepo.searchByC3AndC4(key, c3, c4, pageRequest);
        }
        if (c1 != null && c2 != null && c3 != null && c4 == null) {
            page = productReviewRepo.searchByC1AndC2AndC3(key, c1, c2, c3, pageRequest);
        }
        if (c1 != null && c2 != null && c3 == null && c4 != null) {
            page = productReviewRepo.searchByC1AndC2AndC4(key, c1, c2, c4, pageRequest);
        }
        if (c1 != null && c2 == null && c3 != null && c4 != null) {
            page = productReviewRepo.searchByC1AndC3AndC4(key, c1, c3, c4, pageRequest);
        }
        if (c1 == null && c2 != null && c3 != null && c4 != null) {
            page = productReviewRepo.searchByC2AndC3AndC4(key, c2, c3, c4, pageRequest);
        }
        if (c1 != null && c2 != null && c3 != null && c4 != null) {
            page = productReviewRepo.searchByC1AndC2AndC3AndC4(key, c1, c2, c3, c4, pageRequest);
        }

        return PageDTO.<ProductReviewDTO>builder()
            .totalPage(page.getTotalPages())
            .totalElements(page.getTotalElements())
            .data(page.get().map(this::convert).toList())
            .build();
    }

    private ProductReviewDTO convert(ProductReview productReview) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(productReview, ProductReviewDTO.class);
    }
}

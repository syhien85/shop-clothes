package root.service;

import root.dto.PageDTO;
import root.dto.ProductColorDTO;
import root.dto.SearchDTO;
import root.entity.ProductColor;
import root.exception.DuplicateResourceException;
import root.exception.RequestValidationException;
import root.exception.ResourceNotFoundException;
import root.repository.ColorRepo;
import root.repository.ProductColorRepo;
import root.repository.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProductColorService {

    private final ProductColorRepo productColorRepo;
    private final ProductRepo productRepo;
    private final ColorRepo colorRepo;

    @Transactional
    public void create(ProductColorDTO productColorDTO) {

        if (!productRepo.existsById(productColorDTO.getProduct().getId())) {
            throw new ResourceNotFoundException(
                "product with id [" + productColorDTO.getProduct().getId() + "] not found"
            );
        }

        if (!colorRepo.existsById(productColorDTO.getColor().getId())) {
            throw new ResourceNotFoundException(
                "color with id [" + productColorDTO.getColor().getId() + "] not found"
            );
        }

        Optional<ProductColor> existsProductWithColorIdAndProductId =
            productColorRepo.findByProductIdAndColorId(
                productColorDTO.getProduct().getId(),
                productColorDTO.getColor().getId()
            );
        if (existsProductWithColorIdAndProductId.isPresent()) {
            throw new DuplicateResourceException(
                "product color of product with id [%s] and color with id [%s] already taken"
                    .formatted(
                        productColorDTO.getProduct().getId(),
                        productColorDTO.getColor().getId()
                    )
            );
        }

        ProductColor productColor = new ModelMapper().map(productColorDTO, ProductColor.class);
        productColorRepo.save(productColor);
    }

    @Transactional
    public void update(ProductColorDTO productColorDTO) {
        ProductColor currentProductColor = productColorRepo.findById(productColorDTO.getId())
            .orElseThrow(
                () -> new ResourceNotFoundException(
                    "productColor with id [" + productColorDTO.getId() + "] not found"
                )
            );

        if (productColorDTO.getQuantity() <= 0) {
            throw new RequestValidationException("quantity of product more than 0");
        }
        currentProductColor.setQuantity(productColorDTO.getQuantity());

        currentProductColor.setImages(productColorDTO.getImages());

        productColorRepo.save(currentProductColor);
    }

    @Transactional
    public void delete(Integer id) {
        if (!productColorRepo.existsById(id)) {
            throw new ResourceNotFoundException("productColor with id [" + id + "] not found");
        }
        productColorRepo.deleteById(id);
    }

    public ProductColorDTO getById(Integer id) {
        return productColorRepo.findById(id).map(this::convert).orElseThrow(
            () -> new ResourceNotFoundException("productColor with id [" + id + "] not found")
        );
    }

    public PageDTO<ProductColorDTO> searchService(SearchDTO searchDTO) {
        Sort sortBy = Sort.by("id").descending();

        if (searchDTO.getSortedField() != null && !searchDTO.getSortedField().isEmpty()) {
            sortBy = Sort.by(searchDTO.getSortedField()).ascending();
        }

        PageRequest pageRequest = PageRequest.of(
            searchDTO.getCurrentPage(), searchDTO.getSize(), sortBy
        );

        Page<ProductColor> page = productColorRepo.searchByColorName(
            "%" + searchDTO.getKeyword() + "%", pageRequest
        );

        return PageDTO.<ProductColorDTO>builder()
            .totalPage(page.getTotalPages())
            .totalElements(page.getTotalElements())
            .data(page.get().map(this::convert).toList())
            .build();
    }

    private ProductColorDTO convert(ProductColor productColor) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(productColor, ProductColorDTO.class);
    }
}

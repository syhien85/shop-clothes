package root.service;

import root.dto.PageDTO;
import root.dto.ProductDTO;
import root.dto.SearchProductDTO;
import root.entity.Category;
import root.entity.Product;
import root.exception.ResourceNotFoundException;
import root.repository.CategoryRepo;
import root.repository.ProductRepo;
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
public class ProductService {

    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;

    @Transactional
    public void create(ProductDTO productDTO) {
        if (!categoryRepo.existsById(productDTO.getCategory().getId())) {
            throw new ResourceNotFoundException(
                "product with id [" + productDTO.getCategory().getId() + "] not found"
            );
        }

        Product product = new ModelMapper().map(productDTO, Product.class);
        productRepo.save(product);
    }

    @Transactional
    public void update(ProductDTO productDTO) {
        Product currentProduct = productRepo.findById(productDTO.getId()).orElseThrow(
            () -> new ResourceNotFoundException(
                "product with id [" + productDTO.getId() + "] not found"
            )
        );

        currentProduct.setName(productDTO.getName());
        currentProduct.setImage(productDTO.getImage());
        currentProduct.setDescription(productDTO.getDescription());
        currentProduct.setPrice(productDTO.getPrice());

        Category currentCategory = categoryRepo.findById(productDTO.getCategory().getId())
            .orElseThrow(
                () -> new ResourceNotFoundException(
                    "product with id [" + productDTO.getCategory().getId() + "] not found"
                )
            );
        currentProduct.setCategory(currentCategory);

        productRepo.save(currentProduct);
    }

    @Transactional
    public void delete(Long id) {
        if (!productRepo.existsById(id)) {
            throw new ResourceNotFoundException("product with id [" + id + "] not found");
        }
        productRepo.deleteById(id);
    }

    public ProductDTO getById(Long id) {
        return productRepo.findById(id).map(this::convert).orElseThrow(
            () -> new ResourceNotFoundException("product with id [" + id + "] not found")
        );
    }

    public PageDTO<ProductDTO> searchService(SearchProductDTO searchDTO) {
        Sort sortBy = Sort.by("id").descending();

        if (searchDTO.getSortedField() != null && !searchDTO.getSortedField().isEmpty()) {
            sortBy = Sort.by(searchDTO.getSortedField()).ascending();
        }

        PageRequest pageRequest = PageRequest.of(
            searchDTO.getCurrentPage(), searchDTO.getSize(), sortBy
        );

        String key = "%" + searchDTO.getKeyword() + "%";

        Double c1 = searchDTO.getPriceMin();
        Double c2 = searchDTO.getPriceMax();
        Integer c3 = searchDTO.getCategoryId();

        Page<Product> page = productRepo.findAll(pageRequest);

        if (c1 == null && c2 == null && c3 == null) {
            page = productRepo.searchBy(key, pageRequest);
        }
        if (c1 != null && c2 == null && c3 == null) {
            page = productRepo.searchByC1(key, c1, pageRequest);
        }
        if (c1 == null && c2 != null && c3 == null) {
            page = productRepo.searchByC2(key, c2, pageRequest);
        }
        if (c1 == null && c2 == null && c3 != null) {
            page = productRepo.searchByC3(key, c3, pageRequest);
        }
        if (c1 != null && c2 != null && c3 == null) {
            page = productRepo.searchByC1AndC2(key, c1, c2, pageRequest);
        }
        if (c1 != null && c2 == null && c3 != null) {
            page = productRepo.searchByC1AndC3(key, c1, c3, pageRequest);
        }
        if (c1 != null && c2 != null && c3 != null) {
            page = productRepo.searchByC1AndC2AndC3(key, c1, c2, c3, pageRequest);
            System.out.println(1111);
        }

        return PageDTO.<ProductDTO>builder()
            .totalPage(page.getTotalPages())
            .totalElements(page.getTotalElements())
            .data(page.get().map(this::convert).toList())
            .build();
    }

    private ProductDTO convert(Product product) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(product, ProductDTO.class);
    }
}

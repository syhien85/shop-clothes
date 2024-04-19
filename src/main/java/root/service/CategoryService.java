package root.service;

import root.dto.CategoryDTO;
import root.dto.PageDTO;
import root.dto.SearchDTO;
import root.entity.Category;
import root.exception.DuplicateResourceException;
import root.exception.RequestValidationException;
import root.exception.ResourceNotFoundException;
import root.repository.CategoryRepo;
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
public class CategoryService {

    private final CategoryRepo categoryRepo;

    @Transactional
    public void create(CategoryDTO categoryDTO) {
        if (categoryRepo.existsByName(categoryDTO.getName())) {
            throw new DuplicateResourceException(
                "category with name [%s] already taken".formatted(categoryDTO.getName())
            );
        }
        Category category = new ModelMapper().map(categoryDTO, Category.class);
        categoryRepo.save(category);
    }

    @Transactional
    public void update(CategoryDTO categoryDTO) {
        Category currentCategory = categoryRepo.findById(categoryDTO.getId()).orElseThrow(
            () -> new ResourceNotFoundException(
                "category with id [" + categoryDTO.getId() + "] not found"
            )
        );

        boolean changes = false;

        if (
            categoryDTO.getName() != null && !categoryDTO.getName().equals(currentCategory.getName())
        ) {
            if (categoryRepo.existsByName(categoryDTO.getName())) {
                throw new DuplicateResourceException(
                    "category with name [%s] already taken".formatted(categoryDTO.getName())
                );
            }

            currentCategory.setName(categoryDTO.getName());
            changes = true;
        }

        if (!changes) {
            throw new RequestValidationException("no data changes found");
        }

        categoryRepo.save(currentCategory);
    }

    @Transactional
    public void delete(Integer id) {
        if (!categoryRepo.existsById(id)) {
            throw new ResourceNotFoundException("category with id [" + id + "] not found");
        }
        categoryRepo.deleteById(id);
    }

    public CategoryDTO getById(Integer id) {
        return categoryRepo.findById(id).map(this::convert).orElseThrow(
            () -> new ResourceNotFoundException("category with id [" + id + "] not found")
        );
    }

    public PageDTO<CategoryDTO> searchService(SearchDTO searchDTO) {
        Sort sortBy = Sort.by("id").descending();

        if (searchDTO.getSortedField() != null && !searchDTO.getSortedField().isEmpty()) {
            sortBy = Sort.by(searchDTO.getSortedField()).ascending();
        }

        PageRequest pageRequest = PageRequest.of(
            searchDTO.getCurrentPage(), searchDTO.getSize(), sortBy
        );

        Page<Category> page = categoryRepo.searchByName(
            "%" + searchDTO.getKeyword() + "%", pageRequest
        );

        return PageDTO.<CategoryDTO>builder()
            .totalPage(page.getTotalPages())
            .totalElements(page.getTotalElements())
            .data(page.get().map(this::convert).toList())
            .build();
    }

    private CategoryDTO convert(Category category) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(category, CategoryDTO.class);
    }
}

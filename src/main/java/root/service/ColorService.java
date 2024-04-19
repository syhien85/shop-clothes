package root.service;

import root.dto.ColorDTO;
import root.dto.PageDTO;
import root.dto.SearchDTO;
import root.entity.Color;
import root.exception.DuplicateResourceException;
import root.exception.ResourceNotFoundException;
import root.repository.ColorRepo;
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
public class ColorService {

    private final ColorRepo colorRepo;

    @Transactional
    public void create(ColorDTO colorDTO) {
        if (colorRepo.existsByName(colorDTO.getName())) {
            throw new DuplicateResourceException(
                "color with name [%s] already taken".formatted(colorDTO.getName())
            );
        }
        Color color = new ModelMapper().map(colorDTO, Color.class);
        colorRepo.save(color);
    }

    @Transactional
    public void update(ColorDTO colorDTO) {
        Color currentColor = colorRepo.findById(colorDTO.getId()).orElseThrow(
            () -> new ResourceNotFoundException(
                "color with id [" + colorDTO.getId() + "] not found"
            )
        );

        if (colorDTO.getName() != null) {
            if (colorRepo.existsByName(colorDTO.getName())) {
                throw new DuplicateResourceException(
                    "color with name [%s] already taken".formatted(colorDTO.getName())
                );
            }
            currentColor.setName(colorDTO.getName());
        }

        colorRepo.save(currentColor);
    }

    @Transactional
    public void delete(Integer id) {
        if (!colorRepo.existsById(id)) {
            throw new ResourceNotFoundException("color with id [" + id + "] not found");
        }
        colorRepo.deleteById(id);
    }

    public ColorDTO getById(Integer id) {
        return colorRepo.findById(id).map(this::convert).orElseThrow(
            () -> new ResourceNotFoundException("color with id [" + id + "] not found")
        );
    }

    public PageDTO<ColorDTO> searchService(SearchDTO searchDTO) {
        Sort sortBy = Sort.by("id").descending();

        if (searchDTO.getSortedField() != null && !searchDTO.getSortedField().isEmpty()) {
            sortBy = Sort.by(searchDTO.getSortedField()).ascending();
        }

        PageRequest pageRequest = PageRequest.of(
            searchDTO.getCurrentPage(), searchDTO.getSize(), sortBy
        );

        Page<Color> page = colorRepo.searchByName(
            "%" + searchDTO.getKeyword() + "%", pageRequest
        );

        return PageDTO.<ColorDTO>builder()
            .totalPage(page.getTotalPages())
            .totalElements(page.getTotalElements())
            .data(page.get().map(this::convert).toList())
            .build();
    }

    private ColorDTO convert(Color color) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(color, ColorDTO.class);
    }
}

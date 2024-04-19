package root.service;

import root.dto.PageDTO;
import root.dto.RoleDTO;
import root.dto.SearchDTO;
import root.entity.Role;
import root.exception.DuplicateResourceException;
import root.exception.RequestValidationException;
import root.exception.ResourceNotFoundException;
import root.repository.RoleRepo;
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
public class RoleService {

    private final RoleRepo roleRepo;

    @Transactional
    public void create(RoleDTO roleDTO) {
        if (roleRepo.existsByName(roleDTO.getName())) {
            throw new DuplicateResourceException(
                "role with name [%s] already taken".formatted(roleDTO.getName())
            );
        }
        Role role = new ModelMapper().map(roleDTO, Role.class);
        roleRepo.save(role);
    }

    @Transactional
    public void update(RoleDTO roleDTO) {
        Role currentRole = roleRepo.findById(roleDTO.getId()).orElseThrow(
            () -> new ResourceNotFoundException(
                "role with id [" + roleDTO.getId() + "] not found"
            )
        );

        boolean changes = false;

        if (
            roleDTO.getName() != null && !roleDTO.getName().equals(currentRole.getName())
        ) {
            if (roleRepo.existsByName(roleDTO.getName())) {
                throw new DuplicateResourceException(
                    "role with name [%s] already taken".formatted(roleDTO.getName())
                );
            }

            currentRole.setName(roleDTO.getName());
            changes = true;
        }

        if (!changes) {
            throw new RequestValidationException("no data changes found");
        }

        roleRepo.save(currentRole);
    }

    @Transactional
    public void delete(Integer id) {
        if (!roleRepo.existsById(id)) {
            throw new ResourceNotFoundException("role with id [" + id + "] not found");
        }
        roleRepo.deleteById(id);
    }

    public RoleDTO getById(Integer id) {
        return roleRepo.findById(id).map(this::convert).orElseThrow(
            () -> new ResourceNotFoundException("role with id [" + id + "] not found")
        );
    }

    public PageDTO<RoleDTO> searchService(SearchDTO searchDTO) {
        Sort sortBy = Sort.by("id").descending();

        if (searchDTO.getSortedField() != null && !searchDTO.getSortedField().isEmpty()) {
            sortBy = Sort.by(searchDTO.getSortedField()).ascending();
        }

        PageRequest pageRequest = PageRequest.of(
            searchDTO.getCurrentPage(),
            searchDTO.getSize(),
            sortBy
        );

        Page<Role> page = roleRepo.searchByName("%" + searchDTO.getKeyword() + "%", pageRequest);

        return PageDTO.<RoleDTO>builder()
            .totalPage(page.getTotalPages())
            .totalElements(page.getTotalElements())
            .data(page.get().map(this::convert).toList())
            .build();
    }

    private RoleDTO convert(Role role) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(role, RoleDTO.class);
    }
}

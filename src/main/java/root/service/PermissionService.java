package root.service;

import root.dto.PermissionDTO;
import root.entity.Permission;
import root.exception.ResourceNotFoundException;
import root.repository.PermissionRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PermissionService {

    private final PermissionRepo permissionRepo;

    @Transactional
    public void create(PermissionDTO permissionDTO) {
        Permission permission = new ModelMapper().map(permissionDTO, Permission.class);
        permissionRepo.save(permission);
    }

    @Transactional
    public void update(PermissionDTO permissionDTO) {
        Permission currentPermission = permissionRepo.findById(permissionDTO.getId()).orElseThrow(
            () -> new ResourceNotFoundException(
                "permission with id [" + permissionDTO.getId() + "] not found"
            )
        );

        currentPermission.setPath(permissionDTO.getPath());
        currentPermission.setMethod(permissionDTO.getMethod());
        currentPermission.setVisibility(permissionDTO.isVisibility());
        currentPermission.setRoles(permissionDTO.getRoles());

        permissionRepo.save(currentPermission);
    }

    @Transactional
    public void delete(Integer id) {
        if (!permissionRepo.existsById(id)) {
            throw new ResourceNotFoundException("permission with id [" + id + "] not found");
        }
        permissionRepo.deleteById(id);
    }

    public PermissionDTO getById(Integer id) {
        return permissionRepo.findById(id).map(this::convert).orElseThrow(
            () -> new ResourceNotFoundException("permission with id [" + id + "] not found")
        );
    }

    public List<PermissionDTO> listService() {
        List<Permission> permissions = permissionRepo.findAll();
        return permissions.stream().map(this::convert).toList();
    }

    private PermissionDTO convert(Permission permission) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(permission, PermissionDTO.class);
    }
}

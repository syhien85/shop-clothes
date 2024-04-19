package root.repository;

import org.springframework.data.repository.CrudRepository;
import root.entity.Permission;

import java.util.List;

public interface PermissionRepo extends CrudRepository<Permission, Integer> {
    List<Permission> findAll();
}

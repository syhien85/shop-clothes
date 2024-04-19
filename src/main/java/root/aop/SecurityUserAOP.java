package root.aop;

import root.dto.UserDTO;
import root.exception.ForbiddenException;
import root.exception.ResourceNotFoundException;
import root.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Aspect
@Component
@Slf4j
public class SecurityUserAOP {
    private final UserRepo userRepo;

    @Before("execution(* root.service.UserService.update(*))")
    public void update(JoinPoint joinPoint) {
        UserDTO userDTO = (UserDTO) joinPoint.getArgs()[0]; // array các tham số
        if (!userRepo.existsById(userDTO.getId())) {
            throw new ResourceNotFoundException("user with id [" + userDTO.getId() + "] not found");
        }
        String usernameRequest = userRepo.findById(userDTO.getId()).get().getUsername();
        String usernameSecurity = getUsernameSecurityContextHolder();

        List<String> roles = getRolesSecurityContextHolder();
        // Chỉ username nào sở hữu user.id đó và role ADMIN, còn lại bị cấm
        if (roles == null || !roles.contains("ADMIN")) {
            if (usernameRequest != null && !usernameRequest.equals(usernameSecurity)) {
                throw new ForbiddenException("Forbidden AOP");
            }
        }
    }

    @Before("execution(* root.service.UserService.updatePassword(*))")
    public void updatePassword(JoinPoint joinPoint) {
        UserDTO userDTO = (UserDTO) joinPoint.getArgs()[0]; // array các tham số
        if (!userRepo.existsById(userDTO.getId())) {
            throw new ResourceNotFoundException("user with id [" + userDTO.getId() + "] not found");
        }
        String usernameRequest = userRepo.findById(userDTO.getId()).get().getUsername();
        String usernameSecurity = getUsernameSecurityContextHolder();

        List<String> roles = getRolesSecurityContextHolder();
        // Chỉ username nào sở hữu user.id đó và role ADMIN, còn lại bị cấm
        if (roles == null || !roles.contains("ADMIN")) {
            if (usernameRequest != null && !usernameRequest.equals(usernameSecurity)) {
                throw new ForbiddenException("Forbidden ADMIN AOP");
            }
        }
    }

    @Before("execution(* root.service.UserService.delete(*))")
    public void delete(JoinPoint joinPoint) {
        long id = (long) joinPoint.getArgs()[0]; // array các tham số
        if (!userRepo.existsById(id)) {
            throw new ResourceNotFoundException("user with id [" + id + "] not found");
        }
        String usernameRequest = userRepo.findById(id).get().getUsername();
        String usernameSecurity = getUsernameSecurityContextHolder();

        List<String> roles = getRolesSecurityContextHolder();
        // Chỉ username nào sở hữu user.id đó và role ADMIN, còn lại bị cấm
        if (roles == null || !roles.contains("ADMIN")) {
            if (usernameRequest != null && !usernameRequest.equals(usernameSecurity)) {
                throw new ForbiddenException("Forbidden ADMIN AOP");
            }
        }
    }

    @Before("execution(* root.service.UserService.getById(*))")
    public void getById(JoinPoint joinPoint) {
        long id = (long) joinPoint.getArgs()[0]; // array các tham số
        if (!userRepo.existsById(id)) {
            throw new ResourceNotFoundException("user with id [" + id + "] not found");
        }
        String usernameRequest = userRepo.findById(id).get().getUsername();
        String usernameSecurity = getUsernameSecurityContextHolder();

        List<String> roles = getRolesSecurityContextHolder();
        // Chỉ username nào sở hữu user.id đó và role ADMIN, còn lại bị cấm
        if (roles == null || !roles.contains("ADMIN")) {
            if (usernameRequest != null && !usernameRequest.equals(usernameSecurity)) {
                throw new ForbiddenException("Forbidden ADMIN AOP");
            }
        }
    }

    /*@Before("execution(* root.service.UserService.searchByName(*))")
    public void searchService(JoinPoint joinPoint) {
        List<String> roles = getRolesSecurityContextHolder();
        if (roles == null || !roles.contains("ADMIN")) {
            throw new ForbiddenException("Forbidden ADMIN AOP");
        }
    }*/

    private static List<String> getRolesSecurityContextHolder() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            List<String> roles = authentication
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
            return roles;
        }
        return null;
    }

    private static String getUsernameSecurityContextHolder() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return authentication.getName();
        }
        return null;
    }
}

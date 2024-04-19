package root.interceptor;

import lombok.RequiredArgsConstructor;
import root.entity.Permission;
import root.exception.ForbiddenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import root.repository.PermissionRepo;

import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class SecurityInterceptor implements HandlerInterceptor {

    private final PermissionRepo permissionRepo;

    @Override
    public boolean preHandle(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Object handler
    ) {
        List<String> rolesSecurityContext = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            rolesSecurityContext = authentication
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        }

        String path = request.getServletPath();
        String method = request.getMethod();

        List<Permission> allPermission = permissionRepo.findAll();

        for (Permission permission : allPermission) {

            String pPath = permission.getPath();
            String pMethod = permission.getMethod();

            // chỉ check các path & method có trong bảng Permission
            if (path.equals(pPath) && method.contains(pMethod)) {

                // Visibility == true => return true;
                if (permission.isVisibility()) return true;

                // securityContext == ADMIN => return true;
                if (rolesSecurityContext != null && rolesSecurityContext.contains("ADMIN")) {
                    return true;
                }

                // chỉ cần một trong các roles của Security Context có trong list roles của permission
                boolean rolesHasPermisstionRoles = false;
                for (String pRole : permission.getRoles()) {
                    if (rolesSecurityContext != null && rolesSecurityContext.contains(pRole)) {
                        rolesHasPermisstionRoles = true;
                        break;
                    }
                }

                // nếu ko có role nào thì throw exception
                if (!rolesHasPermisstionRoles) {
                    throw new ForbiddenException("Forbidden Interceptor");
                }
            }
        }

        return true;
    }
}


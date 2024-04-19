package root;

import root.entity.Role;
import root.entity.User;
import root.repository.RoleRepo;
import root.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class DemoData implements ApplicationRunner {

    private final RoleRepo roleRepo;
    private final UserRepo userRepo;

    // Insert demo data
    @Override
    public void run(ApplicationArguments args) throws Exception {
        Role role = new Role();

//        role.setName("ROLE_ADMIN");
        role.setName("ADMIN");

        if (!roleRepo.existsByName(role.getName())) {
            try {
                roleRepo.save(role);

                User user = new User();
                user.setName("Admin");
                user.setUsername("admin");
                user.setPassword(new BCryptPasswordEncoder().encode("123123"));
                user.setEmail("syhien085@yahoo.com");

                Date From18YearAgo = Date.from(ZonedDateTime.now(ZoneOffset.UTC).minusYears(20).toInstant());
                user.setBirthdate(From18YearAgo);

                user.setRoles(List.of(role));

                userRepo.save(user);

                log.warn("Created username: admin, role: ADMIN");

            } catch (Exception e) {

            }
        }
    }
}

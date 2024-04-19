package root.service;

import root.dto.PageDTO;
import root.dto.SearchDTO;
import root.dto.UserDTO;
import root.entity.Role;
import root.entity.User;
import root.exception.DuplicateResourceException;
import root.exception.ResourceNotFoundException;
import root.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final UserRepo userRepo;
    private final EmailService emailService;

    @Transactional
    public void create(UserDTO userDTO) {
        User user = new ModelMapper().map(userDTO, User.class);
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        if (userRepo.existsByUsername(userDTO.getUsername())) {
            throw new DuplicateResourceException("username [" + userDTO.getUsername() + "] already taken");
        }
        if (userRepo.existsByEmail(userDTO.getEmail())) {
            throw new DuplicateResourceException("email [" + userDTO.getEmail() + "] already taken");
        }
        userRepo.save(user);

        String bodyEmail =
            "<p>Hi " + user.getName() + ",</p>" +
                "<p>Thanks for signing up and creating an account!</p>" +
                "<p>Your account information: </p>" +
                "<p>Username: <b>" + user.getName() + "</b></p>" +
                "<p>Email: <b>" + user.getEmail() + "</b></p>";

        emailService.sendMail(
//            user.getEmail(),
            "syhien85@hotmail.com",
            "Account successfully created - Project 3 Springboot",
            bodyEmail
        );
    }

    @Transactional
    public void update(UserDTO userDTO) {

        User currentUser = userRepo.findById(userDTO.getId()).orElseThrow(
            () -> new ResourceNotFoundException("user with id [" + userDTO.getId() + "] not found")
        );

        currentUser.setName(userDTO.getName());
        currentUser.setAge(userDTO.getAge());
        currentUser.setUsername(userDTO.getUsername());
        currentUser.setHomeAddress(userDTO.getHomeAddress());
        currentUser.setBirthdate(userDTO.getBirthdate());

        String email = userDTO.getEmail();
        if (email != null && !email.equals(currentUser.getEmail())) {
            if (userRepo.existsByEmail(userDTO.getEmail())) {
                throw new DuplicateResourceException("email [" + userDTO.getEmail() + "] already taken");
            }
            currentUser.setEmail(userDTO.getEmail());
        }

        userRepo.save(currentUser);

    }

    @Transactional
    public void updatePassword(UserDTO userDTO) {
        User currentUser = userRepo.findById(userDTO.getId()).orElseThrow(
            () -> new ResourceNotFoundException("user with id [" + userDTO.getId() + "] not found")
        );

        String passwordEncoder = new BCryptPasswordEncoder().encode(userDTO.getPassword());
        if (userDTO.getPassword() != null && !passwordEncoder.equals(currentUser.getPassword())) {
            currentUser.setPassword(passwordEncoder);
        }

        userRepo.save(currentUser);
    }

    @Transactional
    public void forgotPassword(String usernameOrEmail) {
        User currentUser = userRepo.findByUsernameOrEmail(usernameOrEmail).orElseThrow(
            () -> new ResourceNotFoundException(
                "user with username or email [" + usernameOrEmail + "] not exists"
            )
        );

        // generate password
        String passwordGenerated = getPasswordGenerated();

        String bodyEmail =
            "<p>Hi " + currentUser.getUsername() + ",</p>" +
            "<p>Use your secret code!</p>" +
            "<h3><b>" + passwordGenerated + "</b></h3>" +
            "<p>If you did not forget your password, you can ignore this email.</p>";

        emailService.sendMail(
            currentUser.getEmail(),
            "Password retrieval - Project 3 Springboot",
            bodyEmail
        );

        String passwordEncoder = new BCryptPasswordEncoder().encode(passwordGenerated);

        currentUser.setPassword(passwordEncoder);

        userRepo.save(currentUser);
    }

    private static String getPasswordGenerated() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 6;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    @Transactional
    public void updateAvatar(UserDTO userDTO) {
        User currentUser = userRepo.findById(userDTO.getId()).orElseThrow(
            () -> new ResourceNotFoundException("user with id [" + userDTO.getId() + "] not found")
        );
        currentUser.setAvatarUrl(userDTO.getAvatarUrl());
        userRepo.save(currentUser);
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepo.existsById(id)) {
            throw new ResourceNotFoundException("user with id [" + id + "] not found");
        }
        userRepo.deleteById(id);
    }

    public UserDTO getById(Long id) {
        return userRepo.findById(id).map(this::convert).orElseThrow(
            () -> new ResourceNotFoundException("user with id [" + id + "] not found")
        );
    }

    public PageDTO<UserDTO> searchService(SearchDTO searchDTO) {
        Sort sortBy = Sort.by("id").descending();

        if (searchDTO.getSortedField() != null && !searchDTO.getSortedField().isEmpty()) {
            sortBy = Sort.by(searchDTO.getSortedField()).ascending();
        }

        PageRequest pageRequest = PageRequest.of(
            searchDTO.getCurrentPage(),
            searchDTO.getSize(),
            sortBy
        );

        Page<User> page = userRepo.searchByName("%" + searchDTO.getKeyword() + "%", pageRequest);

        return PageDTO.<UserDTO>builder()
            .totalPage(page.getTotalPages())
            .totalElements(page.getTotalElements())
            .data(page.get().map(this::convert).toList())
            .build();
    }

    private UserDTO convert(User user) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(user, UserDTO.class);
    }

    // Security
    /*@Transactional*/
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userEntity = userRepo.findByUsername(username);
        if (userEntity.isEmpty()) {
            throw new UsernameNotFoundException("Not Found Username");
        }
        // convert User -> UserDetails
        // chuyển role về quyền(authority) trong security
        List<SimpleGrantedAuthority> authorities = userEntity.get().getRoles()
            .stream()
            .map((Role role) -> new SimpleGrantedAuthority(role.getName()))
            .toList();

        // User này là con của UserDetails
        return new org.springframework.security.core.userdetails.User(
            username,
            userEntity.get().getPassword(),
            authorities
        );
    }
}

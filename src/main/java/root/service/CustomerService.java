package root.service;

import root.dto.CustomerDTO;
import root.dto.PageDTO;
import root.dto.SearchDTO;
import root.entity.Customer;
import root.entity.Role;
import root.entity.User;
import root.exception.DuplicateResourceException;
import root.exception.RequestValidationException;
import root.exception.ResourceNotFoundException;
import root.repository.CustomerRepo;
import root.repository.RoleRepo;
import root.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CustomerService {

    private final CustomerRepo customerRepo;
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;

    @Transactional
    public void create(CustomerDTO customerDTO) {

        String userEmail = customerDTO.getUser().getEmail();
        if (userEmail == null || userEmail.isEmpty()) {
            throw new RequestValidationException("user with email must not be blank");
        }
        if(userRepo.existsByEmail(userEmail)) {
            throw new DuplicateResourceException("email [" + userEmail + "] already taken");
        }

        String userName = customerDTO.getUser().getName();
        if (userName == null || userName.isEmpty()) {
            throw new RequestValidationException("user with name must not be blank");
        }

        Customer customer = new ModelMapper().map(customerDTO, Customer.class);

        Role role =  roleRepo.findByName("CUSTOMER")
            .orElseThrow(() ->
                new RequestValidationException("role with name [CUSTOMER] is not found")
            );

        User user = customer.getUser();

        user.setRoles(List.of(role));

        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));

        customer.setUser(user);

        customerRepo.save(customer);
    }

    @Transactional
    public void update(CustomerDTO customerDTO) {

        Customer currentCustomer = customerRepo.findById(customerDTO.getUser().getId()).orElseThrow(
            () -> new ResourceNotFoundException(
                "customer with id [" + customerDTO.getUser().getId() + "] not found"
            )
        );

        User currentUser = currentCustomer.getUser();

        currentUser.setName(customerDTO.getUser().getName());
        currentUser.setAge(customerDTO.getUser().getAge());
        currentUser.setUsername(customerDTO.getUser().getUsername());
        currentUser.setHomeAddress(customerDTO.getUser().getHomeAddress());
        currentUser.setBirthdate(customerDTO.getUser().getBirthdate());

        if(userRepo.existsByEmail(customerDTO.getUser().getEmail())) {
            throw new DuplicateResourceException(
                "email [" + customerDTO.getUser().getEmail() + "] already taken"
            );
        }
        currentUser.setEmail(customerDTO.getUser().getEmail());

        currentCustomer.setUser(currentUser);
        customerRepo.save(currentCustomer);
    }

    @Transactional
    public void updatePassword(CustomerDTO customerDTO) {
        Customer currentCustomer = customerRepo.findById(customerDTO.getUser().getId()).orElseThrow(
            () -> new ResourceNotFoundException(
                "customer with id [" + customerDTO.getUser().getId() + "] not found"
            )
        );

        if (customerDTO.getUser().getPassword() != null) {
            String passwordEncoder =
                new BCryptPasswordEncoder().encode(customerDTO.getUser().getPassword());
            currentCustomer.getUser().setPassword(passwordEncoder);
        }

        currentCustomer.setUser(currentCustomer.getUser());
        customerRepo.save(currentCustomer);
    }

    @Transactional
    public void delete(Long id) {
        if (!customerRepo.existsById(id)) {
            throw new ResourceNotFoundException("customer with id [" + id + "] not found");
        }
        customerRepo.deleteById(id);
    }

    public CustomerDTO getById(Long id) {
        return customerRepo.findById(id).map(this::convert).orElseThrow(
            () -> new ResourceNotFoundException("customer with id [" + id + "] not found")
        );
    }

    public PageDTO<CustomerDTO> searchName(SearchDTO searchDTO) {
        Sort sortBy = Sort.by("id").descending();

        if (searchDTO.getSortedField() != null && !searchDTO.getSortedField().isEmpty()) {
            sortBy = Sort.by(searchDTO.getSortedField()).ascending();
        }

        PageRequest pageRequest = PageRequest.of(
            searchDTO.getCurrentPage(),
            searchDTO.getSize(),
            sortBy
        );

        Page<Customer> page = customerRepo.searchByName(
            "%" + searchDTO.getKeyword() + "%",
            pageRequest
        );

        return PageDTO.<CustomerDTO>builder()
            .totalPage(page.getTotalPages())
            .totalElements(page.getTotalElements())
            .data(page.get().map(this::convert).toList())
            .build();
    }

    private CustomerDTO convert(Customer customer) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(customer, CustomerDTO.class);
    }
}

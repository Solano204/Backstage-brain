package neobank.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import neobank.dto.UserRegistrationRequest;
import neobank.entity.User;
import neobank.exception.ResourceAlreadyExistsException;
import neobank.exception.ResourceNotFoundException;
import neobank.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(UserRegistrationRequest request) {
        log.info("Registering new user with email: {}", request.email());

        if (userRepository.existsByEmail(request.email())) {
            throw new ResourceAlreadyExistsException("Email already registered");
        }

        if (request.phoneNumber() != null && userRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new ResourceAlreadyExistsException("Phone number already registered");
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phoneNumber(request.phoneNumber())
                .roles(Set.of("ROLE_USER"))
                .createdAt(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Transactional
    public User updateUser(Long userId, String firstName, String lastName, String phoneNumber) {
        User user = findById(userId);

        if (firstName != null) user.setFirstName(firstName);
        if (lastName != null) user.setLastName(lastName);
        if (phoneNumber != null) {
            if (userRepository.existsByPhoneNumber(phoneNumber)) {
                throw new ResourceAlreadyExistsException("Phone number already in use");
            }
            user.setPhoneNumber(phoneNumber);
        }

        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Transactional
    public void deactivateUser(Long userId) {
        User user = findById(userId);
        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}
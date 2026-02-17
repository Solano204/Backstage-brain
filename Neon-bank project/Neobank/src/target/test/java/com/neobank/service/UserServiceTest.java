package com.neobank.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_Success() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "test@example.com", "password123", "John", "Doe", "+1234567890"
        );

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(request.phoneNumber())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User result = userService.registerUser(request);

        assertNotNull(result);
        assertEquals(request.email(), result.getEmail());
        assertEquals(request.firstName(), result.getFirstName());
        assertEquals(request.lastName(), result.getLastName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_EmailAlreadyExists() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "existing@example.com", "password123", "John", "Doe", null
        );

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () ->
                userService.registerUser(request)
        );
        verify(userRepository, never()).save(any());
    }

    @Test
    void findByEmail_Success() {
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        User result = userService.findByEmail("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void findByEmail_NotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                userService.findByEmail("nonexistent@example.com")
        );
    }

    @Test
    void updateUser_Success() {
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User result = userService.updateUser(1L, "Jane", "Smith", "+9876543210");

        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals("+9876543210", result.getPhoneNumber());
    }

    @Test
    void deactivateUser_Success() {
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .active(true)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        userService.deactivateUser(1L);

        assertFalse(user.isActive());
        verify(userRepository).save(user);
    }
}
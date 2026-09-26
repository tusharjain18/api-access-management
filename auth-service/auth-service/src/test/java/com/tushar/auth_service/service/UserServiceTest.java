package com.tushar.auth_service.service;

import com.tushar.auth_service.exception.ResourceNotFoundException;
import com.tushar.auth_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.tushar.auth_service.dto.UserResponse;
import com.tushar.auth_service.entity.Role;
import com.tushar.auth_service.entity.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
    void getUserProfile_shouldReturnUserProfile_whenUserExists() {
        Role userRole = new Role("USER");

        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(userRole);

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        UserResponse result = userService.getUserProfile("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("USER", result.getRole());

        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void getUserProfile_shouldThrowException_whenUserDoesNotExist() {
        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getUserProfile("unknown")
        );

        verify(userRepository).findByUsername("unknown");
    }
}
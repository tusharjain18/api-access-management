package com.tushar.auth_service.service;

import com.tushar.auth_service.dto.MessageResponse;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    private AdminService adminService;

    @BeforeEach
    void setUp() {
        adminService = new AdminService(userRepository);
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        Role userRole = new Role("USER");

        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setPassword("password");
        user1.setRole(userRole);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword("password");
        user2.setRole(userRole);

        when(userRepository.findAll())
                .thenReturn(List.of(user1, user2));

        List<UserResponse> result = adminService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
        assertEquals("USER", result.get(0).getRole());

        verify(userRepository).findAll();
    }

    @Test
    void deleteUser_shouldDeleteUser_whenUserIsNotCurrentUser() {
        Role userRole = new Role("USER");

        User user = new User();
        user.setUsername("user1");
        user.setEmail("user1@example.com");
        user.setPassword("password");
        user.setRole(userRole);

        when(userRepository.findById(1L))
                .thenReturn(java.util.Optional.of(user));

        MessageResponse result =
                adminService.deleteUser(1L, "admin");

        assertNotNull(result);
        assertEquals("User deleted successfully", result.getMessage());

        verify(userRepository).findById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_shouldThrowException_whenUserDoesNotExist() {
        when(userRepository.findById(99L))
                .thenReturn(java.util.Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> adminService.deleteUser(99L, "admin")
        );

        verify(userRepository).findById(99L);
        verify(userRepository, never()).deleteById(99L);
    }
}
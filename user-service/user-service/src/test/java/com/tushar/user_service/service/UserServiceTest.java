package com.tushar.user_service.service;

import com.tushar.user_service.exception.DuplicateResourceException;
import com.tushar.user_service.exception.ResourceNotFoundException;
import com.tushar.user_service.repository.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.tushar.user_service.dto.UserRequest;
import com.tushar.user_service.dto.UserResponse;
import com.tushar.user_service.entity.User;
import com.tushar.user_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_shouldCreateSuccessfully() {

        UserRequest request = new UserRequest();
        request.setUsername("alice");
        request.setEmail("alice@example.com");
        request.setFirstName("Alice");
        request.setLastName("Smith");
        request.setActive(true);

        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);

        User savedUser = new User(
                "alice",
                "alice@example.com",
                "Alice",
                "Smith",
                true
        );

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.createUser(request);

        assertEquals("alice", response.getUsername());
        assertEquals("alice@example.com", response.getEmail());
        assertEquals("Alice", response.getFirstName());
        assertEquals("Smith", response.getLastName());
        assertTrue(response.isActive());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_shouldThrowException_whenUsernameAlreadyExists() {

        UserRequest request = new UserRequest();
        request.setUsername("alice");
        request.setEmail("alice@example.com");
        request.setFirstName("Alice");
        request.setLastName("Smith");
        request.setActive(true);

        when(userRepository.existsByUsername("alice")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> userService.createUser(request)
        );

        assertEquals("Username already exists", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_shouldThrowException_whenEmailAlreadyExists() {

        UserRequest request = new UserRequest();
        request.setUsername("alice2");
        request.setEmail("alice@example.com");
        request.setFirstName("Alice");
        request.setLastName("Smith");
        request.setActive(true);

        when(userRepository.existsByUsername("alice2")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> userService.createUser(request)
        );

        assertEquals("Email already exists", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    void getUserById_shouldThrowException_whenUserDoesNotExist() {

        when(userRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getUserById(999L)
        );

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getUserById_shouldReturnUser_whenUserExists() {

        User user = new User(
                "bob",
                "bob@example.com",
                "Bob",
                "Smith",
                true
        );

        when(userRepository.findById(1L))
                .thenReturn(java.util.Optional.of(user));

        UserResponse response = userService.getUserById(1L);

        assertEquals("bob", response.getUsername());
        assertEquals("bob@example.com", response.getEmail());
        assertEquals("Bob", response.getFirstName());
        assertEquals("Smith", response.getLastName());
        assertTrue(response.isActive());
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {

        User user1 = new User(
                "alice",
                "alice@example.com",
                "Alice",
                "Smith",
                true
        );

        User user2 = new User(
                "bob",
                "bob@example.com",
                "Bob",
                "Jones",
                true
        );

        when(userRepository.findAll())
                .thenReturn(java.util.List.of(user1, user2));

        java.util.List<UserResponse> responses = userService.getAllUsers();

        assertEquals(2, responses.size());

        assertEquals("alice", responses.get(0).getUsername());
        assertEquals("bob", responses.get(1).getUsername());

        verify(userRepository).findAll();
    }

    @Test
    void updateUser_shouldUpdateSuccessfully() {
        User existingUser = new User(
                "alice",
                "alice@example.com",
                "Alice",
                "Smith",
                true
        );

        UserRequest request = new UserRequest();
        request.setUsername("alice");
        request.setEmail("alice.new@example.com");
        request.setFirstName("Alice");
        request.setLastName("Updated");
        request.setActive(true);

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(existingUser));

        when(userRepository.existsByEmail("alice.new@example.com"))
                .thenReturn(false);

        when(userRepository.save(existingUser))
                .thenReturn(existingUser);

        UserResponse response = userService.updateUser(2L, request);

        assertEquals("alice", response.getUsername());
        assertEquals("alice.new@example.com", response.getEmail());
        assertEquals("Updated", response.getLastName());

        verify(userRepository).save(existingUser);
    }

    @Test
    void deleteUser_shouldDeleteSuccessfully() {
        User existingUser = new User(
                "alice",
                "alice@example.com",
                "Alice",
                "Smith",
                true
        );

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(existingUser));

        userService.deleteUser(2L);

        verify(userRepository).delete(existingUser);
    }
}

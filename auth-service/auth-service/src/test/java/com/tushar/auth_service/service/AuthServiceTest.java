package com.tushar.auth_service.service;

import com.tushar.auth_service.dto.RegisterRequest;
import com.tushar.auth_service.exception.DuplicateResourceException;
import com.tushar.auth_service.exception.InvalidCredentialsException;
import com.tushar.auth_service.repository.RoleRepository;
import com.tushar.auth_service.repository.UserRepository;
import com.tushar.auth_service.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.tushar.auth_service.dto.LoginRequest;
import com.tushar.auth_service.dto.LoginResponse;
import com.tushar.auth_service.entity.Role;
import com.tushar.auth_service.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RefreshTokenService refreshTokenService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                userRepository,
                passwordEncoder,
                jwtUtil,
                roleRepository,
                refreshTokenService
        );
    }
    @Test
    void login_shouldReturnLoginResponse_whenCredentialsAreValid() {

        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Test@12345");

        Role role = new Role("USER");

        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("encoded-password");
        user.setRole(role);

        when(userRepository.findByUsername("testuser"))
                .thenReturn(java.util.Optional.of(user));

        when(passwordEncoder.matches("Test@12345", "encoded-password"))
                .thenReturn(true);

        when(jwtUtil.generateToken("testuser", "USER"))
                .thenReturn("access-token");

        var refreshToken = new com.tushar.auth_service.entity.RefreshToken(
                "refresh-token",
                user,
                java.time.Instant.now().plusSeconds(3600)
        );

        when(refreshTokenService.createRefreshToken(user))
                .thenReturn(refreshToken);

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("access-token", response.getToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("USER", response.getRole());
    }
    @Test
    void login_shouldThrowException_whenPasswordIsInvalid() {

        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("WrongPassword");

        Role role = new Role("USER");

        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("encoded-password");
        user.setRole(role);

        when(userRepository.findByUsername("testuser"))
                .thenReturn(java.util.Optional.of(user));

        when(passwordEncoder.matches(
                "WrongPassword",
                "encoded-password"))
                .thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        verify(jwtUtil, never())
                .generateToken(anyString(), anyString());

        verify(refreshTokenService, never())
                .createRefreshToken(any(User.class));
    }
    @Test
    void login_shouldThrowException_whenUserDoesNotExist() {

        LoginRequest request = new LoginRequest();
        request.setUsername("unknown");
        request.setPassword("Test@12345");

        when(userRepository.findByUsername("unknown"))
                .thenReturn(java.util.Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        verify(passwordEncoder, never())
                .matches(anyString(), anyString());

        verify(jwtUtil, never())
                .generateToken(anyString(), anyString());

        verify(refreshTokenService, never())
                .createRefreshToken(any(User.class));
    }

    @Test
    void register_shouldThrowException_whenUsernameAlreadyExists() {

        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("new@example.com");
        request.setPassword("Test@12345");

        when(userRepository.existsByUsername("testuser"))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> authService.register(request)
        );

        verify(userRepository, never())
                .save(any(User.class));

        verify(passwordEncoder, never())
                .encode(anyString());
    }

    @Test
    void register_shouldThrowException_whenEmailAlreadyExists() {

        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("testuser@example.com");
        request.setPassword("Test@12345");

        when(userRepository.existsByUsername("newuser"))
                .thenReturn(false);

        when(userRepository.existsByEmail("testuser@example.com"))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> authService.register(request)
        );

        verify(userRepository, never())
                .save(any(User.class));

        verify(passwordEncoder, never())
                .encode(anyString());
    }

    @Test
    void register_shouldCreateUser_whenRequestIsValid() {

        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("newuser@example.com");
        request.setPassword("Test@12345");

        Role userRole = new Role("USER");

        when(userRepository.existsByUsername("newuser"))
                .thenReturn(false);

        when(userRepository.existsByEmail("newuser@example.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("Test@12345"))
                .thenReturn("encoded-password");

        when(roleRepository.findByName("USER"))
                .thenReturn(java.util.Optional.of(userRole));

        User savedUser = new User();
        savedUser.setUsername("newuser");
        savedUser.setEmail("newuser@example.com");
        savedUser.setPassword("encoded-password");
        savedUser.setRole(userRole);

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        User result = authService.register(request);

        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("newuser@example.com", result.getEmail());
        assertEquals("encoded-password", result.getPassword());
        assertEquals("USER", result.getRole().getName());

        verify(passwordEncoder).encode("Test@12345");
        verify(userRepository).save(any(User.class));
    }
}
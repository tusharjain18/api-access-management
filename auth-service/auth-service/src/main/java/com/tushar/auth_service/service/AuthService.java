package com.tushar.auth_service.service;

import com.tushar.auth_service.dto.LoginRequest;
import com.tushar.auth_service.dto.LoginResponse;
import com.tushar.auth_service.dto.RefreshTokenResponse;
import com.tushar.auth_service.dto.RegisterRequest;
import com.tushar.auth_service.entity.RefreshToken;
import com.tushar.auth_service.entity.Role;
import com.tushar.auth_service.entity.User;
import com.tushar.auth_service.exception.DuplicateResourceException;
import com.tushar.auth_service.exception.InvalidCredentialsException;
import com.tushar.auth_service.repository.RoleRepository;
import com.tushar.auth_service.repository.UserRepository;
import com.tushar.auth_service.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RoleRepository roleRepository;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       RoleRepository roleRepository, RefreshTokenService refreshTokenService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.roleRepository = roleRepository;
        this.refreshTokenService = refreshTokenService;
    }
    public User register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");        }

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        // Never store the plain-text password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Default role assigned by the server
        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(new Role("USER")));

        user.setRole(userRole);
        return userRepository.save(user);
    }
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid username or password"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new InvalidCredentialsException(
                    "Invalid username or password");
        }

        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole().getName()
        );

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user);

        return new LoginResponse(
                token,
                refreshToken.getToken(),
                user.getUsername(),
                user.getRole().getName()
        );
    }

    public RefreshTokenResponse refreshToken(String refreshTokenValue) {

        RefreshToken refreshToken = refreshTokenService
                .findByToken(refreshTokenValue);

        refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();

        String newAccessToken = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole().getName()
        );

        return new RefreshTokenResponse(
                newAccessToken,
                refreshToken.getToken()
        );
    }
}
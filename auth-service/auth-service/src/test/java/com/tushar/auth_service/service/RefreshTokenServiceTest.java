package com.tushar.auth_service.service;

import com.tushar.auth_service.entity.RefreshToken;
import com.tushar.auth_service.entity.User;
import com.tushar.auth_service.exception.ResourceNotFoundException;
import com.tushar.auth_service.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        refreshTokenService = new RefreshTokenService(refreshTokenRepository);

        ReflectionTestUtils.setField(
                refreshTokenService,
                "refreshExpiration",
                604800000L
        );
    }
    @Test
    void findByToken_shouldReturnRefreshToken_whenTokenExists() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");

        RefreshToken refreshToken = new RefreshToken(
                "refresh-token",
                user,
                Instant.now().plusSeconds(3600)
        );

        when(refreshTokenRepository.findByToken("refresh-token"))
                .thenReturn(Optional.of(refreshToken));

        RefreshToken result =
                refreshTokenService.findByToken("refresh-token");

        assertNotNull(result);
        assertEquals("refresh-token", result.getToken());

        verify(refreshTokenRepository).findByToken("refresh-token");
    }

    @Test
    void findByToken_shouldThrowException_whenTokenDoesNotExist() {
        when(refreshTokenRepository.findByToken("invalid-token"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> refreshTokenService.findByToken("invalid-token")
        );

        verify(refreshTokenRepository).findByToken("invalid-token");
    }

    @Test
    void verifyExpiration_shouldThrowException_whenTokenIsExpired() {
        User user = new User();
        user.setUsername("testuser");

        RefreshToken refreshToken = new RefreshToken(
                "expired-token",
                user,
                Instant.now().minusSeconds(3600)
        );

        assertThrows(
                RuntimeException.class,
                () -> refreshTokenService.verifyExpiration(refreshToken)
        );

        verify(refreshTokenRepository).delete(refreshToken);
    }

    @Test
    void verifyExpiration_shouldReturnToken_whenTokenIsValid() {
        User user = new User();
        user.setUsername("testuser");

        RefreshToken refreshToken = new RefreshToken(
                "valid-token",
                user,
                Instant.now().plusSeconds(3600)
        );

        RefreshToken result =
                refreshTokenService.verifyExpiration(refreshToken);

        assertNotNull(result);
        assertEquals("valid-token", result.getToken());

        verify(refreshTokenRepository, never()).delete(refreshToken);
    }

    @Test
    void createRefreshToken_shouldCreateAndSaveToken() {
        User user = new User();
        user.setUsername("testuser");

        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken result = refreshTokenService.createRefreshToken(user);

        assertNotNull(result);
        assertNotNull(result.getToken());
        assertEquals(user, result.getUser());
        assertNotNull(result.getExpiryDate());
        assertTrue(result.getExpiryDate().isAfter(Instant.now()));

        verify(refreshTokenRepository).deleteByUserId(user.getId());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }
}
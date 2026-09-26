package com.tushar.auth_service.controller;

import com.tushar.auth_service.dto.LogoutRequest;
import com.tushar.auth_service.dto.MessageResponse;
import com.tushar.auth_service.dto.UserResponse;
import com.tushar.auth_service.service.AdminService;
import com.tushar.auth_service.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final RefreshTokenService refreshTokenService;

    public AdminController(AdminService adminService, RefreshTokenService refreshTokenService) {
        this.adminService = adminService;
        this.refreshTokenService = refreshTokenService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        return "Welcome Admin " + authentication.getName()
                + ", this is the admin dashboard.";
    }

    @GetMapping("/users")
    public List<UserResponse> getAllUsers() {
        return adminService.getAllUsers();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<MessageResponse> deleteUser(
            @PathVariable Long id,
            Authentication authentication) {

        MessageResponse response = adminService.deleteUser(
                id,
                authentication.getName()
        );

        if (response.getMessage().equals("You cannot delete your own account")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(
            @Valid @RequestBody LogoutRequest request) {

        refreshTokenService.deleteByToken(
                request.getRefreshToken()
        );

        return ResponseEntity.ok(
                new MessageResponse("Logged out successfully")
        );
    }
}
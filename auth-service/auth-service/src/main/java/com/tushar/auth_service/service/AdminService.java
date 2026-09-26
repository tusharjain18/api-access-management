package com.tushar.auth_service.service;

import com.tushar.auth_service.dto.MessageResponse;
import com.tushar.auth_service.dto.UserResponse;
import com.tushar.auth_service.entity.User;
import com.tushar.auth_service.exception.ResourceNotFoundException;
import com.tushar.auth_service.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole().getName()
                ))
                .toList();
    }

    public MessageResponse deleteUser(Long id, String currentUsername) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (user.getUsername().equals(currentUsername)) {
            return new MessageResponse("You cannot delete your own account");
        }

        userRepository.deleteById(id);

        return new MessageResponse("User deleted successfully");
    }
}
package com.tushar.auth_service.config;

import com.tushar.auth_service.entity.Role;
import com.tushar.auth_service.entity.User;
import com.tushar.auth_service.repository.RoleRepository;
import com.tushar.auth_service.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeData(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            Role userRole = roleRepository.findByName("USER")
                    .orElseGet(() ->
                            roleRepository.save(new Role("USER")));

            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseGet(() ->
                            roleRepository.save(new Role("ADMIN")));

            if (!userRepository.existsByUsername("admin")) {

                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@example.com");
                admin.setPassword(
                        passwordEncoder.encode("Admin@123")
                );
                admin.setRole(adminRole);

                userRepository.save(admin);
            }
        };
    }
}
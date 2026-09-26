package com.tushar.api_management_service.repository;

import com.tushar.api_management_service.entity.Api;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiRepository extends JpaRepository<Api, Long> {

    Optional<Api> findByName(String name);

    boolean existsByName(String name);
}
package com.tushar.api_management_service.service;

import com.tushar.api_management_service.dto.ApiRequest;
import com.tushar.api_management_service.dto.ApiResponse;
import com.tushar.api_management_service.entity.Api;
import com.tushar.api_management_service.exception.DuplicateResourceException;
import com.tushar.api_management_service.exception.ResourceNotFoundException;
import com.tushar.api_management_service.kafka.AuditEventProducer;
import com.tushar.api_management_service.repository.ApiRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApiService {

    private final ApiRepository apiRepository;
    private final AuditEventProducer auditEventProducer;

    public ApiService(
            ApiRepository apiRepository,
            AuditEventProducer auditEventProducer
    ) {
        this.apiRepository = apiRepository;
        this.auditEventProducer = auditEventProducer;
    }

    public ApiResponse createApi(ApiRequest request) {

        if (apiRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException(
                    "API with this name already exists"
            );
        }

        Api api = new Api(
                request.getName(),
                request.getBaseUrl(),
                request.getDescription(),
                request.isActive()
        );

        Api savedApi = apiRepository.save(api);

        auditEventProducer.sendAuditEvent(
                "API_CREATED",
                getCurrentUsername(),
                "API",
                "Created API: " + savedApi.getName()
        );

        return toResponse(savedApi);
    }

    @Cacheable("apis")
    public List<ApiResponse> getAllApis() {
        return apiRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ApiResponse getApiById(Long id) {

        Api api = apiRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("API not found")
                );

        return toResponse(api);
    }

    public ApiResponse updateApi(Long id, ApiRequest request) {

        Api api = apiRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("API not found")
                );

        api.setName(request.getName());
        api.setBaseUrl(request.getBaseUrl());
        api.setDescription(request.getDescription());
        api.setActive(request.isActive());

        Api updatedApi = apiRepository.save(api);

        auditEventProducer.sendAuditEvent(
                "API_UPDATED",
                getCurrentUsername(),
                "API",
                "Updated API: " + updatedApi.getName()
        );

        return toResponse(updatedApi);
    }

    public void deleteApi(Long id) {

        Api api = apiRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("API not found")
                );

        apiRepository.deleteById(id);

        auditEventProducer.sendAuditEvent(
                "API_DELETED",
                getCurrentUsername(),
                "API",
                "Deleted API: " + api.getName()
        );
    }

    private ApiResponse toResponse(Api api) {

        return new ApiResponse(
                api.getId(),
                api.getName(),
                api.getBaseUrl(),
                api.getDescription(),
                api.isActive()
        );
    }

    private String getCurrentUsername() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return authentication.getName();
    }
}
package com.tushar.api_management_service.service;

import com.tushar.api_management_service.dto.ApiRequest;
import com.tushar.api_management_service.dto.ApiResponse;
import com.tushar.api_management_service.entity.Api;
import com.tushar.api_management_service.exception.DuplicateResourceException;
import com.tushar.api_management_service.exception.ResourceNotFoundException;
import com.tushar.api_management_service.repository.ApiRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApiService {

    private final ApiRepository apiRepository;

    public ApiService(ApiRepository apiRepository) {
        this.apiRepository = apiRepository;
    }

    public ApiResponse createApi(ApiRequest request) {

        if (apiRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("API with this name already exists");
        }

        Api api = new Api(
                request.getName(),
                request.getBaseUrl(),
                request.getDescription(),
                request.isActive()
        );

        Api savedApi = apiRepository.save(api);

        return toResponse(savedApi);
    }

    public List<ApiResponse> getAllApis() {
        return apiRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
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
    public ApiResponse getApiById(Long id) {
        Api api = apiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("API not found"));
        return toResponse(api);
    }

    public void deleteApi(Long id) {
        if (!apiRepository.existsById(id)) {
            throw new ResourceNotFoundException("API not found");
        }

        apiRepository.deleteById(id);
    }

    public ApiResponse updateApi(Long id, ApiRequest request) {

        Api api = apiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("API not found"));

        api.setName(request.getName());
        api.setBaseUrl(request.getBaseUrl());
        api.setDescription(request.getDescription());
        api.setActive(request.isActive());

        Api updatedApi = apiRepository.save(api);

        return toResponse(updatedApi);
    }
}
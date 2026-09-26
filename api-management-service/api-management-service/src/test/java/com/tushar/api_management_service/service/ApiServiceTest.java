package com.tushar.api_management_service.service;

import com.tushar.api_management_service.dto.ApiRequest;
import com.tushar.api_management_service.dto.ApiResponse;
import com.tushar.api_management_service.entity.Api;
import com.tushar.api_management_service.exception.DuplicateResourceException;
import com.tushar.api_management_service.exception.ResourceNotFoundException;
import com.tushar.api_management_service.repository.ApiRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiServiceTest {

    @Mock
    private ApiRepository apiRepository;

    @InjectMocks
    private ApiService apiService;

    @Test
    void createApi_shouldCreateSuccessfully() {

        ApiRequest request = new ApiRequest();
        request.setName("Customer API");
        request.setBaseUrl("http://localhost:9000");
        request.setDescription("Customer management API");
        request.setActive(true);

        Api savedApi = new Api(
                "Customer API",
                "http://localhost:9000",
                "Customer management API",
                true
        );

        when(apiRepository.existsByName("Customer API"))
                .thenReturn(false);

        when(apiRepository.save(any(Api.class)))
                .thenReturn(savedApi);

        ApiResponse response = apiService.createApi(request);

        assertEquals("Customer API", response.getName());
        assertEquals("http://localhost:9000", response.getBaseUrl());
        assertEquals("Customer management API", response.getDescription());
        assertEquals(true, response.isActive());

        verify(apiRepository).existsByName("Customer API");
        verify(apiRepository).save(any(Api.class));
    }

    @Test
    void createApi_shouldThrowExceptionWhenNameAlreadyExists() {

        ApiRequest request = new ApiRequest();
        request.setName("Customer API");
        request.setBaseUrl("http://localhost:9000");
        request.setDescription("Customer management API");
        request.setActive(true);

        when(apiRepository.existsByName("Customer API"))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> apiService.createApi(request)
        );

        verify(apiRepository).existsByName("Customer API");
        verify(apiRepository, never()).save(any(Api.class));
    }

    @Test
    void getApiById_shouldThrowExceptionWhenApiNotFound() {

        when(apiRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> apiService.getApiById(999L)
        );

        verify(apiRepository).findById(999L);
    }

    @Test
    void getApiById_shouldReturnApiSuccessfully() {

        Api api = new Api(
                "Customer API",
                "http://localhost:9000",
                "Customer management API",
                true
        );

        when(apiRepository.findById(1L))
                .thenReturn(Optional.of(api));

        ApiResponse response = apiService.getApiById(1L);

        assertEquals("Customer API", response.getName());
        assertEquals("http://localhost:9000", response.getBaseUrl());
        assertEquals("Customer management API", response.getDescription());
        assertEquals(true, response.isActive());

        verify(apiRepository).findById(1L);
    }

    @Test
    void deleteApi_shouldDeleteSuccessfully() {

        when(apiRepository.existsById(1L))
                .thenReturn(true);

        apiService.deleteApi(1L);

        verify(apiRepository).existsById(1L);
        verify(apiRepository).deleteById(1L);
    }

    @Test
    void updateApi_shouldUpdateSuccessfully() {

        Api existingApi = new Api(
                "Customer API",
                "http://localhost:9000",
                "Customer management API",
                true
        );

        ApiRequest request = new ApiRequest();
        request.setName("Updated Customer API");
        request.setBaseUrl("http://localhost:9100");
        request.setDescription("Updated customer API");
        request.setActive(false);

        when(apiRepository.findById(1L))
                .thenReturn(Optional.of(existingApi));

        when(apiRepository.save(any(Api.class)))
                .thenReturn(existingApi);

        ApiResponse response = apiService.updateApi(1L, request);

        assertEquals("Updated Customer API", response.getName());
        assertEquals("http://localhost:9100", response.getBaseUrl());
        assertEquals("Updated customer API", response.getDescription());
        assertEquals(false, response.isActive());

        verify(apiRepository).findById(1L);
        verify(apiRepository).save(existingApi);
    }

    @Test
    void deleteApi_shouldThrowExceptionWhenApiNotFound() {

        when(apiRepository.existsById(999L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> apiService.deleteApi(999L)
        );

        verify(apiRepository).existsById(999L);
        verify(apiRepository, never()).deleteById(999L);
    }
}




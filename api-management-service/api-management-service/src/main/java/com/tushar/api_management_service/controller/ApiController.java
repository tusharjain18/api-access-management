package com.tushar.api_management_service.controller;

import com.tushar.api_management_service.dto.ApiRequest;
import com.tushar.api_management_service.dto.ApiResponse;
import com.tushar.api_management_service.service.ApiService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/apis")
public class ApiController {

    private final ApiService apiService;

    public ApiController(ApiService apiService) {
        this.apiService = apiService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createApi(
            @Valid @RequestBody ApiRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiService.createApi(request));
    }

    @GetMapping
    public ResponseEntity<List<ApiResponse>> getAllApis() {
        return ResponseEntity.ok(apiService.getAllApis());
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getApiById(
            @PathVariable Long id) {

        return ResponseEntity.ok(apiService.getApiById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApi(@PathVariable Long id) {
        apiService.deleteApi(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateApi(
            @PathVariable Long id,
            @Valid @RequestBody ApiRequest request) {

        return ResponseEntity.ok(apiService.updateApi(id, request));
    }
}
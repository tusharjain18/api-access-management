package com.tushar.api_management_service.dto;

public class ApiResponse {

    private Long id;
    private String name;
    private String baseUrl;
    private String description;
    private boolean active;

    public ApiResponse() {
    }

    public ApiResponse(
            Long id,
            String name,
            String baseUrl,
            String description,
            boolean active) {
        this.id = id;
        this.name = name;
        this.baseUrl = baseUrl;
        this.description = description;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }
}
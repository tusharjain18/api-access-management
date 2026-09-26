package com.tushar.api_management_service.dto;

import jakarta.validation.constraints.NotBlank;

public class ApiRequest {

    @NotBlank(message = "API name is required")
    private String name;

    @NotBlank(message = "Base URL is required")
    private String baseUrl;

    @NotBlank(message = "Description is required")
    private String description;

    private boolean active = true;

    public ApiRequest() {
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

    public void setName(String name) {
        this.name = name;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
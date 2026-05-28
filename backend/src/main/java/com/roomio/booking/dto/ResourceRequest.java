package com.roomio.booking.dto;

import com.roomio.booking.model.ResourceStatus;
import com.roomio.booking.model.ResourceType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ResourceRequest(
    @NotBlank String name,
    @NotNull ResourceType type,
    @NotBlank String location,
    @Min(1) Integer capacity,
    ResourceStatus status,
    @NotBlank String description,
    List<String> features) {
}

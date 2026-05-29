package com.roomio.booking.dto;

import com.roomio.booking.model.ResourceStatus;
import com.roomio.booking.model.ResourceType;
import java.util.List;

public record ResourceResponse(
    String id,
    String name,
    ResourceType type,
    String location,
    Integer capacity,
    ResourceStatus status,
    String description,
    String imageUrl,
    List<String> features) {
}

package com.roomio.booking.service;

import com.roomio.booking.dto.ResourceRequest;
import com.roomio.booking.dto.ResourceResponse;
import com.roomio.booking.model.Resource;
import com.roomio.booking.model.ResourceStatus;
import com.roomio.booking.repository.ResourceRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResourceService {
  private final ResourceRepository resources;

  public ResourceService(ResourceRepository resources) {
    this.resources = resources;
  }

  @Transactional(readOnly = true)
  public List<ResourceResponse> list() {
    return resources.findAll().stream().map(Mappers::resource).toList();
  }

  @Transactional(readOnly = true)
  public ResourceResponse get(String id) {
    return Mappers.resource(find(id));
  }

  @Transactional
  public ResourceResponse create(ResourceRequest request) {
    Resource resource = resources.save(new Resource(
      null,
      request.name().trim(),
      request.type(),
      request.location().trim(),
      request.capacity(),
      request.status() == null ? ResourceStatus.ACTIVE : request.status(),
      request.description().trim(),
      normalizeImageUrl(request.imageUrl()),
      request.features()));
    return Mappers.resource(resource);
  }

  @Transactional
  public ResourceResponse update(String id, ResourceRequest request) {
    Resource resource = find(id);
    resource.setName(request.name().trim());
    resource.setType(request.type());
    resource.setLocation(request.location().trim());
    resource.setCapacity(request.capacity());
    resource.setStatus(request.status() == null ? ResourceStatus.ACTIVE : request.status());
    resource.setDescription(request.description().trim());
    resource.setImageUrl(normalizeImageUrl(request.imageUrl()));
    resource.setFeatures(request.features());
    return Mappers.resource(resource);
  }

  @Transactional
  public ResourceResponse deactivate(String id) {
    Resource resource = find(id);
    resource.setStatus(ResourceStatus.INACTIVE);
    return Mappers.resource(resource);
  }

  Resource find(String id) {
    return resources.findById(id).orElseThrow(() -> new NotFoundException("Resource not found."));
  }

  private String normalizeImageUrl(String imageUrl) {
    if (imageUrl == null || imageUrl.isBlank()) {
      return null;
    }

    String trimmed = imageUrl.trim();
    if (trimmed.startsWith("http://") || trimmed.startsWith("https://") || trimmed.startsWith("data:image/")) {
      return trimmed;
    }

    throw new IllegalArgumentException("Image must be an http(s) URL or an uploaded image.");
  }
}

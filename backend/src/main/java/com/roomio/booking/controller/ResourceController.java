package com.roomio.booking.controller;

import com.roomio.booking.dto.ResourceRequest;
import com.roomio.booking.dto.ResourceResponse;
import com.roomio.booking.service.ResourceService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {
  private final ResourceService resourceService;

  public ResourceController(ResourceService resourceService) {
    this.resourceService = resourceService;
  }

  @GetMapping
  public List<ResourceResponse> list() {
    return resourceService.list();
  }

  @GetMapping("/{id}")
  public ResourceResponse get(@PathVariable String id) {
    return resourceService.get(id);
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResourceResponse create(@Valid @RequestBody ResourceRequest request) {
    return resourceService.create(request);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResourceResponse update(@PathVariable String id, @Valid @RequestBody ResourceRequest request) {
    return resourceService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResourceResponse deactivate(@PathVariable String id) {
    return resourceService.deactivate(id);
  }
}

package com.roomio.booking.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "resources")
public class Resource {
  @Id
  private String id;

  @Column(nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ResourceType type;

  @Column(nullable = false)
  private String location;

  @Column(nullable = false)
  private Integer capacity;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ResourceStatus status;

  @Column(nullable = false, length = 1200)
  private String description;

  @Lob
  @Column(length = 2_000_000)
  private String imageUrl;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "resource_features", joinColumns = @JoinColumn(name = "resource_id"))
  @Column(name = "feature", nullable = false)
  private List<String> features = new ArrayList<>();

  protected Resource() {
  }

  public Resource(String id, String name, ResourceType type, String location, Integer capacity, ResourceStatus status,
      String description, String imageUrl, List<String> features) {
    this.id = id == null ? UUID.randomUUID().toString() : id;
    this.name = name;
    this.type = type;
    this.location = location;
    this.capacity = capacity;
    this.status = status;
    this.description = description;
    this.imageUrl = imageUrl;
    this.features = new ArrayList<>(features == null ? List.of() : features);
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ResourceType getType() {
    return type;
  }

  public void setType(ResourceType type) {
    this.type = type;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public Integer getCapacity() {
    return capacity;
  }

  public void setCapacity(Integer capacity) {
    this.capacity = capacity;
  }

  public ResourceStatus getStatus() {
    return status;
  }

  public void setStatus(ResourceStatus status) {
    this.status = status;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public List<String> getFeatures() {
    return features;
  }

  public void setFeatures(List<String> features) {
    this.features = new ArrayList<>(features == null ? List.of() : features);
  }
}

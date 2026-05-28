package com.roomio.booking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "bookings")
public class Booking {
  @Id
  private String id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "resource_id", nullable = false)
  private Resource resource;

  @Column(nullable = false)
  private String eventName;

  @Column(nullable = false)
  private Integer pax;

  @Column(nullable = false)
  private LocalDate startDate;

  @Column(nullable = false)
  private LocalDate endDate;

  @Column(nullable = false)
  private LocalTime startTime;

  @Column(nullable = false)
  private LocalTime endTime;

  @Column(nullable = false)
  private LocalDateTime startDateTime;

  @Column(nullable = false)
  private LocalDateTime endDateTime;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BookingStatus status;

  @Column(nullable = false)
  private Instant createdAt;

  protected Booking() {
  }

  public Booking(String id, User user, Resource resource, String eventName, Integer pax, LocalDate startDate,
      LocalTime startTime, LocalDate endDate, LocalTime endTime, BookingStatus status) {
    this.id = id == null ? UUID.randomUUID().toString() : id;
    this.user = user;
    this.resource = resource;
    this.eventName = eventName;
    this.pax = pax;
    this.startDate = startDate;
    this.startTime = startTime;
    this.endDate = endDate;
    this.endTime = endTime;
    this.startDateTime = LocalDateTime.of(startDate, startTime);
    this.endDateTime = LocalDateTime.of(endDate, endTime);
    this.status = status;
    this.createdAt = Instant.now();
  }

  public String getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public Resource getResource() {
    return resource;
  }

  public String getEventName() {
    return eventName;
  }

  public void setEventName(String eventName) {
    this.eventName = eventName;
  }

  public Integer getPax() {
    return pax;
  }

  public void setPax(Integer pax) {
    this.pax = pax;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public LocalTime getStartTime() {
    return startTime;
  }

  public LocalTime getEndTime() {
    return endTime;
  }

  public LocalDateTime getStartDateTime() {
    return startDateTime;
  }

  public LocalDateTime getEndDateTime() {
    return endDateTime;
  }

  public BookingStatus getStatus() {
    return status;
  }

  public void setStatus(BookingStatus status) {
    this.status = status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void reschedule(String eventName, Integer pax, LocalDate startDate, LocalTime startTime,
      LocalDate endDate, LocalTime endTime) {
    this.eventName = eventName;
    this.pax = pax;
    this.startDate = startDate;
    this.startTime = startTime;
    this.endDate = endDate;
    this.endTime = endTime;
    this.startDateTime = LocalDateTime.of(startDate, startTime);
    this.endDateTime = LocalDateTime.of(endDate, endTime);
  }
}

package com.roomio.booking.service;

import com.roomio.booking.dto.AvailabilityRequest;
import com.roomio.booking.dto.AvailabilityResponse;
import com.roomio.booking.dto.BookingRequest;
import com.roomio.booking.dto.BookingResponse;
import com.roomio.booking.dto.SuggestionResponse;
import com.roomio.booking.model.Booking;
import com.roomio.booking.model.BookingStatus;
import com.roomio.booking.model.Resource;
import com.roomio.booking.model.ResourceStatus;
import com.roomio.booking.model.Role;
import com.roomio.booking.model.User;
import com.roomio.booking.repository.BookingRepository;
import com.roomio.booking.repository.ResourceRepository;
import com.roomio.booking.repository.UserRepository;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {
  private static final long MIN_DURATION_MINUTES = 30;
  private static final long MAX_DURATION_MINUTES = 120;
  private static final Set<BookingStatus> BLOCKING_STATUSES = Set.of(BookingStatus.CONFIRMED);

  private final BookingRepository bookings;
  private final UserRepository users;
  private final ResourceRepository resources;

  public BookingService(BookingRepository bookings, UserRepository users, ResourceRepository resources) {
    this.bookings = bookings;
    this.users = users;
    this.resources = resources;
  }

  @Transactional
  public BookingResponse create(String email, BookingRequest request) {
    User user = currentUser(email);
    Resource resource = resource(request.resourceId());
    AvailabilityResponse availability = checkAvailability(user, new AvailabilityRequest(
      request.resourceId(),
      request.pax(),
      request.startDate(),
      request.startTime(),
      request.endDate(),
      request.endTime()));

    if (!availability.available()) {
      throw new ConflictException(availability);
    }

    Booking booking = bookings.save(new Booking(
      null,
      user,
      resource,
      request.eventName().trim(),
      request.pax(),
      request.startDate(),
      request.startTime(),
      request.endDate(),
      request.endTime(),
      BookingStatus.CONFIRMED));
    return Mappers.booking(booking);
  }

  @Transactional(readOnly = true)
  public List<BookingResponse> myBookings(String email) {
    User user = currentUser(email);
    return bookings.findByUserIdOrderByStartDateTimeDesc(user.getId()).stream().map(Mappers::booking).toList();
  }

  @Transactional(readOnly = true)
  public List<BookingResponse> all() {
    return bookings.findAllByOrderByStartDateTimeDesc().stream().map(Mappers::booking).toList();
  }

  @Transactional
  public BookingResponse update(String email, String id, BookingRequest request) {
    User user = currentUser(email);
    Booking booking = booking(id);
    if (user.getRole() != Role.ADMIN && !booking.getUser().getId().equals(user.getId())) {
      throw new ForbiddenException("You can only update your own bookings.");
    }
    if (booking.getStatus() != BookingStatus.CONFIRMED) {
      throw new IllegalArgumentException("Only confirmed bookings can be updated.");
    }

    AvailabilityResponse availability = checkAvailabilityExcluding(user, booking.getId(), new AvailabilityRequest(
      request.resourceId(),
      request.pax(),
      request.startDate(),
      request.startTime(),
      request.endDate(),
      request.endTime()), true);
    if (!availability.available()) {
      throw new ConflictException(availability);
    }

    if (!booking.getResource().getId().equals(request.resourceId())) {
      throw new IllegalArgumentException("Changing a booking resource is not supported by this endpoint.");
    }

    booking.reschedule(
      request.eventName().trim(),
      request.pax(),
      request.startDate(),
      request.startTime(),
      request.endDate(),
      request.endTime());
    return Mappers.booking(booking);
  }

  @Transactional
  public BookingResponse cancel(String email, String id) {
    User user = currentUser(email);
    Booking booking = booking(id);
    if (user.getRole() != Role.ADMIN && !booking.getUser().getId().equals(user.getId())) {
      throw new ForbiddenException("You can only cancel your own bookings.");
    }
    booking.setStatus(BookingStatus.CANCELLED);
    return Mappers.booking(booking);
  }

  @Transactional
  public BookingResponse updateStatus(String id, BookingStatus status) {
    Booking booking = booking(id);
    booking.setStatus(status);
    return Mappers.booking(booking);
  }

  @Transactional(readOnly = true)
  public AvailabilityResponse checkAvailability(String email, AvailabilityRequest request) {
    return checkAvailability(currentUser(email), request);
  }

  @Transactional(readOnly = true)
  public AvailabilityResponse checkAvailability(User user, AvailabilityRequest request) {
    return checkAvailabilityExcluding(user, null, request, true);
  }

  @Transactional(readOnly = true)
  public List<SuggestionResponse> suggestions(String email, AvailabilityRequest request) {
    User user = currentUser(email);
    return generateSuggestions(user, request);
  }

  private AvailabilityResponse checkAvailabilityExcluding(User user, String excludedBookingId, AvailabilityRequest request,
      boolean includeSuggestions) {
    Resource resource = resource(request.resourceId());
    LocalDateTime start = LocalDateTime.of(request.startDate(), request.startTime());
    LocalDateTime end = LocalDateTime.of(request.endDate(), request.endTime());
    long duration = Duration.between(start, end).toMinutes();

    if (!end.isAfter(start)) {
      return unavailable("End date and time must be after the start date and time.", "invalid-time", null, List.of());
    }
    if (start.isBefore(LocalDateTime.now())) {
      return unavailable("Past slots cannot be booked.", "past", null, List.of());
    }
    if (duration < MIN_DURATION_MINUTES) {
      return unavailable("Bookings must be at least 30 minutes long.", "duration-too-short", null, List.of());
    }
    if (duration > MAX_DURATION_MINUTES) {
      return unavailable("Bookings cannot be longer than 2 hours.", "duration-too-long", null, List.of());
    }
    if (resource.getStatus() != ResourceStatus.ACTIVE) {
      return unavailable("Inactive resources cannot be booked.", "inactive-resource", null, List.of());
    }
    if (request.pax() != null && request.pax() > resource.getCapacity()) {
      return unavailable("Requested pax exceeds the resource capacity of " + resource.getCapacity() + ".", "capacity", null, List.of());
    }

    Booking resourceConflict = firstConflict(bookings
      .findByResourceIdAndStatusInAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
        resource.getId(), BLOCKING_STATUSES, end, start), excludedBookingId);
    if (resourceConflict != null) {
      return unavailable(
        "This resource is already booked for the selected time.",
        "resource-conflict",
        resourceConflict,
        includeSuggestions ? generateSuggestions(user, request) : List.of());
    }

    Booking userConflict = firstConflict(bookings
      .findByUserIdAndStatusInAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
        user.getId(), BLOCKING_STATUSES, end, start), excludedBookingId);
    if (userConflict != null) {
      return unavailable(
        "You already have another booking during the selected time.",
        "user-conflict",
        userConflict,
        includeSuggestions ? generateSuggestions(user, request) : List.of());
    }

    return new AvailabilityResponse(true, "This slot is available.", "available", null, List.of());
  }

  private Booking firstConflict(List<Booking> conflicts, String excludedBookingId) {
    return conflicts.stream()
      .filter(item -> excludedBookingId == null || !item.getId().equals(excludedBookingId))
      .min(Comparator.comparing(Booking::getStartDateTime))
      .orElse(null);
  }

  private AvailabilityResponse unavailable(String reason, String type, Booking conflict, List<SuggestionResponse> suggestions) {
    return new AvailabilityResponse(false, reason, type, conflict == null ? null : Mappers.booking(conflict), suggestions);
  }

  private List<SuggestionResponse> generateSuggestions(User user, AvailabilityRequest request) {
    Resource selected = resource(request.resourceId());
    Duration duration = Duration.between(
      LocalDateTime.of(request.startDate(), request.startTime()),
      LocalDateTime.of(request.endDate(), request.endTime()));
    List<SuggestionResponse> suggestions = new ArrayList<>();

    for (int offsetMinutes : List.of(60, -60, 120)) {
      LocalDateTime candidateStart = LocalDateTime.of(request.startDate(), request.startTime()).plusMinutes(offsetMinutes);
      LocalDateTime candidateEnd = candidateStart.plus(duration);
      maybeAddSuggestion(
        suggestions,
        user,
        selected,
        candidateStart.toLocalDate(),
        candidateStart.toLocalTime(),
        candidateEnd.toLocalDate(),
        candidateEnd.toLocalTime(),
        offsetMinutes > 0 ? "+" + (offsetMinutes / 60) + " hr later" : Math.abs(offsetMinutes / 60) + " hr earlier",
        "Same resource at a nearby available time");
    }

    resources.findAll().stream()
      .filter(resource -> !resource.getId().equals(selected.getId()))
      .filter(resource -> resource.getStatus() == ResourceStatus.ACTIVE)
      .filter(resource -> resource.getType() == selected.getType())
      .filter(resource -> resource.getCapacity() >= selected.getCapacity())
      .limit(2)
      .forEach(resource -> maybeAddSuggestion(
        suggestions,
        user,
        resource,
        request.startDate(),
        request.startTime(),
        request.endDate(),
        request.endTime(),
        suggestions.isEmpty() ? "Same time" : "Alternative",
        "Similar resource available at your selected time"));

    return suggestions.stream().limit(3).toList();
  }

  private void maybeAddSuggestion(List<SuggestionResponse> suggestions, User user, Resource resource, LocalDate startDate,
      LocalTime startTime, LocalDate endDate, LocalTime endTime, String label, String reason) {
    if (suggestions.stream().anyMatch(item -> item.resourceId().equals(resource.getId())
        && item.date().equals(startDate)
        && item.startTime().equals(startTime))) {
      return;
    }
    if (startTime.isBefore(LocalTime.of(8, 0)) || endTime.isAfter(LocalTime.of(20, 0)) || !startDate.equals(endDate)) {
      return;
    }

    AvailabilityResponse availability = checkAvailabilityExcluding(user, null, new AvailabilityRequest(
      resource.getId(),
      1,
      startDate,
      startTime,
      endDate,
      endTime), false);
    if (!availability.available()) {
      return;
    }

    suggestions.add(new SuggestionResponse(
      resource.getId(),
      resource.getName(),
      resource.getLocation(),
      resource.getCapacity(),
      startDate,
      startDate,
      endDate,
      startTime,
      endTime,
      label,
      reason));
  }

  private User currentUser(String email) {
    return users.findByEmailIgnoreCase(email).orElseThrow(() -> new NotFoundException("User not found."));
  }

  private Resource resource(String id) {
    return resources.findById(id).orElseThrow(() -> new NotFoundException("Resource not found."));
  }

  private Booking booking(String id) {
    return bookings.findById(id).orElseThrow(() -> new NotFoundException("Booking not found."));
  }
}

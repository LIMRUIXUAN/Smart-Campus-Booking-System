package com.roomio.booking.service;

import com.roomio.booking.dto.LandingRoomSnapshotResponse;
import com.roomio.booking.model.Booking;
import com.roomio.booking.model.BookingStatus;
import com.roomio.booking.model.Resource;
import com.roomio.booking.model.ResourceStatus;
import com.roomio.booking.repository.BookingRepository;
import com.roomio.booking.repository.ResourceRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LandingService {
  private static final Set<BookingStatus> LIVE_STATUSES = Set.of(BookingStatus.CONFIRMED);

  private final ResourceRepository resources;
  private final BookingRepository bookings;

  public LandingService(ResourceRepository resources, BookingRepository bookings) {
    this.resources = resources;
    this.bookings = bookings;
  }

  @Transactional(readOnly = true)
  public List<LandingRoomSnapshotResponse> rooms() {
    LocalDateTime now = LocalDateTime.now();
    List<Resource> activeResources = resources.findAll().stream()
      .filter(resource -> resource.getStatus() == ResourceStatus.ACTIVE)
      .toList();

    Map<String, List<Booking>> bookingsByResource = bookings.findByStatusInOrderByStartDateTimeAsc(LIVE_STATUSES).stream()
      .collect(Collectors.groupingBy(booking -> booking.getResource().getId()));

    return activeResources.stream()
      .map(resource -> toCandidate(resource, bookingsByResource.getOrDefault(resource.getId(), List.of()), now))
      .sorted(snapshotComparator())
      .map(RoomSnapshotCandidate::response)
      .toList();
  }

  private RoomSnapshotCandidate toCandidate(Resource resource, List<Booking> resourceBookings, LocalDateTime now) {
    Booking activeBooking = resourceBookings.stream()
      .filter(booking -> !booking.getStartDateTime().isAfter(now) && booking.getEndDateTime().isAfter(now))
      .findFirst()
      .orElse(null);

    if (activeBooking != null) {
      Booking nextBooking = resourceBookings.stream()
        .filter(booking -> booking.getStartDateTime().isAfter(activeBooking.getEndDateTime()))
        .findFirst()
        .orElse(null);

      return new RoomSnapshotCandidate(
        new LandingRoomSnapshotResponse(
          resource.getId(),
          resource.getName(),
          resource.getLocation(),
          resource.getCapacity(),
          "Now active",
          "Booked",
          activeBooking.getStartTime().toString(),
          activeBooking.getEndTime().toString(),
          nextBooking != null ? nextBooking.getStartTime().toString() : activeBooking.getEndTime().toString(),
          true),
        activeBooking.getStartDateTime());
    }

    Booking nextBooking = resourceBookings.stream()
      .filter(booking -> booking.getStartDateTime().isAfter(now))
      .findFirst()
      .orElse(null);

    if (nextBooking != null) {
      return new RoomSnapshotCandidate(
        new LandingRoomSnapshotResponse(
          resource.getId(),
          resource.getName(),
          resource.getLocation(),
          resource.getCapacity(),
          "Next booking",
          "Upcoming",
          nextBooking.getStartTime().toString(),
          nextBooking.getEndTime().toString(),
          nextBooking.getStartTime().toString(),
          false),
        nextBooking.getStartDateTime());
    }

    return new RoomSnapshotCandidate(
      new LandingRoomSnapshotResponse(
        resource.getId(),
        resource.getName(),
        resource.getLocation(),
        resource.getCapacity(),
        "Available now",
        "Open",
        null,
        null,
        null,
        false),
      null);
  }

  private Comparator<RoomSnapshotCandidate> snapshotComparator() {
    return Comparator
      .comparing((RoomSnapshotCandidate candidate) -> candidate.response().activeNow()).reversed()
      .thenComparing(candidate -> candidate.sortTime() == null ? LocalDateTime.MAX : candidate.sortTime())
      .thenComparing(candidate -> candidate.response().resourceName());
  }

  private record RoomSnapshotCandidate(LandingRoomSnapshotResponse response, LocalDateTime sortTime) {
  }
}

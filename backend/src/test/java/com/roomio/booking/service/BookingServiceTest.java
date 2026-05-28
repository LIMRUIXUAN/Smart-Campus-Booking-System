package com.roomio.booking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.roomio.booking.dto.AvailabilityRequest;
import com.roomio.booking.dto.BookingRequest;
import com.roomio.booking.model.Booking;
import com.roomio.booking.model.BookingStatus;
import com.roomio.booking.model.Resource;
import com.roomio.booking.model.ResourceStatus;
import com.roomio.booking.model.ResourceType;
import com.roomio.booking.model.Role;
import com.roomio.booking.model.User;
import com.roomio.booking.repository.BookingRepository;
import com.roomio.booking.repository.ResourceRepository;
import com.roomio.booking.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class BookingServiceTest {
  @Autowired
  BookingService bookingService;

  @Autowired
  BookingRepository bookings;

  @Autowired
  ResourceRepository resources;

  @Autowired
  UserRepository users;

  User student;
  User other;
  Resource roomA;
  Resource roomB;
  Resource inactiveRoom;
  LocalDate date;

  @BeforeEach
  void setUp() {
    bookings.deleteAll();
    resources.deleteAll();
    users.deleteAll();

    student = users.save(new User("u-student-test", "Alya Tan", "student-test@campus.test", "hash", Role.STUDENT));
    other = users.save(new User("u-other-test", "Nadia Lee", "other-test@campus.test", "hash", Role.STUDENT));
    roomA = resources.save(new Resource("room-a-test", "Study Room A", ResourceType.ROOM, "Library", 4,
      ResourceStatus.ACTIVE, "Quiet room", List.of("Wi-Fi")));
    roomB = resources.save(new Resource("room-b-test", "Study Room B", ResourceType.ROOM, "Library", 4,
      ResourceStatus.ACTIVE, "Quiet room", List.of("Wi-Fi")));
    inactiveRoom = resources.save(new Resource("room-inactive-test", "Inactive Room", ResourceType.ROOM, "Library", 4,
      ResourceStatus.INACTIVE, "Maintenance", List.of()));
    date = LocalDate.now().plusDays(3);
  }

  @Test
  void preventsDoubleBookingForSameResource() {
    confirmed(other, roomA, LocalTime.of(10, 0), LocalTime.of(11, 0));

    var result = bookingService.checkAvailability(student, availability(roomA, LocalTime.of(10, 30), LocalTime.of(11, 30)));

    assertThat(result.available()).isFalse();
    assertThat(result.type()).isEqualTo("resource-conflict");
  }

  @Test
  void allowsBookingWhenNewBookingEndsExactlyAtExistingStart() {
    confirmed(other, roomA, LocalTime.of(10, 0), LocalTime.of(11, 0));

    var result = bookingService.checkAvailability(student, availability(roomA, LocalTime.of(9, 0), LocalTime.of(10, 0)));

    assertThat(result.available()).isTrue();
  }

  @Test
  void allowsBookingWhenNewBookingStartsExactlyAtExistingEnd() {
    confirmed(other, roomA, LocalTime.of(10, 0), LocalTime.of(11, 0));

    var result = bookingService.checkAvailability(student, availability(roomA, LocalTime.of(11, 0), LocalTime.of(12, 0)));

    assertThat(result.available()).isTrue();
  }

  @Test
  void preventsSameUserOverlappingBookingsAcrossResources() {
    confirmed(student, roomA, LocalTime.of(10, 0), LocalTime.of(11, 0));

    var result = bookingService.checkAvailability(student, availability(roomB, LocalTime.of(10, 30), LocalTime.of(11, 30)));

    assertThat(result.available()).isFalse();
    assertThat(result.type()).isEqualTo("user-conflict");
  }

  @Test
  void rejectsBookingLongerThanTwoHours() {
    var result = bookingService.checkAvailability(student, availability(roomA, LocalTime.of(9, 0), LocalTime.of(11, 30)));

    assertThat(result.available()).isFalse();
    assertThat(result.type()).isEqualTo("duration-too-long");
  }

  @Test
  void rejectsBookingShorterThanThirtyMinutes() {
    var result = bookingService.checkAvailability(student, availability(roomA, LocalTime.of(9, 0), LocalTime.of(9, 20)));

    assertThat(result.available()).isFalse();
    assertThat(result.type()).isEqualTo("duration-too-short");
  }

  @Test
  void rejectsBookingInThePast() {
    var result = bookingService.checkAvailability(student, new AvailabilityRequest(
      roomA.getId(),
      2,
      LocalDate.now().minusDays(1),
      LocalTime.of(9, 0),
      LocalDate.now().minusDays(1),
      LocalTime.of(10, 0)));

    assertThat(result.available()).isFalse();
    assertThat(result.type()).isEqualTo("past");
  }

  @Test
  void allowsCancelledBookingSlotReuse() {
    bookings.save(new Booking("cancelled-test", other, roomA, "Cancelled", 2, date,
      LocalTime.of(10, 0), date, LocalTime.of(11, 0), BookingStatus.CANCELLED));

    var result = bookingService.checkAvailability(student, availability(roomA, LocalTime.of(10, 0), LocalTime.of(11, 0)));

    assertThat(result.available()).isTrue();
  }

  @Test
  void rejectsBookingForInactiveResource() {
    var result = bookingService.checkAvailability(student, availability(inactiveRoom, LocalTime.of(9, 0), LocalTime.of(10, 0)));

    assertThat(result.available()).isFalse();
    assertThat(result.type()).isEqualTo("inactive-resource");
  }

  @Test
  void returnsSuggestionsWhenSelectedResourceIsUnavailable() {
    confirmed(other, roomA, LocalTime.of(10, 0), LocalTime.of(11, 0));

    var result = bookingService.checkAvailability(student, availability(roomA, LocalTime.of(10, 0), LocalTime.of(11, 0)));

    assertThat(result.available()).isFalse();
    assertThat(result.suggestions()).isNotEmpty();
  }

  @Test
  void createThrowsConflictExceptionForUnavailableSlot() {
    confirmed(other, roomA, LocalTime.of(10, 0), LocalTime.of(11, 0));

    assertThatThrownBy(() -> bookingService.create(student.getEmail(), booking(roomA, LocalTime.of(10, 30), LocalTime.of(11, 30))))
      .isInstanceOf(ConflictException.class)
      .hasMessageContaining("already booked");
  }

  private Booking confirmed(User user, Resource resource, LocalTime start, LocalTime end) {
    return bookings.save(new Booking(null, user, resource, "Existing booking", 2, date, start, date, end, BookingStatus.CONFIRMED));
  }

  private AvailabilityRequest availability(Resource resource, LocalTime start, LocalTime end) {
    return new AvailabilityRequest(resource.getId(), 2, date, start, date, end);
  }

  private BookingRequest booking(Resource resource, LocalTime start, LocalTime end) {
    return new BookingRequest(resource.getId(), "Study session", 2, date, start, date, end);
  }
}

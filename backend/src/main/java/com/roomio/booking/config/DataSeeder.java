package com.roomio.booking.config;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements CommandLineRunner {
  private final boolean enabled;
  private final UserRepository users;
  private final ResourceRepository resources;
  private final BookingRepository bookings;
  private final PasswordEncoder passwordEncoder;

  public DataSeeder(
      @Value("${app.seed.enabled:true}") boolean enabled,
      UserRepository users,
      ResourceRepository resources,
      BookingRepository bookings,
      PasswordEncoder passwordEncoder) {
    this.enabled = enabled;
    this.users = users;
    this.resources = resources;
    this.bookings = bookings;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  @Transactional
  public void run(String... args) {
    if (!enabled || users.count() > 0 || resources.count() > 0 || bookings.count() > 0) {
      return;
    }

    User student = users.save(new User("u-student", "Alya Tan", "student@campus.test", passwordEncoder.encode("password"), Role.STUDENT));
    users.save(new User("u-admin", "Mr. Kumar", "admin@campus.test", passwordEncoder.encode("password"), Role.ADMIN));
    User other = users.save(new User("u-other", "Nadia Lee", "nadia@campus.test", passwordEncoder.encode("password"), Role.STUDENT));

    Resource studyA = resources.save(new Resource("r-study-a", "Study Room A", ResourceType.ROOM, "Main Library, Floor 2", 4,
      ResourceStatus.ACTIVE, "Quiet enclosed space for small group work with a whiteboard and power outlets.",
      List.of("Wi-Fi", "Whiteboard", "Power")));
    Resource studyB = resources.save(new Resource("r-study-b", "Study Room B", ResourceType.ROOM, "Main Library, Floor 2", 4,
      ResourceStatus.ACTIVE, "Adjacent study room with similar capacity and a writable wall.",
      List.of("Wi-Fi", "Whiteboard")));
    Resource discussion = resources.save(new Resource("r-discussion-c", "Discussion Room C", ResourceType.ROOM, "Learning Commons", 6,
      ResourceStatus.ACTIVE, "Flexible room for tutorials, peer discussion, and presentations.",
      List.of("Display", "Power", "Movable chairs")));
    Resource lab = resources.save(new Resource("r-lab-1", "Computer Lab 1", ResourceType.LAB, "CS Building, Level 3", 24,
      ResourceStatus.ACTIVE, "Windows lab with development tools and projector support.",
      List.of("Projector", "PCs", "Air conditioning")));
    Resource projector = resources.save(new Resource("r-projector", "4K Projector Pro", ResourceType.EQUIPMENT, "IT Helpdesk, Building 4", 1,
      ResourceStatus.ACTIVE, "Portable 4K projector kit for presentations and student events.",
      List.of("HDMI", "Carry case")));
    resources.save(new Resource("r-chem-b", "Chemistry Lab B", ResourceType.LAB, "Science Block, Floor 1", 20,
      ResourceStatus.INACTIVE, "Temporarily inactive while maintenance checks are completed.",
      List.of("Benches", "Ventilation")));

    LocalDate today = LocalDate.now();
    bookings.save(new Booking("b-conflict-resource", other, studyA, "Design Principles 101", 3, today.plusDays(4),
      LocalTime.of(14, 0), today.plusDays(4), LocalTime.of(15, 0), BookingStatus.CONFIRMED));
    bookings.save(new Booking("b-conflict-user", student, lab, "Programming lab prep", 1, today.plusDays(5),
      LocalTime.of(9, 0), today.plusDays(5), LocalTime.of(10, 30), BookingStatus.CONFIRMED));
    bookings.save(new Booking("b-upcoming", student, studyB, "Study session", 2, today.plusDays(7),
      LocalTime.of(11, 0), today.plusDays(7), LocalTime.of(12, 0), BookingStatus.CONFIRMED));
    bookings.save(new Booking("b-cancelled", student, discussion, "Peer review", 5, today.minusDays(4),
      LocalTime.of(13, 0), today.minusDays(4), LocalTime.of(14, 0), BookingStatus.CANCELLED));
    bookings.save(new Booking("b-completed", student, lab, "Completed lab practice", 1, today.minusDays(2),
      LocalTime.of(10, 0), today.minusDays(2), LocalTime.of(11, 30), BookingStatus.COMPLETED));
    bookings.save(new Booking("b-no-show", other, projector, "Presentation rehearsal", 1, today.minusDays(1),
      LocalTime.of(15, 0), today.minusDays(1), LocalTime.of(16, 0), BookingStatus.NO_SHOW));
  }
}

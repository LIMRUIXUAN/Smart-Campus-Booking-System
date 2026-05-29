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
  private static final String ROOM_IMAGE = "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 800 500'><rect width='800' height='500' fill='%23f6efe6'/><rect x='60' y='70' width='680' height='360' rx='32' fill='%23ffffff'/><rect x='110' y='130' width='220' height='170' rx='20' fill='%23d6e6f2'/><rect x='360' y='130' width='220' height='24' rx='12' fill='%23243b53' opacity='0.9'/><rect x='360' y='176' width='170' height='18' rx='9' fill='%235a7184' opacity='0.85'/><rect x='360' y='215' width='210' height='18' rx='9' fill='%235a7184' opacity='0.72'/><rect x='110' y='326' width='470' height='22' rx='11' fill='%23aac4d6'/><rect x='110' y='366' width='390' height='18' rx='9' fill='%23c7d8e5'/><circle cx='640' cy='347' r='54' fill='%23ffb703'/><path d='M616 347h48M640 323v48' stroke='%23243b53' stroke-width='18' stroke-linecap='round'/></svg>";
  private static final String LAB_IMAGE = "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 800 500'><rect width='800' height='500' fill='%23eef6f4'/><rect x='55' y='60' width='690' height='380' rx='34' fill='%23ffffff'/><rect x='95' y='110' width='610' height='60' rx='20' fill='%231f6f78'/><rect x='120' y='200' width='170' height='150' rx='18' fill='%23dbeafe'/><rect x='315' y='200' width='170' height='150' rx='18' fill='%23e9f5db'/><rect x='510' y='200' width='170' height='150' rx='18' fill='%23fde68a'/><path d='M165 238v74c0 18 14 32 32 32s32-14 32-32v-74' fill='none' stroke='%231f6f78' stroke-width='16' stroke-linecap='round'/><path d='M360 238v74c0 18 14 32 32 32s32-14 32-32v-74' fill='none' stroke='%232a9d8f' stroke-width='16' stroke-linecap='round'/><path d='M555 238v74c0 18 14 32 32 32s32-14 32-32v-74' fill='none' stroke='%23d97706' stroke-width='16' stroke-linecap='round'/></svg>";
  private static final String EQUIPMENT_IMAGE = "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 800 500'><rect width='800' height='500' fill='%23f3f0ff'/><rect x='70' y='55' width='660' height='390' rx='36' fill='%23ffffff'/><rect x='120' y='110' width='380' height='230' rx='24' fill='%231e293b'/><rect x='145' y='135' width='330' height='180' rx='16' fill='%237dd3fc'/><rect x='530' y='140' width='130' height='200' rx='24' fill='%23ddd6fe'/><circle cx='595' cy='205' r='34' fill='%238b5cf6'/><path d='M560 300h70' stroke='%231e293b' stroke-width='16' stroke-linecap='round'/><path d='M260 368h110M315 340v28' stroke='%2364748b' stroke-width='18' stroke-linecap='round'/></svg>";

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
      ROOM_IMAGE, List.of("Wi-Fi", "Whiteboard", "Power")));
    Resource studyB = resources.save(new Resource("r-study-b", "Study Room B", ResourceType.ROOM, "Main Library, Floor 2", 4,
      ResourceStatus.ACTIVE, "Adjacent study room with similar capacity and a writable wall.",
      ROOM_IMAGE, List.of("Wi-Fi", "Whiteboard")));
    Resource discussion = resources.save(new Resource("r-discussion-c", "Discussion Room C", ResourceType.ROOM, "Learning Commons", 6,
      ResourceStatus.ACTIVE, "Flexible room for tutorials, peer discussion, and presentations.",
      ROOM_IMAGE, List.of("Display", "Power", "Movable chairs")));
    Resource lab = resources.save(new Resource("r-lab-1", "Computer Lab 1", ResourceType.LAB, "CS Building, Level 3", 24,
      ResourceStatus.ACTIVE, "Windows lab with development tools and projector support.",
      LAB_IMAGE, List.of("Projector", "PCs", "Air conditioning")));
    Resource projector = resources.save(new Resource("r-projector", "4K Projector Pro", ResourceType.EQUIPMENT, "IT Helpdesk, Building 4", 1,
      ResourceStatus.ACTIVE, "Portable 4K projector kit for presentations and student events.",
      EQUIPMENT_IMAGE, List.of("HDMI", "Carry case")));
    resources.save(new Resource("r-chem-b", "Chemistry Lab B", ResourceType.LAB, "Science Block, Floor 1", 20,
      ResourceStatus.INACTIVE, "Temporarily inactive while maintenance checks are completed.",
      LAB_IMAGE, List.of("Benches", "Ventilation")));

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

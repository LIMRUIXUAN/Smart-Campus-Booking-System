package com.roomio.booking.repository;

import com.roomio.booking.model.Booking;
import com.roomio.booking.model.BookingStatus;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, String> {
  List<Booking> findByUserIdOrderByStartDateTimeDesc(String userId);

  List<Booking> findAllByOrderByStartDateTimeDesc();

  List<Booking> findByResourceIdAndStatusInAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
      String resourceId,
      Collection<BookingStatus> statuses,
      LocalDateTime end,
      LocalDateTime start);

  List<Booking> findByUserIdAndStatusInAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
      String userId,
      Collection<BookingStatus> statuses,
      LocalDateTime end,
      LocalDateTime start);
}

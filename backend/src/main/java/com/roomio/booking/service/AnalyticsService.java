package com.roomio.booking.service;

import com.roomio.booking.dto.AnalyticsSummaryResponse;
import com.roomio.booking.dto.ResourceUsageResponse;
import com.roomio.booking.dto.StatusDistributionResponse;
import com.roomio.booking.model.Booking;
import com.roomio.booking.model.BookingStatus;
import com.roomio.booking.model.Resource;
import com.roomio.booking.repository.BookingRepository;
import com.roomio.booking.repository.ResourceRepository;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnalyticsService {
  private static final DateTimeFormatter HOUR_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

  private final BookingRepository bookings;
  private final ResourceRepository resources;

  public AnalyticsService(BookingRepository bookings, ResourceRepository resources) {
    this.bookings = bookings;
    this.resources = resources;
  }

  @Transactional(readOnly = true)
  public AnalyticsSummaryResponse summary() {
    List<Booking> all = bookings.findAll();
    List<Booking> active = all.stream().filter(booking -> booking.getStatus() != BookingStatus.CANCELLED).toList();
    String mostBooked = resourceUsage().stream()
      .max(Comparator.comparingLong(ResourceUsageResponse::bookings))
      .map(ResourceUsageResponse::resourceName)
      .orElse("No data");
    String peakHour = active.stream()
      .collect(Collectors.groupingBy(booking -> booking.getStartTime().format(HOUR_FORMAT), Collectors.counting()))
      .entrySet()
      .stream()
      .max(Map.Entry.comparingByValue())
      .map(Map.Entry::getKey)
      .orElse("No data");

    return new AnalyticsSummaryResponse(
      all.size(),
      active.size(),
      all.stream().filter(booking -> booking.getStatus() == BookingStatus.CANCELLED).count(),
      all.stream().filter(booking -> booking.getStatus() == BookingStatus.NO_SHOW).count(),
      mostBooked,
      peakHour);
  }

  @Transactional(readOnly = true)
  public List<ResourceUsageResponse> resourceUsage() {
    List<Booking> all = bookings.findAll();
    Map<String, Long> counts = all.stream()
      .filter(booking -> booking.getStatus() != BookingStatus.CANCELLED)
      .collect(Collectors.groupingBy(booking -> booking.getResource().getId(), Collectors.counting()));

    return resources.findAll().stream()
      .map(resource -> new ResourceUsageResponse(resource.getId(), resource.getName(), counts.getOrDefault(resource.getId(), 0L)))
      .toList();
  }

  @Transactional(readOnly = true)
  public List<StatusDistributionResponse> statusDistribution() {
    Map<BookingStatus, Long> counts = bookings.findAll().stream()
      .map(Booking::getStatus)
      .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

    return List.of(BookingStatus.CONFIRMED, BookingStatus.COMPLETED, BookingStatus.CANCELLED, BookingStatus.NO_SHOW)
      .stream()
      .map(status -> new StatusDistributionResponse(status.getValue(), counts.getOrDefault(status, 0L)))
      .toList();
  }
}

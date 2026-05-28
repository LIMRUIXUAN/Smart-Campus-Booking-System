package com.roomio.booking.controller;

import com.roomio.booking.dto.BookingRequest;
import com.roomio.booking.dto.BookingResponse;
import com.roomio.booking.dto.StatusUpdateRequest;
import com.roomio.booking.service.BookingService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
  private final BookingService bookingService;

  public BookingController(BookingService bookingService) {
    this.bookingService = bookingService;
  }

  @PostMapping
  @PreAuthorize("hasRole('STUDENT')")
  public BookingResponse create(Principal principal, @Valid @RequestBody BookingRequest request) {
    return bookingService.create(principal.getName(), request);
  }

  @GetMapping("/my")
  public List<BookingResponse> myBookings(Principal principal) {
    return bookingService.myBookings(principal.getName());
  }

  @GetMapping("/all")
  @PreAuthorize("hasRole('ADMIN')")
  public List<BookingResponse> all() {
    return bookingService.all();
  }

  @PutMapping("/{id}")
  public BookingResponse update(Principal principal, @PathVariable String id, @Valid @RequestBody BookingRequest request) {
    return bookingService.update(principal.getName(), id, request);
  }

  @PatchMapping("/{id}/cancel")
  public BookingResponse cancel(Principal principal, @PathVariable String id) {
    return bookingService.cancel(principal.getName(), id);
  }

  @PatchMapping("/{id}/status")
  @PreAuthorize("hasRole('ADMIN')")
  public BookingResponse updateStatus(@PathVariable String id, @Valid @RequestBody StatusUpdateRequest request) {
    return bookingService.updateStatus(id, request.status());
  }
}

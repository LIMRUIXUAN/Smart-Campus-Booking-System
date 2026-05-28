package com.roomio.booking.controller;

import com.roomio.booking.dto.AvailabilityRequest;
import com.roomio.booking.dto.AvailabilityResponse;
import com.roomio.booking.dto.SuggestionResponse;
import com.roomio.booking.service.BookingService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {
  private final BookingService bookingService;

  public AvailabilityController(BookingService bookingService) {
    this.bookingService = bookingService;
  }

  @GetMapping
  public AvailabilityResponse check(Principal principal, @Valid @ModelAttribute AvailabilityQuery query) {
    return bookingService.checkAvailability(principal.getName(), query.toRequest());
  }

  @GetMapping("/suggestions")
  public List<SuggestionResponse> suggestions(Principal principal, @Valid @ModelAttribute AvailabilityQuery query) {
    return bookingService.suggestions(principal.getName(), query.toRequest());
  }

  public static class AvailabilityQuery {
    private String resourceId;
    private Integer pax;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private java.time.LocalDate startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private java.time.LocalTime startTime;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private java.time.LocalDate endDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private java.time.LocalTime endTime;

    AvailabilityRequest toRequest() {
      return new AvailabilityRequest(resourceId, pax, startDate, startTime, endDate, endTime);
    }

    public String getResourceId() {
      return resourceId;
    }

    public void setResourceId(String resourceId) {
      this.resourceId = resourceId;
    }

    public Integer getPax() {
      return pax;
    }

    public void setPax(Integer pax) {
      this.pax = pax;
    }

    public java.time.LocalDate getStartDate() {
      return startDate;
    }

    public void setStartDate(java.time.LocalDate startDate) {
      this.startDate = startDate;
    }

    public java.time.LocalTime getStartTime() {
      return startTime;
    }

    public void setStartTime(java.time.LocalTime startTime) {
      this.startTime = startTime;
    }

    public java.time.LocalDate getEndDate() {
      return endDate;
    }

    public void setEndDate(java.time.LocalDate endDate) {
      this.endDate = endDate;
    }

    public java.time.LocalTime getEndTime() {
      return endTime;
    }

    public void setEndTime(java.time.LocalTime endTime) {
      this.endTime = endTime;
    }
  }
}

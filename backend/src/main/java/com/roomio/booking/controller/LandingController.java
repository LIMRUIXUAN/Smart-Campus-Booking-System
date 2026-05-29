package com.roomio.booking.controller;

import com.roomio.booking.dto.LandingRoomSnapshotResponse;
import com.roomio.booking.service.LandingService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/landing")
public class LandingController {
  private final LandingService landingService;

  public LandingController(LandingService landingService) {
    this.landingService = landingService;
  }

  @GetMapping("/rooms")
  public List<LandingRoomSnapshotResponse> rooms() {
    return landingService.rooms();
  }
}

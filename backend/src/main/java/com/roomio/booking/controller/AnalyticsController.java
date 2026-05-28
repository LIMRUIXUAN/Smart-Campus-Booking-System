package com.roomio.booking.controller;

import com.roomio.booking.dto.AnalyticsSummaryResponse;
import com.roomio.booking.dto.ResourceUsageResponse;
import com.roomio.booking.dto.StatusDistributionResponse;
import com.roomio.booking.service.AnalyticsService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
  private final AnalyticsService analyticsService;

  public AnalyticsController(AnalyticsService analyticsService) {
    this.analyticsService = analyticsService;
  }

  @GetMapping("/summary")
  public AnalyticsSummaryResponse summary() {
    return analyticsService.summary();
  }

  @GetMapping("/resource-usage")
  public List<ResourceUsageResponse> resourceUsage() {
    return analyticsService.resourceUsage();
  }

  @GetMapping("/status-distribution")
  public List<StatusDistributionResponse> statusDistribution() {
    return analyticsService.statusDistribution();
  }
}

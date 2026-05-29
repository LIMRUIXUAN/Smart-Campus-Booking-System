package com.roomio.booking.dto;

public record LandingRoomSnapshotResponse(
    String resourceId,
    String resourceName,
    String location,
    Integer capacity,
    String displayState,
    String badge,
    String startTime,
    String endTime,
    String nextTransitionTime,
    boolean activeNow) {
}

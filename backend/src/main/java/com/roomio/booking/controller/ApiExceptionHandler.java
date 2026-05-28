package com.roomio.booking.controller;

import com.roomio.booking.dto.ApiErrorResponse;
import com.roomio.booking.service.ConflictException;
import com.roomio.booking.service.ForbiddenException;
import com.roomio.booking.service.NotFoundException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(ConflictException.class)
  ResponseEntity<Object> conflict(ConflictException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getAvailability());
  }

  @ExceptionHandler(NotFoundException.class)
  ResponseEntity<ApiErrorResponse> notFound(NotFoundException ex) {
    return error(HttpStatus.NOT_FOUND, ex.getMessage(), Map.of());
  }

  @ExceptionHandler(ForbiddenException.class)
  ResponseEntity<ApiErrorResponse> forbidden(ForbiddenException ex) {
    return error(HttpStatus.FORBIDDEN, ex.getMessage(), Map.of());
  }

  @ExceptionHandler({IllegalArgumentException.class, BadCredentialsException.class})
  ResponseEntity<ApiErrorResponse> badRequest(RuntimeException ex) {
    return error(HttpStatus.BAD_REQUEST, ex.getMessage(), Map.of());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ApiErrorResponse> validation(MethodArgumentNotValidException ex) {
    Map<String, String> fields = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error -> fields.put(error.getField(), error.getDefaultMessage()));
    return error(HttpStatus.BAD_REQUEST, "Request validation failed.", fields);
  }

  private ResponseEntity<ApiErrorResponse> error(HttpStatus status, String message, Map<String, String> fields) {
    return ResponseEntity.status(status).body(new ApiErrorResponse(
      Instant.now(),
      status.value(),
      status.getReasonPhrase(),
      message,
      fields));
  }
}

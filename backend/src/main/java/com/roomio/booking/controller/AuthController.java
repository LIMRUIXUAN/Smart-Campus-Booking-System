package com.roomio.booking.controller;

import com.roomio.booking.dto.AuthRequest;
import com.roomio.booking.dto.AuthResponse;
import com.roomio.booking.dto.ChangePasswordRequest;
import com.roomio.booking.dto.RegisterRequest;
import com.roomio.booking.dto.UpdateProfileRequest;
import com.roomio.booking.dto.ActionResponse;
import com.roomio.booking.service.AuthService;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
    return authService.register(request);
  }

  @PostMapping("/login")
  public AuthResponse login(@Valid @RequestBody AuthRequest request) {
    return authService.login(request);
  }

  @PutMapping("/me")
  public AuthResponse updateProfile(@Valid @RequestBody UpdateProfileRequest request, Principal principal) {
    return authService.updateProfile(principal.getName(), request);
  }

  @PatchMapping("/me/password")
  public ActionResponse changePassword(@Valid @RequestBody ChangePasswordRequest request, Principal principal) {
    return authService.changePassword(principal.getName(), request);
  }
}

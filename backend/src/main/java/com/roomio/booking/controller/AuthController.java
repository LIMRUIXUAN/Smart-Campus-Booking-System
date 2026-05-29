package com.roomio.booking.controller;

import com.roomio.booking.dto.ActionResponse;
import com.roomio.booking.dto.AuthRequest;
import com.roomio.booking.dto.AuthResponse;
import com.roomio.booking.dto.ChangePasswordRequest;
import com.roomio.booking.dto.CodeConfirmationRequest;
import com.roomio.booking.dto.PasswordResetChallengeResponse;
import com.roomio.booking.dto.PasswordResetCodeVerifyRequest;
import com.roomio.booking.dto.PasswordResetConfirmRequest;
import com.roomio.booking.dto.PasswordResetRequest;
import com.roomio.booking.dto.PasswordResetVerificationResponse;
import com.roomio.booking.dto.PasswordConfirmationRequest;
import com.roomio.booking.dto.RegisterRequest;
import com.roomio.booking.dto.UpdateNotificationSettingsRequest;
import com.roomio.booking.dto.UpdateProfileRequest;
import com.roomio.booking.dto.VerificationChallengeResponse;
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

  @PostMapping("/password-reset/request")
  public PasswordResetChallengeResponse requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {
    return authService.requestPasswordReset(request);
  }

  @PostMapping("/password-reset/verify")
  public PasswordResetVerificationResponse verifyPasswordResetCode(
      @Valid @RequestBody PasswordResetCodeVerifyRequest request) {
    return authService.verifyPasswordResetCode(request);
  }

  @PostMapping("/password-reset/confirm")
  public ActionResponse resetPassword(@Valid @RequestBody PasswordResetConfirmRequest request) {
    return authService.resetPassword(request);
  }

  @PutMapping("/me")
  public AuthResponse updateProfile(@Valid @RequestBody UpdateProfileRequest request, Principal principal) {
    return authService.updateProfile(principal.getName(), request);
  }

  @PatchMapping("/me/password")
  public ActionResponse changePassword(@Valid @RequestBody ChangePasswordRequest request, Principal principal) {
    return authService.changePassword(principal.getName(), request);
  }

  @PostMapping("/me/email-verification/request")
  public VerificationChallengeResponse requestEmailVerification(Principal principal) {
    return authService.requestEmailVerification(principal.getName());
  }

  @PostMapping("/me/email-verification/confirm")
  public AuthResponse confirmEmailVerification(@Valid @RequestBody CodeConfirmationRequest request, Principal principal) {
    return authService.confirmEmailVerification(principal.getName(), request);
  }

  @PostMapping("/me/two-factor/request")
  public VerificationChallengeResponse requestTwoFactor(Principal principal) {
    return authService.requestTwoFactorSetup(principal.getName());
  }

  @PostMapping("/me/two-factor/confirm")
  public AuthResponse confirmTwoFactor(@Valid @RequestBody CodeConfirmationRequest request, Principal principal) {
    return authService.confirmTwoFactorSetup(principal.getName(), request);
  }

  @PostMapping("/me/two-factor/disable")
  public AuthResponse disableTwoFactor(@Valid @RequestBody PasswordConfirmationRequest request, Principal principal) {
    return authService.disableTwoFactor(principal.getName(), request);
  }

  @PutMapping("/me/notifications")
  public AuthResponse updateNotificationSettings(@RequestBody UpdateNotificationSettingsRequest request, Principal principal) {
    return authService.updateNotificationSettings(principal.getName(), request);
  }
}

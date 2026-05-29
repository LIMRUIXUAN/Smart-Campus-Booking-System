package com.roomio.booking.service;

import com.roomio.booking.dto.ActionResponse;
import com.roomio.booking.dto.AuthRequest;
import com.roomio.booking.dto.AuthResponse;
import com.roomio.booking.dto.ChangePasswordRequest;
import com.roomio.booking.dto.CodeConfirmationRequest;
import com.roomio.booking.dto.PasswordConfirmationRequest;
import com.roomio.booking.dto.RegisterRequest;
import com.roomio.booking.dto.UpdateNotificationSettingsRequest;
import com.roomio.booking.dto.UpdateProfileRequest;
import com.roomio.booking.dto.VerificationChallengeResponse;
import com.roomio.booking.model.Role;
import com.roomio.booking.model.User;
import com.roomio.booking.repository.UserRepository;
import com.roomio.booking.security.JwtService;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
  private final UserRepository users;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthService(UserRepository users, PasswordEncoder passwordEncoder, JwtService jwtService) {
    this.users = users;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  @Transactional
  public AuthResponse register(RegisterRequest request) {
    String email = request.email().trim().toLowerCase(Locale.ROOT);
    if (users.existsByEmailIgnoreCase(email)) {
      throw new IllegalArgumentException("An account already exists for this email.");
    }

    User user = users.save(new User(
      null,
      request.name().trim(),
      email,
      passwordEncoder.encode(request.password()),
      Role.STUDENT));

    return new AuthResponse(Mappers.user(user), jwtService.issue(user));
  }

  @Transactional(readOnly = true)
  public AuthResponse login(AuthRequest request) {
    User user = users.findByEmailIgnoreCase(request.email())
      .orElseThrow(() -> new BadCredentialsException("Invalid email or password."));

    if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
      throw new BadCredentialsException("Invalid email or password.");
    }

    return new AuthResponse(Mappers.user(user), jwtService.issue(user));
  }

  @Transactional
  public AuthResponse updateProfile(String currentEmail, UpdateProfileRequest request) {
    User user = currentUser(currentEmail);

    String nextName = request.name().trim();
    String nextEmail = request.email().trim().toLowerCase(Locale.ROOT);
    boolean emailChanged = !user.getEmail().equalsIgnoreCase(nextEmail);

    users.findByEmailIgnoreCase(nextEmail)
      .filter(existing -> !existing.getId().equals(user.getId()))
      .ifPresent(existing -> {
        throw new IllegalArgumentException("An account already exists for this email.");
      });

    user.setName(nextName);
    user.setEmail(nextEmail);
    if (emailChanged) {
      user.setEmailVerified(false);
      user.setPendingEmailVerificationCode(null);
      user.setPendingEmailVerificationExpiresAt(null);
      user.setTwoFactorEnabled(false);
      user.setPendingTwoFactorCode(null);
      user.setPendingTwoFactorExpiresAt(null);
    }

    return new AuthResponse(Mappers.user(user), jwtService.issue(user));
  }

  @Transactional
  public ActionResponse changePassword(String currentEmail, ChangePasswordRequest request) {
    User user = currentUser(currentEmail);

    if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
      throw new BadCredentialsException("Current password is incorrect.");
    }

    String nextPassword = request.newPassword().trim();
    if (passwordEncoder.matches(nextPassword, user.getPasswordHash())) {
      throw new IllegalArgumentException("New password must be different from the current password.");
    }

    user.setPasswordHash(passwordEncoder.encode(nextPassword));
    return new ActionResponse("Password updated successfully.");
  }

  @Transactional
  public VerificationChallengeResponse requestEmailVerification(String currentEmail) {
    User user = currentUser(currentEmail);
    if (user.isEmailVerified()) {
      throw new IllegalArgumentException("Email is already verified.");
    }

    String code = issueCode();
    Instant expiresAt = Instant.now().plusSeconds(600);
    user.setPendingEmailVerificationCode(code);
    user.setPendingEmailVerificationExpiresAt(expiresAt);
    return new VerificationChallengeResponse("Verification code generated for local testing.", code, expiresAt);
  }

  @Transactional
  public AuthResponse confirmEmailVerification(String currentEmail, CodeConfirmationRequest request) {
    User user = currentUser(currentEmail);
    validateCode(user.getPendingEmailVerificationCode(), user.getPendingEmailVerificationExpiresAt(), request.code(), "Email verification code is invalid or expired.");
    user.setEmailVerified(true);
    user.setPendingEmailVerificationCode(null);
    user.setPendingEmailVerificationExpiresAt(null);
    return new AuthResponse(Mappers.user(user), jwtService.issue(user));
  }

  @Transactional
  public VerificationChallengeResponse requestTwoFactorSetup(String currentEmail) {
    User user = currentUser(currentEmail);
    if (!user.isEmailVerified()) {
      throw new IllegalArgumentException("Verify your email before enabling two-step login.");
    }
    if (user.isTwoFactorEnabled()) {
      throw new IllegalArgumentException("Two-step login is already enabled.");
    }

    String code = issueCode();
    Instant expiresAt = Instant.now().plusSeconds(600);
    user.setPendingTwoFactorCode(code);
    user.setPendingTwoFactorExpiresAt(expiresAt);
    return new VerificationChallengeResponse("Two-step setup code generated for local testing.", code, expiresAt);
  }

  @Transactional
  public AuthResponse confirmTwoFactorSetup(String currentEmail, CodeConfirmationRequest request) {
    User user = currentUser(currentEmail);
    validateCode(user.getPendingTwoFactorCode(), user.getPendingTwoFactorExpiresAt(), request.code(), "Two-step setup code is invalid or expired.");
    user.setTwoFactorEnabled(true);
    user.setPendingTwoFactorCode(null);
    user.setPendingTwoFactorExpiresAt(null);
    return new AuthResponse(Mappers.user(user), jwtService.issue(user));
  }

  @Transactional
  public AuthResponse disableTwoFactor(String currentEmail, PasswordConfirmationRequest request) {
    User user = currentUser(currentEmail);
    if (!user.isTwoFactorEnabled()) {
      throw new IllegalArgumentException("Two-step login is not enabled.");
    }
    if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
      throw new BadCredentialsException("Current password is incorrect.");
    }

    user.setTwoFactorEnabled(false);
    user.setPendingTwoFactorCode(null);
    user.setPendingTwoFactorExpiresAt(null);
    return new AuthResponse(Mappers.user(user), jwtService.issue(user));
  }

  @Transactional
  public AuthResponse updateNotificationSettings(String currentEmail, UpdateNotificationSettingsRequest request) {
    User user = currentUser(currentEmail);
    user.setBookingAlertsEnabled(request.bookingAlertsEnabled());
    user.setEmailDigestEnabled(request.emailDigestEnabled());
    user.setPushNotificationsEnabled(request.pushNotificationsEnabled());
    return new AuthResponse(Mappers.user(user), jwtService.issue(user));
  }

  private User currentUser(String currentEmail) {
    return users.findByEmailIgnoreCase(currentEmail)
      .orElseThrow(() -> new BadCredentialsException("Invalid session."));
  }

  private String issueCode() {
    return String.format("%06d", ThreadLocalRandom.current().nextInt(0, 1_000_000));
  }

  private void validateCode(String storedCode, Instant expiresAt, String submittedCode, String message) {
    if (storedCode == null || expiresAt == null || Instant.now().isAfter(expiresAt) || !storedCode.equals(submittedCode.trim())) {
      throw new IllegalArgumentException(message);
    }
  }
}

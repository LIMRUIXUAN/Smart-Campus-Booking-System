package com.roomio.booking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

import com.roomio.booking.dto.ChangePasswordRequest;
import com.roomio.booking.dto.CodeConfirmationRequest;
import com.roomio.booking.dto.PasswordResetCodeVerifyRequest;
import com.roomio.booking.dto.PasswordResetConfirmRequest;
import com.roomio.booking.dto.PasswordResetRequest;
import com.roomio.booking.dto.PasswordConfirmationRequest;
import com.roomio.booking.dto.RegisterRequest;
import com.roomio.booking.dto.UpdateNotificationSettingsRequest;
import com.roomio.booking.dto.UpdateProfileRequest;
import com.roomio.booking.model.Role;
import com.roomio.booking.model.User;
import com.roomio.booking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class AuthServiceTest {
  @Autowired
  AuthService authService;

  @Autowired
  UserRepository users;

  @Autowired
  PasswordEncoder passwordEncoder;

  @MockBean
  PasswordResetMailer passwordResetMailer;

  @BeforeEach
  void setUp() {
    users.deleteAll();
  }

  @Test
  void updateProfileChangesNameAndEmailAndIssuesFreshToken() {
    var registered = authService.register(new RegisterRequest("Alya Tan", "student@campus.test", "password"));

    var updated = authService.updateProfile("student@campus.test", new UpdateProfileRequest("Alya Noor", "alya@campus.test"));

    assertThat(updated.user().name()).isEqualTo("Alya Noor");
    assertThat(updated.user().email()).isEqualTo("alya@campus.test");
    assertThat(updated.token()).isNotBlank();
    assertThat(users.findByEmailIgnoreCase("alya@campus.test")).isPresent();
    assertThat(users.findByEmailIgnoreCase("student@campus.test")).isEmpty();
    assertThat(registered.token()).isNotEqualTo(updated.token());
  }

  @Test
  void updateProfileRejectsEmailAlreadyUsedByAnotherAccount() {
    users.save(new User("u-admin-test", "Admin", "admin@campus.test", "hash", Role.ADMIN));
    authService.register(new RegisterRequest("Alya Tan", "student@campus.test", "password"));

    assertThatThrownBy(() -> authService.updateProfile(
      "student@campus.test",
      new UpdateProfileRequest("Alya Tan", "admin@campus.test")))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("already exists");
  }

  @Test
  void changePasswordUpdatesStoredHash() {
    authService.register(new RegisterRequest("Alya Tan", "student@campus.test", "password"));

    var result = authService.changePassword("student@campus.test", new ChangePasswordRequest("password", "newpass1"));

    assertThat(result.message()).contains("successfully");
    User updated = users.findByEmailIgnoreCase("student@campus.test").orElseThrow();
    assertThat(passwordEncoder.matches("newpass1", updated.getPasswordHash())).isTrue();
  }

  @Test
  void changePasswordRejectsWrongCurrentPassword() {
    authService.register(new RegisterRequest("Alya Tan", "student@campus.test", "password"));

    assertThatThrownBy(() -> authService.changePassword(
      "student@campus.test",
      new ChangePasswordRequest("wrongpass", "newpass1")))
      .isInstanceOf(BadCredentialsException.class)
      .hasMessageContaining("Current password is incorrect");
  }

  @Test
  void changePasswordRejectsReusingCurrentPassword() {
    authService.register(new RegisterRequest("Alya Tan", "student@campus.test", "password"));

    assertThatThrownBy(() -> authService.changePassword(
      "student@campus.test",
      new ChangePasswordRequest("password", "password")))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("must be different");
  }

  @Test
  void emailVerificationRoundTripMarksUserVerified() {
    authService.register(new RegisterRequest("Alya Tan", "student@campus.test", "password"));

    var challenge = authService.requestEmailVerification("student@campus.test");
    var result = authService.confirmEmailVerification("student@campus.test", new CodeConfirmationRequest(challenge.code()));

    assertThat(result.user().emailVerified()).isTrue();
  }

  @Test
  void enablingTwoFactorRequiresVerifiedEmail() {
    authService.register(new RegisterRequest("Alya Tan", "student@campus.test", "password"));

    assertThatThrownBy(() -> authService.requestTwoFactorSetup("student@campus.test"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("Verify your email");
  }

  @Test
  void twoFactorRoundTripCanEnableAndDisable() {
    authService.register(new RegisterRequest("Alya Tan", "student@campus.test", "password"));
    var emailChallenge = authService.requestEmailVerification("student@campus.test");
    authService.confirmEmailVerification("student@campus.test", new CodeConfirmationRequest(emailChallenge.code()));

    var twoFactorChallenge = authService.requestTwoFactorSetup("student@campus.test");
    var enabled = authService.confirmTwoFactorSetup("student@campus.test", new CodeConfirmationRequest(twoFactorChallenge.code()));
    var disabled = authService.disableTwoFactor("student@campus.test", new PasswordConfirmationRequest("password"));

    assertThat(enabled.user().twoFactorEnabled()).isTrue();
    assertThat(disabled.user().twoFactorEnabled()).isFalse();
  }

  @Test
  void notificationSettingsCanBeUpdated() {
    authService.register(new RegisterRequest("Alya Tan", "student@campus.test", "password"));

    var updated = authService.updateNotificationSettings(
      "student@campus.test",
      new UpdateNotificationSettingsRequest(false, true, false));

    assertThat(updated.user().bookingAlertsEnabled()).isFalse();
    assertThat(updated.user().emailDigestEnabled()).isTrue();
    assertThat(updated.user().pushNotificationsEnabled()).isFalse();
  }

  @Test
  void passwordResetRoundTripUpdatesStoredHash() {
    authService.register(new RegisterRequest("Alya Tan", "student@campus.test", "password"));

    var challenge = authService.requestPasswordReset(new PasswordResetRequest("student@campus.test"));
    var verification = authService.verifyPasswordResetCode(
      new PasswordResetCodeVerifyRequest("student@campus.test", users.findByEmailIgnoreCase("student@campus.test").orElseThrow().getPasswordResetCode()));
    var result = authService.resetPassword(new PasswordResetConfirmRequest(verification.token(), "newpass1"));

    assertThat(result.message()).contains("Password reset successfully");
    assertThat(challenge.expiresAt()).isNotNull();
    User updated = users.findByEmailIgnoreCase("student@campus.test").orElseThrow();
    assertThat(passwordEncoder.matches("newpass1", updated.getPasswordHash())).isTrue();
    assertThat(updated.getPasswordResetToken()).isNull();
    verify(passwordResetMailer).sendPasswordResetPin(anyString(), anyString(), anyString());
  }

  @Test
  void passwordResetRejectsInvalidToken() {
    authService.register(new RegisterRequest("Alya Tan", "student@campus.test", "password"));

    assertThatThrownBy(() -> authService.resetPassword(new PasswordResetConfirmRequest("bad-token", "newpass1")))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("invalid or expired");
  }

  @Test
  void passwordResetForUnknownEmailReturnsGenericResponse() {
    var challenge = authService.requestPasswordReset(new PasswordResetRequest("missing@campus.test"));

    assertThat(challenge.expiresAt()).isNull();
    assertThat(challenge.message()).contains("If that email exists");
  }

  @Test
  void passwordResetVerifyRejectsWrongCode() {
    authService.register(new RegisterRequest("Alya Tan", "student@campus.test", "password"));
    authService.requestPasswordReset(new PasswordResetRequest("student@campus.test"));

    assertThatThrownBy(() -> authService.verifyPasswordResetCode(
      new PasswordResetCodeVerifyRequest("student@campus.test", "9999")))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("Reset PIN is invalid or expired");
  }
}

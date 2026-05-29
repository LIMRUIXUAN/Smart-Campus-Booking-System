package com.roomio.booking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.roomio.booking.dto.ChangePasswordRequest;
import com.roomio.booking.dto.RegisterRequest;
import com.roomio.booking.dto.UpdateProfileRequest;
import com.roomio.booking.model.Role;
import com.roomio.booking.model.User;
import com.roomio.booking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
}

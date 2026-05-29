package com.roomio.booking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
  @Id
  private String id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String passwordHash;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @Column(nullable = false)
  private boolean emailVerified;

  private String pendingEmailVerificationCode;

  private Instant pendingEmailVerificationExpiresAt;

  @Column(nullable = false)
  private boolean twoFactorEnabled;

  private String pendingTwoFactorCode;

  private Instant pendingTwoFactorExpiresAt;

  private String passwordResetCode;

  private Instant passwordResetCodeExpiresAt;

  private String passwordResetToken;

  private Instant passwordResetExpiresAt;

  @Column(nullable = false)
  private boolean bookingAlertsEnabled;

  @Column(nullable = false)
  private boolean emailDigestEnabled;

  @Column(nullable = false)
  private boolean pushNotificationsEnabled;

  @Column(nullable = false)
  private Instant createdAt;

  protected User() {
  }

  public User(String id, String name, String email, String passwordHash, Role role) {
    this.id = id == null ? UUID.randomUUID().toString() : id;
    this.name = name;
    this.email = email;
    this.passwordHash = passwordHash;
    this.role = role;
    this.emailVerified = false;
    this.twoFactorEnabled = false;
    this.bookingAlertsEnabled = true;
    this.emailDigestEnabled = true;
    this.pushNotificationsEnabled = true;
    this.createdAt = Instant.now();
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public boolean isEmailVerified() {
    return emailVerified;
  }

  public void setEmailVerified(boolean emailVerified) {
    this.emailVerified = emailVerified;
  }

  public String getPendingEmailVerificationCode() {
    return pendingEmailVerificationCode;
  }

  public void setPendingEmailVerificationCode(String pendingEmailVerificationCode) {
    this.pendingEmailVerificationCode = pendingEmailVerificationCode;
  }

  public Instant getPendingEmailVerificationExpiresAt() {
    return pendingEmailVerificationExpiresAt;
  }

  public void setPendingEmailVerificationExpiresAt(Instant pendingEmailVerificationExpiresAt) {
    this.pendingEmailVerificationExpiresAt = pendingEmailVerificationExpiresAt;
  }

  public boolean isTwoFactorEnabled() {
    return twoFactorEnabled;
  }

  public void setTwoFactorEnabled(boolean twoFactorEnabled) {
    this.twoFactorEnabled = twoFactorEnabled;
  }

  public String getPendingTwoFactorCode() {
    return pendingTwoFactorCode;
  }

  public void setPendingTwoFactorCode(String pendingTwoFactorCode) {
    this.pendingTwoFactorCode = pendingTwoFactorCode;
  }

  public Instant getPendingTwoFactorExpiresAt() {
    return pendingTwoFactorExpiresAt;
  }

  public void setPendingTwoFactorExpiresAt(Instant pendingTwoFactorExpiresAt) {
    this.pendingTwoFactorExpiresAt = pendingTwoFactorExpiresAt;
  }

  public String getPasswordResetCode() {
    return passwordResetCode;
  }

  public void setPasswordResetCode(String passwordResetCode) {
    this.passwordResetCode = passwordResetCode;
  }

  public Instant getPasswordResetCodeExpiresAt() {
    return passwordResetCodeExpiresAt;
  }

  public void setPasswordResetCodeExpiresAt(Instant passwordResetCodeExpiresAt) {
    this.passwordResetCodeExpiresAt = passwordResetCodeExpiresAt;
  }

  public String getPasswordResetToken() {
    return passwordResetToken;
  }

  public void setPasswordResetToken(String passwordResetToken) {
    this.passwordResetToken = passwordResetToken;
  }

  public Instant getPasswordResetExpiresAt() {
    return passwordResetExpiresAt;
  }

  public void setPasswordResetExpiresAt(Instant passwordResetExpiresAt) {
    this.passwordResetExpiresAt = passwordResetExpiresAt;
  }

  public boolean isBookingAlertsEnabled() {
    return bookingAlertsEnabled;
  }

  public void setBookingAlertsEnabled(boolean bookingAlertsEnabled) {
    this.bookingAlertsEnabled = bookingAlertsEnabled;
  }

  public boolean isEmailDigestEnabled() {
    return emailDigestEnabled;
  }

  public void setEmailDigestEnabled(boolean emailDigestEnabled) {
    this.emailDigestEnabled = emailDigestEnabled;
  }

  public boolean isPushNotificationsEnabled() {
    return pushNotificationsEnabled;
  }

  public void setPushNotificationsEnabled(boolean pushNotificationsEnabled) {
    this.pushNotificationsEnabled = pushNotificationsEnabled;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}

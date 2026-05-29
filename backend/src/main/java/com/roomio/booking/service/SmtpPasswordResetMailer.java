package com.roomio.booking.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SmtpPasswordResetMailer implements PasswordResetMailer {
  private final JavaMailSender mailSender;
  private final String fromAddress;
  private final String username;
  private final String password;

  public SmtpPasswordResetMailer(
      JavaMailSender mailSender,
      @Value("${app.mail.from:}") String fromAddress,
      @Value("${spring.mail.username:}") String username,
      @Value("${spring.mail.password:}") String password) {
    this.mailSender = mailSender;
    this.fromAddress = fromAddress == null ? "" : fromAddress.trim();
    this.username = username == null ? "" : username.trim();
    this.password = password == null ? "" : password.trim();
  }

  @Override
  public void sendPasswordResetPin(String email, String name, String pin) {
    if (fromAddress.isBlank() || username.isBlank() || password.isBlank()) {
      throw new IllegalStateException(
        "Email sending is not configured. Add MAIL_USERNAME, MAIL_PASSWORD, and MAIL_FROM to backend/.env.");
    }

    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(fromAddress);
    message.setTo(email);
    message.setSubject("RooMio password reset PIN");
    message.setText("""
      Hi %s,

      Your RooMio password reset PIN is: %s

      This PIN will expire in 10 minutes.
      If you did not request this reset, you can ignore this email.

      RooMio Support
      """.formatted(name, pin));
    mailSender.send(message);
  }
}

package com.roomio.booking.service;

public interface PasswordResetMailer {
  void sendPasswordResetPin(String email, String name, String pin);
}

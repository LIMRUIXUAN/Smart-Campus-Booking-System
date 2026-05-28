package com.roomio.booking.service;

import com.roomio.booking.dto.AuthRequest;
import com.roomio.booking.dto.AuthResponse;
import com.roomio.booking.dto.RegisterRequest;
import com.roomio.booking.model.Role;
import com.roomio.booking.model.User;
import com.roomio.booking.repository.UserRepository;
import com.roomio.booking.security.JwtService;
import java.util.Locale;
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
}

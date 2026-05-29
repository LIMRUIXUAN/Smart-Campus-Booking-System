package com.roomio.booking.repository;

import com.roomio.booking.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
  Optional<User> findByEmailIgnoreCase(String email);

  Optional<User> findByPasswordResetToken(String token);

  boolean existsByEmailIgnoreCase(String email);
}

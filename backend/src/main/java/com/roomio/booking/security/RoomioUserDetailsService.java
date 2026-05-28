package com.roomio.booking.security;

import com.roomio.booking.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class RoomioUserDetailsService implements UserDetailsService {
  private final UserRepository users;

  public RoomioUserDetailsService(UserRepository users) {
    this.users = users;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return users.findByEmailIgnoreCase(username)
      .map(RoomioUserDetails::new)
      .orElseThrow(() -> new UsernameNotFoundException("User not found."));
  }
}

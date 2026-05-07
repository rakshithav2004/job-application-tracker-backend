package com.jobtracker.service;

import com.jobtracker.dto.AuthRequest;
import com.jobtracker.dto.AuthResponse;
import com.jobtracker.dto.RegisterRequest;
import com.jobtracker.entity.Role;
import com.jobtracker.entity.User;
import com.jobtracker.exception.BadRequestException;
import com.jobtracker.exception.UnauthorizedException;
import com.jobtracker.repository.UserRepository;
import com.jobtracker.security.JwtUtil;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  public String register(RegisterRequest request) {

    if (userRepository.existsByEmail(request.getEmail())) {
      throw new BadRequestException("Email already exists");
    }
    User user = new User();
    user.setName(request.getName());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(Role.USER);
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());
    userRepository.save(user);
    return "User registered successfully";
  }

  public AuthResponse login(AuthRequest request) {
    User user =
        userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new BadRequestException("Invalid email or password"));
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new UnauthorizedException("Invalid email or password");
    }

    String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

    return new AuthResponse(token, "Login successful");
  }
}

package com.jobtracker.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private JwtUtil jwtUtil;

  @InjectMocks private AuthService authService;

  private RegisterRequest registerRequest;
  private AuthRequest authRequest;
  private User testUser;

  @BeforeEach
  void setUp() {
    registerRequest = new RegisterRequest();
    registerRequest.setName("John Doe");
    registerRequest.setEmail("john@example.com");
    registerRequest.setPassword("password123");

    authRequest = new AuthRequest();
    authRequest.setEmail("john@example.com");
    authRequest.setPassword("password123");

    testUser = new User();
    testUser.setId("user123");
    testUser.setName("John Doe");
    testUser.setEmail("john@example.com");
    testUser.setPassword("encodedPassword");
    testUser.setRole(Role.USER);
    testUser.setCreatedAt(LocalDateTime.now());
    testUser.setUpdatedAt(LocalDateTime.now());
  }

  @Test
  void testRegisterSuccess() {
    when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
    when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
    when(userRepository.save(any(User.class))).thenReturn(testUser);
    String result = authService.register(registerRequest);
    assertEquals("User registered successfully", result);
    verify(userRepository, times(1)).existsByEmail(registerRequest.getEmail());
    verify(passwordEncoder, times(1)).encode(registerRequest.getPassword());
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void testRegisterWithExistingEmail() {
    when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);
    assertThrows(
        BadRequestException.class,
        () -> authService.register(registerRequest),
        "Email already exists");
    verify(userRepository, times(1)).existsByEmail(registerRequest.getEmail());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void testLoginSuccess() {
    String token = "jwt-token-123";
    when(userRepository.findByEmail(authRequest.getEmail())).thenReturn(Optional.of(testUser));
    when(passwordEncoder.matches(authRequest.getPassword(), testUser.getPassword()))
        .thenReturn(true);
    when(jwtUtil.generateToken(testUser.getEmail(), testUser.getRole().name())).thenReturn(token);
    AuthResponse response = authService.login(authRequest);
    assertNotNull(response);
    assertEquals(token, response.getToken());
    assertEquals("Login successful", response.getMessage());
    verify(userRepository, times(1)).findByEmail(authRequest.getEmail());
    verify(passwordEncoder, times(1)).matches(authRequest.getPassword(), testUser.getPassword());
    verify(jwtUtil, times(1)).generateToken(testUser.getEmail(), testUser.getRole().name());
  }

  @Test
  void testLoginWithInvalidEmail() {
    when(userRepository.findByEmail(authRequest.getEmail())).thenReturn(Optional.empty());
    assertThrows(
        BadRequestException.class,
        () -> authService.login(authRequest),
        "Invalid email or password");
    verify(userRepository, times(1)).findByEmail(authRequest.getEmail());
    verify(passwordEncoder, never()).matches(anyString(), anyString());
  }

  @Test
  void testLoginWithInvalidPassword() {
    when(userRepository.findByEmail(authRequest.getEmail())).thenReturn(Optional.of(testUser));
    when(passwordEncoder.matches(authRequest.getPassword(), testUser.getPassword()))
        .thenReturn(false);
    assertThrows(
        UnauthorizedException.class,
        () -> authService.login(authRequest),
        "Invalid email or password");
    verify(userRepository, times(1)).findByEmail(authRequest.getEmail());
    verify(passwordEncoder, times(1)).matches(authRequest.getPassword(), testUser.getPassword());
    verify(jwtUtil, never()).generateToken(anyString(), anyString());
  }
}

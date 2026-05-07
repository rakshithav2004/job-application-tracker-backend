package com.jobtracker.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.jobtracker.entity.Role;
import com.jobtracker.entity.User;
import com.jobtracker.exception.ResourceNotFoundException;
import com.jobtracker.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private UserService userService;

  private User testUser;
  private String userEmail;

  @BeforeEach
  void setUp() {
    userEmail = "john@example.com";

    testUser = new User();
    testUser.setId("user123");
    testUser.setName("John Doe");
    testUser.setEmail(userEmail);
    testUser.setPassword("encodedPassword");
    testUser.setRole(Role.USER);
    testUser.setCreatedAt(LocalDateTime.now());
    testUser.setUpdatedAt(LocalDateTime.now());
  }

  @Test
  void testGetProfileSuccess() {
    when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
    User result = userService.getProfile(userEmail);
    assertNotNull(result);
    assertEquals("John Doe", result.getName());
    assertEquals(userEmail, result.getEmail());
    assertEquals(Role.USER, result.getRole());
    verify(userRepository, times(1)).findByEmail(userEmail);
  }

  @Test
  void testGetProfileNotFound() {
    when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());
    assertThrows(
        ResourceNotFoundException.class, () -> userService.getProfile(userEmail), "User not found");
    verify(userRepository, times(1)).findByEmail(userEmail);
  }
}

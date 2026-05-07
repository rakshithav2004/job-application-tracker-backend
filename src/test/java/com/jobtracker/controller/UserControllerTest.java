package com.jobtracker.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.jobtracker.entity.Role;
import com.jobtracker.entity.User;
import com.jobtracker.exception.ResourceNotFoundException;
import com.jobtracker.service.UserService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

  @TestConfiguration
  static class UserControllerTestConfig {
    @Bean
    public UserService userService() {
      return mock(UserService.class);
    }
  }

  @Autowired private MockMvc mockMvc;

  @Autowired private UserService userService;

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
  @WithMockUser(username = "john@example.com", roles = "USER")
  void testGetProfileSuccess() throws Exception {
    // Arrange
    when(userService.getProfile("john@example.com")).thenReturn(testUser);
    mockMvc
        .perform(get("/users/me"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("user123"))
        .andExpect(jsonPath("$.name").value("John Doe"))
        .andExpect(jsonPath("$.email").value("john@example.com"))
        .andExpect(jsonPath("$.role").value("USER"));
  }

  @Test
  @WithMockUser(username = "nonexistent@example.com", roles = "USER")
  void testGetProfileNotFound() throws Exception {
    when(userService.getProfile("nonexistent@example.com"))
        .thenThrow(new ResourceNotFoundException("User not found"));
    mockMvc
        .perform(get("/users/me"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("User not found"));
  }

  @Test
  void testGetProfileRequiresAuthentication() throws Exception {
    mockMvc.perform(get("/users/me")).andExpect(status().isForbidden());
  }
}

package com.jobtracker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtracker.dto.AuthRequest;
import com.jobtracker.dto.AuthResponse;
import com.jobtracker.dto.RegisterRequest;
import com.jobtracker.exception.BadRequestException;
import com.jobtracker.exception.UnauthorizedException;
import com.jobtracker.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

  @TestConfiguration
  static class AuthControllerTestConfig {
    @Bean
    public AuthService authService() {
      return mock(AuthService.class);
    }
  }

  @Autowired private MockMvc mockMvc;

  @Autowired private AuthService authService;

  @Autowired private ObjectMapper objectMapper;

  private RegisterRequest registerRequest;
  private AuthRequest authRequest;
  private AuthResponse authResponse;

  @BeforeEach
  void setUp() {
    registerRequest = new RegisterRequest();
    registerRequest.setName("John Doe");
    registerRequest.setEmail("john@example.com");
    registerRequest.setPassword("password123");

    authRequest = new AuthRequest();
    authRequest.setEmail("john@example.com");
    authRequest.setPassword("password123");

    authResponse = new AuthResponse("jwt-token-123", "Login successful");
  }

  @Test
  void testRegisterSuccess() throws Exception {
    when(authService.register(any(RegisterRequest.class)))
        .thenReturn("User registered successfully");
    mockMvc
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isOk())
        .andExpect(content().string("User registered successfully"));
  }

  @Test
  void testRegisterWithInvalidEmail() throws Exception {
    RegisterRequest invalidRequest = new RegisterRequest();
    invalidRequest.setName("John Doe");
    invalidRequest.setEmail("invalid-email");
    invalidRequest.setPassword("password123");
    mockMvc
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testRegisterWithMissingField() throws Exception {
    RegisterRequest incompleteRequest = new RegisterRequest();
    incompleteRequest.setName("John Doe");
    mockMvc
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(incompleteRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testRegisterWithDuplicateEmail() throws Exception {
    when(authService.register(any(RegisterRequest.class)))
        .thenThrow(new BadRequestException("Email already exists"));
    mockMvc
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Email already exists"));
  }

  @Test
  void testLoginSuccess() throws Exception {
    when(authService.login(any(AuthRequest.class))).thenReturn(authResponse);
    mockMvc
        .perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("jwt-token-123"))
        .andExpect(jsonPath("$.message").value("Login successful"));
  }

  @Test
  void testLoginWithInvalidEmail() throws Exception {
    AuthRequest invalidRequest = new AuthRequest();
    invalidRequest.setEmail("invalid-email");
    invalidRequest.setPassword("password123");
    mockMvc
        .perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testLoginWithInvalidCredentials() throws Exception {
    when(authService.login(any(AuthRequest.class)))
        .thenThrow(new UnauthorizedException("Invalid email or password"));
    mockMvc
        .perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Invalid email or password"));
  }

  @Test
  void testLoginWithMissingField() throws Exception {
    AuthRequest incompleteRequest = new AuthRequest();
    incompleteRequest.setEmail("john@example.com");
    mockMvc
        .perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(incompleteRequest)))
        .andExpect(status().isBadRequest());
  }
}

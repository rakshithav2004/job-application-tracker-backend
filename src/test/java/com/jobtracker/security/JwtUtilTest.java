package com.jobtracker.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(
    properties = {
      "jwt.secret=test-secret-key-with-minimum-32-characters-length",
      "jwt.expiration=86400000"
    })
class JwtUtilTest {

  @Autowired private JwtUtil jwtUtil;

  private String testEmail;
  private String testRole;

  @BeforeEach
  void setUp() {
    testEmail = "test@example.com";
    testRole = "USER";
  }

  @Test
  void testGenerateTokenSuccess() {
    String token = jwtUtil.generateToken(testEmail, testRole);
    assertNotNull(token);
    assertFalse(token.isEmpty());
    assertTrue(token.contains("."));
  }

  @Test
  void testExtractEmailSuccess() {
    String token = jwtUtil.generateToken(testEmail, testRole);
    String extractedEmail = jwtUtil.extractEmail(token);
    assertEquals(testEmail, extractedEmail);
  }

  @Test
  void testExtractRoleSuccess() {
    String token = jwtUtil.generateToken(testEmail, testRole);
    String extractedRole = jwtUtil.extractRole(token);
    assertEquals(testRole, extractedRole);
  }

  @Test
  void testValidateTokenSuccess() {
    String token = jwtUtil.generateToken(testEmail, testRole);
    boolean isValid = jwtUtil.validateToken(token);
    assertTrue(isValid);
  }

  @Test
  void testValidateTokenWithInvalidToken() {
    String invalidToken = "invalid.token.here";
    boolean isValid = jwtUtil.validateToken(invalidToken);
    assertFalse(isValid);
  }

  @Test
  void testValidateTokenWithExpiredToken() {
    String invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalid.signature";
    boolean isValid = jwtUtil.validateToken(invalidToken);
    assertFalse(isValid);
  }

  @Test
  void testTokenDoesNotContainPassword() {
    String token = jwtUtil.generateToken(testEmail, testRole);
    assertFalse(token.contains("password"));
  }

  @Test
  void testGenerateDifferentTokensForDifferentUsers() {
    String token1 = jwtUtil.generateToken("user1@example.com", "USER");
    String token2 = jwtUtil.generateToken("user2@example.com", "USER");
    assertNotEquals(token1, token2);
  }

  @Test
  void testGenerateDifferentTokensForDifferentRoles() {
    String userToken = jwtUtil.generateToken(testEmail, "USER");
    String adminToken = jwtUtil.generateToken(testEmail, "ADMIN");
    assertNotEquals(userToken, adminToken);
    assertEquals("USER", jwtUtil.extractRole(userToken));
    assertEquals("ADMIN", jwtUtil.extractRole(adminToken));
  }
}

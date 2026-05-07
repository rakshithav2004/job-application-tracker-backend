package com.jobtracker.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserEntityTest {

  private User user;

  @BeforeEach
  void setUp() {
    user = new User();
  }

  @Test
  void testUserEntityCreation() {
    user.setId("user123");
    user.setName("John Doe");
    user.setEmail("john@example.com");
    user.setPassword("encodedPassword");
    user.setRole(Role.USER);
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());

    assertEquals("user123", user.getId());
    assertEquals("John Doe", user.getName());
    assertEquals("john@example.com", user.getEmail());
    assertEquals("encodedPassword", user.getPassword());
    assertEquals(Role.USER, user.getRole());
    assertNotNull(user.getCreatedAt());
    assertNotNull(user.getUpdatedAt());
  }

  @Test
  void testUserWithAdminRole() {
    user.setRole(Role.ADMIN);
    assertEquals(Role.ADMIN, user.getRole());
  }

  @Test
  void testUserWithNullValues() {
    assertNull(user.getId());
    assertNull(user.getName());
    assertNull(user.getEmail());
    assertNull(user.getRole());
  }
}

class JobApplicationEntityTest {

  private JobApplication job;

  @BeforeEach
  void setUp() {
    job = new JobApplication();
  }

  @Test
  void testJobApplicationEntityCreation() {
    job.setId("job123");
    job.setUserId("user123");
    job.setCompanyName("TechCorp");
    job.setRole("Senior Developer");
    job.setStatus(JobStatus.APPLIED);
    job.setAppliedDate(LocalDateTime.now());
    job.setLastUpdated(LocalDateTime.now());
    assertEquals("job123", job.getId());
    assertEquals("user123", job.getUserId());
    assertEquals("TechCorp", job.getCompanyName());
    assertEquals("Senior Developer", job.getRole());
    assertEquals(JobStatus.APPLIED, job.getStatus());
    assertNotNull(job.getAppliedDate());
    assertNotNull(job.getLastUpdated());
  }

  @Test
  void testJobApplicationWithDifferentStatuses() {
    job.setStatus(JobStatus.APPLIED);
    assertEquals(JobStatus.APPLIED, job.getStatus());
    job.setStatus(JobStatus.INTERVIEW);
    assertEquals(JobStatus.INTERVIEW, job.getStatus());
    job.setStatus(JobStatus.OFFER);
    assertEquals(JobStatus.OFFER, job.getStatus());
    job.setStatus(JobStatus.REJECTED);
    assertEquals(JobStatus.REJECTED, job.getStatus());
  }

  @Test
  void testJobApplicationWithNullValues() {
    assertNull(job.getId());
    assertNull(job.getUserId());
    assertNull(job.getCompanyName());
    assertNull(job.getRole());
    assertNull(job.getStatus());
  }

  @Test
  void testJobApplicationUpdateFields() {
    // Act
    LocalDateTime initialDate = LocalDateTime.now();
    job.setAppliedDate(initialDate);

    LocalDateTime updatedDate = initialDate.plusDays(5);
    job.setLastUpdated(updatedDate);

    // Assert
    assertEquals(initialDate, job.getAppliedDate());
    assertEquals(updatedDate, job.getLastUpdated());
    assertNotEquals(job.getAppliedDate(), job.getLastUpdated());
  }
}

class RoleEnumTest {

  @Test
  void testRoleEnumValues() {
    assertNotNull(Role.USER);
    assertNotNull(Role.ADMIN);
    assertEquals(2, Role.values().length);
  }

  @Test
  void testRoleEnumName() {
    assertEquals("USER", Role.USER.name());
    assertEquals("ADMIN", Role.ADMIN.name());
  }
}

class JobStatusEnumTest {

  @Test
  void testJobStatusEnumValues() {
    assertNotNull(JobStatus.APPLIED);
    assertNotNull(JobStatus.INTERVIEW);
    assertNotNull(JobStatus.OFFER);
    assertNotNull(JobStatus.REJECTED);
    assertEquals(4, JobStatus.values().length);
  }

  @Test
  void testJobStatusEnumName() {
    assertEquals("APPLIED", JobStatus.APPLIED.name());
    assertEquals("INTERVIEW", JobStatus.INTERVIEW.name());
    assertEquals("OFFER", JobStatus.OFFER.name());
    assertEquals("REJECTED", JobStatus.REJECTED.name());
  }

  @Test
  void testJobStatusProgression() {
    JobStatus[] expectedProgression = {JobStatus.APPLIED, JobStatus.INTERVIEW, JobStatus.OFFER};
    assertEquals("APPLIED", expectedProgression[0].name());
    assertEquals("INTERVIEW", expectedProgression[1].name());
    assertEquals("OFFER", expectedProgression[2].name());
  }
}

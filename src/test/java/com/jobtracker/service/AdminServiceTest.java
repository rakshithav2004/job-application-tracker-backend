package com.jobtracker.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.jobtracker.entity.JobApplication;
import com.jobtracker.entity.JobStatus;
import com.jobtracker.entity.Role;
import com.jobtracker.entity.User;
import com.jobtracker.repository.JobRepository;
import com.jobtracker.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private JobRepository jobRepository;

  @InjectMocks private AdminService adminService;

  private User testUser1;
  private User testUser2;
  private JobApplication testJob1;
  private JobApplication testJob2;

  @BeforeEach
  void setUp() {
    testUser1 = new User();
    testUser1.setId("user1");
    testUser1.setName("John Doe");
    testUser1.setEmail("john@example.com");
    testUser1.setRole(Role.USER);

    testUser2 = new User();
    testUser2.setId("user2");
    testUser2.setName("Jane Smith");
    testUser2.setEmail("jane@example.com");
    testUser2.setRole(Role.USER);

    testJob1 = new JobApplication();
    testJob1.setId("job1");
    testJob1.setUserId("user1");
    testJob1.setCompanyName("TechCorp");
    testJob1.setRole("Developer");
    testJob1.setStatus(JobStatus.APPLIED);
    testJob1.setAppliedDate(LocalDateTime.now());

    testJob2 = new JobApplication();
    testJob2.setId("job2");
    testJob2.setUserId("user2");
    testJob2.setCompanyName("DataCorp");
    testJob2.setRole("Data Engineer");
    testJob2.setStatus(JobStatus.INTERVIEW);
    testJob2.setAppliedDate(LocalDateTime.now());
  }

  @Test
  void testGetAllUsersSuccess() {
    List<User> users = Arrays.asList(testUser1, testUser2);
    when(userRepository.findAll()).thenReturn(users);
    List<User> result = adminService.getAllUsers();
    assertEquals(2, result.size());
    assertEquals("John Doe", result.get(0).getName());
    assertEquals("Jane Smith", result.get(1).getName());
    verify(userRepository, times(1)).findAll();
  }

  @Test
  void testGetAllUsersEmpty() {
    when(userRepository.findAll()).thenReturn(Arrays.asList());
    List<User> result = adminService.getAllUsers();
    assertTrue(result.isEmpty());
    verify(userRepository, times(1)).findAll();
  }

  @Test
  void testGetAllJobsSuccess() {
    List<JobApplication> jobs = Arrays.asList(testJob1, testJob2);
    when(jobRepository.findAll()).thenReturn(jobs);
    List<JobApplication> result = adminService.getAllJobs();
    assertEquals(2, result.size());
    assertEquals("TechCorp", result.get(0).getCompanyName());
    assertEquals("DataCorp", result.get(1).getCompanyName());
    verify(jobRepository, times(1)).findAll();
  }

  @Test
  void testGetAllJobsEmpty() {
    when(jobRepository.findAll()).thenReturn(Arrays.asList());
    List<JobApplication> result = adminService.getAllJobs();
    assertTrue(result.isEmpty());
    verify(jobRepository, times(1)).findAll();
  }

  @Test
  void testGetSystemStatsSuccess() {
    when(userRepository.count()).thenReturn(5L);
    when(jobRepository.count()).thenReturn(12L);
    Map<String, Long> stats = adminService.getSystemStats();
    assertNotNull(stats);
    assertEquals(5L, stats.get("users"));
    assertEquals(12L, stats.get("jobs"));
    verify(userRepository, times(1)).count();
    verify(jobRepository, times(1)).count();
  }

  @Test
  void testGetSystemStatsEmpty() {
    when(userRepository.count()).thenReturn(0L);
    when(jobRepository.count()).thenReturn(0L);
    Map<String, Long> stats = adminService.getSystemStats();
    assertNotNull(stats);
    assertEquals(0L, stats.get("users"));
    assertEquals(0L, stats.get("jobs"));
  }
}

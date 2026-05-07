package com.jobtracker.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.jobtracker.entity.JobApplication;
import com.jobtracker.entity.JobStatus;
import com.jobtracker.entity.Role;
import com.jobtracker.entity.User;
import com.jobtracker.service.AdminService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
class AdminControllerTest {

  @TestConfiguration
  static class AdminControllerTestConfig {
    @Bean
    public AdminService adminService() {
      return mock(AdminService.class);
    }
  }

  @Autowired private MockMvc mockMvc;

  @Autowired private AdminService adminService;

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
    testUser1.setCreatedAt(LocalDateTime.now());

    testUser2 = new User();
    testUser2.setId("user2");
    testUser2.setName("Jane Smith");
    testUser2.setEmail("jane@example.com");
    testUser2.setRole(Role.USER);
    testUser2.setCreatedAt(LocalDateTime.now());

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
  @WithMockUser(roles = "ADMIN")
  void testGetUsersSuccess() throws Exception {
    List<User> users = Arrays.asList(testUser1, testUser2);
    when(adminService.getAllUsers()).thenReturn(users);
    mockMvc
        .perform(get("/admin/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].name").value("John Doe"))
        .andExpect(jsonPath("$[1].name").value("Jane Smith"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void testGetUsersEmpty() throws Exception {
    when(adminService.getAllUsers()).thenReturn(Arrays.asList());
    mockMvc
        .perform(get("/admin/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @WithMockUser(roles = "USER")
  void testGetUsersWithUserRole() throws Exception {
    mockMvc.perform(get("/admin/users")).andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void testGetJobsSuccess() throws Exception {
    List<JobApplication> jobs = Arrays.asList(testJob1, testJob2);
    when(adminService.getAllJobs()).thenReturn(jobs);
    mockMvc
        .perform(get("/admin/jobs"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].companyName").value("TechCorp"))
        .andExpect(jsonPath("$[1].companyName").value("DataCorp"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void testGetJobsEmpty() throws Exception {
    when(adminService.getAllJobs()).thenReturn(Arrays.asList());
    mockMvc
        .perform(get("/admin/jobs"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @WithMockUser(roles = "USER")
  void testGetJobsWithUserRole() throws Exception {
    mockMvc.perform(get("/admin/jobs")).andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void testGetStatsSuccess() throws Exception {
    Map<String, Long> stats = new HashMap<>();
    stats.put("users", 10L);
    stats.put("jobs", 45L);
    when(adminService.getSystemStats()).thenReturn(stats);
    mockMvc
        .perform(get("/admin/stats"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.users").value(10))
        .andExpect(jsonPath("$.jobs").value(45));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void testGetStatsEmpty() throws Exception {
    Map<String, Long> stats = new HashMap<>();
    stats.put("users", 0L);
    stats.put("jobs", 0L);
    when(adminService.getSystemStats()).thenReturn(stats);
    mockMvc
        .perform(get("/admin/stats"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.users").value(0))
        .andExpect(jsonPath("$.jobs").value(0));
  }

  @Test
  @WithMockUser(roles = "USER")
  void testGetStatsWithUserRole() throws Exception {
    mockMvc.perform(get("/admin/stats")).andExpect(status().isForbidden());
  }
}

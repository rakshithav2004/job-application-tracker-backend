package com.jobtracker.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtracker.dto.JobRequest;
import com.jobtracker.dto.JobResponse;
import com.jobtracker.entity.JobStatus;
import com.jobtracker.exception.ResourceNotFoundException;
import com.jobtracker.service.JobService;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class JobControllerTest {

  @TestConfiguration
  static class JobControllerTestConfig {
    @Bean
    public JobService jobService() {
      return mock(JobService.class);
    }
  }

  @Autowired private MockMvc mockMvc;

  @Autowired private JobService jobService;

  @Autowired private ObjectMapper objectMapper;

  private JobRequest jobRequest;
  private JobResponse jobResponse;
  private String userId;
  private String jobId;

  @BeforeEach
  void setUp() {
    userId = "user123";
    jobId = "job123";

    jobRequest = new JobRequest();
    jobRequest.setCompanyName("TechCorp");
    jobRequest.setRole("Senior Developer");

    jobResponse =
        JobResponse.builder()
            .id(jobId)
            .companyName("TechCorp")
            .role("Senior Developer")
            .status(JobStatus.APPLIED)
            .appliedDate(LocalDateTime.now())
            .lastUpdated(LocalDateTime.now())
            .build();
  }

  @Test
  @WithMockUser(roles = "USER")
  void testCreateJobSuccess() throws Exception {
    when(jobService.createJob(any(JobRequest.class), anyString())).thenReturn(jobResponse);
    mockMvc
        .perform(
            post("/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jobRequest))
                .param("userId", userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.companyName").value("TechCorp"))
        .andExpect(jsonPath("$.role").value("Senior Developer"))
        .andExpect(jsonPath("$.status").value("APPLIED"));
  }

  @Test
  @WithMockUser(roles = "USER")
  void testCreateJobWithInvalidInput() throws Exception {
    JobRequest invalidRequest = new JobRequest();
    mockMvc
        .perform(
            post("/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
                .param("userId", userId))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(roles = "USER")
  void testGetUserJobsSuccess() throws Exception {
    List<JobResponse> jobResponses =
        Arrays.asList(
            jobResponse,
            JobResponse.builder()
                .id("job124")
                .companyName("DataCorp")
                .role("Data Engineer")
                .status(JobStatus.INTERVIEW)
                .appliedDate(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build());
    when(jobService.getJobsByUser(userId)).thenReturn(jobResponses);
    mockMvc
        .perform(get("/jobs/user/{userId}", userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].companyName").value("TechCorp"))
        .andExpect(jsonPath("$[1].companyName").value("DataCorp"));
  }

  @Test
  @WithMockUser(roles = "USER")
  void testGetUserJobsEmpty() throws Exception {
    when(jobService.getJobsByUser(userId)).thenReturn(Arrays.asList());
    mockMvc
        .perform(get("/jobs/user/{userId}", userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @WithMockUser(roles = "USER")
  void testUpdateStatusJobNotFound() throws Exception {
    when(jobService.updateStatus(jobId, JobStatus.INTERVIEW))
        .thenThrow(new ResourceNotFoundException("Job not found"));
    mockMvc
        .perform(put("/jobs/{id}/status", jobId).param("status", "INTERVIEW"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Job not found"));
  }

  @Test
  @WithMockUser(roles = "USER")
  void testDeleteJobSuccess() throws Exception {
    doNothing().when(jobService).deleteJob(jobId);
    mockMvc.perform(delete("/jobs/{id}", jobId)).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(roles = "USER")
  void testSearchByCompanySuccess() throws Exception {
    List<JobResponse> searchResults = Arrays.asList(jobResponse);
    when(jobService.search("Tech")).thenReturn(searchResults);
    mockMvc
        .perform(get("/jobs/search").param("company", "Tech"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].companyName").value("TechCorp"));
  }

  @Test
  @WithMockUser(roles = "USER")
  void testSearchByCompanyNotFound() throws Exception {
    when(jobService.search("NonExistent")).thenReturn(Arrays.asList());
    mockMvc
        .perform(get("/jobs/search").param("company", "NonExistent"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @WithMockUser(roles = "USER")
  void testGetStatsSuccess() throws Exception {
    Map<String, Long> stats = new HashMap<>();
    stats.put("total", 5L);
    stats.put("applied", 2L);
    stats.put("interview", 2L);
    stats.put("rejected", 1L);
    stats.put("offer", 0L);
    when(jobService.getStats(userId)).thenReturn(stats);
    mockMvc
        .perform(get("/jobs/stats/{userId}", userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.total").value(5))
        .andExpect(jsonPath("$.applied").value(2))
        .andExpect(jsonPath("$.interview").value(2))
        .andExpect(jsonPath("$.rejected").value(1))
        .andExpect(jsonPath("$.offer").value(0));
  }

  @Test
  void testJobsEndpointRequiresAuthentication() throws Exception {
    mockMvc.perform(get("/jobs/user/user123")).andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void testJobsEndpointWithAdminRole() throws Exception {
    List<JobResponse> jobResponses = Arrays.asList(jobResponse);
    when(jobService.getJobsByUser(userId)).thenReturn(jobResponses);
    mockMvc
        .perform(get("/jobs/user/{userId}", userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1));
  }
}

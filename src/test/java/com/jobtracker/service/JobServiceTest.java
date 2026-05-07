package com.jobtracker.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.jobtracker.dto.JobRequest;
import com.jobtracker.dto.JobResponse;
import com.jobtracker.entity.JobApplication;
import com.jobtracker.entity.JobStatus;
import com.jobtracker.exception.ResourceNotFoundException;
import com.jobtracker.repository.JobRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

  @Mock private JobRepository jobRepository;

  @InjectMocks private JobService jobService;

  private JobRequest jobRequest;
  private JobApplication jobApplication;
  private String userId;
  private String jobId;

  @BeforeEach
  void setUp() {
    userId = "user123";
    jobId = "job123";

    jobRequest = new JobRequest();
    jobRequest.setCompanyName("TechCorp");
    jobRequest.setRole("Senior Developer");

    jobApplication = new JobApplication();
    jobApplication.setId(jobId);
    jobApplication.setUserId(userId);
    jobApplication.setCompanyName("TechCorp");
    jobApplication.setRole("Senior Developer");
    jobApplication.setStatus(JobStatus.APPLIED);
    jobApplication.setAppliedDate(LocalDateTime.now());
    jobApplication.setLastUpdated(LocalDateTime.now());
  }

  @Test
  void testCreateJobSuccess() {
    when(jobRepository.save(any(JobApplication.class))).thenReturn(jobApplication);
    JobResponse response = jobService.createJob(jobRequest, userId);
    assertNotNull(response);
    assertEquals("TechCorp", response.getCompanyName());
    assertEquals("Senior Developer", response.getRole());
    assertEquals(JobStatus.APPLIED, response.getStatus());
    verify(jobRepository, times(1)).save(any(JobApplication.class));
  }

  @Test
  void testGetJobsByUserSuccess() {
    List<JobApplication> jobs =
        Arrays.asList(
            jobApplication, createAdditionalJob("job124", "AnotherCorp", "Junior Developer"));
    when(jobRepository.findByUserId(userId)).thenReturn(jobs);
    List<JobResponse> responses = jobService.getJobsByUser(userId);
    assertEquals(2, responses.size());
    assertEquals("TechCorp", responses.get(0).getCompanyName());
    assertEquals("AnotherCorp", responses.get(1).getCompanyName());
    verify(jobRepository, times(1)).findByUserId(userId);
  }

  @Test
  void testGetJobsByUserEmpty() {
    when(jobRepository.findByUserId(userId)).thenReturn(Arrays.asList());
    List<JobResponse> responses = jobService.getJobsByUser(userId);
    assertTrue(responses.isEmpty());
    verify(jobRepository, times(1)).findByUserId(userId);
  }

  @Test
  void testUpdateStatusSuccess() {
    JobStatus newStatus = JobStatus.INTERVIEW;
    when(jobRepository.findById(jobId)).thenReturn(Optional.of(jobApplication));
    when(jobRepository.save(any(JobApplication.class))).thenReturn(jobApplication);
    JobResponse response = jobService.updateStatus(jobId, newStatus);
    assertNotNull(response);
    verify(jobRepository, times(1)).findById(jobId);
    verify(jobRepository, times(1)).save(any(JobApplication.class));
  }

  @Test
  void testUpdateStatusJobNotFound() {
    when(jobRepository.findById(jobId)).thenReturn(Optional.empty());
    assertThrows(
        ResourceNotFoundException.class,
        () -> jobService.updateStatus(jobId, JobStatus.INTERVIEW),
        "Job not found");
    verify(jobRepository, times(1)).findById(jobId);
    verify(jobRepository, never()).save(any(JobApplication.class));
  }

  @Test
  void testDeleteJobSuccess() {
    jobService.deleteJob(jobId);
    verify(jobRepository, times(1)).deleteById(jobId);
  }

  @Test
  void testSearchByCompanySuccess() {
    List<JobApplication> jobs = Arrays.asList(jobApplication);
    when(jobRepository.findByCompanyNameContainingIgnoreCase("Tech")).thenReturn(jobs);
    List<JobResponse> responses = jobService.search("Tech");
    assertEquals(1, responses.size());
    assertEquals("TechCorp", responses.get(0).getCompanyName());
    verify(jobRepository, times(1)).findByCompanyNameContainingIgnoreCase("Tech");
  }

  @Test
  void testSearchByCompanyNotFound() {
    when(jobRepository.findByCompanyNameContainingIgnoreCase("NonExistent"))
        .thenReturn(Arrays.asList());
    List<JobResponse> responses = jobService.search("NonExistent");
    assertTrue(responses.isEmpty());
    verify(jobRepository, times(1)).findByCompanyNameContainingIgnoreCase("NonExistent");
  }

  @Test
  void testGetStatsSuccess() {
    JobApplication appliedJob = jobApplication;
    JobApplication interviewJob = createJobWithStatus("job124", JobStatus.INTERVIEW);
    JobApplication rejectedJob = createJobWithStatus("job125", JobStatus.REJECTED);
    JobApplication offerJob = createJobWithStatus("job126", JobStatus.OFFER);

    List<JobApplication> jobs = Arrays.asList(appliedJob, interviewJob, rejectedJob, offerJob);
    when(jobRepository.findByUserId(userId)).thenReturn(jobs);
    Map<String, Long> stats = jobService.getStats(userId);
    assertEquals(4L, stats.get("total"));
    assertEquals(1L, stats.get("applied"));
    assertEquals(1L, stats.get("interview"));
    assertEquals(1L, stats.get("rejected"));
    assertEquals(1L, stats.get("offer"));
    verify(jobRepository, times(1)).findByUserId(userId);
  }

  @Test
  void testGetStatsEmpty() {
    when(jobRepository.findByUserId(userId)).thenReturn(Arrays.asList());
    Map<String, Long> stats = jobService.getStats(userId);
    assertEquals(0L, stats.get("total"));
    assertEquals(0L, stats.get("applied"));
    assertEquals(0L, stats.get("interview"));
    assertEquals(0L, stats.get("rejected"));
    assertEquals(0L, stats.get("offer"));
  }

  private JobApplication createAdditionalJob(String id, String company, String role) {
    JobApplication job = new JobApplication();
    job.setId(id);
    job.setUserId(userId);
    job.setCompanyName(company);
    job.setRole(role);
    job.setStatus(JobStatus.APPLIED);
    job.setAppliedDate(LocalDateTime.now());
    job.setLastUpdated(LocalDateTime.now());
    return job;
  }

  private JobApplication createJobWithStatus(String id, JobStatus status) {
    JobApplication job = new JobApplication();
    job.setId(id);
    job.setUserId(userId);
    job.setCompanyName("TestCorp");
    job.setRole("Test Role");
    job.setStatus(status);
    job.setAppliedDate(LocalDateTime.now());
    job.setLastUpdated(LocalDateTime.now());
    return job;
  }
}

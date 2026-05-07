package com.jobtracker.service;

import com.jobtracker.dto.JobRequest;
import com.jobtracker.dto.JobResponse;
import com.jobtracker.entity.JobApplication;
import com.jobtracker.entity.JobStatus;
import com.jobtracker.exception.ResourceNotFoundException;
import com.jobtracker.repository.JobRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobService {

  private final JobRepository jobRepository;

  public JobResponse createJob(JobRequest request, String userId) {

    JobApplication job = new JobApplication();
    job.setCompanyName(request.getCompanyName());
    job.setRole(request.getRole());
    job.setUserId(userId);
    job.setStatus(JobStatus.APPLIED);
    job.setAppliedDate(LocalDateTime.now());
    job.setLastUpdated(LocalDateTime.now());

    return map(jobRepository.save(job));
  }

  public List<JobResponse> getJobsByUser(String userId) {
    return jobRepository.findByUserId(userId).stream().map(this::map).toList();
  }

  public JobResponse updateStatus(String jobId, JobStatus status) {

    JobApplication job =
        jobRepository
            .findById(jobId)
            .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

    job.setStatus(status);
    job.setLastUpdated(LocalDateTime.now());

    return map(jobRepository.save(job));
  }

  public void deleteJob(String jobId) {
    jobRepository.deleteById(jobId);
  }

  public List<JobResponse> search(String company) {
    return jobRepository.findByCompanyNameContainingIgnoreCase(company).stream()
        .map(this::map)
        .toList();
  }

  public Map<String, Long> getStats(String userId) {
    List<JobApplication> jobs = jobRepository.findByUserId(userId);

    return Map.of(
        "total", (long) jobs.size(),
        "applied", jobs.stream().filter(j -> j.getStatus() == JobStatus.APPLIED).count(),
        "interview", jobs.stream().filter(j -> j.getStatus() == JobStatus.INTERVIEW).count(),
        "rejected", jobs.stream().filter(j -> j.getStatus() == JobStatus.REJECTED).count(),
        "offer", jobs.stream().filter(j -> j.getStatus() == JobStatus.OFFER).count());
  }

  private JobResponse map(JobApplication job) {
    return JobResponse.builder()
        .id(job.getId())
        .companyName(job.getCompanyName())
        .role(job.getRole())
        .status(job.getStatus())
        .appliedDate(job.getAppliedDate())
        .lastUpdated(job.getLastUpdated())
        .build();
  }
}

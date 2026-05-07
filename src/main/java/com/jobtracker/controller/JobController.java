package com.jobtracker.controller;

import com.jobtracker.dto.JobRequest;
import com.jobtracker.dto.JobResponse;
import com.jobtracker.entity.JobStatus;
import com.jobtracker.service.JobService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

  private final JobService jobService;

  @PostMapping
  public JobResponse create(@Valid @RequestBody JobRequest request, @RequestParam String userId) {
    return jobService.createJob(request, userId);
  }

  @GetMapping("/user/{userId}")
  public List<JobResponse> getUserJobs(@PathVariable String userId) {
    return jobService.getJobsByUser(userId);
  }

  @PutMapping("/{id}/status")
  public JobResponse updateStatus(@PathVariable String id, @RequestParam JobStatus status) {
    return jobService.updateStatus(id, status);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable String id) {
    jobService.deleteJob(id);
  }

  @GetMapping("/search")
  public List<JobResponse> search(@RequestParam String company) {
    return jobService.search(company);
  }

  @GetMapping("/stats/{userId}")
  public Map<String, Long> stats(@PathVariable String userId) {
    return jobService.getStats(userId);
  }
}

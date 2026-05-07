package com.jobtracker.service;

import com.jobtracker.entity.JobApplication;
import com.jobtracker.entity.User;
import com.jobtracker.repository.JobRepository;
import com.jobtracker.repository.UserRepository;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

  private final UserRepository userRepository;
  private final JobRepository jobRepository;

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  public List<JobApplication> getAllJobs() {
    return jobRepository.findAll();
  }

  public Map<String, Long> getSystemStats() {
    return Map.of(
        "users", userRepository.count(),
        "jobs", jobRepository.count());
  }
}

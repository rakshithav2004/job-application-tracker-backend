package com.jobtracker.controller;

import com.jobtracker.entity.JobApplication;
import com.jobtracker.entity.User;
import com.jobtracker.service.AdminService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

  private final AdminService adminService;

  @GetMapping("/users")
  public List<User> getUsers() {
    return adminService.getAllUsers();
  }

  @GetMapping("/jobs")
  public List<JobApplication> getJobs() {
    return adminService.getAllJobs();
  }

  @GetMapping("/stats")
  public Map<String, Long> stats() {
    return adminService.getSystemStats();
  }
}

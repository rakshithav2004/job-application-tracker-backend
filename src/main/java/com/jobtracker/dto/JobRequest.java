package com.jobtracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JobRequest {

  @NotBlank(message = "Company name is required")
  private String companyName;

  @NotBlank(message = "Role is required")
  private String role;
}

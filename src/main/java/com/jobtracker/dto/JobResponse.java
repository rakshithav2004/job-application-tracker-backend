package com.jobtracker.dto;

import com.jobtracker.entity.JobStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobResponse {

  private String id;
  private String companyName;
  private String role;
  private JobStatus status;
  private LocalDateTime appliedDate;
  private LocalDateTime lastUpdated;
}

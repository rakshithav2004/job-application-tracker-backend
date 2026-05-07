package com.jobtracker.entity;

import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "jobs")
public class JobApplication {

  @Id private String id;

  private String userId;
  private String companyName;
  private String role;
  private JobStatus status;
  private LocalDateTime appliedDate;
  private LocalDateTime lastUpdated;
}

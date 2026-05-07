package com.jobtracker.repository;

import com.jobtracker.entity.JobApplication;
import com.jobtracker.entity.JobStatus;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JobRepository extends MongoRepository<JobApplication, String> {

  List<JobApplication> findByUserId(String userId);

  List<JobApplication> findByStatus(JobStatus status);

  List<JobApplication> findByCompanyNameContainingIgnoreCase(String companyName);
}

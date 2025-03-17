package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Job;
import com.example.demo.domain.Skill;
import com.example.demo.service.JobService;
import com.example.demo.util.annotation.ApiMessage;
import com.example.demo.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class JobController {

    private final AuthController authController;

    private final JobService jobService;

    public JobController(JobService jobService, AuthController authController) {
        this.jobService = jobService;
        this.authController = authController;
    }

    @PostMapping("/jobs")
    @ApiMessage("create a job")
    public ResponseEntity<Job> createAJob(@Valid @RequestBody Job job) {
        Job newJob = this.jobService.handleSaveJob(job);
        return ResponseEntity.created(null).body(newJob);
    }

    @PutMapping("/jobs")
    @ApiMessage("update a job")
    public ResponseEntity<Job> updateJob(@Valid @RequestBody Job job) throws IdInvalidException {
        boolean isExistJob = this.jobService.existJob(job.getId());
        if (isExistJob) {
            throw new IdInvalidException("Job với id=" + job.getId() + " không tồn tại");
        }
        return ResponseEntity.ok().body(this.jobService.handleUpdateJob(job));
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("delete a job")
    public ResponseEntity<Job> deleteAJob(@Valid @PathVariable("id") Long id) throws IdInvalidException {
        boolean isExistJob = this.jobService.existJob(id);
        if (!isExistJob) {
            throw new IdInvalidException("Job với id=" + id + " không tồn tại");
        }
        this.jobService.handleDeleteJob(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("get a job")
    public ResponseEntity<Job> getAJob(@Valid @PathVariable("id") Long id) throws IdInvalidException {
        boolean isExistJob = this.jobService.existJob(id);
        if (!isExistJob) {
            throw new IdInvalidException("Job với id=" + id + " không tồn tại");
        }
        Optional<Job> newJobOptional = this.jobService.getJobById(id);
        if (newJobOptional.isPresent()) {
            Job newJob = newJobOptional.get();
            return ResponseEntity.created(null).body(newJob);
        }
        return null;
    }

    @GetMapping("/jobs")
    @ApiMessage("get all job")
    public ResponseEntity<List<Job>> fetchAllJob(@Filter Specification<Job> spec,
            Pageable pageable) {
        Page<Job> jobs = this.jobService.getAllJob(spec, pageable);
        List<Job> listJobs = jobs.getContent();
        return ResponseEntity.ok().body(listJobs);
    }
}
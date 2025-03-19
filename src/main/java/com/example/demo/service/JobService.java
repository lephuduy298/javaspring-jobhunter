package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.hibernate.query.named.ResultMappingMementoNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.demo.controller.AuthController;
import com.example.demo.domain.Company;
import com.example.demo.domain.Job;
import com.example.demo.domain.Skill;
import com.example.demo.domain.dto.response.ResultPaginationDTO;
import com.example.demo.domain.dto.response.job.ResCreateJobDTO;
import com.example.demo.domain.dto.response.job.ResUpdateJobDTO;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.repository.JobRepository;

import jakarta.validation.Valid;

@Service
public class JobService {

    private final AuthController authController;

    private final SkillService skillService;

    private final JobRepository jobRepository;

    private final CompanyRepository companyRepository;

    public JobService(SkillService skillService, JobRepository jobRepository, CompanyRepository companyRepository,
            AuthController authController) {
        this.skillService = skillService;
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.authController = authController;
    }

    public ResCreateJobDTO handleSaveJob(Job job) {
        if (job.getSkills() != null) {

            List<Long> skills = job.getSkills()
                    .stream().map(s -> s.getId())
                    .collect(Collectors.toList());

            List<Skill> skillAvailible = this.skillService.getIdIn(skills);
            job.setSkills(skillAvailible);

        }

        this.jobRepository.save(job);

        ResCreateJobDTO dto = new ResCreateJobDTO();
        dto.setId(job.getId());
        dto.setName(job.getName());
        dto.setSalary(job.getSalary());
        dto.setQuantity(job.getQuantity());
        dto.setLocation(job.getLocation());
        dto.setLevel(job.getLevel());
        dto.setStartDate(job.getStartDate());
        dto.setEndDate(job.getEndDate());
        dto.setActive(job.isActive());
        dto.setCreatedAt(job.getCreatedAt());
        dto.setCreatedBy(job.getCreatedBy());

        if (job.getSkills() != null) {
            List<String> skills = job.getSkills()
                    .stream().map(item -> item.getName())
                    .collect(Collectors.toList());

            dto.setSkills(skills);
        }

        // if (job.getCompany() != null) {
        // Optional<Company> comOptional =
        // this.companyRepository.findById(job.getCompany().getId());
        // if (comOptional.isPresent()) {
        // dto.setCompany(comOptional.get());
        // }
        // }

        return dto;
    }

    public ResUpdateJobDTO handleUpdateJob(Job job, Job jobInDB) {
        // TODO Auto-generated method stub
        if (job.getSkills() != null) {

            List<Long> skills = job.getSkills()
                    .stream().map(s -> s.getId())
                    .collect(Collectors.toList());

            List<Skill> skillAvailible = this.skillService.getIdIn(skills);

            jobInDB.setSkills(skillAvailible);

        }

        if (job.getCompany() != null) {
            Optional<Company> comOptional = this.companyRepository.findById(job.getCompany().getId());
            if (comOptional.isPresent()) {
                jobInDB.setCompany(comOptional.get());
            }
        }

        jobInDB.setId(job.getId());
        jobInDB.setName(job.getName());
        jobInDB.setSalary(job.getSalary());
        jobInDB.setQuantity(job.getQuantity());
        jobInDB.setLocation(job.getLocation());
        jobInDB.setLevel(job.getLevel());
        jobInDB.setStartDate(job.getStartDate());
        jobInDB.setEndDate(job.getEndDate());
        jobInDB.setActive(job.isActive());

        Job currentJob = this.jobRepository.save(jobInDB);

        ResUpdateJobDTO dto = new ResUpdateJobDTO();
        // convert response
        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLocation(currentJob.getLocation());
        dto.setLevel(currentJob.getLevel());
        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());
        dto.setActive(currentJob.isActive());
        dto.setUpdatedAt(currentJob.getUpdatedAt());
        dto.setUpdatedBy(currentJob.getUpdatedBy());

        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills()
                    .stream().map(item -> item.getName())
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }
        return dto;
    }

    public Optional<Job> findById(long id) {
        return this.jobRepository.findById(id);
    }

    public void handleDeleteJob(long id) {
        this.jobRepository.deleteById(id);
    }

    public boolean isExistJob(long id) {
        return this.jobRepository.existsById(id);
    }

    public Optional<Job> getJobById(Long id) {
        return this.jobRepository.findById(id);
    }

    public ResultPaginationDTO getAllJob(Specification<Job> spec, Pageable pageable) {
        Page<Job> jobPage = this.jobRepository.findAll(spec, pageable);

        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(jobPage.getNumber() + 1);
        meta.setPageSize(jobPage.getSize());
        meta.setPages(jobPage.getTotalPages());
        meta.setTotal(jobPage.getTotalElements());

        result.setResult(jobPage.getContent());
        result.setMeta(meta);

        return result;
    }

}

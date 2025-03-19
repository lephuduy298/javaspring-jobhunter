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

import com.example.demo.domain.Job;
import com.example.demo.domain.Skill;
import com.example.demo.domain.dto.response.ResultPaginationDTO;
import com.example.demo.domain.dto.response.job.ResCreateJobDTO;
import com.example.demo.domain.dto.response.job.ResUpdateJobDTO;
import com.example.demo.repository.JobRepository;

import jakarta.validation.Valid;

@Service
public class JobService {

    private final SkillService skillService;

    private final JobRepository jobRepository;

    public JobService(SkillService skillService, JobRepository jobRepository) {
        this.skillService = skillService;
        this.jobRepository = jobRepository;
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

        return dto;
    }

    public ResUpdateJobDTO handleUpdateJob(Job job) {
        // TODO Auto-generated method stub
        if (job.getSkills() != null) {

            List<Long> skills = job.getSkills()
                    .stream().map(s -> s.getId())
                    .collect(Collectors.toList());

            List<Skill> skillAvailible = this.skillService.getIdIn(skills);

            job.setSkills(skillAvailible);

        }
        this.jobRepository.save(job);

        ResUpdateJobDTO dto = new ResUpdateJobDTO();
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

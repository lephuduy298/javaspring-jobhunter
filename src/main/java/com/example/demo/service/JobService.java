package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Job;
import com.example.demo.domain.Skill;
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

    public Job handleSaveJob(Job job) {
        // TODO Auto-generated method stub
        if (job.getSkills() != null) {

            List<Skill> listSkills = job.getSkills();

            List<Long> listIds = new ArrayList<>();

            for (Skill skill : listSkills) {
                listIds.add(skill.getId());
            }

            List<Skill> skillAvailible = this.skillService.getIdIn(listIds);

            job.setSkills(skillAvailible);

        }
        return this.jobRepository.save(job);
    }

    public Job handleUpdateJob(Job job) {
        // TODO Auto-generated method stub
        if (job.getSkills() != null) {

            List<Skill> listSkills = job.getSkills();

            List<Long> listIds = new ArrayList<>();

            for (Skill skill : listSkills) {
                listIds.add(skill.getId());
            }

            List<Skill> skillAvailible = this.skillService.getIdIn(listIds);

            job.setSkills(skillAvailible);

        }
        return this.jobRepository.save(job);
    }

    public Optional<Job> findById(long id) {
        // TODO Auto-generated method stub
        return this.jobRepository.findById(id);
    }

    public void handleDeleteJob(long id) {
        // TODO Auto-generated method stub
        this.jobRepository.deleteById(id);
    }

    public boolean existJob(long id) {
        // TODO Auto-generated method stub
        return this.jobRepository.existsById(id);
    }

    public Optional<Job> getJobById(Long id) {
        // TODO Auto-generated method stub
        return this.jobRepository.findById(id);
    }

    public Page<Job> getAllJob(Specification<Job> spec, Pageable pageable) {
        // TODO Auto-generated method stub
        return this.jobRepository.findAll(spec, pageable);
    }

}

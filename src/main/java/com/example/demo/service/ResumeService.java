package com.example.demo.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Job;
import com.example.demo.domain.Resume;
import com.example.demo.domain.dto.response.ResultPaginationDTO;
import com.example.demo.domain.dto.response.resume.ResCreateResumeDTO;
import com.example.demo.domain.dto.response.resume.ResFetchResumeDTO;
import com.example.demo.domain.dto.response.resume.ResUpdateResumeDTO;
import com.example.demo.repository.JobRepository;
import com.example.demo.repository.ResumeRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.SecurityUtil;

import jakarta.validation.Valid;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;

    private final UserRepository userRepository;

    private final JobRepository jobRepository;

    public ResumeService(ResumeRepository resumeRepository, UserRepository userRepository,
            JobRepository jobRepository) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    public void handleSave(Resume resume) {
        this.resumeRepository.save(resume);
    }

    public ResCreateResumeDTO convertToResCreateResumeDTO(Resume resume) {
        ResCreateResumeDTO res = new ResCreateResumeDTO();
        res.setId(resume.getId());
        res.setCreateAt(resume.getCreatedAt());
        res.setCreateBy(resume.getCreatedBy());
        return res;
    }

    public boolean isExistResume(long id) {
        return this.resumeRepository.existsById(id);
    }

    public Optional<Resume> findResumeById(long id) {
        return this.resumeRepository.findById(id);
    }

    public void handleUpdateResume(Resume updateResume) {
        this.resumeRepository.save(updateResume);
    }

    public ResUpdateResumeDTO convertToResUpdateResumeDTO(Resume resume) {
        ResUpdateResumeDTO res = new ResUpdateResumeDTO();
        res.setUpdatedAt(Instant.now());
        res.setUpdatedBy(SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                : "");
        return res;
    }

    public void deleteResume(long id) {
        this.resumeRepository.deleteById(id);
    }

    public ResFetchResumeDTO convertToFetchResumeDTO(Resume resume) {
        ResFetchResumeDTO res = new ResFetchResumeDTO();
        res.setId(resume.getId());
        res.setEmail(resume.getEmail());
        res.setStatus(resume.getStatus());
        res.setUrl(resume.getUrl());
        res.setCreatedAt(resume.getCreatedAt());
        res.setCreatedBy(resume.getCreatedBy());
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());

        ResFetchResumeDTO.ResumeUser resUser = new ResFetchResumeDTO.ResumeUser();
        resUser.setId(resume.getUser().getId());
        resUser.setName(resume.getUser().getName());
        res.setUser(resUser);

        ResFetchResumeDTO.ResumeJob resJob = new ResFetchResumeDTO.ResumeJob();
        resJob.setId(resume.getJob().getId());
        resJob.setName(resume.getJob().getName());
        res.setJob(resJob);

        return res;
    }

    public ResultPaginationDTO getAllResume(Specification<Resume> spec, Pageable pageable) {
        Page<Resume> resumePage = this.resumeRepository.findAll(spec, pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(resumePage.getNumber() + 1);
        meta.setPageSize(resumePage.getSize());
        meta.setPages(resumePage.getTotalPages());
        meta.setTotal(resumePage.getTotalElements());

        result.setMeta(meta);
        result.setResult(resumePage.stream()
                .map(res -> this.convertToFetchResumeDTO(res)).collect(Collectors.toList()).stream());

        return result;
    }

    public boolean checkValidUserAndJob(Resume resume) {
        if (resume.getUser() == null || resume.getJob() == null) {
            return false;
        }

        boolean existUser = this.userRepository.existsById(resume.getUser().getId());

        boolean existJob = this.jobRepository.existsById(resume.getJob().getId());

        if (!existUser || !existJob) {
            return false;
        }

        return true;
    }

}

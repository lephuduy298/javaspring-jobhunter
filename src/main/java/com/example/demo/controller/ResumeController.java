package com.example.demo.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

import com.example.demo.domain.Company;
import com.example.demo.domain.Job;
import com.example.demo.domain.Resume;
import com.example.demo.domain.User;
import com.example.demo.domain.dto.response.ResultPaginationDTO;
import com.example.demo.domain.dto.response.resume.ResCreateResumeDTO;
import com.example.demo.domain.dto.response.resume.ResFetchResumeDTO;
import com.example.demo.domain.dto.response.resume.ResUpdateResumeDTO;
import com.example.demo.repository.ResumeRepository;
import com.example.demo.service.JobService;
import com.example.demo.service.ResumeService;
import com.example.demo.service.UserService;
import com.example.demo.util.SecurityUtil;
import com.example.demo.util.annotation.ApiMessage;
import com.example.demo.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.node.FilterNode;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {

    private final ResumeRepository resumeRepository;

    private final UserService userService;

    private final JobService jobService;

    private final ResumeService resumeService;

    private final FilterSpecificationConverter filterSpecificationConverter;

    private final FilterBuilder filterBuilder;

    public ResumeController(UserService userService, JobService jobService, ResumeService resumeService,
            ResumeRepository resumeRepository, FilterSpecificationConverter filterSpecificationConverter,
            FilterBuilder filterBuilder) {
        this.userService = userService;
        this.jobService = jobService;
        this.resumeService = resumeService;
        this.resumeRepository = resumeRepository;
        this.filterBuilder = filterBuilder;
        this.filterSpecificationConverter = filterSpecificationConverter;
    }

    @PostMapping("/resumes")
    @ApiMessage("create a resume")
    public ResponseEntity<ResCreateResumeDTO> createResume(@Valid @RequestBody Resume resume)
            throws IdInvalidException {
        // check user
        boolean validResume = this.resumeService.checkValidUserAndJob(resume);
        if (!validResume) {
            throw new IdInvalidException("job or user is not exist. Please try again");
        }

        this.resumeService.handleSave(resume);
        return ResponseEntity.created(null).body(this.resumeService.convertToResCreateResumeDTO(resume));
    }

    @PutMapping("/resumes")
    @ApiMessage("update a resume")
    public ResponseEntity<ResUpdateResumeDTO> updateResume(@RequestBody Resume resume)
            throws IdInvalidException {
        // check resume
        boolean existResume = this.resumeService.isExistResume(resume.getId());
        if (!existResume) {
            throw new IdInvalidException("Không tồn tại resume với id=" + resume.getId());
        }

        Optional<Resume> resumeOptional = this.resumeService.findResumeById(resume.getId());

        if (resumeOptional.isPresent()) {
            Resume updateResume = resumeOptional.get();
            updateResume.setStatus(resume.getStatus());
            this.resumeService.handleUpdateResume(updateResume);
            return ResponseEntity.ok().body(this.resumeService.convertToResUpdateResumeDTO(resume));
        }
        return null;
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("delete a resume")
    public void deleteResume(@PathVariable("id") long id)
            throws IdInvalidException {
        // check resume
        boolean existResume = this.resumeService.isExistResume(id);
        if (!existResume) {
            throw new IdInvalidException("Không tồn tại resume với id=" + id);
        }

        this.resumeService.deleteResume(id);
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("fetch a resume")
    public ResponseEntity<ResFetchResumeDTO> fetchResume(@PathVariable("id") long id)
            throws IdInvalidException {
        // check resume
        boolean existResume = this.resumeService.isExistResume(id);
        if (!existResume) {
            throw new IdInvalidException("Không tồn tại resume với id=" + id);
        }

        Optional<Resume> resumeOptional = this.resumeService.findResumeById(id);

        if (resumeOptional.isPresent()) {
            Resume resume = resumeOptional.get();
            return ResponseEntity.ok().body(this.resumeService.convertToFetchResumeDTO(resume));
        }
        return null;
    }

    @GetMapping("/resumes")
    @ApiMessage("fetch all resume")
    public ResponseEntity<ResultPaginationDTO> fetchAllResumeByCompany(
            @Filter Specification<Resume> spec,
            Pageable pageable)
            throws IdInvalidException {
        // fetch all user;
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        User currentUser = this.userService.fetchUserByEmail(email);

        List<Long> listJobIds = null;

        // lấy job id thuộc công ty của người dùng hiện tại (người hr)
        if (currentUser != null) {
            Company currentCom = currentUser.getCompany();
            if (currentCom != null) {
                List<Job> listJobs = currentCom.getJobs();
                if (listJobs != null) {
                    listJobIds = listJobs.stream().map(job -> job.getId())
                            .collect(Collectors.toList());
                }
            }
        }

        FilterNode filterNode = filterBuilder.field("job")
                .in(filterBuilder.input(listJobIds)).get();

        FilterSpecification<Resume> filterSpec = filterSpecificationConverter.convert(filterNode);

        Specification<Resume> finalSpec = filterSpec.and(spec);

        return ResponseEntity.ok().body(this.resumeService.getAllResume(finalSpec, pageable));
    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("get list of resumes by user")
    public ResponseEntity<ResultPaginationDTO> fetchAllResumeByUser(
            Pageable pageable) {
        // fetch all user;
        return ResponseEntity.ok().body(this.resumeService.getResumeByUser(pageable));
    }

}

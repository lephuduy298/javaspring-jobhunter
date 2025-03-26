package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.demo.controller.SkillController;
import com.example.demo.domain.Skill;
import com.example.demo.domain.User;
import com.example.demo.domain.dto.response.ResultPaginationDTO;
import com.example.demo.repository.SkillRepository;

import jakarta.validation.Valid;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill handleSaveSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public Skill handleUpdateSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public boolean existSkill(String name) {
        return this.skillRepository.existsByName(name);
    }

    public List<Skill> getAllSkill() {
        return this.skillRepository.findAll();
    }

    public ResultPaginationDTO fetchAllSkill(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> skills = this.skillRepository.findAll(spec, pageable);

        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(skills.getNumber() + 1);
        meta.setPageSize(skills.getSize());
        meta.setPages(skills.getTotalPages());
        meta.setTotal(skills.getTotalElements());

        result.setResult(skills.getContent());
        result.setMeta(meta);

        return result;
    }

    public List<Skill> getIdIn(List<Long> listIds) {
        return this.skillRepository.findByIdIn(listIds);
    }

    public Optional<Skill> findById(long id) {
        return this.skillRepository.findById(id);
    }

    public boolean existSkillById(Long id) {
        return this.skillRepository.existsById(id);
    }

    public void handleDeleteSkill(Long id) {
        Optional<Skill> skillOptional = this.skillRepository.findById(id);

        // delete job
        Skill currentSkill = skillOptional.get();
        currentSkill.getJobs().forEach(job -> job.getSkills().remove(currentSkill));

        // delete subscriber
        currentSkill.getSubscribers().forEach(sub -> sub.getSkills().remove(currentSkill));

        this.skillRepository.delete(currentSkill);
    }

}

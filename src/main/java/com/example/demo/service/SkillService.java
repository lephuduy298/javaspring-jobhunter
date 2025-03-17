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
import com.example.demo.repository.SkillRepository;

import jakarta.validation.Valid;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill handleSaveSkill(Skill skill) {
        // TODO Auto-generated method stub
        return this.skillRepository.save(skill);
    }

    public Skill handleUpdateSkill(Skill skill) {
        // TODO Auto-generated method stub
        Optional<Skill> updateSkillOptional = this.skillRepository.findById(skill.getId());
        if (updateSkillOptional.isPresent()) {
            Skill updateSkill = updateSkillOptional.get();
            updateSkill.setName(skill.getName());
            return this.skillRepository.save(updateSkill);
        }
        return null;
    }

    public boolean existSkill(String name) {
        // TODO Auto-generated method stub
        return this.skillRepository.existsByName(name);
    }

    public List<Skill> getAllSkill() {
        // TODO Auto-generated method stub
        return this.skillRepository.findAll();
    }

    public Page<Skill> getAllSkill(Specification<Skill> spec, Pageable pageable) {
        // TODO Auto-generated method stub
        return this.skillRepository.findAll(spec, pageable);
    }

    public List<Skill> getIdIn(List<Long> listIds) {
        // TODO Auto-generated method stub
        return this.skillRepository.findByIdIn(listIds);
    }

}

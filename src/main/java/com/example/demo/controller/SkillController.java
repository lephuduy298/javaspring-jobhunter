package com.example.demo.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Skill;
import com.example.demo.domain.User;
import com.example.demo.service.SkillService;
import com.example.demo.util.annotation.ApiMessage;
import com.example.demo.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage("Create a skill")
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody Skill skill) throws IdInvalidException {
        boolean isExistSkill = this.skillService.existSkill(skill.getName());
        if (isExistSkill) {
            throw new IdInvalidException("Skill " + skill.getName() + "đã tồn tại");
        }
        Skill newSkill = this.skillService.handleSaveSkill(skill);
        return ResponseEntity.created(null).body(newSkill);
    }

    @PutMapping("/skills")
    @ApiMessage("Update a skill")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill skill) throws IdInvalidException {
        boolean isExistSkill = this.skillService.existSkill(skill.getName());
        if (isExistSkill) {
            throw new IdInvalidException("Skill " + skill.getName() + "đã tồn tại");
        }
        Skill newSkill = this.skillService.handleUpdateSkill(skill);
        return ResponseEntity.ok().body(newSkill);
    }

    @GetMapping("/skills")
    @ApiMessage("Fetch a skill")
    public ResponseEntity<List<Skill>> fetchAllSkill(@Filter Specification<Skill> spec,
            Pageable pageable) {
        Page<Skill> skills = this.skillService.getAllSkill(spec, pageable);
        List<Skill> listSkill = skills.getContent();
        return ResponseEntity.ok().body(listSkill);
    }
}

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

import com.example.demo.domain.Skill;
import com.example.demo.domain.User;
import com.example.demo.domain.dto.response.ResultPaginationDTO;
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
        Optional<Skill> skillOptional = this.skillService.findById(skill.getId());
        if (skillOptional.isPresent()) {
            Skill newSkill = skillOptional.get();
            boolean isExistSkill = this.skillService.existSkill(skill.getName());
            if (isExistSkill) {
                throw new IdInvalidException("Skill " + skill.getName() + "đã tồn tại");
            }
            newSkill.setName(skill.getName());
            return ResponseEntity.ok().body(this.skillService.handleUpdateSkill(newSkill));
        }
        return null;
    }

    @GetMapping("/skills")
    @ApiMessage("Fetch a skill")
    public ResponseEntity<ResultPaginationDTO> fetchAll(@Filter Specification<Skill> spec,
            Pageable pageable) {

        return ResponseEntity.ok().body(this.skillService.fetchAllSkill(spec, pageable));
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("Delete a skill")
    public ResponseEntity<Skill> deleteASkill(@PathVariable("id") Long id) throws IdInvalidException {
        boolean isExistSkill = this.skillService.existSkillById(id);
        if (!isExistSkill) {
            throw new IdInvalidException("Job với id=" + id + " không tồn tại");
        }
        this.skillService.handleDeleteSkill(id);
        return ResponseEntity.ok().body(null);
    }
}

package com.example.demo.domain.dto.response.job;

import java.time.Instant;
import java.util.List;

import com.example.demo.domain.Company;
import com.example.demo.domain.Skill;
import com.example.demo.util.SecurityUtil;
import com.example.demo.util.constant.LevelEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResCreateJobDTO {
    private long id;
    private String name;
    private String location;
    private Double salary;
    private int quantity;
    private LevelEnum level;

    private Instant startDate;
    private Instant endDate;

    private boolean active;

    private Instant createdAt;
    private String createdBy;

    private List<String> skills;

}

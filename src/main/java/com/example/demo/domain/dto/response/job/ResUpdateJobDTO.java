package com.example.demo.domain.dto.response.job;

import java.time.Instant;
import java.util.List;

import com.example.demo.util.constant.LevelEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResUpdateJobDTO {
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

package com.example.demo.domain.dto.response.resume;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResCreateResumeDTO {
    private long id;
    private Instant createAt;
    private String createBy;
}

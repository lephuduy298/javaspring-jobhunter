package com.example.demo.domain.dto.response.resume;

import java.time.Instant;

import com.example.demo.util.constant.ResumeStatusEnum;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResFetchResumeDTO {
    private long id;
    private String email;
    private String url;

    @Enumerated(EnumType.STRING)
    private ResumeStatusEnum status;
    private Instant createdAt;
    private String createdBy;
    private String updatedBy;
    private Instant updatedAt;
    private ResumeJob job;
    private ResumeUser user;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResumeJob {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResumeUser {
        private long id;
        private String name;
    }
}

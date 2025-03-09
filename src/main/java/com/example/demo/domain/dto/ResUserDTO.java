package com.example.demo.domain.dto;

import java.time.Instant;

import com.example.demo.util.constant.GenderEnum;
import com.nimbusds.jose.crypto.impl.PRFParams;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResUserDTO {
    private long id;
    private String name;
    private String email;
    private int age;
    private GenderEnum gender;
    private String address;
    private Instant createAt;
    private Instant updateAt;
}

package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.error.IdInvalidException;

@RestController
public class HelloController {
    @GetMapping("/")
    public String getHelloPage() throws IdInvalidException {
        if (true)
            throw new IdInvalidException("check valid exception");
        return "Hello World Spring";
    }
}

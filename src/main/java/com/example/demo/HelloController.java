package com.example.demo;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.util.error.IdInvalidException;

// @RestController
// public class HelloController {
//     @GetMapping("/")
//     public String getHelloPage() // throws IdInvalidException
//     {
//         // if (true)
//         // throw new IdInvalidException("check valid exception");
//         return "Hello World Spring";
//     }
// }

@RestController
public class HelloController {
    @GetMapping("/")
    public Map<String, String> getHelloPage() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello World Spring");
        return response;
    }
}

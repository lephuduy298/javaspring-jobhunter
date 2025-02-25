package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.User;
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // @GetMapping("/user/create")
    @PostMapping("/user/create")
    public User createUser(@RequestBody User user) {
        // User user = new User();
        // user.setName("Le Phu Duy");
        // user.setEmail("leduyphucat@gmail.com");
        // user.setPassword("lephuduy");

        User newUser = this.userService.handleSaveUser(user);

        return newUser;
    }
}

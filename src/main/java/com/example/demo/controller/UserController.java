package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Company;
import com.example.demo.domain.User;
import com.example.demo.domain.dto.ResCreateUserDTO;
import com.example.demo.domain.dto.ResUpdateUserDTO;
import com.example.demo.domain.dto.ResUserDTO;
import com.example.demo.domain.dto.ResultPaginationDTO;
import com.example.demo.service.UserService;
import com.example.demo.util.annotation.ApiMessage;
import com.example.demo.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")

public class UserController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // @GetMapping("/users/create")
    @PostMapping("/users")
    public ResponseEntity<ResCreateUserDTO> createUser(@Valid @RequestBody User user) throws IdInvalidException {

        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        List<User> users = this.userService.fetchAllUser();
        boolean isExist = this.userService.isExistEmail(user.getEmail());
        if (isExist) {
            throw new IdInvalidException("Email " + user.getEmail() + " đã tồn tại, vui lòng sử dụng email khác!!!");
        }
        User newUser = this.userService.handleSaveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(newUser));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("delete a user")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        User user = this.userService.fetchUserByID(id);

        if (user == null) {
            throw new IdInvalidException("User với id: " + id + " không tồn tại");
        }
        this.userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body("delete user");
    }

    @GetMapping("/users/{id}")
    @ApiMessage("fetch user by id")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) throws IdInvalidException {
        User user = this.userService.fetchUserByID(id);

        if (user == null) {
            throw new IdInvalidException("User với id: " + id + " không tồn tại");
        }

        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUserDTO(user));
    }

    @GetMapping("/users")
    @ApiMessage("fetch all user")
    public ResponseEntity<ResultPaginationDTO> getAllUsers(
            @Filter Specification<User> spec,
            Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(this.userService.fetchAllUser(spec, pageable));
    }

    @PutMapping("/users")
    @ApiMessage("update user")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User newUser) throws IdInvalidException {
        User user = this.userService.fetchUserByID(newUser.getId());
        if (user == null) {
            throw new IdInvalidException("User với id: " + newUser.getId() + " không tồn tại");
        }
        User updateUser = this.userService.handleUpdateUser(newUser);
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUpdateUserDTO(updateUser));
    }
}

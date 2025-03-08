package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.demo.domain.User;
import com.example.demo.domain.dto.Meta;
import com.example.demo.domain.dto.ResultPaginationDTO;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleSaveUser(User user) {
        return this.userRepository.save(user);
    }

    public void deleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public User fetchUserByID(long id) {
        Optional<User> user = this.userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }

    public ResultPaginationDTO fetchAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> userPages = this.userRepository.findAll(spec, pageable);
        Meta mt = new Meta();
        ResultPaginationDTO rs = new ResultPaginationDTO();

        mt.setPage(userPages.getNumber() + 1);
        mt.setPageSize(userPages.getSize());
        mt.setPages(userPages.getTotalPages());
        mt.setTotal(userPages.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(userPages.getContent());
        return rs;
    }

    public User handleUpdateUser(User newUser) {
        User currentUser = this.fetchUserByID(newUser.getId());
        if (newUser != null) {
            currentUser.setEmail(newUser.getEmail());
            currentUser.setName(newUser.getName());
            currentUser.setPassword(newUser.getPassword());
            this.userRepository.save(currentUser);
        }
        return currentUser;

    }

    public User fetchUserByEmail(String username) {
        // TODO Auto-generated method stub
        return this.userRepository.findByEmail(username);
    }

}

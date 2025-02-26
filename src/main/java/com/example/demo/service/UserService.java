package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.domain.User;
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

    public List<User> fetchAllUser() {
        return this.userRepository.findAll();
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

}

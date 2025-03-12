package com.example.demo.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.demo.domain.User;
import com.example.demo.domain.dto.Meta;
import com.example.demo.domain.dto.ResCreateUserDTO;
import com.example.demo.domain.dto.ResUpdateUserDTO;
import com.example.demo.domain.dto.ResUserDTO;
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

        List<ResUserDTO> listUserDTO = userPages.getContent()
                .stream().map(item -> new ResUserDTO(
                        item.getId(),
                        item.getName(),
                        item.getEmail(),
                        item.getAge(),
                        item.getGender(),
                        item.getAddress(),
                        item.getCreateAt(),
                        item.getUpdateAt()))
                .collect(Collectors.toList());
        rs.setResult(listUserDTO);
        return rs;
    }

    public User handleUpdateUser(User newUser) {
        User currentUser = this.fetchUserByID(newUser.getId());
        if (newUser != null) {
            currentUser.setAddress(newUser.getAddress());
            currentUser.setAge(newUser.getAge());
            currentUser.setGender(newUser.getGender());
            currentUser.setName(newUser.getName());
            this.userRepository.save(currentUser);
        }
        return currentUser;

    }

    public User fetchUserByEmail(String username) {
        // TODO Auto-generated method stub
        return this.userRepository.findByEmail(username);
    }

    public List<User> fetchAllUser() {
        // TODO Auto-generated method stub
        return this.userRepository.findAll();
    }

    public boolean isExistEmail(String email) {
        // TODO Auto-generated method stub
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User newUser) {
        // TODO Auto-generated method stub
        ResCreateUserDTO res = new ResCreateUserDTO();
        res.setId(newUser.getId());
        res.setName(newUser.getName());
        res.setEmail(newUser.getEmail());
        res.setAddress(newUser.getAddress());
        res.setAge(newUser.getAge());
        res.setGender(newUser.getGender());
        res.setCreateAt(newUser.getCreateAt());
        return res;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User newUser) {
        // TODO Auto-generated method stub
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        res.setId(newUser.getId());
        res.setName(newUser.getName());
        res.setEmail(newUser.getEmail());
        res.setAddress(newUser.getAddress());
        res.setAge(newUser.getAge());
        res.setGender(newUser.getGender());
        res.setUpdateAt(newUser.getUpdateAt());
        return res;
    }

    public ResUserDTO convertToResUserDTO(User newUser) {
        ResUserDTO res = new ResUserDTO();
        res.setId(newUser.getId());
        res.setName(newUser.getName());
        res.setEmail(newUser.getEmail());
        res.setAddress(newUser.getAddress());
        res.setAge(newUser.getAge());
        res.setGender(newUser.getGender());
        res.setUpdateAt(newUser.getUpdateAt());
        res.setCreateAt(newUser.getCreateAt());
        return res;
    }

    public void updateUserToken(String token, String email) {
        // TODO Auto-generated method stub
        User currentUser = this.userRepository.findByEmail(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String refresh_token, String email) {
        // TODO Auto-generated method stub
        return this.userRepository.findByRefreshTokenAndEmail(refresh_token, email);
    }

}

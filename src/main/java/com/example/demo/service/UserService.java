package com.example.demo.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Company;
import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.domain.dto.response.ResCreateUserDTO;
import com.example.demo.domain.dto.response.ResUpdateUserDTO;
import com.example.demo.domain.dto.response.ResUserDTO;
import com.example.demo.domain.dto.response.ResultPaginationDTO;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {

    // private final CompanyRepository companyRepository;

    private final UserRepository userRepository;

    private final CompanyService companyService;

    private final RoleService roleService;

    public UserService(UserRepository userRepository, CompanyService companyService, RoleService roleService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.roleService = roleService;
        // this.companyRepository = companyRepository;
    }

    public User handleSaveUser(User user) {
        if (user.getCompany() != null) {
            Optional<Company> company = this.companyService.fetchById(user.getCompany().getId());
            user.setCompany(company.isPresent() ? company.get() : null);
        }

        if (user.getRole() != null) {
            Optional<Role> role = this.roleService.findRoleById(user.getRole().getId());
            user.setRole(role.isPresent() ? role.get() : null);
        }
        return this.userRepository.save(user);
    }

    public void deleteUser(long id) {
        Optional<Company> companyOptional = this.companyService.fetchById(id);

        if (companyOptional.isPresent()) {
            Company com = companyOptional.get();
            List<User> users = this.userRepository.findAllByCompany(com);
            this.userRepository.deleteAll(users);
        }
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
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        ResultPaginationDTO rs = new ResultPaginationDTO();

        mt.setPage(userPages.getNumber() + 1);
        mt.setPageSize(userPages.getSize());
        mt.setPages(userPages.getTotalPages());
        mt.setTotal(userPages.getTotalElements());

        rs.setMeta(mt);

        List<ResUserDTO> listUserDTO = userPages.getContent()
                .stream().map(item -> this.convertToResUserDTO(item))
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

            if (newUser.getCompany() != null) {
                Optional<Company> companyOptional = this.companyService.fetchById(newUser.getCompany().getId());
                currentUser.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
            }

            if (newUser.getRole() != null) {
                Optional<Role> role = this.roleService.findRoleById(newUser.getRole().getId());
                currentUser.setRole(role.isPresent() ? role.get() : null);
            }

            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;

    }

    public User fetchUserByEmail(String username) {
        return this.userRepository.findByEmail(username);
    }

    public List<User> fetchAllUser() {
        return this.userRepository.findAll();
    }

    public boolean isExistEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User newUser) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        ResCreateUserDTO.CompanyUser com = new ResCreateUserDTO.CompanyUser();
        res.setId(newUser.getId());
        res.setName(newUser.getName());
        res.setEmail(newUser.getEmail());
        res.setAddress(newUser.getAddress());
        res.setAge(newUser.getAge());
        res.setGender(newUser.getGender());
        res.setCreatedAt(newUser.getCreatedAt());
        if (newUser.getCompany() != null) {
            com.setId(newUser.getCompany().getId());
            com.setName(newUser.getCompany().getName());
            res.setCompany(com);
        }

        return res;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User newUser) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        ResUpdateUserDTO.CompanyUser com = new ResUpdateUserDTO.CompanyUser();

        res.setId(newUser.getId());
        res.setName(newUser.getName());
        res.setEmail(newUser.getEmail());
        res.setAddress(newUser.getAddress());
        res.setAge(newUser.getAge());
        res.setGender(newUser.getGender());
        res.setUpdatedAt(newUser.getUpdatedAt());
        if (newUser.getCompany() != null) {
            com.setId(newUser.getCompany().getId());
            com.setName(newUser.getCompany().getName());
            res.setCompany(com);
        }
        return res;
    }

    public ResUserDTO convertToResUserDTO(User newUser) {
        ResUserDTO res = new ResUserDTO();
        ResUserDTO.CompanyUser com = new ResUserDTO.CompanyUser();
        ResUserDTO.RoleUser role = new ResUserDTO.RoleUser();

        res.setId(newUser.getId());
        res.setName(newUser.getName());
        res.setEmail(newUser.getEmail());
        res.setAddress(newUser.getAddress());
        res.setAge(newUser.getAge());
        res.setGender(newUser.getGender());
        res.setUpdatedAt(newUser.getUpdatedAt());
        res.setCreatedAt(newUser.getCreatedAt());

        if (newUser.getCompany() != null) {
            com.setId(newUser.getCompany().getId());
            com.setName(newUser.getCompany().getName());
            res.setCompany(com);
        }

        if (newUser.getRole() != null) {
            role.setId(newUser.getRole().getId());
            role.setName(newUser.getRole().getName());
            res.setRole(role);
        }
        return res;
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.userRepository.findByEmail(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String refresh_token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(refresh_token, email);
    }

    // public boolean isExistCompany(long companyId) {
    // return this.companyRepository.existsById(companyId);
    // }

    public boolean isExistUser(long id) {
        return this.userRepository.existsById(id);
    }

}

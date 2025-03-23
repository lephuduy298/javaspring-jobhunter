package com.example.demo.controller;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Permission;
import com.example.demo.domain.Role;
import com.example.demo.domain.dto.response.ResultPaginationDTO;
import com.example.demo.service.RoleService;
import com.example.demo.util.annotation.ApiMessage;
import com.example.demo.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("create a role")
    public ResponseEntity<Role> createARole(@Valid @RequestBody Role role) throws IdInvalidException {

        // kiểm tra tên trùng lặp
        boolean isExistRole = this.roleService.existRoleName(role.getName());

        if (isExistRole) {
            throw new IdInvalidException("Role với tên " + role.getName() + " đã tồn tại");
        }

        return ResponseEntity.created(null).body(this.roleService.handleCreateRole(role));
    }

    @PutMapping("/roles")
    @ApiMessage("update a role")
    public ResponseEntity<Role> updateARole(@Valid @RequestBody Role role) throws IdInvalidException {

        // kiểm tra exist id
        Optional<Role> existRoleById = this.roleService.findRoleById(role.getId());
        if (!existRoleById.isPresent() || existRoleById == null) {
            throw new IdInvalidException("Role với id=" + role.getId() + " không tồn tại");
        }

        // kiểm tra tên trùng lặp
        // boolean existRoleByName = this.roleService.existRoleName(role.getName());

        // if (existRoleByName) {
        // throw new IdInvalidException("Role với tên " + role.getName() + " đã tồn
        // tại");
        // }

        return ResponseEntity.ok().body(this.roleService.handleUpdateRole(role, existRoleById.get()));
    }

    @GetMapping("/roles")
    @ApiMessage("Fetch roles with pageable")
    public ResponseEntity<ResultPaginationDTO> fetchRoleWithPageable(
            @Filter Specification<Role> spec,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.roleService.getAllPermission(spec, pageable));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> deleteARole(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Role> existRoleById = this.roleService.findRoleById(id);
        if (!existRoleById.isPresent() || existRoleById == null) {
            throw new IdInvalidException("Role với id=" + id + " không tồn tại");
        }
        this.roleService.deleteARole(existRoleById.get().getId());
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("Fetch roles by id")
    public ResponseEntity<Role> fetchRoleById(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Role> roleOptional = this.roleService.findRoleById(id);

        if (!roleOptional.isPresent()) {
            throw new IdInvalidException("role với id=" + id + " không tồn tại");
        }
        return ResponseEntity.ok().body(roleOptional.get());
    }
}

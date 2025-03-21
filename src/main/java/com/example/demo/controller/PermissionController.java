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

import com.example.demo.domain.Job;
import com.example.demo.domain.Permission;
import com.example.demo.domain.Role;
import com.example.demo.domain.dto.response.ResultPaginationDTO;
import com.example.demo.service.PermissionService;
import com.example.demo.util.annotation.ApiMessage;
import com.example.demo.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("create a permission")
    public ResponseEntity<Permission> createAPermission(@Valid @RequestBody Permission permission)
            throws IdInvalidException {
        // check exist permission
        boolean existPermission = this.permissionService.isExistByApiPathAndMethodAndModule(
                permission.getApiPath(),
                permission.getMethod(),
                permission.getModule());

        if (existPermission) {
            throw new IdInvalidException("permission đã tồn tại. vui lòng thử lại");
        }

        Permission currentPermission = this.permissionService.handleSavePermission(permission);
        return ResponseEntity.created(null).body(currentPermission);
    }

    @PutMapping("/permissions")
    @ApiMessage("update a permission")
    public ResponseEntity<Permission> updateAPermission(@Valid @RequestBody Permission permission)
            throws IdInvalidException {

        // check id valid
        Optional<Permission> pOptional = this.permissionService.getPermissionById(permission.getId());
        if (pOptional == null) {
            throw new IdInvalidException("Permission với id=" + permission.getId() + " không tồn tại");
        }
        // check exist permission
        boolean existPermission = this.permissionService.isExistByApiPathAndMethodAndModule(
                permission.getApiPath(),
                permission.getMethod(),
                permission.getModule());

        if (existPermission) {
            throw new IdInvalidException("permission đã tồn tại. vui lòng thử lại");
        }

        Permission currentPermission = this.permissionService.handleUpdatePermission(permission, pOptional.get());
        return ResponseEntity.created(null).body(currentPermission);
    }

    @GetMapping("/permissions")
    @ApiMessage("Fetch permission with pageable")
    public ResponseEntity<ResultPaginationDTO> fetchPermission(
            @Filter Specification<Permission> spec,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.permissionService.getAllPermission(spec, pageable));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete a permission")
    public ResponseEntity<Void> deleteAPermission(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Permission> existPermissionById = this.permissionService.getPermissionById(id);
        if (!existPermissionById.isPresent() || existPermissionById == null) {
            throw new IdInvalidException("Permission với id=" + id + " không tồn tại");
        }
        this.permissionService.deleteAPermission(existPermissionById.get());
        return ResponseEntity.ok().body(null);
    }
}

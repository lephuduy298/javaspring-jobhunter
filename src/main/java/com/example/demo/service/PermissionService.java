package com.example.demo.service;

import com.example.demo.domain.Job;
import com.example.demo.domain.Permission;
import com.example.demo.domain.dto.response.ResultPaginationDTO;
import com.example.demo.repository.PermissionRepository;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean isExistByApiPathAndMethodAndModule(String apiPath, String method, String module) {
        return this.permissionRepository.existsByApiPathAndMethodAndModule(apiPath, method, module);
    }

    public Permission handleSavePermission(Permission permission) {
        return this.permissionRepository.save(permission);
    }

    public Optional<Permission> getPermissionById(long id) {

        return this.permissionRepository.findById(id);
    }

    public Permission handleUpdatePermission(Permission permission, Permission permissionDB) {
        permissionDB.setName(permission.getName());
        permissionDB.setApiPath(permission.getApiPath());
        permissionDB.setMethod(permission.getMethod());
        permissionDB.setModule(permission.getModule());
        return this.permissionRepository.save(permissionDB);
    }

    public ResultPaginationDTO getAllPermission(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> permissionPage = this.permissionRepository.findAll(spec, pageable);
        List<Permission> listPermission = permissionPage.getContent();

        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(permissionPage.getNumber() + 1);
        meta.setPageSize(permissionPage.getSize());
        meta.setPages(permissionPage.getTotalPages());
        meta.setTotal(permissionPage.getTotalElements());

        result.setMeta(meta);
        result.setResult(listPermission);

        return result;
    }

    public List<Permission> getIdIn(List<Long> pIds) {
        return this.permissionRepository.findByIdIn(pIds);
    }

    public void deleteAPermission(Permission permission) {
        permission.getRoles().forEach(role -> role.getPermissions().remove(permission));
        this.permissionRepository.deleteById(permission.getId());
    }

    public boolean isExistPermission(Permission permission) {
        return this.permissionRepository.existsByApiPathAndMethodAndModule(
                permission.getApiPath(),
                permission.getMethod(),
                permission.getModule());
    }

}

package com.example.identityService.service;

import com.example.identityService.dto.request.RoleRequest;
import com.example.identityService.dto.response.RoleResponse;
import com.example.identityService.entity.Role;
import com.example.identityService.mapper.RoleMapper;
import com.example.identityService.repository.PermissionRepository;
import com.example.identityService.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    public RoleResponse create(RoleRequest request){
        Role role = roleMapper.toRole(request);
        var permisstions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permisstions));
        role =  roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAll(){
        var role = roleRepository.findAll();
        return role.stream().map(roleMapper::toRoleResponse).toList();
    }

    public void delete(String name){
        roleRepository.deleteById(name);
    }
}

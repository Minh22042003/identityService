package com.example.identityService.mapper;

import com.example.identityService.dto.request.RoleRequest;
import com.example.identityService.dto.response.RoleResponse;
import com.example.identityService.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest roleRequest);

    RoleResponse toRoleResponse(Role role);
}

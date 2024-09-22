package com.example.identityService.mapper;

import com.example.identityService.dto.request.PermissionRequest;
import com.example.identityService.dto.response.PermissionResponse;
import com.example.identityService.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest permissionRequest);
    PermissionResponse toPermissionResponse(Permission permission);
}

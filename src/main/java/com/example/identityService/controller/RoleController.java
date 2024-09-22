package com.example.identityService.controller;

import com.example.identityService.dto.request.ApiResponse;
import com.example.identityService.dto.request.RoleRequest;
import com.example.identityService.dto.response.RoleResponse;
import com.example.identityService.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @PostMapping
    ApiResponse<RoleResponse> create(@RequestBody RoleRequest roleRequest){
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.create(roleRequest))
                .build();
    }

    @GetMapping("/getall")
    ApiResponse<List<RoleResponse>> getAll(){
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAll())
                .build();
    }

    @DeleteMapping("/{roleID}")
    ApiResponse<Void> delete(@PathVariable String roleID){
        roleService.delete(roleID);
        return ApiResponse.<Void>builder().build();
    }
}

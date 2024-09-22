package com.example.identityService.controller;

import com.example.identityService.dto.request.ApiResponse;
import com.example.identityService.dto.request.PermissionRequest;
import com.example.identityService.dto.response.PermissionResponse;
import com.example.identityService.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    ApiResponse<PermissionResponse> create(@RequestBody PermissionRequest permissionRequest){
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.create(permissionRequest))
                .build();
    }

    @GetMapping("/getall")
    ApiResponse<List<PermissionResponse>> getAll(){
        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionService.getAll())
                .build();
    }

    @DeleteMapping("/{permissionID}")
    ApiResponse<Void> delete(@PathVariable String permissionID){
        permissionService.delete(permissionID);
        return ApiResponse.<Void>builder().build();
    }
}

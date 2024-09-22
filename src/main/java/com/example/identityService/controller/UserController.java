package com.example.identityService.controller;

import com.example.identityService.dto.request.ApiResponse;
import com.example.identityService.dto.request.UserCreationRequest;
import com.example.identityService.dto.request.UserUpdateRequest;
import com.example.identityService.dto.response.UserResponse;
import com.example.identityService.entity.User;
import com.example.identityService.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest request){
        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.createUser(request));
        return apiResponse;
    }

    @GetMapping
    ApiResponse<List<UserResponse>>  getUser(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        ApiResponse<List<UserResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.getUser());
        return apiResponse;
    }

    @GetMapping("/{userId}")
    UserResponse getDirectUser(@PathVariable String userId){

        return  userService.getDirectUser(userId);
    }

    @GetMapping("/myInfo")
    UserResponse getMyInfo(){
        return  userService.getMyInfo();
    }

    @PutMapping("/{userId}")
    User updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest userUpdateRequest){
        return userService.updateUser(userId, userUpdateRequest);
    }

    @DeleteMapping("/{userId}")
    String deleteUser(@PathVariable String userId){
        userService.deleteUser(userId);
        return "User have been deleted";
    }
}

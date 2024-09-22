package com.example.identityService.service;

import com.example.identityService.dto.request.UserCreationRequest;
import com.example.identityService.dto.request.UserUpdateRequest;
import com.example.identityService.dto.response.UserResponse;
import com.example.identityService.entity.User;
import com.example.identityService.enums.Role;
import com.example.identityService.exception.AppException;
import com.example.identityService.exception.ErrorCode;
import com.example.identityService.mapper.UserMapper;
import com.example.identityService.repository.RoleRepository;
import com.example.identityService.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    public User createUser(UserCreationRequest request){
        if (userRepository.existsByUserName(request.getUserName())){
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        HashSet<String> roles = new HashSet<>();
        roles.add(Role.User.name());
        //user.setRoles(roles);
        return userRepository.save(user);
    }

    @PreAuthorize("hasAuthority('SCOPE_Admin')")
    public List<UserResponse> getUser(){
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    @PreAuthorize("hasAuthority('SCOPE_Admin')")
    public UserResponse getDirectUser(String userId){
        return userMapper.toUserResponse(userRepository.findById(userId).orElseThrow(() ->
                new AppException(ErrorCode.NOTFOUND_USER))) ;
    }

    public User updateUser(String userId, UserUpdateRequest userUpdateRequest){
        User user = userRepository.findById(userId).orElseThrow(() ->
                new AppException(ErrorCode.NOTFOUND_USER));
        userMapper.updateUser(user, userUpdateRequest);
        user.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
        var role = roleRepository.findAllById(userUpdateRequest.getRoles());
        user.setRoles(new HashSet<>(role));
        return userRepository.save(user);
    }

    public void deleteUser(String userId){
        userRepository.deleteById(userId);
    }

    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        var name = context.getAuthentication().getName();

        var user = userRepository.findByUserName(name).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_EXISTED_BY_USERNAME));
        return userMapper.toUserResponse(user);
    }


}

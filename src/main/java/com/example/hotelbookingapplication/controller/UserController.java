package com.example.hotelbookingapplication.controller;

import com.example.hotelbookingapplication.aop.CheckUserRights;
import com.example.hotelbookingapplication.dto.request.UpsertUserRequest;
import com.example.hotelbookingapplication.dto.response.UserResponse;
import com.example.hotelbookingapplication.mapper.UserMapper;
import com.example.hotelbookingapplication.model.jpa.RoleType;
import com.example.hotelbookingapplication.service.impl.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Validated
public class UserController {

    private final UserService userService;

    private final UserMapper userMapper;

    @ResponseStatus(HttpStatus.OK)
    @CheckUserRights
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/id/{id}")
    public UserResponse findById(@PathVariable Integer id){
         return userMapper.userToResponse(userService.findById(id));
    }

    @ResponseStatus(HttpStatus.OK)
    @CheckUserRights
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/username/{username}")
    public UserResponse findByUsername(@PathVariable(name = "username") String username){
        return userMapper.userToResponse(userService.findByUsername(username));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserResponse save(@RequestParam @Valid RoleType role, @RequestBody @Valid UpsertUserRequest request){
        return userMapper.userToResponse(userService.save(role,userMapper.requestToUser(request)));
    }

    @ResponseStatus(HttpStatus.OK)
    @CheckUserRights
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Integer id,
                               @RequestParam(required = false) RoleType role,
                               @RequestBody @Valid UpsertUserRequest request){
        return userMapper.userToResponse(userService.update(role,userMapper.requestToUser(id,request)));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CheckUserRights
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id){
        userService.deleteById(id);
    }
}

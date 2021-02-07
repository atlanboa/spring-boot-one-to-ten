package com.plee.auth.controller;

import com.plee.auth.domain.User;
import com.plee.auth.dto.UserDto;
import com.plee.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/email")
    public ResponseEntity<UserDto> findUserByEmail(@RequestParam String email) {
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(null).build());
    }

    @GetMapping("/user/id")
    public ResponseEntity<UserDto> findUserById(@RequestParam Long id) {
        User user = userService.get(id);
        return ResponseEntity.ok(UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(null)
                .build());
    }

    @PostMapping("/user")
    public ResponseEntity<UserDto> addUser(@RequestBody UserDto userDto) {
        User user = userService.add(User.builder()
                .id(null)
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .build());
        return ResponseEntity.ok(UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(null)
                .build());
    }
}

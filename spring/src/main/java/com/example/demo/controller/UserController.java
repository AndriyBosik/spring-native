package com.example.demo.controller;

import com.example.demo.dto.PageDto;
import com.example.demo.dto.UserDto;
import com.example.demo.model.PageRequest;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<PageDto<UserDto>> getUsers(PageRequest pageRequest) {
        PageDto<UserDto> pageDto = userService.findAll(pageRequest);
        return ResponseEntity.ok(pageDto);
    }
}

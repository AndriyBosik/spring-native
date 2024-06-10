package com.example.demo.service;

import com.example.demo.dto.PageDto;
import com.example.demo.dto.UserDto;
import com.example.demo.model.PageRequest;

public interface UserService {
    PageDto<UserDto> findAll(PageRequest pageRequest);
}

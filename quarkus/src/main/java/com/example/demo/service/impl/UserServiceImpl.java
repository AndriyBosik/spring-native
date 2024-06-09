package com.example.demo.service.impl;

import com.example.demo.dto.PageDto;
import com.example.demo.dto.UserDto;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.PageRequest;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public PageDto<UserDto> findAll(PageRequest pageRequest) {
        List<UserDto> items = userRepository.findAll(pageRequest).stream()
                .map(userMapper::toDto)
                .toList();
        long totalCount = userRepository.findTotalCount();
        return new PageDto<>(pageRequest, totalCount, items);
    }
}

package com.example.demo.config;

import com.example.demo.dto.ErrorInfoDto;
import com.example.demo.dto.GreetingDto;
import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Configuration;

@Configuration
@RegisterReflectionForBinding({
        UserDto.class,
        ErrorInfoDto.class,
        GreetingDto.class,
        User.class})
public class ReflectionConfig {
}

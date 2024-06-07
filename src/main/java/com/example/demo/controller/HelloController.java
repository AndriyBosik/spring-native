package com.example.demo.controller;

import com.example.demo.dto.GreetingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
@RequiredArgsConstructor
public class HelloController {
    @GetMapping("/{name}")
    public ResponseEntity<GreetingDto> hello(@PathVariable("name") String name) {
        GreetingDto dto = new GreetingDto("Hello, %s!".formatted(name));
        return ResponseEntity.ok(dto);
    }
}

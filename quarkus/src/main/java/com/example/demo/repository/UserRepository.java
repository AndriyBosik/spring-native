package com.example.demo.repository;

import com.example.demo.entity.User;
import com.example.demo.model.PageRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserRepository {
    List<User> findAll(
            @Param("pageRequest") PageRequest pageRequest);

    long findTotalCount();
}

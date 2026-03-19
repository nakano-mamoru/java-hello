package com.example.demo.ex0001;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface Ex0001Mapper {

    List<Ex0001User> findAll();

    Ex0001User findById(Long userId);

    int insert(Ex0001User user);

    int update(Ex0001User user);

    int delete(Long userId);
}
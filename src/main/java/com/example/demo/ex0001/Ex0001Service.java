package com.example.demo.ex0001;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class Ex0001Service {

    private final Ex0001Mapper mapper;

    public Ex0001Service(Ex0001Mapper mapper) {
        this.mapper = mapper;
    }

    public List<Ex0001User> getUsers() {
        return mapper.findAll();
    }

    public Ex0001User getUser(Long id) {
        return mapper.findById(id);
    }

    public void createUser(Ex0001User user) {
        mapper.insert(user);
    }

    public void updateUser(Ex0001User user) {
        mapper.update(user);
    }

    public void deleteUser(Long id) {
        mapper.delete(id);
    }
}
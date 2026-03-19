package com.example.demo.ex0001;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/ex0001/users")
public class Ex0001Controller {

    private final Ex0001Service service;

    public Ex0001Controller(Ex0001Service service) {
        this.service = service;
    }

    @GetMapping
    public List<Ex0001User> list() {
        return service.getUsers();
    }

    @GetMapping("/{id}")
    public Ex0001User get(@PathVariable Long id) {
        return service.getUser(id);
    }

    @PostMapping
    public void create(@RequestBody Ex0001User user) {
        service.createUser(user);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody Ex0001User user) {
        user.setUserId(id);
        service.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteUser(id);
    }
}
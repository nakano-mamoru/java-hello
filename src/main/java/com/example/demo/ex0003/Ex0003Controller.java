package com.example.demo.ex0003;

import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/ex0003/customer")
public class Ex0003Controller {

    @GetMapping
    public String list() {
        return "OK1";
    }

    @PostMapping("/create2")
    public String create(@RequestBody String request) {
        return "OK2"+request;
    }
    @PostMapping("/create")
    public String create(@Valid @RequestBody CustomerRequest request) throws Exception
    {
        // System.out.println("Received request: " + new ObjectMapper().writeValueAsString(request));
        return "OK";
    }
}
package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.VersionInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class VersionController {

    private static VersionInfo cachedVersion;
    static {
        try(var is = VersionController.class.getClassLoader().getResourceAsStream("version.json")) {
            cachedVersion = new ObjectMapper().readValue(is, VersionInfo.class);
        } catch (Exception e) {
            throw new RuntimeException("version.json 読み込み失敗", e);
        }
    }
    @GetMapping("/version")
    public VersionInfo version() {
        return cachedVersion;
    }
}

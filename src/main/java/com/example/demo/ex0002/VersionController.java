package com.example.demo.ex0002;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.VersionInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class VersionController {

    private static final Logger logger = LoggerFactory.getLogger(VersionController.class);

    private static VersionInfo cachedVersion;
    static {
        try(var is = VersionController.class.getClassLoader().getResourceAsStream("version.json")) {
            cachedVersion = new ObjectMapper().readValue(is, VersionInfo.class);
            logger.info("version.json loaded: {}", cachedVersion);
        } catch (Exception e) {
            logger.error("version.json 読み込み失敗", e);
            throw new RuntimeException("version.json 読み込み失敗", e);
        }
    }

    @GetMapping("/version")
    public VersionInfo version() {
        logger.info("GET /version called");
        return cachedVersion;
    }
}

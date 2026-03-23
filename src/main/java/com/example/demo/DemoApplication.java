package com.example.demo;

import com.example.demo.security.UserMaster;
import com.example.demo.security.UserMasterMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

	private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);

	public static void main(String[] args) {
		logger.info("Starting DemoApplication");
		SpringApplication.run(DemoApplication.class, args);
		logger.info("DemoApplication started");
	}

	@Bean
	public CommandLineRunner init(UserMasterMapper mapper) {
		return args -> {
			var admin = mapper.findByUserName("admin");
			if (admin == null) {
				var user = new UserMaster();
				user.setUserId("admin");
				user.setUserName("admin");
				user.setPassword("admin");
				user.setRole("ROLE_ADMIN");
				user.setUserCreatedAt("2026-01-01 00:00:00");
				mapper.insertUser(user);
				logger.info("Created default admin user");
			}
		};
	}
}

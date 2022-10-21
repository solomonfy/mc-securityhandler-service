package com.medochemie.ordermanagement.SecurityHandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SecurityHandler {

	public static void main(String[] args) {
		SpringApplication.run(SecurityHandler.class, args);
	}

	//everytime the app is started, the encoder will be available.
	@Bean
	PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}

}

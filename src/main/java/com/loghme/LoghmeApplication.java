package com.loghme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class LoghmeApplication {
	public static void main(String[] args) {
		SpringApplication.run(LoghmeApplication.class, args);
	}

}

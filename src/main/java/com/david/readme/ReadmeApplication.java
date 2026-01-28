package com.david.readme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@SpringBootApplication
public class ReadmeApplication {
	@RequestMapping("/")
	public String home(){
		return "Hot Reload!";
	}
	public static void main(String[] args) {
		SpringApplication.run(ReadmeApplication.class, args);
	}
}

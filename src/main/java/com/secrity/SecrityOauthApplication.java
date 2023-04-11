package com.secrity;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@MapperScan("com.secrity.mapper")
@SpringBootApplication
public class SecrityOauthApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(SecrityOauthApplication.class, args);
		System.out.println("sdf");
	}

}

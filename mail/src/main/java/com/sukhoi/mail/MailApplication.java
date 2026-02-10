package com.sukhoi.mail;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class MailApplication {

	public static void main(String[] args) {
		SpringApplication.run(MailApplication.class, args);
	}

}

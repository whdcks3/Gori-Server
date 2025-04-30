package com.whdcks3.portfolio.gory_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class GoryServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoryServerApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void init() {
		// if (!roleRepository.findByName(ERole.ROLE_USER).isPresent()) {
		// roleRepository.save(new Role(ERole.ROLE_USER));
		// }
	}
}

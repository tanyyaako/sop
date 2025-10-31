package com.example.roomBooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.hateoas.config.EnableHypermediaSupport;

@SpringBootApplication(
		scanBasePackages = {"com.example.roomBooking", "org.example.hotelbookingapi"},
		exclude = {DataSourceAutoConfiguration.class}
)
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)

public class RoomBookingRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoomBookingRestApplication.class, args);
	}

}

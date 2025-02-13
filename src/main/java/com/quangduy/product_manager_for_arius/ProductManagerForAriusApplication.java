package com.quangduy.product_manager_for_arius;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProductManagerForAriusApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductManagerForAriusApplication.class, args);
	}

}

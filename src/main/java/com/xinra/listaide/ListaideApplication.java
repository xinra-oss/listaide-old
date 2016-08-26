package com.xinra.listaide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages={"com.xinra.listaide"})
@EnableTransactionManagement
public class ListaideApplication {
	
	public static void main(String[] args) {		
		SpringApplication.run(ListaideApplication.class, args);
	}
	
}

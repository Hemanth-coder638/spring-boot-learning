package com.hemanth.DependencyInjectionDemo2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DependencyInjectionDemo2Application implements CommandLineRunner {
    @Autowired
    CakeBaker cakeBaker;
    public void run(String...args){
        cakeBaker.bakeCake();
    }

	public static void main(String[] args) {
		SpringApplication.run(DependencyInjectionDemo2Application.class, args);
	}

}

package org.parcial_1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        System.out.println("App running");
    }
}
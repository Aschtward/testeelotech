package com.elotech.teste;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TesteApplication {

    public static void main(String[] args) {
        SpringApplication.run(TesteApplication.class, args);
    }

}

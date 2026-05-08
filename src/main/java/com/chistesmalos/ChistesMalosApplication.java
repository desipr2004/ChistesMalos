package com.chistesmalos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Clase principal de la aplicación ChistesMalos.
 * Punto de entrada para Spring Boot.
 */
@SpringBootApplication
@EnableCaching
@EnableScheduling
public class ChistesMalosApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChistesMalosApplication.class, args);
    }

}

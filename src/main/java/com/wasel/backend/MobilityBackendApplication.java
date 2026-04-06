package com.wasel.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;


@SpringBootApplication
@EnableCaching
public class MobilityBackendApplication {

    public static void main(String[] args) {

        SpringApplication.run(MobilityBackendApplication.class, args);


    }
}
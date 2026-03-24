package com.wasel.backend;

import com.wasel.backend.model.User;
import com.wasel.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;


@SpringBootApplication
public class MobilityBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MobilityBackendApplication.class, args);
    }

    }

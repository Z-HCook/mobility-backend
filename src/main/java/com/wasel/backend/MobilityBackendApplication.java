package com.wasel.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@SpringBootApplication
public class MobilityBackendApplication {

    public static void main(String[] args) {

        SpringApplication.run(MobilityBackendApplication.class, args);

        try {

            Connection conn = DriverManager.getConnection(
                    "jdbc:postgresql://aws-1-ap-northeast-2.pooler.supabase.com:6543/postgres",
                    "postgres.briotpjealadhecdcuah",
                    "12219461@kmdz"
            );

            Statement stmt = conn.createStatement();

            stmt.executeUpdate("INSERT INTO \"user\" (id) VALUES (44)");

            System.out.println("Insert successful!");

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
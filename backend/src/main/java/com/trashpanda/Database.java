package com.trashpanda;

import java.nio.file.*;
import java.sql.*;
import java.util.stream.Collectors;

//javac -cp ".;C:\Users\chris\Downloads\postgresql-42.7.5.jar" Database.java
//java -cp ".;C:\Users\chris\Downloads\postgresql-42.7.5.jar" Database

public class Database {

    public static void initializeDatabase() {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "password";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = Files.lines(Paths.get("src/main/java/com/trashpanda/sql/init.sql"))
                    .collect(Collectors.joining("\n"));

            for (String statement : sql.split(";")) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty()) {
                    try (PreparedStatement ps = conn.prepareStatement(trimmed)) {
                        ps.execute();
                    }
                }
            }

            System.out.println("SQL file executed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        initializeDatabase();
    }
}

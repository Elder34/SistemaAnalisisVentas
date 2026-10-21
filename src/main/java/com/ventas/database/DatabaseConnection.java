
package com.ventas.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
        "jdbc:postgresql://localhost:5432/sistema_analisis_ventas";

    private static final String USER = "postgres";

    private static final String PASSWORD = 
        System.getenv("DB_PASSWORD");

    public static Connection getConnection()
            throws SQLException {

        if (PASSWORD == null || PASSWORD.isBlank()) {
            throw new SQLException(
                "No se configuro la variable DB_PASSWORD"
            );
        }

        return DriverManager.getConnection(
            URL, USER, PASSWORD
        );
    }
}


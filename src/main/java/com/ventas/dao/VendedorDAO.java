package com.ventas.dao;

import com.ventas.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VendedorDAO {

    public int obtenerOCrearVendedor(
            Connection conexion,
            String nombre) throws SQLException {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del vendedor es obligatorio"
            );
        }

        String sql = """
        INSERT INTO "Vendedores"
            (codigo_vendedor, nombre_vendedor)
        VALUES (?, ?)
        ON CONFLICT (codigo_vendedor)
        DO UPDATE SET
            nombre_vendedor = EXCLUDED.nombre_vendedor
        RETURNING id_vendedor
        """;

        try (PreparedStatement sentencia
                = conexion.prepareStatement(sql)) {

            sentencia.setString(1, nombre);
            sentencia.setString(2, nombre);

            try (ResultSet resultado
                    = sentencia.executeQuery()) {

                if (resultado.next()) {
                    return resultado.getInt("id_vendedor");
                }
            }
        }

        throw new SQLException(
                "No se pudo obtener el ID del vendedor"
        );
    }

}

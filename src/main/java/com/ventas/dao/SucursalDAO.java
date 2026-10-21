package com.ventas.dao;

import com.ventas.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SucursalDAO {

    public int obtenerOCrearSucursal(
            Connection conexion,
            String nombre) throws SQLException {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre de la sucursal es obligatorio"
            );
        }

        String sql = """
        INSERT INTO "Sucursales" (nombre_sucursal)
        VALUES (?)
        ON CONFLICT (nombre_sucursal)
        DO UPDATE SET
            nombre_sucursal = EXCLUDED.nombre_sucursal
        RETURNING id_sucursal
        """;

        try (PreparedStatement sentencia
                = conexion.prepareStatement(sql)) {

            sentencia.setString(1, nombre);

            try (ResultSet resultado
                    = sentencia.executeQuery()) {

                if (resultado.next()) {
                    return resultado.getInt("id_sucursal");
                }
            }
        }

        throw new SQLException(
                "No se pudo obtener el ID de la sucursal"
        );
    }

}

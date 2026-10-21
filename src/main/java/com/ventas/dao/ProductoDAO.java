
package com.ventas.dao;

import com.ventas.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProductoDAO {

   
public void guardarProducto(
        Connection conexion,
        String codigo,
        String nombre) throws SQLException {

    if (codigo == null || codigo.isBlank()
            || nombre == null || nombre.isBlank()) {
        throw new IllegalArgumentException(
            "El codigo y nombre son obligatorios"
        );
    }

    String sql = """
        INSERT INTO "Productos"
            (codigo_producto, nombre_producto)
        VALUES (?, ?)
        ON CONFLICT (codigo_producto)
        DO NOTHING
        """;

    try (PreparedStatement sentencia =
            conexion.prepareStatement(sql)) {

        sentencia.setString(1, codigo);
        sentencia.setString(2, nombre);

        sentencia.executeUpdate();
    }
}

}


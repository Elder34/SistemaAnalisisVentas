package com.ventas.dao;

import com.ventas.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ArchivoProcesadoDAO {

    public int registrarArchivo(
            String nombreArchivo,
            String tipoArchivo) throws SQLException {

        String sql = """
            INSERT INTO "ArchivosProcesados"
                (nombre_archivo, tipo_archivo,
                 registros_recibidos,
                 registros_aceptados,
                 registros_rechazados,
                 registros_duplicados)
            VALUES (?, ?, 0, 0, 0, 0)
            RETURNING id_archivo
            """;

        try (Connection conexion
                = DatabaseConnection.getConnection(); PreparedStatement sentencia
                = conexion.prepareStatement(sql)) {

            sentencia.setString(1, nombreArchivo);
            sentencia.setString(2, tipoArchivo);

            try (ResultSet resultado
                    = sentencia.executeQuery()) {

                if (resultado.next()) {
                    return resultado.getInt("id_archivo");
                }
            }
        }

        throw new SQLException(
                "No se pudo registrar el archivo"
        );
    }

    public void actualizarEstadisticas(
            int idArchivo,
            int recibidos,
            int aceptados,
            int rechazados,
            int duplicados) throws SQLException {

        String sql = """
            UPDATE "ArchivosProcesados"
            SET registros_recibidos = ?,
                registros_aceptados = ?,
                registros_rechazados = ?,
                registros_duplicados = ?
            WHERE id_archivo = ?
            """;

        try (Connection conexion
                = DatabaseConnection.getConnection(); PreparedStatement sentencia
                = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, recibidos);
            sentencia.setInt(2, aceptados);
            sentencia.setInt(3, rechazados);
            sentencia.setInt(4, duplicados);
            sentencia.setInt(5, idArchivo);

            int filas = sentencia.executeUpdate();

            if (filas != 1) {
                throw new SQLException(
                        "No se encontro el archivo con ID: "
                        + idArchivo
                );
            }
        }
    }
}

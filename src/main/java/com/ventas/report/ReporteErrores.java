package com.ventas.report;

import com.ventas.database.DatabaseConnection;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReporteErrores {

    public void generarReporte(Path ruta)
            throws SQLException, IOException {

        String sql = """
            SELECT
                a.nombre_archivo,
                e.numero_registro,
                e.id_venta_origen,
                e.tipo_error,
                e.motivo_rechazo,
                e.datos_originales
            FROM "ErroresProcesamiento" e
            INNER JOIN "ArchivosProcesados" a
                ON e.id_archivo = a.id_archivo
            ORDER BY e.id_error
            """;

        try (Connection conexion
                = DatabaseConnection.getConnection(); PreparedStatement sentencia
                = conexion.prepareStatement(sql); ResultSet resultado
                = sentencia.executeQuery(); BufferedWriter escritor
                = Files.newBufferedWriter(
                        ruta,
                        StandardCharsets.UTF_8
                )) {

            escritor.write(
                    "REPORTE DE ERRORES DE PROCESAMIENTO"
            );
            escritor.newLine();

            escritor.write(
                    "================================"
            );
            escritor.newLine();
            escritor.newLine();

            int contador = 0;

            while (resultado.next()) {

                contador++;

                escritor.write(
                        "ERROR #" + contador
                );
                escritor.newLine();

                escritor.write(
                        "Archivo: "
                        + resultado.getString(
                                "nombre_archivo"
                        )
                );
                escritor.newLine();

                escritor.write(
                        "Registro: "
                        + resultado.getInt(
                                "numero_registro"
                        )
                );
                escritor.newLine();

                escritor.write(
                        "ID venta: "
                        + resultado.getString(
                                "id_venta_origen"
                        )
                );
                escritor.newLine();

                escritor.write(
                        "Tipo: "
                        + resultado.getString(
                                "tipo_error"
                        )
                );
                escritor.newLine();

                escritor.write(
                        "Motivo: "
                        + resultado.getString(
                                "motivo_rechazo"
                        )
                );
                escritor.newLine();

                escritor.write(
                        "Datos: "
                        + resultado.getString(
                                "datos_originales"
                        )
                );
                escritor.newLine();

                escritor.write(
                        "--------------------------------"
                );
                escritor.newLine();
                escritor.newLine();
            }

            escritor.write(
                    "TOTAL DE ERRORES: " + contador
            );

            System.out.println(
                    "Reporte de errores generado: "
                    + ruta.toAbsolutePath()
            );
        }
    }
}

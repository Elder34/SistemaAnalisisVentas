package com.ventas.dao;

import com.ventas.database.DatabaseConnection;
import com.ventas.model.VentaRaw;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class ErrorProcesamientoDAO {

    public void registrarError(
            int idArchivo,
            VentaRaw venta,
            String tipoError,
            String motivo)
            throws SQLException {

        String sql = """
            INSERT INTO "ErroresProcesamiento"
                (id_archivo,
                 id_venta_origen,
                 numero_registro,
                 sucursal_origen,
                 tipo_error,
                 motivo_rechazo,
                 datos_originales)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conexion
                = DatabaseConnection.getConnection(); PreparedStatement sentencia
                = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idArchivo);

            if (venta != null) {

                // Error asociado a una venta individual
                sentencia.setString(
                        2, venta.getIdVenta()
                );

                sentencia.setInt(
                        3, venta.getNumeroRegistro()
                );

                sentencia.setString(
                        4, venta.getSucursal()
                );

                sentencia.setString(5, tipoError);
                sentencia.setString(6, motivo);

                sentencia.setString(
                        7, describirVenta(venta)
                );

            } else {

                // Error de lectura de un archivo completo
                sentencia.setNull(2, Types.VARCHAR);
                sentencia.setNull(3, Types.INTEGER);
                sentencia.setNull(4, Types.VARCHAR);

                sentencia.setString(5, tipoError);
                sentencia.setString(6, motivo);

                sentencia.setNull(7, Types.VARCHAR);
            }

            sentencia.executeUpdate();
        }
    }

    private String describirVenta(VentaRaw venta) {

        return "id_venta=" + venta.getIdVenta()
                + "; fecha=" + venta.getFecha()
                + "; sucursal=" + venta.getSucursal()
                + "; codigo_producto="
                + venta.getCodigoProducto()
                + "; producto=" + venta.getProducto()
                + "; cantidad=" + venta.getCantidad()
                + "; precio_unitario="
                + venta.getPrecioUnitario()
                + "; vendedor=" + venta.getVendedor();
    }
}

package com.ventas.dao;

import com.ventas.database.DatabaseConnection;
import com.ventas.model.Venta;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class VentaDAO {

    public boolean guardarVenta(
            Connection conexion,
            Venta venta) throws SQLException {

        String sql = """
        INSERT INTO "Ventas"
            (id_venta, fecha, id_sucursal,
             codigo_producto, cantidad,
             precio_unitario, monto_total,
             id_vendedor, id_archivo)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT (id_venta) DO NOTHING
        """;

        try (PreparedStatement sentencia
                = conexion.prepareStatement(sql)) {

            sentencia.setString(1, venta.getIdVenta());

            sentencia.setDate(
                    2,
                    java.sql.Date.valueOf(venta.getFecha())
            );

            sentencia.setInt(3, venta.getIdSucursal());
            sentencia.setString(4, venta.getCodigoProducto());
            sentencia.setInt(5, venta.getCantidad());
            sentencia.setBigDecimal(6, venta.getPrecioUnitario());
            sentencia.setBigDecimal(7, venta.calcularMontoTotal());
            sentencia.setInt(8, venta.getIdVendedor());
            sentencia.setInt(9, venta.getIdArchivo());

            return sentencia.executeUpdate() == 1;
        }
    }

    public boolean existeVenta(
            Connection conexion,
            String idVenta) throws SQLException {

        String sql = """
        SELECT 1
        FROM "Ventas"
        WHERE id_venta = ?
        """;

        try (PreparedStatement sentencia
                = conexion.prepareStatement(sql)) {

            sentencia.setString(1, idVenta);

            try (ResultSet resultado
                    = sentencia.executeQuery()) {

                return resultado.next();
            }
        }
    }

}

package com.ventas.service;

import com.ventas.dao.ProductoDAO;
import com.ventas.dao.SucursalDAO;
import com.ventas.dao.VendedorDAO;
import com.ventas.dao.VentaDAO;
import com.ventas.database.DatabaseConnection;
import com.ventas.model.Venta;
import com.ventas.model.VentaRaw;
import com.ventas.processor.ResultadoVenta;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class VentaService {

    private final SucursalDAO sucursalDAO
            = new SucursalDAO();

    private final ProductoDAO productoDAO
            = new ProductoDAO();

    private final VendedorDAO vendedorDAO
            = new VendedorDAO();

    private final VentaDAO ventaDAO
            = new VentaDAO();

    public ResultadoVenta procesarVenta(
            VentaRaw raw,
            int idArchivo) throws SQLException {

        // Se espera una venta previamente normalizada
        // y validada por VentaValidator.
        try (Connection conexion
                = DatabaseConnection.getConnection()) {

            conexion.setAutoCommit(false);

            try {

                if (ventaDAO.existeVenta(
                        conexion, raw.getIdVenta())) {

                    conexion.rollback();
                    return ResultadoVenta.DUPLICADA;
                }

                int idSucursal
                        = sucursalDAO.obtenerOCrearSucursal(
                                conexion,
                                raw.getSucursal()
                        );

                productoDAO.guardarProducto(
                        conexion,
                        raw.getCodigoProducto(),
                        raw.getProducto()
                );

                int idVendedor
                        = vendedorDAO.obtenerOCrearVendedor(
                                conexion,
                                raw.getVendedor()
                        );

                Venta venta = new Venta(
                        raw.getIdVenta(),
                        LocalDate.parse(raw.getFecha()),
                        idSucursal,
                        raw.getCodigoProducto(),
                        Integer.parseInt(raw.getCantidad()),
                        new BigDecimal(raw.getPrecioUnitario()),
                        idVendedor,
                        idArchivo
                );

                boolean insertada
                        = ventaDAO.guardarVenta(
                                conexion, venta
                        );

                if (!insertada) {
                    conexion.rollback();
                    return ResultadoVenta.DUPLICADA;
                }

                conexion.commit();

                return ResultadoVenta.ACEPTADA;

            } catch (SQLException | RuntimeException e) {

                try {
                    conexion.rollback();
                } catch (SQLException errorRollback) {
                    e.addSuppressed(errorRollback);
                }

                throw e;
            }
        }
    }
}

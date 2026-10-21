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

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ReporteAnalisis {

    public void generarReporte(Path ruta)
            throws SQLException, IOException {

        String sql = """
        SELECT
            COUNT(*) AS total_ventas,
            COALESCE(SUM(monto_total), 0)
                AS ingresos_totales,
            COALESCE(AVG(monto_total), 0)
                AS promedio_venta,
            COALESCE(SUM(cantidad), 0)
                AS unidades_vendidas
        FROM "Ventas"
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
                    "REPORTE DE ANALISIS DE VENTAS"
            );
            escritor.newLine();

            escritor.write(
                    "============================"
            );
            escritor.newLine();
            escritor.newLine();

            if (resultado.next()) {

                long totalVentas
                        = resultado.getLong("total_ventas");

                BigDecimal ingresos
                        = resultado.getBigDecimal(
                                "ingresos_totales"
                        );

                BigDecimal promedio
                        = resultado.getBigDecimal(
                                "promedio_venta"
                        );

                long unidades
                        = resultado.getLong(
                                "unidades_vendidas"
                        );

                escritor.write(
                        "1. ESTADISTICAS GENERALES"
                );
                escritor.newLine();

                escritor.write(
                        "------------------------"
                );
                escritor.newLine();

                escritor.write(
                        "Total de ventas: " + totalVentas
                );
                escritor.newLine();

                escritor.write(
                        "Unidades vendidas: " + unidades
                );
                escritor.newLine();

                escritor.write(
                        "Ingresos totales: Q"
                        + ingresos.setScale(
                                2,
                                RoundingMode.HALF_UP
                        )
                );
                escritor.newLine();

                escritor.write(
                        "Promedio por venta: Q"
                        + promedio.setScale(
                                2,
                                RoundingMode.HALF_UP
                        )
                );
                escritor.newLine();
            }

            // 2. Ventas por sucursal
            escribirVentasPorSucursal(
                    conexion,
                    escritor
            );

            // 3. Ventas por producto
            escribirVentasPorProducto(
                    conexion,
                    escritor
            );

            // 4. Ventas por vendedor
            escribirVentasPorVendedor(
                    conexion,
                    escritor
            );

            // 5. Top 3 vendedores
            escribirTopVendedores(
                    conexion,
                    escritor
            );

            // 6. Ventas por mes
            escribirVentasPorMes(
                    conexion,
                    escritor
            );

            // 7. Ventas de mayor y menor monto
            escribirVentasExtremas(
                    conexion,
                    escritor
            );

            // 8. Meses con mayores y menores ingresos
            escribirMesesExtremos(
                    conexion,
                    escritor
            );
            //9. Archivos procesados
            escribirEstadisticasProcesamiento(
                    conexion,
                    escritor
            );
            //Conclusiones de analisis 
            escribirConclusiones(
                    conexion,
                    escritor
            );

            System.out.println(
                    "Reporte de analisis generado: "
                    + ruta.toAbsolutePath()
            );
        }
    }

    private void escribirVentasPorSucursal(
            Connection conexion,
            BufferedWriter escritor)
            throws SQLException, IOException {

        String sql = """
            SELECT
                s.nombre_sucursal,
                COUNT(v.id_venta) AS total_ventas,
                COALESCE(SUM(v.cantidad), 0)
                    AS unidades_vendidas,
                COALESCE(SUM(v.monto_total), 0)
                    AS ingresos_totales,
                COALESCE(AVG(v.monto_total), 0)
                    AS promedio_venta
            FROM "Sucursales" s
            INNER JOIN "Ventas" v
                ON s.id_sucursal = v.id_sucursal
            GROUP BY
                s.id_sucursal,
                s.nombre_sucursal
            ORDER BY ingresos_totales DESC,
                     s.nombre_sucursal ASC
            """;

        escritor.newLine();
        escritor.newLine();

        escritor.write("2. VENTAS POR SUCURSAL");
        escritor.newLine();

        escritor.write("---------------------");
        escritor.newLine();

        try (PreparedStatement sentencia
                = conexion.prepareStatement(sql); ResultSet resultado
                = sentencia.executeQuery()) {

            boolean hayDatos = false;

            while (resultado.next()) {

                hayDatos = true;

                String sucursal
                        = resultado.getString(
                                "nombre_sucursal"
                        );

                long ventas
                        = resultado.getLong("total_ventas");

                long unidades
                        = resultado.getLong(
                                "unidades_vendidas"
                        );

                BigDecimal ingresos
                        = resultado.getBigDecimal(
                                "ingresos_totales"
                        );

                BigDecimal promedio
                        = resultado.getBigDecimal(
                                "promedio_venta"
                        );

                escritor.newLine();

                escritor.write(
                        "Sucursal: " + sucursal
                );
                escritor.newLine();

                escritor.write(
                        "Ventas: " + ventas
                );
                escritor.newLine();

                escritor.write(
                        "Unidades vendidas: " + unidades
                );
                escritor.newLine();

                escritor.write(
                        "Ingresos: Q"
                        + ingresos.setScale(
                                2,
                                RoundingMode.HALF_UP
                        )
                );
                escritor.newLine();

                escritor.write(
                        "Promedio por venta: Q"
                        + promedio.setScale(
                                2,
                                RoundingMode.HALF_UP
                        )
                );
                escritor.newLine();
            }

            if (!hayDatos) {
                escritor.write(
                        "No hay ventas registradas por sucursal."
                );
                escritor.newLine();
            }
        }
    }

    private void escribirVentasPorProducto(
            Connection conexion,
            BufferedWriter escritor)
            throws SQLException, IOException {

        String sql = """
            SELECT
                p.codigo_producto,
                p.nombre_producto,
                COUNT(v.id_venta) AS total_ventas,
                COALESCE(SUM(v.cantidad), 0)
                    AS unidades_vendidas,
                COALESCE(SUM(v.monto_total), 0)
                    AS ingresos_totales,
                COALESCE(AVG(v.precio_unitario), 0)
                    AS precio_promedio
            FROM "Productos" p
            INNER JOIN "Ventas" v
                ON p.codigo_producto = v.codigo_producto
            GROUP BY
                p.codigo_producto,
                p.nombre_producto
            ORDER BY ingresos_totales DESC,
                     p.nombre_producto ASC
            """;

        escritor.newLine();
        escritor.newLine();

        escritor.write("3. VENTAS POR PRODUCTO");
        escritor.newLine();

        escritor.write("---------------------");
        escritor.newLine();

        try (PreparedStatement sentencia
                = conexion.prepareStatement(sql); ResultSet resultado
                = sentencia.executeQuery()) {

            boolean hayDatos = false;

            while (resultado.next()) {

                hayDatos = true;

                String codigo
                        = resultado.getString(
                                "codigo_producto"
                        );

                String nombre
                        = resultado.getString(
                                "nombre_producto"
                        );

                long ventas
                        = resultado.getLong(
                                "total_ventas"
                        );

                long unidades
                        = resultado.getLong(
                                "unidades_vendidas"
                        );

                BigDecimal ingresos
                        = resultado.getBigDecimal(
                                "ingresos_totales"
                        );

                BigDecimal precioPromedio
                        = resultado.getBigDecimal(
                                "precio_promedio"
                        );

                escritor.newLine();

                escritor.write(
                        "Codigo: " + codigo
                );
                escritor.newLine();

                escritor.write(
                        "Producto: " + nombre
                );
                escritor.newLine();

                escritor.write(
                        "Ventas realizadas: " + ventas
                );
                escritor.newLine();

                escritor.write(
                        "Unidades vendidas: " + unidades
                );
                escritor.newLine();

                escritor.write(
                        "Ingresos generados: Q"
                        + ingresos.setScale(
                                2,
                                RoundingMode.HALF_UP
                        )
                );
                escritor.newLine();

                escritor.write(
                        "Precio promedio: Q"
                        + precioPromedio.setScale(
                                2,
                                RoundingMode.HALF_UP
                        )
                );
                escritor.newLine();
            }

            if (!hayDatos) {
                escritor.write(
                        "No hay ventas registradas por producto."
                );
                escritor.newLine();
            }
        }
    }

    private void escribirVentasPorVendedor(
            Connection conexion,
            BufferedWriter escritor)
            throws SQLException, IOException {

        String sql = """
        SELECT
            ve.nombre_vendedor,
            COUNT(v.id_venta) AS total_ventas,
            COALESCE(SUM(v.cantidad), 0)
                AS unidades_vendidas,
            COALESCE(SUM(v.monto_total), 0)
                AS ingresos_totales,
            COALESCE(AVG(v.monto_total), 0)
                AS promedio_venta
        FROM "Vendedores" ve
        INNER JOIN "Ventas" v
            ON ve.id_vendedor = v.id_vendedor
        GROUP BY
            ve.id_vendedor,
            ve.nombre_vendedor
        ORDER BY ingresos_totales DESC,
                 ve.nombre_vendedor ASC
        """;

        escritor.newLine();
        escritor.newLine();

        escritor.write("4. VENTAS POR VENDEDOR");
        escritor.newLine();
        escritor.write("---------------------");
        escritor.newLine();

        try (PreparedStatement sentencia
                = conexion.prepareStatement(sql); ResultSet resultado
                = sentencia.executeQuery()) {

            boolean hayDatos = false;

            while (resultado.next()) {

                hayDatos = true;

                String vendedor
                        = resultado.getString("nombre_vendedor");

                long ventas
                        = resultado.getLong("total_ventas");

                long unidades
                        = resultado.getLong("unidades_vendidas");

                BigDecimal ingresos
                        = resultado.getBigDecimal("ingresos_totales");

                BigDecimal promedio
                        = resultado.getBigDecimal("promedio_venta");

                escritor.newLine();

                escritor.write("Vendedor: " + vendedor);
                escritor.newLine();

                escritor.write("Ventas realizadas: " + ventas);
                escritor.newLine();

                escritor.write("Unidades vendidas: " + unidades);
                escritor.newLine();

                escritor.write(
                        "Ingresos generados: Q"
                        + ingresos.setScale(
                                2, RoundingMode.HALF_UP
                        )
                );
                escritor.newLine();

                escritor.write(
                        "Promedio por venta: Q"
                        + promedio.setScale(
                                2, RoundingMode.HALF_UP
                        )
                );
                escritor.newLine();
            }

            if (!hayDatos) {
                escritor.write(
                        "No hay ventas registradas por vendedor."
                );
                escritor.newLine();
            }
        }
    }

    private void escribirTopVendedores(
            Connection conexion,
            BufferedWriter escritor)
            throws SQLException, IOException {

        String sql = """
        SELECT
            ve.nombre_vendedor,
            COUNT(v.id_venta) AS total_ventas,
            COALESCE(SUM(v.monto_total), 0)
                AS ingresos_totales
        FROM "Vendedores" ve
        INNER JOIN "Ventas" v
            ON ve.id_vendedor = v.id_vendedor
        GROUP BY
            ve.id_vendedor,
            ve.nombre_vendedor
        ORDER BY ingresos_totales DESC,
                 total_ventas DESC,
                 ve.nombre_vendedor ASC
        LIMIT 3
        """;

        escritor.newLine();
        escritor.newLine();

        escritor.write("5. TOP 3 VENDEDORES");
        escritor.newLine();
        escritor.write("------------------");
        escritor.newLine();

        try (PreparedStatement sentencia
                = conexion.prepareStatement(sql); ResultSet resultado
                = sentencia.executeQuery()) {

            int posicion = 0;

            while (resultado.next()) {

                posicion++;

                String vendedor
                        = resultado.getString("nombre_vendedor");

                long ventas
                        = resultado.getLong("total_ventas");

                BigDecimal ingresos
                        = resultado.getBigDecimal("ingresos_totales");

                escritor.newLine();

                escritor.write(
                        posicion + ". " + vendedor
                );
                escritor.newLine();

                escritor.write(
                        "   Ventas realizadas: " + ventas
                );
                escritor.newLine();

                escritor.write(
                        "   Ingresos generados: Q"
                        + ingresos.setScale(
                                2, RoundingMode.HALF_UP
                        )
                );
                escritor.newLine();
            }

            if (posicion == 0) {
                escritor.write(
                        "No hay vendedores con ventas registradas."
                );
                escritor.newLine();
            }
        }
    }

    private void escribirVentasPorMes(
            Connection conexion,
            BufferedWriter escritor)
            throws SQLException, IOException {

        String sql = """
        SELECT
            EXTRACT(YEAR FROM fecha)::int AS anio,
            EXTRACT(MONTH FROM fecha)::int AS mes,
            COUNT(*) AS total_ventas,
            COALESCE(SUM(cantidad), 0)
                AS unidades_vendidas,
            COALESCE(SUM(monto_total), 0)
                AS ingresos_totales,
            COALESCE(AVG(monto_total), 0)
                AS promedio_venta
        FROM "Ventas"
        GROUP BY
            EXTRACT(YEAR FROM fecha),
            EXTRACT(MONTH FROM fecha)
        ORDER BY anio ASC, mes ASC
        """;

        String[] nombresMeses = {
            "Enero", "Febrero", "Marzo",
            "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre",
            "Octubre", "Noviembre", "Diciembre"
        };

        escritor.newLine();
        escritor.newLine();

        escritor.write("6. VENTAS POR MES");
        escritor.newLine();
        escritor.write("----------------");
        escritor.newLine();

        try (PreparedStatement sentencia
                = conexion.prepareStatement(sql); ResultSet resultado
                = sentencia.executeQuery()) {

            boolean hayDatos = false;

            while (resultado.next()) {

                hayDatos = true;

                int anio = resultado.getInt("anio");
                int mes = resultado.getInt("mes");

                long ventas
                        = resultado.getLong("total_ventas");

                long unidades
                        = resultado.getLong("unidades_vendidas");

                BigDecimal ingresos
                        = resultado.getBigDecimal("ingresos_totales");

                BigDecimal promedio
                        = resultado.getBigDecimal("promedio_venta");

                escritor.newLine();

                escritor.write(
                        "Periodo: "
                        + nombresMeses[mes - 1]
                        + " " + anio
                );
                escritor.newLine();

                escritor.write(
                        "Ventas realizadas: " + ventas
                );
                escritor.newLine();

                escritor.write(
                        "Unidades vendidas: " + unidades
                );
                escritor.newLine();

                escritor.write(
                        "Ingresos generados: Q"
                        + ingresos.setScale(
                                2, RoundingMode.HALF_UP
                        )
                );
                escritor.newLine();

                escritor.write(
                        "Promedio por venta: Q"
                        + promedio.setScale(
                                2, RoundingMode.HALF_UP
                        )
                );
                escritor.newLine();
            }

            if (!hayDatos) {
                escritor.write(
                        "No hay ventas registradas por mes."
                );
                escritor.newLine();
            }
        }
    }

    private void escribirVentasExtremas(
            Connection conexion,
            BufferedWriter escritor)
            throws SQLException, IOException {

        String sql = """
        SELECT
            v.id_venta,
            v.fecha,
            p.nombre_producto,
            s.nombre_sucursal,
            ve.nombre_vendedor,
            v.cantidad,
            v.precio_unitario,
            v.monto_total
        FROM "Ventas" v
        INNER JOIN "Productos" p
            ON v.codigo_producto = p.codigo_producto
        INNER JOIN "Sucursales" s
            ON v.id_sucursal = s.id_sucursal
        INNER JOIN "Vendedores" ve
            ON v.id_vendedor = ve.id_vendedor
        ORDER BY v.monto_total DESC,
                 v.id_venta ASC
        LIMIT 1
        """;

        String sqlMenor = """
        SELECT
            v.id_venta,
            v.fecha,
            p.nombre_producto,
            s.nombre_sucursal,
            ve.nombre_vendedor,
            v.cantidad,
            v.precio_unitario,
            v.monto_total
        FROM "Ventas" v
        INNER JOIN "Productos" p
            ON v.codigo_producto = p.codigo_producto
        INNER JOIN "Sucursales" s
            ON v.id_sucursal = s.id_sucursal
        INNER JOIN "Vendedores" ve
            ON v.id_vendedor = ve.id_vendedor
        ORDER BY v.monto_total ASC,
                 v.id_venta ASC
        LIMIT 1
        """;

        escritor.newLine();
        escritor.newLine();

        escritor.write("7. VENTAS DE MAYOR Y MENOR MONTO");
        escritor.newLine();
        escritor.write("--------------------------------");
        escritor.newLine();

        try (PreparedStatement sentencia
                = conexion.prepareStatement(sql); ResultSet resultado
                = sentencia.executeQuery()) {

            escritor.newLine();
            escritor.write("VENTA DE MAYOR MONTO");
            escritor.newLine();

            if (resultado.next()) {
                escribirDetalleVenta(resultado, escritor);
            } else {
                escritor.write("No hay ventas registradas.");
                escritor.newLine();
            }
        }

        try (PreparedStatement sentencia
                = conexion.prepareStatement(sqlMenor); ResultSet resultado
                = sentencia.executeQuery()) {

            escritor.newLine();
            escritor.write("VENTA DE MENOR MONTO");
            escritor.newLine();

            if (resultado.next()) {
                escribirDetalleVenta(resultado, escritor);
            } else {
                escritor.write("No hay ventas registradas.");
                escritor.newLine();
            }
        }
    }

    private void escribirDetalleVenta(
            ResultSet resultado,
            BufferedWriter escritor)
            throws SQLException, IOException {

        escritor.write(
                "ID venta: "
                + resultado.getString("id_venta")
        );
        escritor.newLine();

        escritor.write(
                "Fecha: "
                + resultado.getDate("fecha")
        );
        escritor.newLine();

        escritor.write(
                "Producto: "
                + resultado.getString("nombre_producto")
        );
        escritor.newLine();

        escritor.write(
                "Sucursal: "
                + resultado.getString("nombre_sucursal")
        );
        escritor.newLine();

        escritor.write(
                "Vendedor: "
                + resultado.getString("nombre_vendedor")
        );
        escritor.newLine();

        escritor.write(
                "Cantidad: "
                + resultado.getInt("cantidad")
        );
        escritor.newLine();

        BigDecimal precio
                = resultado.getBigDecimal("precio_unitario");

        BigDecimal monto
                = resultado.getBigDecimal("monto_total");

        escritor.write(
                "Precio unitario: Q"
                + precio.setScale(
                        2, RoundingMode.HALF_UP
                )
        );
        escritor.newLine();

        escritor.write(
                "Monto total: Q"
                + monto.setScale(
                        2, RoundingMode.HALF_UP
                )
        );
        escritor.newLine();
    }

    private void escribirMesesExtremos(
            Connection conexion,
            BufferedWriter escritor)
            throws SQLException, IOException {

        String sql = """
        SELECT
            EXTRACT(YEAR FROM fecha)::int AS anio,
            EXTRACT(MONTH FROM fecha)::int AS mes,
            COUNT(*) AS total_ventas,
            COALESCE(SUM(monto_total), 0)
                AS ingresos_totales
        FROM "Ventas"
        GROUP BY
            EXTRACT(YEAR FROM fecha),
            EXTRACT(MONTH FROM fecha)
        ORDER BY ingresos_totales DESC,
                 anio ASC,
                 mes ASC
        """;

        String[] nombresMeses = {
            "Enero", "Febrero", "Marzo",
            "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre",
            "Octubre", "Noviembre", "Diciembre"
        };

        escritor.newLine();
        escritor.newLine();

        escritor.write("8. MESES CON MAYORES Y MENORES INGRESOS");
        escritor.newLine();
        escritor.write("--------------------------------------");
        escritor.newLine();

        try (PreparedStatement sentencia
                = conexion.prepareStatement(sql); ResultSet resultado
                = sentencia.executeQuery()) {

            String mesMayor = null;
            String mesMenor = null;

            BigDecimal ingresoMayor = null;
            BigDecimal ingresoMenor = null;

            long ventasMayor = 0;
            long ventasMenor = 0;

            while (resultado.next()) {

                int anio = resultado.getInt("anio");
                int mes = resultado.getInt("mes");

                String periodo
                        = nombresMeses[mes - 1] + " " + anio;

                BigDecimal ingresos
                        = resultado.getBigDecimal(
                                "ingresos_totales"
                        );

                long ventas
                        = resultado.getLong("total_ventas");

                if (mesMayor == null) {

                    mesMayor = periodo;
                    ingresoMayor = ingresos;
                    ventasMayor = ventas;
                }

                if (ingresoMenor == null
                        || ingresos.compareTo(ingresoMenor) < 0) {

                    mesMenor = periodo;
                    ingresoMenor = ingresos;
                    ventasMenor = ventas;
                }
            }

            if (mesMayor == null) {

                escritor.write(
                        "No hay ventas registradas."
                );
                escritor.newLine();

                return;
            }

            escritor.newLine();
            escritor.write("MES CON MAYORES INGRESOS");
            escritor.newLine();

            escritor.write("Periodo: " + mesMayor);
            escritor.newLine();

            escritor.write(
                    "Ventas realizadas: " + ventasMayor
            );
            escritor.newLine();

            escritor.write(
                    "Ingresos: Q"
                    + ingresoMayor.setScale(
                            2, RoundingMode.HALF_UP
                    )
            );
            escritor.newLine();

            escritor.newLine();
            escritor.write("MES CON MENORES INGRESOS");
            escritor.newLine();

            escritor.write("Periodo: " + mesMenor);
            escritor.newLine();

            escritor.write(
                    "Ventas realizadas: " + ventasMenor
            );
            escritor.newLine();

            escritor.write(
                    "Ingresos: Q"
                    + ingresoMenor.setScale(
                            2, RoundingMode.HALF_UP
                    )
            );
            escritor.newLine();
        }
    }

    private void escribirEstadisticasProcesamiento(
            Connection conexion,
            BufferedWriter escritor)
            throws SQLException, IOException {

        String sql = """
        SELECT
            COUNT(*) AS total_archivos,
            COALESCE(SUM(registros_recibidos), 0)
                AS total_recibidos,
            COALESCE(SUM(registros_aceptados), 0)
                AS total_aceptados,
            COALESCE(SUM(registros_rechazados), 0)
                AS total_rechazados,
            COALESCE(SUM(registros_duplicados), 0)
                AS total_duplicados
        FROM "ArchivosProcesados"
        """;

        escritor.newLine();
        escritor.newLine();

        escritor.write("9. ESTADISTICAS DE PROCESAMIENTO");
        escritor.newLine();
        escritor.write("--------------------------------");
        escritor.newLine();

        try (PreparedStatement sentencia
                = conexion.prepareStatement(sql); ResultSet resultado
                = sentencia.executeQuery()) {

            if (resultado.next()) {

                long archivos
                        = resultado.getLong("total_archivos");

                long recibidos
                        = resultado.getLong("total_recibidos");

                long aceptados
                        = resultado.getLong("total_aceptados");

                long rechazados
                        = resultado.getLong("total_rechazados");

                long duplicados
                        = resultado.getLong("total_duplicados");

                BigDecimal porcentajeRechazo
                        = BigDecimal.ZERO;

                if (recibidos > 0) {

                    porcentajeRechazo
                            = BigDecimal.valueOf(rechazados)
                                    .multiply(
                                            BigDecimal.valueOf(100)
                                    )
                                    .divide(
                                            BigDecimal.valueOf(recibidos),
                                            2,
                                            RoundingMode.HALF_UP
                                    );
                }

                escritor.newLine();

                escritor.write(
                        "Archivos procesados: " + archivos
                );
                escritor.newLine();

                escritor.write(
                        "Registros recibidos: " + recibidos
                );
                escritor.newLine();

                escritor.write(
                        "Registros aceptados: " + aceptados
                );
                escritor.newLine();

                escritor.write(
                        "Registros rechazados: " + rechazados
                );
                escritor.newLine();

                escritor.write(
                        "Registros duplicados: " + duplicados
                );
                escritor.newLine();

                escritor.write(
                        "Porcentaje de rechazo: "
                        + porcentajeRechazo + "%"
                );
                escritor.newLine();
            }
        }
    }

    private void escribirConclusiones(
            Connection conexion,
            BufferedWriter escritor)
            throws SQLException, IOException {

        escritor.newLine();
        escritor.newLine();

        escritor.write("10. CONCLUSIONES DEL ANALISIS");
        escritor.newLine();
        escritor.write("----------------------------");
        escritor.newLine();

        // Verificar si existen ventas
        String sqlTotal = """
        SELECT COUNT(*) AS total
        FROM "Ventas"
        """;

        try (PreparedStatement sentencia
                = conexion.prepareStatement(sqlTotal); ResultSet resultado
                = sentencia.executeQuery()) {

            resultado.next();

            if (resultado.getLong("total") == 0) {
                escritor.newLine();
                escritor.write(
                        "No existen ventas suficientes "
                        + "para generar conclusiones."
                );
                escritor.newLine();
                return;
            }
        }

        // 1. Sucursal con mayores ingresos
        String sqlSucursal = """
        SELECT
            s.nombre_sucursal,
            SUM(v.monto_total) AS ingresos
        FROM "Ventas" v
        INNER JOIN "Sucursales" s
            ON v.id_sucursal = s.id_sucursal
        GROUP BY s.id_sucursal, s.nombre_sucursal
        ORDER BY ingresos DESC, s.nombre_sucursal ASC
        LIMIT 1
        """;

        try (PreparedStatement sentencia
                = conexion.prepareStatement(sqlSucursal); ResultSet resultado
                = sentencia.executeQuery()) {

            if (resultado.next()) {

                escritor.newLine();
                escritor.write(
                        "1. La sucursal con mayores ingresos es "
                        + resultado.getString("nombre_sucursal")
                        + ", con Q"
                        + resultado.getBigDecimal("ingresos")
                                .setScale(2, RoundingMode.HALF_UP)
                        + "."
                );
                escritor.newLine();
            }
        }

        // 2. Producto con mayores ingresos
        String sqlProducto = """
        SELECT
            p.nombre_producto,
            SUM(v.monto_total) AS ingresos
        FROM "Ventas" v
        INNER JOIN "Productos" p
            ON v.codigo_producto = p.codigo_producto
        GROUP BY p.codigo_producto, p.nombre_producto
        ORDER BY ingresos DESC, p.nombre_producto ASC
        LIMIT 1
        """;

        try (PreparedStatement sentencia
                = conexion.prepareStatement(sqlProducto); ResultSet resultado
                = sentencia.executeQuery()) {

            if (resultado.next()) {

                escritor.write(
                        "2. El producto que genera mayores "
                        + "ingresos es "
                        + resultado.getString("nombre_producto")
                        + ", con Q"
                        + resultado.getBigDecimal("ingresos")
                                .setScale(2, RoundingMode.HALF_UP)
                        + "."
                );
                escritor.newLine();
            }
        }

        // 3. Vendedor con mayores ingresos
        String sqlVendedor = """
        SELECT
            ve.nombre_vendedor,
            SUM(v.monto_total) AS ingresos
        FROM "Ventas" v
        INNER JOIN "Vendedores" ve
            ON v.id_vendedor = ve.id_vendedor
        GROUP BY ve.id_vendedor, ve.nombre_vendedor
        ORDER BY ingresos DESC, ve.nombre_vendedor ASC
        LIMIT 1
        """;

        try (PreparedStatement sentencia
                = conexion.prepareStatement(sqlVendedor); ResultSet resultado
                = sentencia.executeQuery()) {

            if (resultado.next()) {

                escritor.write(
                        "3. El vendedor con mayores ingresos es "
                        + resultado.getString("nombre_vendedor")
                        + ", con Q"
                        + resultado.getBigDecimal("ingresos")
                                .setScale(2, RoundingMode.HALF_UP)
                        + "."
                );
                escritor.newLine();
            }
        }

        // 4. Mes con mayores ingresos
        String sqlMes = """
        SELECT
            EXTRACT(YEAR FROM fecha)::int AS anio,
            EXTRACT(MONTH FROM fecha)::int AS mes,
            SUM(monto_total) AS ingresos
        FROM "Ventas"
        GROUP BY
            EXTRACT(YEAR FROM fecha),
            EXTRACT(MONTH FROM fecha)
        ORDER BY ingresos DESC, anio ASC, mes ASC
        LIMIT 1
        """;

        String[] nombresMeses = {
            "Enero", "Febrero", "Marzo",
            "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre",
            "Octubre", "Noviembre", "Diciembre"
        };

        try (PreparedStatement sentencia
                = conexion.prepareStatement(sqlMes); ResultSet resultado
                = sentencia.executeQuery()) {

            if (resultado.next()) {

                int mes = resultado.getInt("mes");
                int anio = resultado.getInt("anio");

                escritor.write(
                        "4. El mes con mayores ingresos es "
                        + nombresMeses[mes - 1]
                        + " de " + anio
                        + ", con Q"
                        + resultado.getBigDecimal("ingresos")
                                .setScale(2, RoundingMode.HALF_UP)
                        + "."
                );
                escritor.newLine();
            }
        }

        // 5. Porcentaje de registros rechazados
        String sqlRechazos = """
        SELECT
            COALESCE(SUM(registros_recibidos), 0)
                AS recibidos,
            COALESCE(SUM(registros_rechazados), 0)
                AS rechazados
        FROM "ArchivosProcesados"
        """;

        try (PreparedStatement sentencia
                = conexion.prepareStatement(sqlRechazos); ResultSet resultado
                = sentencia.executeQuery()) {

            if (resultado.next()) {

                long recibidos
                        = resultado.getLong("recibidos");

                long rechazados
                        = resultado.getLong("rechazados");

                BigDecimal porcentaje = BigDecimal.ZERO;

                if (recibidos > 0) {
                    porcentaje
                            = BigDecimal.valueOf(rechazados)
                                    .multiply(BigDecimal.valueOf(100))
                                    .divide(
                                            BigDecimal.valueOf(recibidos),
                                            2,
                                            RoundingMode.HALF_UP
                                    );
                }

                escritor.write(
                        "5. El porcentaje historico de registros "
                        + "rechazados es del "
                        + porcentaje
                        + "%."
                );
                escritor.newLine();
            }
        }
    }

}

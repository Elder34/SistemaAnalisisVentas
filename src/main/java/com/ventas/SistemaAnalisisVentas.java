package com.ventas;

import com.ventas.processor.ArchivoProcessor;
import com.ventas.report.ReporteErrores;
import com.ventas.report.ReporteAnalisis;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

public class SistemaAnalisisVentas {

    public static void main(String[] args) {

        ArchivoProcessor procesador
                = new ArchivoProcessor();

        ReporteErrores reporteErrores
                = new ReporteErrores();

        ReporteAnalisis reporteAnalisis
                = new ReporteAnalisis();

        try {

            procesador.procesarCarpeta(
                    Path.of("datos")
            );

            reporteErrores.generarReporte(
                    Path.of("errores_procesamiento.txt")
            );

            reporteAnalisis.generarReporte(
                    Path.of("reporte_analisis.txt")
            );

            System.out.println(
                    "\nProceso finalizado correctamente."
            );

        } catch (IOException | SQLException e) {

            System.out.println(
                    "Error general: " + e.getMessage()
            );
        }
    }

}

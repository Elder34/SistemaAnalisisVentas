package com.ventas.processor;

import com.ventas.model.VentaRaw;
import com.ventas.reader.CsvReader;
import com.ventas.reader.JsonReader;
import com.ventas.reader.XmlReader;
import com.ventas.validation.VentaNormalizer;
import com.ventas.validation.VentaValidator;
import com.ventas.dao.ArchivoProcesadoDAO;
import com.ventas.dao.ErrorProcesamientoDAO;
import com.ventas.service.VentaService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class ArchivoProcessor {

    private final ErrorProcesamientoDAO errorDAO
            = new ErrorProcesamientoDAO();

    private final ArchivoProcesadoDAO archivoDAO
            = new ArchivoProcesadoDAO();

    private final VentaService ventaService
            = new VentaService();

    private int totalDuplicados;
    private int totalRecibidos;
    private int totalValidos;
    private int totalRechazados;

    public void procesarCarpeta(Path carpeta)
            throws IOException {

        if (!Files.isDirectory(carpeta)) {
            throw new IOException(
                    "La carpeta no existe: " + carpeta
            );
        }

        // Reiniciar contadores de esta ejecución
        totalDuplicados = 0;
        totalRecibidos = 0;
        totalValidos = 0;
        totalRechazados = 0;

        try (Stream<Path> archivos = Files.list(carpeta)) {

            List<Path> rutas = archivos
                    .filter(Files::isRegularFile)
                    .filter(this::esCompatible)
                    .sorted(Comparator.comparing(
                            ruta -> ruta.getFileName().toString()
                    ))
                    .toList();

            for (Path ruta : rutas) {

                System.out.println(
                        "ARCHIVO ENCONTRADO: "
                        + ruta.toAbsolutePath()
                );

                try {
                    procesarArchivo(ruta);

                } catch (IOException | SQLException
                        | RuntimeException e) {

                    System.err.println(
                            "No se pudo procesar el archivo "
                            + ruta.getFileName()
                            + ": " + e.getMessage()
                    );
                }
            }
        }

        mostrarResumen();
    }

    private boolean esCompatible(Path ruta) {

        String nombre = ruta.getFileName()
                .toString()
                .toLowerCase(Locale.ROOT);

        return nombre.endsWith(".csv")
                || nombre.endsWith(".json")
                || nombre.endsWith(".xml");
    }

    private void procesarArchivo(Path ruta)
            throws IOException, SQLException {

        String nombre = ruta.getFileName().toString();
        String extension = nombre.toLowerCase(Locale.ROOT);

        String tipo;

        if (extension.endsWith(".csv")) {
            tipo = "CSV";

        } else if (extension.endsWith(".json")) {
            tipo = "JSON";

        } else if (extension.endsWith(".xml")) {
            tipo = "XML";

        } else {
            return;
        }

        // Registrar el archivo ANTES de leerlo
        int idArchivo = archivoDAO.registrarArchivo(
                nombre, tipo
        );

        int recibidos = 0;
        int aceptados = 0;
        int rechazados = 0;
        int duplicados = 0;

        System.out.println("\nArchivo: " + nombre);

        List<VentaRaw> ventas;

        // Detectar errores de lectura del archivo completo
        try {

            if (tipo.equals("CSV")) {

                ventas = new CsvReader().leer(ruta);

            } else if (tipo.equals("JSON")) {

                ventas = new JsonReader().leer(ruta);

            } else {

                ventas = new XmlReader().leer(ruta);
            }

        } catch (IOException | RuntimeException e) {
            System.out.println(
                    "DIAGNOSTICO: Error de lectura detectado en "
                    + nombre
            );

            System.out.println(
                    "DIAGNOSTICO: " + e.getMessage()
            );
            String motivo = e.getMessage() == null
                    ? "No se pudo leer el archivo"
                    : e.getMessage();

            System.err.println(
                    "ERROR DE LECTURA EN "
                    + nombre + ": " + motivo
            );

            // Se registra un error de archivo,
            // no una venta rechazada.
            errorDAO.registrarError(
                    idArchivo,
                    null,
                    "LECTURA_ARCHIVO",
                    motivo
            );

            archivoDAO.actualizarEstadisticas(
                    idArchivo,
                    0,
                    0,
                    0,
                    0
            );

            System.out.println(
                    "Archivo finalizado con error: "
                    + nombre
            );

            return;
        }

        // Procesar las ventas leídas correctamente
        for (VentaRaw venta : ventas) {

            recibidos++;
            totalRecibidos++;

            try {

                VentaNormalizer.normalizar(venta);

                List<String> errores
                        = VentaValidator.validar(venta);

                if (!errores.isEmpty()) {

                    rechazados++;
                    totalRechazados++;

                    String motivo
                            = String.join("; ", errores);

                    System.out.println(
                            "RECHAZADA: "
                            + venta.getIdVenta()
                    );

                    for (String error : errores) {
                        System.out.println(
                                " - " + error
                        );
                    }

                    // Si falla el registro del error,
                    // no se cuenta dos veces la venta.
                    try {

                        errorDAO.registrarError(
                                idArchivo,
                                venta,
                                "VALIDACION",
                                motivo
                        );

                    } catch (SQLException e) {

                        System.err.println(
                                "No se pudo guardar "
                                + "el error de validacion: "
                                + e.getMessage()
                        );
                    }

                    continue;
                }

                ResultadoVenta resultado
                        = ventaService.procesarVenta(
                                venta,
                                idArchivo
                        );

                switch (resultado) {

                    case ACEPTADA -> {

                        aceptados++;
                        totalValidos++;

                        System.out.println(
                                "ACEPTADA: "
                                + venta.getIdVenta()
                        );
                    }

                    case DUPLICADA -> {

                        duplicados++;
                        totalDuplicados++;

                        System.out.println(
                                "DUPLICADA: "
                                + venta.getIdVenta()
                        );
                    }

                    case RECHAZADA -> {

                        rechazados++;
                        totalRechazados++;

                        System.out.println(
                                "RECHAZADA: "
                                + venta.getIdVenta()
                        );

                        try {

                            errorDAO.registrarError(
                                    idArchivo,
                                    venta,
                                    "PROCESAMIENTO",
                                    "Venta rechazada "
                                    + "durante el procesamiento"
                            );

                        } catch (SQLException e) {

                            System.err.println(
                                    "No se pudo guardar "
                                    + "el rechazo: "
                                    + e.getMessage()
                            );
                        }
                    }
                }

            } catch (SQLException | RuntimeException e) {

                rechazados++;
                totalRechazados++;

                String motivo = e.getMessage() == null
                        ? "Error sin detalle"
                        : e.getMessage();

                System.err.println(
                        "ERROR EN VENTA "
                        + venta.getIdVenta()
                        + ": " + motivo
                );

                try {

                    errorDAO.registrarError(
                            idArchivo,
                            venta,
                            "PROCESAMIENTO",
                            motivo
                    );

                } catch (SQLException errorRegistro) {

                    System.err.println(
                            "No se pudo guardar el error: "
                            + errorRegistro.getMessage()
                    );
                }
            }
        }

        archivoDAO.actualizarEstadisticas(
                idArchivo,
                recibidos,
                aceptados,
                rechazados,
                duplicados
        );

        System.out.println(
                "Archivo finalizado: " + nombre
        );
    }

    private void mostrarResumen() {

        System.out.println(
                "\n===== RESUMEN ====="
        );

        System.out.println(
                "Registros recibidos: "
                + totalRecibidos
        );

        System.out.println(
                "Registros aceptados: "
                + totalValidos
        );

        System.out.println(
                "Registros rechazados: "
                + totalRechazados
        );

        System.out.println(
                "Registros duplicados: "
                + totalDuplicados
        );
    }
}

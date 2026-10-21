package com.ventas.reader;

import com.ventas.model.VentaRaw;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class CsvReader {

    public List<VentaRaw> leer(Path ruta)
            throws IOException {

        List<VentaRaw> ventas = new ArrayList<>();

        CSVFormat formato = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .get();

        try (Reader archivo = Files.newBufferedReader(
                ruta, StandardCharsets.UTF_8); CSVParser parser = formato.parse(archivo)) {

            Map<String, String> encabezados
                    = new HashMap<>();

            for (String encabezado
                    : parser.getHeaderMap().keySet()) {

                encabezados.put(
                        normalizarEncabezado(encabezado),
                        encabezado
                );
            }

            String[] obligatorios = {
                "id_venta",
                "fecha",
                "sucursal",
                "codigo_producto",
                "producto",
                "cantidad",
                "precio_unitario",
                "vendedor"
            };

            for (String campo : obligatorios) {
                if (!encabezados.containsKey(campo)) {
                    throw new IOException(
                            "Falta la columna obligatoria: "
                            + campo
                    );
                }
            }

            for (CSVRecord registro : parser) {

                VentaRaw venta = new VentaRaw(
                        obtener(registro, encabezados, "id_venta"),
                        obtener(registro, encabezados, "fecha"),
                        obtener(registro, encabezados, "sucursal"),
                        obtener(registro, encabezados, "codigo_producto"),
                        obtener(registro, encabezados, "producto"),
                        obtener(registro, encabezados, "cantidad"),
                        obtener(registro, encabezados, "precio_unitario"),
                        obtener(registro, encabezados, "vendedor"),
                        ruta.getFileName().toString(),
                        Math.toIntExact(registro.getRecordNumber() + 1)
                );

                ventas.add(venta);
            }
        }

        return ventas;
    }

    private String obtener(
            CSVRecord registro,
            Map<String, String> encabezados,
            String campo) {

        String encabezadoOriginal = encabezados.get(campo);

        if (!registro.isMapped(encabezadoOriginal)
                || !registro.isSet(encabezadoOriginal)) {
            return null;
        }

        return registro.get(encabezadoOriginal);
    }

    private String normalizarEncabezado(String texto) {

        String limpio = Normalizer.normalize(
                texto.trim(), Normalizer.Form.NFD
        );

        limpio = limpio.replaceAll("\\p{M}", "");

        return limpio.toLowerCase(Locale.ROOT)
                .replaceAll("[\\s-]+", "_");
    }
}

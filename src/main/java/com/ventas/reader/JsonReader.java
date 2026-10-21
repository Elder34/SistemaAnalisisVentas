package com.ventas.reader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ventas.model.VentaRaw;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class JsonReader {

    private final ObjectMapper mapper = new ObjectMapper();

    public List<VentaRaw> leer(Path ruta)
            throws IOException {

        List<VentaRaw> ventas = new ArrayList<>();

        JsonNode raiz = mapper.readTree(ruta.toFile());

        JsonNode registros;

        if (raiz.isArray()) {
            registros = raiz;

        } else if (raiz.isObject()
                && raiz.has("ventas")
                && raiz.get("ventas").isArray()) {

            registros = raiz.get("ventas");

        } else {
            throw new IOException(
                    "El JSON debe contener un arreglo "
                    + "de ventas o un objeto con la propiedad ventas"
            );
        }

        int numeroRegistro = 1;

        for (JsonNode nodo : registros) {

            VentaRaw venta = new VentaRaw(
                    obtener(nodo, "id_venta"),
                    obtener(nodo, "fecha"),
                    obtener(nodo, "sucursal"),
                    obtener(nodo, "codigo_producto"),
                    obtener(nodo, "producto"),
                    obtener(nodo, "cantidad"),
                    obtener(nodo, "precio_unitario"),
                    obtener(nodo, "vendedor"),
                    ruta.getFileName().toString(),
                    numeroRegistro
            );

            ventas.add(venta);
            numeroRegistro++;
        }

        return ventas;
    }

    private String obtener(JsonNode nodo, String campo) {

        if (!nodo.isObject()) {
            return null;
        }

        for (String nombre : iterable(nodo.fieldNames())) {

            String normalizado = nombre.trim()
                    .toLowerCase(Locale.ROOT)
                    .replaceAll("[\\s-]+", "_");

            if (normalizado.equals(campo)) {

                JsonNode valor = nodo.get(nombre);

                if (valor == null || valor.isNull()) {
                    return null;
                }

                if (valor.isContainerNode()) {
                    return null;
                }

                return valor.asText();
            }
        }

        return null;
    }

    private Iterable<String> iterable(
            java.util.Iterator<String> iterator) {
        return () -> iterator;
    }
}

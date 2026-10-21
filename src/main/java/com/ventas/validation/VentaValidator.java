package com.ventas.validation;

import com.ventas.model.VentaRaw;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class VentaValidator {

    public static List<String> validar(VentaRaw venta) {

        List<String> errores = new ArrayList<>();

        if (vacio(venta.getIdVenta())) {
            errores.add("ID de venta obligatorio");
        }

        if (vacio(venta.getSucursal())) {
            errores.add("Sucursal obligatoria");
        }

        if (vacio(venta.getCodigoProducto())) {
            errores.add("Codigo de producto obligatorio");
        }

        if (vacio(venta.getProducto())) {
            errores.add("Nombre de producto obligatorio");
        }

        if (vacio(venta.getVendedor())) {
            errores.add("Vendedor obligatorio");
        }

        validarFecha(venta.getFecha(), errores);
        validarCantidad(venta.getCantidad(), errores);
        validarPrecio(venta.getPrecioUnitario(), errores);

        return errores;
    }

    private static boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }

    private static void validarFecha(
            String fecha, List<String> errores) {

        if (vacio(fecha)) {
            errores.add("Fecha obligatoria");
            return;
        }

        try {
            LocalDate.parse(fecha);
        } catch (DateTimeParseException e) {
            errores.add(
                    "Fecha invalida: " + fecha
                    + ". Formato esperado: yyyy-MM-dd"
            );
        }
    }

    private static void validarCantidad(
            String cantidad, List<String> errores) {

        if (vacio(cantidad)) {
            errores.add("Cantidad obligatoria");
            return;
        }

        try {
            int valor = Integer.parseInt(cantidad);

            if (valor <= 0) {
                errores.add(
                        "La cantidad debe ser mayor que cero"
                );
            }

        } catch (NumberFormatException e) {
            errores.add(
                    "Cantidad invalida: " + cantidad
            );
        }
    }

    private static void validarPrecio(
            String precio, List<String> errores) {

        if (vacio(precio)) {
            errores.add("Precio unitario obligatorio");
            return;
        }

        try {
            BigDecimal valor = new BigDecimal(precio);

            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                errores.add(
                        "El precio debe ser mayor que cero"
                );
            }

            if (valor.scale() > 2) {
                errores.add(
                        "El precio no puede tener mas de 2 decimales"
                );
            }

            if (valor.precision() - valor.scale() > 10) {
                errores.add(
                        "El precio excede el rango permitido"
                );
            }

        } catch (NumberFormatException e) {
            errores.add(
                    "Precio invalido: " + precio
            );
        }
    }
}

package com.ventas.validation;

import com.ventas.model.VentaRaw;
import java.util.Locale;

public class VentaNormalizer {

    public static void normalizar(VentaRaw venta) {

        venta.setIdVenta(
                limpiar(venta.getIdVenta())
        );

        venta.setFecha(
                limpiar(venta.getFecha())
        );

        venta.setSucursal(
                normalizarNombre(venta.getSucursal())
        );

        venta.setCodigoProducto(
                normalizarCodigo(venta.getCodigoProducto())
        );

        venta.setProducto(
                normalizarNombre(venta.getProducto())
        );

        venta.setCantidad(
                limpiar(venta.getCantidad())
        );

        venta.setPrecioUnitario(
                limpiar(venta.getPrecioUnitario())
        );

        venta.setVendedor(
                normalizarNombre(venta.getVendedor())
        );
    }

    private static String limpiar(String texto) {
        if (texto == null) {
            return null;
        }

        return texto.trim().replaceAll("\\s+", " ");
    }

    private static String normalizarNombre(String texto) {
        String limpio = limpiar(texto);

        if (limpio == null) {
            return null;
        }

        return limpio.toUpperCase(Locale.ROOT);
    }

    private static String normalizarCodigo(String texto) {
        return normalizarNombre(texto);
    }
}

package com.ventas.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Venta {

    private String idVenta;
    private LocalDate fecha;
    private int idSucursal;
    private String codigoProducto;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal montoTotal;
    private int idVendedor;
    private int idArchivo;

    public Venta() {
    }

    public Venta(String idVenta,
            LocalDate fecha,
            int idSucursal,
            String codigoProducto,
            int cantidad,
            BigDecimal precioUnitario,
            int idVendedor,
            int idArchivo) {

        this.idVenta = idVenta;
        this.fecha = fecha;
        this.idSucursal = idSucursal;
        this.codigoProducto = codigoProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.idVendedor = idVendedor;
        this.idArchivo = idArchivo;

        this.montoTotal = calcularMontoTotal();
    }

    public String getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(String idVenta) {
        this.idVenta = idVenta;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public int getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(int idVendedor) {
        this.idVendedor = idVendedor;
    }

    public int getIdArchivo() {
        return idArchivo;
    }

    public void setIdArchivo(int idArchivo) {
        this.idArchivo = idArchivo;
    }

    public BigDecimal calcularMontoTotal() {
        if (precioUnitario == null) {
            throw new IllegalStateException(
                    "El precio unitario no puede ser nulo"
            );
        }

        return precioUnitario.multiply(
                BigDecimal.valueOf(cantidad)
        );
    }
}

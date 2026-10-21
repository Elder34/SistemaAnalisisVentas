package com.ventas.model;

public class VentaRaw {

    private String idVenta;
    private String fecha;
    private String sucursal;
    private String codigoProducto;
    private String producto;
    private String cantidad;
    private String precioUnitario;
    private String vendedor;

    private String archivoOrigen;
    private int numeroRegistro;

    public VentaRaw() {
    }

    public VentaRaw(String idVenta,
            String fecha,
            String sucursal,
            String codigoProducto,
            String producto,
            String cantidad,
            String precioUnitario,
            String vendedor,
            String archivoOrigen,
            int numeroRegistro) {

        this.idVenta = idVenta;
        this.fecha = fecha;
        this.sucursal = sucursal;
        this.codigoProducto = codigoProducto;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.vendedor = vendedor;
        this.archivoOrigen = archivoOrigen;
        this.numeroRegistro = numeroRegistro;
    }

    public String getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(String idVenta) {
        this.idVenta = idVenta;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(String precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public String getArchivoOrigen() {
        return archivoOrigen;
    }

    public void setArchivoOrigen(String archivoOrigen) {
        this.archivoOrigen = archivoOrigen;
    }

    public int getNumeroRegistro() {
        return numeroRegistro;
    }

    public void setNumeroRegistro(int numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }
}

package com.ventas.model;

public class Vendedor {

    private int idVendedor;
    private String codigoVendedor;
    private String nombreVendedor;

    public Vendedor() {
    }

    public Vendedor(int idVendedor,
            String codigoVendedor,
            String nombreVendedor) {
        this.idVendedor = idVendedor;
        this.codigoVendedor = codigoVendedor;
        this.nombreVendedor = nombreVendedor;
    }

    public int getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(int idVendedor) {
        this.idVendedor = idVendedor;
    }

    public String getCodigoVendedor() {
        return codigoVendedor;
    }

    public void setCodigoVendedor(String codigoVendedor) {
        this.codigoVendedor = codigoVendedor;
    }

    public String getNombreVendedor() {
        return nombreVendedor;
    }

    public void setNombreVendedor(String nombreVendedor) {
        this.nombreVendedor = nombreVendedor;
    }
}

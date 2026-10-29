package com.interfaz.ui;

import com.ventas.database.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PanelVentas extends JPanel {

    private JTable tabla;
    private DefaultTableModel modelo;

    public PanelVentas() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modelo = new DefaultTableModel(
            new String[]{"ID Venta", "Fecha", "Sucursal", "Producto", "Cantidad", "Precio U.", "Total", "Vendedor"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabla = new JTable(modelo);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton btnActualizar = new JButton("Actualizar Tabla desde BD");
        btnActualizar.addActionListener(e -> cargarDatosBD());
        
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSur.add(btnActualizar);
        add(panelSur, BorderLayout.SOUTH);

        cargarDatosBD(); // Cargar al iniciar
    }

    public void cargarDatosBD() {
        modelo.setRowCount(0);
        String sql = """
            SELECT v.id_venta, v.fecha, s.nombre_sucursal, p.nombre_producto,
                   v.cantidad, v.precio_unitario, v.monto_total, ve.nombre_vendedor
            FROM "Ventas" v
            INNER JOIN "Sucursales" s ON v.id_sucursal = s.id_sucursal
            INNER JOIN "Productos" p ON v.codigo_producto = p.codigo_producto
            INNER JOIN "Vendedores" ve ON v.id_vendedor = ve.id_vendedor
            ORDER BY v.fecha DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getString("id_venta"),
                    rs.getDate("fecha"),
                    rs.getString("nombre_sucursal"),
                    rs.getString("nombre_producto"),
                    rs.getInt("cantidad"),
                    "Q" + rs.getBigDecimal("precio_unitario"),
                    "Q" + rs.getBigDecimal("monto_total"),
                    rs.getString("nombre_vendedor")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar ventas: " + e.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
        }
    }
}

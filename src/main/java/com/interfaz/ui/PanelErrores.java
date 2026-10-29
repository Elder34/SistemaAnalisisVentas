package com.interfaz.ui;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class PanelErrores extends JPanel {

    private JTextArea txtArea;

    public PanelErrores() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtArea = new JTextArea();
        txtArea.setEditable(false);
        txtArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(new JScrollPane(txtArea), BorderLayout.CENTER);

        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnActualizar = new JButton("Actualizar Errores");
        btnActualizar.addActionListener(e -> cargarReporte());
        panelSur.add(btnActualizar);
        add(panelSur, BorderLayout.SOUTH);

        cargarReporte(); // Cargar al iniciar
    }

    public void cargarReporte() {
        Path ruta = Path.of("errores_procesamiento.txt");
        if (Files.exists(ruta)) {
            try {
                txtArea.setText(Files.readString(ruta));
                txtArea.setCaretPosition(0);
            } catch (Exception e) {
                txtArea.setText("Error al leer el reporte de errores: " + e.getMessage());
            }
        } else {
            txtArea.setText("No hay errores registrados o el archivo 'errores_procesamiento.txt' aún no ha sido generado.");
        }
    }
}
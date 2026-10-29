package com.interfaz.ui;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class PanelReportes extends JPanel {

    private JTextArea txtArea;

    public PanelReportes() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtArea = new JTextArea();
        txtArea.setEditable(false);
        txtArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(new JScrollPane(txtArea), BorderLayout.CENTER);

        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAnalisis = new JButton("Ver Reporte de Análisis");
        JButton btnErrores = new JButton("Ver Reporte de Errores");

        btnAnalisis.addActionListener(e -> cargarArchivo("reporte_analisis.txt"));
        btnErrores.addActionListener(e -> cargarArchivo("errores_procesamiento.txt"));

        panelNorte.add(btnAnalisis);
        panelNorte.add(btnErrores);
        add(panelNorte, BorderLayout.NORTH);

        cargarArchivos();
    }

    public void cargarArchivos() {
        cargarArchivo("reporte_analisis.txt");
    }

    private void cargarArchivo(String nombreArchivo) {
        Path ruta = Path.of(nombreArchivo);
        if (Files.exists(ruta)) {
            try {
                txtArea.setText(Files.readString(ruta));
                txtArea.setCaretPosition(0);
            } catch (Exception e) {
                txtArea.setText("Error al leer el archivo: " + e.getMessage());
            }
        } else {
            txtArea.setText("El archivo " + nombreArchivo + " aún no ha sido generado. Procese los archivos primero.");
        }
    }
}

package com.interfaz.ui;

import com.ventas.processor.ArchivoProcessor;
import com.ventas.report.ReporteAnalisis;
import com.ventas.report.ReporteErrores;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;

public class VentanaPrincipal extends JFrame {

    private JTextField txtRuta;
    private JTextArea txtConsola;
    private JButton btnCarpeta, btnProcesar, btnSalir;
    private File carpetaSeleccionada;
    
    private PanelVentas panelVentas;
    private PanelReportes panelReportes;

    public VentanaPrincipal() {
        super("Sistema de Análisis de Ventas");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Pestañas principales
        JTabbedPane tabbedPane = new JTabbedPane();

        // Pestaña 1: Panel de Control y Procesamiento
        JPanel panelControl = new JPanel(new BorderLayout(10, 10));
        panelControl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        txtRuta = new JTextField("datos", 20);
        txtRuta.setEditable(false);
        btnCarpeta = new JButton("Seleccionar Carpeta");
        btnProcesar = new JButton("Procesar Archivos");
        btnProcesar.setBackground(new Color(46, 204, 113));
        btnProcesar.setForeground(Color.WHITE);

        panelSuperior.add(new JLabel("Carpeta:"));
        panelSuperior.add(txtRuta);
        panelSuperior.add(btnCarpeta);
        panelSuperior.add(btnProcesar);

        txtConsola = new JTextArea();
        txtConsola.setEditable(false);
        txtConsola.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtConsola.setText("Instrucciones:\n1. Seleccione la carpeta de datos.\n2. Haga clic en 'Procesar Archivos'.\n3. Revise las pestañas superiores para ver las ventas y los reportes.\n");
        JScrollPane scrollConsola = new JScrollPane(txtConsola);
        scrollConsola.setBorder(BorderFactory.createTitledBorder("Registro de ejecución"));

        panelControl.add(panelSuperior, BorderLayout.NORTH);
        panelControl.add(scrollConsola, BorderLayout.CENTER);

        tabbedPane.addTab("Control y Procesamiento", panelControl);

        // Pestaña 2: Tabla de Ventas (PostgreSQL)
        panelVentas = new PanelVentas();
        tabbedPane.addTab("Ver Ventas (BD)", panelVentas);

        
        panelReportes = new PanelReportes();
        tabbedPane.addTab("Ver Reportes y Errores", panelReportes);

        add(tabbedPane, BorderLayout.CENTER);

        // Panel Inferior con Botón Salir
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSalir = new JButton("Salir");
        btnSalir.addActionListener(e -> System.exit(0));
        panelInferior.add(btnSalir);
        add(panelInferior, BorderLayout.SOUTH);

        carpetaSeleccionada = new File("datos");

        // ACCIONES
        btnCarpeta.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                carpetaSeleccionada = chooser.getSelectedFile();
                txtRuta.setText(carpetaSeleccionada.getAbsolutePath());
                txtConsola.append("\nCarpeta seleccionada: " + carpetaSeleccionada.getAbsolutePath() + "\n");
            }
        });

        btnProcesar.addActionListener(e -> ejecutarProceso());
    }

    private void ejecutarProceso() {
        btnProcesar.setEnabled(false);
        btnCarpeta.setEnabled(false);
        txtConsola.append("\nIniciando procesamiento...\n");

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                ArchivoProcessor procesador = new ArchivoProcessor();
                procesador.procesarCarpeta(carpetaSeleccionada.toPath());

                ReporteAnalisis analisis = new ReporteAnalisis();
                ReporteErrores errores = new ReporteErrores();
                analisis.generarReporte(Path.of("reporte_analisis.txt"));
                errores.generarReporte(Path.of("errores_procesamiento.txt"));
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    txtConsola.append("\n¡Proceso finalizado con éxito! Reportes actualizados.\n");
                    JOptionPane.showMessageDialog(VentanaPrincipal.this, "Procesamiento completado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    
               
                    panelVentas.cargarDatosBD();
                    panelReportes.cargarArchivos();
                } catch (Exception ex) {
                    txtConsola.append("\nError: " + ex.getMessage() + "\n");
                    JOptionPane.showMessageDialog(VentanaPrincipal.this, "Ocurrió un error durante el procesamiento.", "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnProcesar.setEnabled(true);
                    btnCarpeta.setEnabled(true);
                }
            }
        };
        worker.execute();
    }
}


package Resto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import Cliente.Cliente;
import Mensajes.MensajePedirVinilo;

public class VentanaVinilos extends JFrame {
    private JTable tablaVinilos;
    private DefaultTableModel modelo;
    private JButton botonDescargar;
    private ObjectOutputStream outServidor;
    private Cliente cliente;

    public VentanaVinilos(Map<String, List<Vinilo>> vinilosPorUsuario, ObjectOutputStream outServidor, Cliente cliente) {
        this.outServidor = outServidor;
        this.cliente = cliente;
        setTitle("Lista de Vinilos Compartidos");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        //columnas
        String[] columnas = {"Usuario", "Título", "Artista", "Género", "Década"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        //rellenamos la tabla
        for (Map.Entry<String, List<Vinilo>> entry : vinilosPorUsuario.entrySet()) {
            String usuario = entry.getKey();
            for (Vinilo v : entry.getValue()) {
                modelo.addRow(new Object[]{
                        usuario,
                        v.getNombre(),
                        v.getArtista(),
                        v.getGenero(),
                        v.getDecada()
                });
            }
        }

        tablaVinilos = new JTable(modelo);
        tablaVinilos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(tablaVinilos);
        add(scroll, BorderLayout.CENTER);

        //botón de descargar
        botonDescargar = new JButton("Descargar vinilo seleccionado");
        botonDescargar.addActionListener(e -> {
            try {
                descargarSeleccionado();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        add(botonDescargar, BorderLayout.SOUTH);

        setLocationRelativeTo(null); //centramos
        setVisible(true);
    }

    private void descargarSeleccionado() throws IOException {
        int fila = tablaVinilos.getSelectedRow();
        if (fila != -1) {
            String usuario = (String) modelo.getValueAt(fila, 0);
            String nombreVinilo = (String) modelo.getValueAt(fila, 1);
            String artista = (String) modelo.getValueAt(fila, 2);
            String genero = (String) modelo.getValueAt(fila, 3);
            int decada = (int)modelo.getValueAt(fila, 4);
            Vinilo viniloADescargar = new Vinilo(nombreVinilo, artista, genero, decada);
            if(cliente.comprobarVinilo(viniloADescargar)){
                JOptionPane.showMessageDialog(this, "Ya tienes este vinilo, prueba con otro");
            }else{
                JOptionPane.showMessageDialog(this,
                        "Descargando \"" + nombreVinilo + "\" de " + usuario);
                this.outServidor.writeObject(new MensajePedirVinilo(usuario, nombreVinilo, this.cliente.getNombre()));
                this.outServidor.flush();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona un vinilo para descargar");
        }
    }
}


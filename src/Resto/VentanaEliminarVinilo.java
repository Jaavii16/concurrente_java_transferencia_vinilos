package Resto;

import Cliente.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;
public class VentanaEliminarVinilo extends JFrame {
    private JTable tablaVinilos;
    private DefaultTableModel modelo;
    private JButton botonEliminar;
    private Cliente cliente;

    public VentanaEliminarVinilo(String usuario, List<Vinilo> vinilosPorUsuario, Cliente cliente) {
        this.cliente = cliente;
        setTitle("Lista de Vinilos Propios");
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
        for (Vinilo v : vinilosPorUsuario) {
            modelo.addRow(new Object[]{
                    usuario,
                    v.getNombre(),
                    v.getArtista(),
                    v.getGenero(),
                    v.getDecada()
            });
        }

        tablaVinilos = new JTable(modelo);
        tablaVinilos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(tablaVinilos);
        add(scroll, BorderLayout.CENTER);

        //botón de descargar
        botonEliminar = new JButton("Eliminar vinilo seleccionado");
        botonEliminar.addActionListener(e -> {
            try {
                eliminarSeleccionado();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        add(botonEliminar, BorderLayout.SOUTH);

        setLocationRelativeTo(null); //para centrar
        setVisible(true);
    }

    private void eliminarSeleccionado() throws IOException {
        int fila = tablaVinilos.getSelectedRow();
        if (fila != -1) {
            String nombreVinilo = (String) modelo.getValueAt(fila, 1);
            this.cliente.eliminarVinilo(nombreVinilo);
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona un vinilo para eliminar");
        }
    }
}
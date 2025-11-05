package Resto;

import Cliente.Cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class VentanaPrincipal extends JFrame {

    private Cliente cliente;

    public VentanaPrincipal(Cliente cliente) {
        this.cliente = cliente;

        setTitle("Transferencia de Vinilos: " + cliente.getNombre()) ;
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());

        //Imagen en el panel central para decorar
        JLabel imagenLabel = new JLabel();
        ImageIcon icono = new ImageIcon(getClass().getResource("/vinilo.png"));
        Image imagen = icono.getImage().getScaledInstance(350, 350, Image.SCALE_SMOOTH);
        imagenLabel.setIcon(new ImageIcon(imagen));
        imagenLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(imagenLabel, BorderLayout.CENTER);

        //panel de botones
        JPanel panelBotones = new JPanel();
        JButton btnConsultar = new JButton("Consultar vinilos");
        JButton btnAñadir = new JButton("Añadir vinilo");
        JButton btnEliminar = new JButton("Eliminar vinilo");

        panelBotones.add(btnConsultar);
        panelBotones.add(btnAñadir);
        panelBotones.add(btnEliminar);

        add(panelBotones, BorderLayout.SOUTH);

        //acciones de botones
        btnConsultar.addActionListener(e -> {
            try {
                mostrarVinilosDisponibles();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        btnAñadir.addActionListener(e -> añadirVinilo());
        btnEliminar.addActionListener(e -> {
            try {
                eliminarVinilo();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        //listener para el cierre de ventana
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarConexionYSalir();
            }


        });

        setLocationRelativeTo(null); //centramos
        setVisible(true);


    }

    private void mostrarVinilosDisponibles() throws IOException {
        cliente.consultarVinilos();
    }


    private void añadirVinilo() {
        VentanaAnyadirVinilo ventana = new VentanaAnyadirVinilo(this, cliente);
    }

    private void eliminarVinilo() throws IOException {
        cliente.consultarVinilosPropios();
    }

    private void cerrarConexionYSalir() {
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Seguro que quieres salir?", "Confirmar salida",
                JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                cliente.cerrarConexion(); // llamamos al método cerrarConexion() del Cliente
            } catch (IOException ex) {
                System.err.println("[VentanaPrincipal] Error cerrando conexión: " + ex.getMessage());
            }
            System.exit(0); // cerramos la aplicación
        }
    }
}

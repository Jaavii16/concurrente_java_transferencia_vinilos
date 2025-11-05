package Resto;

import Cliente.Cliente;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class VentanaAnyadirVinilo extends JDialog {

    public VentanaAnyadirVinilo(JFrame parent, Cliente cliente) {
        super(parent, "Añadir nuevo vinilo", true);
        setLayout(new GridLayout(5, 2, 10, 10));
        setSize(400, 250);
        setLocationRelativeTo(parent);

        //casillas a rellenar
        JLabel lblNombre = new JLabel("Nombre del vinilo:");
        JTextField txtNombre = new JTextField();

        JLabel lblArtista = new JLabel("Nombre del artista:");
        JTextField txtArtista = new JTextField();

        JLabel lblGenero = new JLabel("Género:");
        JTextField txtGenero = new JTextField();

        JLabel lblDecada = new JLabel("Década:");
        JTextField txtDecada = new JTextField();

        JButton btnAceptar = new JButton("Aceptar");

        add(lblNombre); add(txtNombre);
        add(lblArtista); add(txtArtista);
        add(lblGenero); add(txtGenero);
        add(lblDecada); add(txtDecada);
        add(new JLabel());
        add(btnAceptar);

        //lo que ejecutamos al hacer clic en aceptar
        btnAceptar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String artista = txtArtista.getText().trim();
            String genero = txtGenero.getText().trim();
            String decadaStr = txtDecada.getText().trim();

            if (nombre.isEmpty() || artista.isEmpty() || genero.isEmpty() || decadaStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor completa todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int decada = Integer.parseInt(decadaStr);
                Vinilo nuevoVinilo = new Vinilo(nombre, artista, genero, decada);
                if(cliente.comprobarVinilo(nuevoVinilo)){
                    JOptionPane.showMessageDialog(this, "Ya tienes este vinilo");
                }
                else{
                    cliente.anyadirVinilo(nuevoVinilo);
                }
                dispose(); //cerrar ventana
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "La década debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        setVisible(true);
    }
}

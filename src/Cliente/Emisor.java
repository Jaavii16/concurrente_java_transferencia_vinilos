package Cliente;

import Mensajes.*;
import Resto.Vinilo;

import java.io.*;
import java.net.*;

public class Emisor implements Runnable {
    private int puerto;
    private ObjectOutputStream outP2P;
    private ObjectInputStream inP2P;
    private Usuario usuario;
    public Emisor(int puerto, Usuario usuario) {
        this.puerto = puerto;
        this.usuario = usuario;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            Socket socket = serverSocket.accept();

            outP2P = new ObjectOutputStream(socket.getOutputStream());
            outP2P.flush();
            inP2P = new ObjectInputStream(socket.getInputStream());

            new Thread(new OyenteP2P(socket, inP2P, outP2P, this)).start(); // oyente que maneja la recepción

        } catch (IOException e) {
            System.err.println("[EmisorP2P] Error: " + e.getMessage());
        }
    }

    protected void confConexionP2P(String nombreVinilo) throws IOException {
        Vinilo vinilo = null;
        for(Vinilo v: this.usuario.getVinilosPropios()){
            if(v.getNombre().equals(nombreVinilo)){
                vinilo = v;
            }
        }
        this.outP2P.writeObject(new MensajeRecibirViniloP2P(vinilo));
    }
}

package Cliente;

import Mensajes.*;
import Resto.Vinilo;

import java.io.*;
import java.net.Socket;

public class Receptor implements Runnable {
    private String ip;
    private int puerto;
    private String nombreVinilo;
    private Cliente cliente;
    private ObjectOutputStream outP2P;
    private ObjectInputStream inP2P;

    public Receptor(String ip, int puerto, String nombreVinilo, Cliente cliente) {
        this.ip = ip;
        this.puerto = puerto;
        this.nombreVinilo = nombreVinilo;
        this.cliente = cliente;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(ip, puerto);
            outP2P = new ObjectOutputStream(socket.getOutputStream());
            outP2P.flush();
            inP2P = new ObjectInputStream(socket.getInputStream());

            new Thread(new OyenteP2P(socket, inP2P, outP2P, this)).start();
            outP2P.writeObject(new MensajeConfConexionP2P(nombreVinilo));
            outP2P.flush();

        } catch (IOException e) {
            System.err.println("[ReceptorP2P] Error al conectar al emisor: " + e.getMessage());
        }
    }


    protected void recibirVinilo(Vinilo v) throws IOException {
        cliente.anyadirVinilo(v);
    }
}

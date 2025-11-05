package Cliente;

import Mensajes.*;
import Resto.Vinilo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Mensajes.MensajesP2PEnum;

import javax.swing.*;

public class OyenteP2P implements Runnable {

    private ObjectInputStream inP2P;
    private ObjectOutputStream outP2P;
    private Socket socket;

    private Emisor emisor = null;
    private Receptor receptor = null;

    public OyenteP2P(Socket socket, ObjectInputStream inP2P, ObjectOutputStream outP2P, Emisor emisor) {
        this.socket = socket;
        this.inP2P = inP2P;
        this.outP2P = outP2P;
        this.emisor = emisor;
    }

    public OyenteP2P(Socket socket, ObjectInputStream inP2P, ObjectOutputStream outP2P, Receptor receptor) {
        this.socket = socket;
        this.inP2P = inP2P;
        this.outP2P = outP2P;
        this.receptor = receptor;
    }

    @Override
    public void run() {
        try {
            while (!socket.isClosed()) {
                if (socket.getInputStream().available() > 0) {
                    Mensaje m = (Mensaje) this.inP2P.readObject();
                    MensajesP2PEnum tipo = MensajesP2PEnum.fromCodigo(m.getTipo());

                    switch (tipo) {
                        case CONF_CONEXION_P2P:
                            if (emisor != null) {
                                MensajeConfConexionP2P m1 = (MensajeConfConexionP2P) m;
                                emisor.confConexionP2P(m1.getNombreVinilo());
                            }
                            break;

                        case RECIBIR_VINILO:
                            if (receptor != null) {
                                MensajeRecibirViniloP2P m2 = (MensajeRecibirViniloP2P) m;
                                receptor.recibirVinilo(m2.getV());
                                cerrarConexion();
                                return;
                            }
                            break;
                    }
                } else {
                    Thread.sleep(20);
                }
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            System.err.println("[OyenteP2P] Error: " + e.getMessage());
        } finally {
            cerrarConexion();
        }
    }

    private void cerrarConexion() {
        try {
            if (outP2P != null) outP2P.close();
        } catch (IOException ignored) {}
        try {
            if (inP2P != null) inP2P.close();
        } catch (IOException ignored) {}
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
    }
}

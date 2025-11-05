package Servidor;

import Cliente.Monitor;
import Cliente.Usuario;
import Locks.LockTicket;
import Locks.Locks;
import Mensajes.*;

import javax.swing.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class OyenteCliente implements  Runnable{

    private final static int CLIENTES_PERMITIDOS = 1000;
    private Socket socket;
    private ObjectInputStream fin;
    private ObjectOutputStream fout;
    private Servidor servidor;
    private Usuario usuario;
    private String ip;

    Locks lock;
    public OyenteCliente(Socket s, Servidor servidor) throws IOException, InterruptedException {
        this.socket = s;//socket que le pasa el Servidor
        this.fout = new ObjectOutputStream(s.getOutputStream());//creamos flujo de salida
        this.fout.flush(); //muy importante para que empiece a funcionar
        this.fin = new ObjectInputStream(s.getInputStream());//creamos flujo de entrada
        this.lock = new LockTicket(CLIENTES_PERMITIDOS);
        this.servidor = servidor;
        this.ip = socket.getInetAddress().getHostAddress();
        this.servidor.registrarClienteTemporal(fin, fout, ip);
    }

    @Override
    public void run() {
        try {
            while (!socket.isClosed()) {
                try {
                    if (socket.getInputStream().available() > 0) {
                        Mensaje m = (Mensaje) fin.readObject();
                        procesarMensaje(m);
                    } else {
                        Thread.sleep(20); //esperamos un poco si no hay datos
                    }
                } catch (EOFException e) {
                    Thread.sleep(20);
                } catch (SocketException se) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            System.err.println("[OyenteCliente] Error de comunicación: " + e.getMessage());
        } finally {
            cerrarConexion();
        }
    }

    private void procesarMensaje(Mensaje m) throws IOException, InterruptedException, ClassNotFoundException {
        MensajesEnum tipo = MensajesEnum.fromCodigo(m.getTipo());
        switch (tipo) {
            case PEDIR_PUERTO_INI:
                MensajePedirPuertoIni m7 = (MensajePedirPuertoIni)m;
                this.servidor.enviarPuertoIni(m7.getId());
                break;
            case CONEXION:
                this.lock.takeLock();
                try {
                    MensajeConexion conexion = (MensajeConexion) m;
                    this.usuario = new Usuario(conexion.getNombre(), new ArrayList<>(conexion.getVinilos()));
                    this.servidor.anyadirUsuario(this.usuario, conexion.getNombre(), this.fin, this.fout, conexion.getIp(), conexion.getPuerto(), conexion.getVinilos(), conexion.getIdTemporal());
                } finally {
                    this.lock.releaseLock();
                }
                break;
            case COMPROBAR_NOMBRE:
                this.lock.takeLock();
                try {
                    MensajeComprobarNombre m1 = (MensajeComprobarNombre) m;
                    this.servidor.comprobarNombre(m1.getNombre(), this.fout);
                } finally {
                    this.lock.releaseLock();
                }
                break;
            case LISTA_VINILOS:
                this.lock.takeLock();
                try {
                    MensajePedirLista m2 = (MensajePedirLista) m;
                    this.servidor.mostrarLista(m2.getNomCliente());
                } finally {
                    this.lock.releaseLock();
                }
                break;
            case PEDIR_VINILO_CS:
                MensajePedirVinilo m3 = (MensajePedirVinilo) m;
                this.lock.takeLock();
                try {
                    if(!this.servidor.viniloPedido(m3.getEmisor(), m3.getNombreVinilo(), m3.getReceptor())){
                        JOptionPane optionPane = new JOptionPane(
                                "Vinilo no disponible para descargar",
                                JOptionPane.WARNING_MESSAGE);

                        JDialog dialog = optionPane.createDialog(null, "Advertencia");
                        dialog.setAlwaysOnTop(true); // Esto lo mantiene delante de todas las ventanas
                        dialog.setModal(true);       // Opcional: bloquea otras interacciones
                        dialog.setVisible(true);
                    }
                } finally {
                    this.lock.releaseLock();
                }
                break;
            case CERRAR_CONEXION:
                this.lock.takeLock();
                try {
                    servidor.eliminarUsuario(usuario);
                } finally {
                    this.lock.releaseLock();
                }
                cerrarConexion();
                break;
            case ADD_VINILO:
                this.lock.takeLock();
                try {
                    MensajeAddVinilo m4 = (MensajeAddVinilo) m;
                    this.servidor.anyadirVinilo(m4.getNombreUsuario(), m4.getVinilo());
                } finally {
                    this.lock.releaseLock();
                }
                break;
            case REMOVE_VINILO:
                this.lock.takeLock();
                try {
                    MensajeRemoveVinilo m5 = (MensajeRemoveVinilo) m;
                    this.servidor.eliminarVinilo(m5.getNombreUsuario(), m5.getVinilo());
                } finally {
                    this.lock.releaseLock();
                }
                break;
            case CONF_VINILO_CS:
                MensajeConfViniloCS m6 = (MensajeConfViniloCS) m;
                this.servidor.infoEmisor(m6.getPuerto(), m6.getIp(), m6.getReceptor(), m6.getNombreVinilo());
                break;
            case PEDIR_PUERTO_P2P:
                MensajePedirPuertoP2P m8 = (MensajePedirPuertoP2P) m;
                this.servidor.enviarPuertoP2P(m8.getNombre());
                break;
            case PEDIR_LISTA_PROPIA:
                this.lock.takeLock();
                try {
                    MensajePedirListaPropia m9 = (MensajePedirListaPropia) m;
                    this.servidor.mostrarListaPropia(m9.getNombre());
                } finally {
                    this.lock.releaseLock();
                }
                break;
            default:
                System.out.println("[OyenteCliente] Tipo de mensaje no reconocido: " + tipo);
        }
    }

    private void cerrarConexion() {
        try {
            if (fout != null) fout.close();
        } catch (IOException ignored) {}
        try {
            if (fin != null) fin.close();
        } catch (IOException ignored) {}
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
    }

}

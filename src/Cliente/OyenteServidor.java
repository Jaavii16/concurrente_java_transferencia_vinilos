package Cliente;

import Mensajes.*;
import Resto.Vinilo;

import javax.swing.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OyenteServidor implements Runnable{

    private ObjectInputStream fin;
    private ObjectOutputStream fout;
    private Cliente cliente;
    private Map<String, List<Vinilo>> vinilosAcumulados;
    private Socket socketServidor;

    public OyenteServidor(Socket socketServidor, ObjectInputStream fin, ObjectOutputStream fout, Cliente cliente) {
        this.socketServidor = socketServidor;
        this.fin = fin;
        this.fout = fout;
        this.cliente = cliente;
        this.vinilosAcumulados = new HashMap<>();
    }

    @Override
    public void run() {
        try {
            while (!socketServidor.isClosed()) {
                try {
                    if (socketServidor.getInputStream().available() > 0) {
                        Mensaje m = (Mensaje) fin.readObject();
                        procesarMensaje(m);
                    } else {
                        Thread.sleep(20);
                    }
                } catch (EOFException e) {
                    Thread.sleep(20); //no hay datos, esperamos un poco
                } catch (SocketException se) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            cerrarConexionServidor();
        }
    }



    private void procesarMensaje(Mensaje m) throws IOException, InterruptedException {
        MensajesEnum tipo = MensajesEnum.fromCodigo(m.getTipo());
        switch (tipo) {
            case ENVIAR_ID_TEMPORAL:
                MensajeEnviarIdTemporal m9 = (MensajeEnviarIdTemporal) m;
                this.cliente.setIdTemporal(m9.getId());
                break;
            case ENVIAR_PUERTO_INI:
                MensajeEnviarPuertoIni m7 = (MensajeEnviarPuertoIni) m;
                this.cliente.setPuertoIni(m7.getPuerto());
                break;
            case CONF_CONEXION:
                MensajeConfConexion m4 = (MensajeConfConexion) m;
                cliente.usuarioRegistrado(m4.getUsuario());
                JOptionPane.showMessageDialog(null, "Usuario correctamente registrado");
                break;
            case CONF_COMPROBAR_NOMBRE:
                MensajeConfComprobarCliente m1 = (MensajeConfComprobarCliente) m;
                cliente.existe(m1.existe());
                break;
            case FILA_LISTA_VINILOS:
                MensajeFilaListaVinilos m2 = (MensajeFilaListaVinilos) m;
                this.vinilosAcumulados.put(m2.getNombreUsuario(), m2.getVinilos());
                if (m2.esUltimo()) {
                    this.cliente.mostrarVinilos(this.vinilosAcumulados);
                    this.vinilosAcumulados = new HashMap<>();
                }
                break;
            case LISTA_VINILOS_PROPIOS:
                MensajeListaVinilosPropios m10 = (MensajeListaVinilosPropios) m;
                this.cliente.mostrarVinilosPropios(m10.getVinilos());
                break;
            case VINILO_ENVIADO_SC:
                MensajeEnviarViniloSC m3 = (MensajeEnviarViniloSC) m;
                cliente.anyadirVinilo(m3.getVinilo());
                break;
            case PEDIR_VINILO_SC:
                MensajePedirViniloSC m5 = (MensajePedirViniloSC) m;
                this.cliente.enviarViniloCS(m5.getReceptor(), m5.getNombreVinilo());
                break;
            case CONF_VINILO_SC:
                MensajeConfViniloSC m6 = (MensajeConfViniloSC) m;
                cliente.iniciarConexionP2P(m6.getIp(), m6.getPuerto(), m6.getNombreVinilo());
                break;
            case ENVIAR_PUERTO_P2P:
                MensajeEnviarPuertoP2P m8 = (MensajeEnviarPuertoP2P) m;
                this.cliente.setPuertoP2P(m8.getPuerto());
                break;
            case VINILO_ELIMINADO:
                MensajeViniloEliminado m11 = (MensajeViniloEliminado) m;
                JOptionPane.showMessageDialog(null, "Vinilo eliminado correctamente.");
                this.cliente.mostrarVinilosPropios(m11.getVinilos());
                break;
            default:
                System.out.println("[OyenteServidor] Tipo de mensaje no reconocido: " + tipo);
        }
    }

    private void cerrarConexionServidor() {
        try {
            if (fout != null) fout.close();
            if (fin != null) fin.close();
            if (socketServidor != null && !socketServidor.isClosed()) socketServidor.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

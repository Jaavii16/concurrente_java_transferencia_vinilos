package Mensajes;


import Resto.Vinilo;

import java.util.List;

public class MensajeConexion extends Mensaje {

    private String nombre;
    private int puerto;
    private List<Vinilo> vinilos;
    private String ip;
    private int idTemporal;

    public MensajeConexion(String nombre, int puerto, List<Vinilo> vinilos, String ip, int idTemporal) {
        this.nombre = nombre;
        this.puerto = puerto;
        this.vinilos = vinilos;
        this.ip = ip;
        this.idTemporal = idTemporal;
    }


    public String getNombre() {
        return nombre;
    }

    public int getPuerto() {
        return puerto;
    }

    public List<Vinilo> getVinilos() {
        return vinilos;
    }

    public String getIp() {
        return ip;
    }

    public int getIdTemporal() {
        return idTemporal;
    }

    @Override
    public int getTipo() {
        return MensajesEnum.CONEXION.getNumero();
    }
}

package Mensajes;

import Resto.Vinilo;

import java.util.List;

public class MensajeFilaListaVinilos extends Mensaje{
    private String nombreUsuario;
    private List<Vinilo> vinilos;
    private boolean esUltimo;

    public MensajeFilaListaVinilos(String nombreUsuario, List<Vinilo> vinilos, boolean esUltimo) {
        this.nombreUsuario = nombreUsuario;
        this.vinilos = vinilos;
        this.esUltimo = esUltimo;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public List<Vinilo> getVinilos() {
        return vinilos;
    }

    public boolean esUltimo() {
        return esUltimo;
    }

    @Override
    public int getTipo() {
        return MensajesEnum.FILA_LISTA_VINILOS.getNumero();
    }
}

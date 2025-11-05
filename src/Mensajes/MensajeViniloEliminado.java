package Mensajes;

import Resto.Vinilo;

import java.util.List;

public class MensajeViniloEliminado extends Mensaje{
    private List<Vinilo> vinilos;
    public MensajeViniloEliminado(List<Vinilo> vinilos) {
        this.vinilos = vinilos;
    }

    public List<Vinilo> getVinilos() {
        return vinilos;
    }

    @Override
    public int getTipo() {
        return MensajesEnum.VINILO_ELIMINADO.getNumero();
    }
}

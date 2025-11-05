package Mensajes;

import Resto.Vinilo;

import java.util.List;

public class MensajeListaVinilosPropios extends Mensaje{
    private List<Vinilo> vinilos;

    public MensajeListaVinilosPropios( List<Vinilo> vinilos) {
        this.vinilos = vinilos;
    }


    public List<Vinilo> getVinilos() {
        return vinilos;
    }


    @Override
    public int getTipo() {
        return MensajesEnum.LISTA_VINILOS_PROPIOS.getNumero();
    }
}

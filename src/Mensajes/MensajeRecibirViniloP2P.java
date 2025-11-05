package Mensajes;

import Resto.Vinilo;

public class MensajeRecibirViniloP2P extends Mensaje{
    private Vinilo v;

    public MensajeRecibirViniloP2P(Vinilo v) {
        this.v = v;
    }

    public Vinilo getV() {
        return v;
    }

    @Override
    public int getTipo() {
        return MensajesP2PEnum.RECIBIR_VINILO.getNumero();
    }
}

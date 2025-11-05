package Mensajes;

import Resto.Vinilo;

public class MensajeEnviarViniloSC extends Mensaje{
    private Vinilo vinilo;
    public MensajeEnviarViniloSC(Vinilo v) {
        this.vinilo = v;
    }

    public Vinilo getVinilo() {
        return vinilo;
    }

    @Override
    public int getTipo() {
        return MensajesEnum.VINILO_ENVIADO_SC.getNumero();
    }
}

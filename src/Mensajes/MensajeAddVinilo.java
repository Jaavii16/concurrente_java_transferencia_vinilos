package Mensajes;

import Resto.Vinilo;
import java.io.Serializable;

public class MensajeAddVinilo extends Mensaje {
    private String nombreUsuario;
    private Vinilo vinilo;

    public MensajeAddVinilo(String nombreUsuario, Vinilo vinilo) {
        this.nombreUsuario = nombreUsuario;
        this.vinilo = vinilo;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public Vinilo getVinilo() {
        return vinilo;
    }

    @Override
    public int getTipo() {
        return MensajesEnum.ADD_VINILO.getNumero();
    }
}

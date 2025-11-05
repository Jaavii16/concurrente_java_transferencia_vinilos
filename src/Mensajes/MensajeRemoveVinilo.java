package Mensajes;

import Resto.Vinilo;
import java.io.Serializable;

public class MensajeRemoveVinilo extends Mensaje {
    private String nombreUsuario;
    private String vinilo;

    public MensajeRemoveVinilo(String nombreUsuario, String vinilo) {
        this.nombreUsuario = nombreUsuario;
        this.vinilo = vinilo;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getVinilo() {
        return vinilo;
    }

    @Override
    public int getTipo() {
        return MensajesEnum.REMOVE_VINILO.getNumero();
    }
}

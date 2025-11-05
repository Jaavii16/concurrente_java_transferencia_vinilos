package Mensajes;

import Mensajes.Mensaje;

public class MensajeConfComprobarCliente extends Mensaje {

    private boolean existe;

    public MensajeConfComprobarCliente(boolean existe) {
        this.existe = existe;
    }

    public boolean existe() {
        return existe;
    }

    @Override
    public int getTipo() {
        return MensajesEnum.CONF_COMPROBAR_NOMBRE.getNumero();
    }
}

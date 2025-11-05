package Mensajes;

import Cliente.Cliente;
import Cliente.Usuario;

public class MensajeConfConexion extends Mensaje{

    private Usuario usuario;
    public MensajeConfConexion(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    @Override
    public int getTipo() {
        return MensajesEnum.CONF_CONEXION.getNumero();
    }
}

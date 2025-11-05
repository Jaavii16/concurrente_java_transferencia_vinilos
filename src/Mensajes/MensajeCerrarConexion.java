package Mensajes;

public class MensajeCerrarConexion extends Mensaje {


    public MensajeCerrarConexion() {
    }

    @Override
    public int getTipo() {
        return MensajesEnum.CERRAR_CONEXION.getNumero();
    }
}

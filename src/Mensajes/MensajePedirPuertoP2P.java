package Mensajes;

public class MensajePedirPuertoP2P extends Mensaje{

    private String nombre;

    public MensajePedirPuertoP2P(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public int getTipo() {
        return MensajesEnum.PEDIR_PUERTO_P2P.getNumero();
    }
}

package Mensajes;

public class MensajeEnviarPuertoP2P extends Mensaje{
    private int puerto;
    public MensajeEnviarPuertoP2P(int puerto) {
        this.puerto = puerto;
    }

    public int getPuerto() {
        return puerto;
    }

    @Override
    public int getTipo() {
        return MensajesEnum.ENVIAR_PUERTO_P2P.getNumero();
    }
}

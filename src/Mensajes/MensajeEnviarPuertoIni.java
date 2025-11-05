package Mensajes;

public class MensajeEnviarPuertoIni extends Mensaje{
    private int puerto;
    public MensajeEnviarPuertoIni(int puerto) {
        this.puerto = puerto;
    }

    public int getPuerto() {
        return puerto;
    }

    @Override
    public int getTipo() {
        return MensajesEnum.ENVIAR_PUERTO_INI.getNumero();
    }
}

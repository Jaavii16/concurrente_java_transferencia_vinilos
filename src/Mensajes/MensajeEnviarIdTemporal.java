package Mensajes;

public class MensajeEnviarIdTemporal extends Mensaje{
    private int id;
    public MensajeEnviarIdTemporal(int numClientes) {
        this.id = numClientes;
    }

    public int getId() {
        return id;
    }

    @Override
    public int getTipo() {
        return MensajesEnum.ENVIAR_ID_TEMPORAL.getNumero();
    }
}

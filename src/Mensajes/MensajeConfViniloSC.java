package Mensajes;

public class MensajeConfViniloSC extends Mensaje{

    private String ip;
    private int puerto;
    private String nombreVinilo;
    public MensajeConfViniloSC(int puerto, String ip, String nombreVinilo) {
        this.ip = ip;
        this.puerto = puerto;
        this.nombreVinilo = nombreVinilo;
    }

    public String getIp() {
        return ip;
    }

    public int getPuerto() {
        return puerto;
    }

    public String getNombreVinilo() {
        return nombreVinilo;
    }

    @Override
    public int getTipo() {
        return MensajesEnum.CONF_VINILO_SC.getNumero();
    }
}

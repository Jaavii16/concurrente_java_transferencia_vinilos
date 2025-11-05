package Mensajes;

public class MensajeConfViniloCS extends Mensaje{
    private String ip;
    private int puerto;
    private String receptor;
    private String nombreVinilo;
    public MensajeConfViniloCS(String ip, int puerto, String receptor, String nombreVinilo) {
        this.ip = ip;
        this.puerto = puerto;
        this.receptor = receptor;
        this.nombreVinilo = nombreVinilo;
    }

    public String getIp() {
        return ip;
    }

    public int getPuerto() {
        return puerto;
    }

    public String getReceptor() {
        return receptor;
    }

    public String getNombreVinilo() {
        return nombreVinilo;
    }

    @Override
    public int getTipo() {
        return MensajesEnum.CONF_VINILO_CS.getNumero();
    }
}

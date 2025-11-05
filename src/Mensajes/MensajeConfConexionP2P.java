package Mensajes;

public class MensajeConfConexionP2P extends Mensaje{

    private String nombreVinilo;
    public MensajeConfConexionP2P(String nombreVinilo) {
        this.nombreVinilo = nombreVinilo;
    }

    public String getNombreVinilo() {
        return nombreVinilo;
    }

    @Override
    public int getTipo() {
        return MensajesP2PEnum.CONF_CONEXION_P2P.getNumero();
    }
}

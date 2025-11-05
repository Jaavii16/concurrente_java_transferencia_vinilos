package Mensajes;
import Cliente.Cliente;

public class MensajePedirVinilo extends Mensaje{

    private String nombreVinilo;
    private String emisor;
    private String receptor;
    public MensajePedirVinilo(String emisor, String nombreVinilo, String receptor) {
        this.nombreVinilo = nombreVinilo;
        this.emisor = emisor;
        this.receptor = receptor;
    }

    public String getNombreVinilo() {
        return nombreVinilo;
    }

    public String getReceptor() {
        return receptor;
    }

    public String getEmisor() {
        return emisor;
    }

    @Override
    public int getTipo() {
        return MensajesEnum.PEDIR_VINILO_CS.getNumero();
    }
}

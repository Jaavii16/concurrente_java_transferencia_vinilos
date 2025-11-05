package Mensajes;

public class MensajePedirViniloSC extends Mensaje{
    private String receptor;
    private String nombreVinilo;

    public MensajePedirViniloSC(String receptor, String nombreVinilo) {
        this.receptor = receptor;
        this.nombreVinilo = nombreVinilo;
    }

    public String getReceptor() {
        return receptor;
    }

    public String getNombreVinilo() {
        return nombreVinilo;
    }


    @Override
    public int getTipo() {
        return MensajesEnum.PEDIR_VINILO_SC.getNumero();
    }
}

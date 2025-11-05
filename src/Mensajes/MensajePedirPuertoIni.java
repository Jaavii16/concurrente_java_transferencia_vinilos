package Mensajes;

public class MensajePedirPuertoIni extends Mensaje{

    private int id;
    public MensajePedirPuertoIni(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public int getTipo() {
        return MensajesEnum.PEDIR_PUERTO_INI.getNumero();
    }
}

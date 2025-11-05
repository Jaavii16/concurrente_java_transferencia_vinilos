package Mensajes;

public class MensajePedirListaPropia extends Mensaje{
    private String nombre;

    public MensajePedirListaPropia(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public int getTipo() {
        return MensajesEnum.PEDIR_LISTA_PROPIA.getNumero();
    }
}

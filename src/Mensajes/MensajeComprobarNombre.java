package Mensajes;

public class MensajeComprobarNombre extends Mensaje {

    private String nombre;


    public MensajeComprobarNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }


    @Override
    public int getTipo() {
        return MensajesEnum.COMPROBAR_NOMBRE.getNumero();
    }
}

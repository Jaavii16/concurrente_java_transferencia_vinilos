package Mensajes;


public class MensajePedirLista extends Mensaje{
    String nomCliente;
    public MensajePedirLista(String cliente) {
        this.nomCliente = cliente;
    }

    public String getNomCliente() {
        return nomCliente;
    }

    @Override
    public int getTipo() {
        return MensajesEnum.LISTA_VINILOS.getNumero();
    }
}

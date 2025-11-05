package Mensajes;

public enum MensajesEnum {
    // Mensajes entre clientes y servidores
    CONEXION(1), //de cliente a servidor
    CONF_CONEXION(2), //de servidor a cliente
    LISTA_VINILOS(3), //de cliente a servidor
    FILA_LISTA_VINILOS(14), //de servidor a cliente
    PEDIR_VINILO_CS(4), //de cliente a servidor
    PEDIR_VINILO_SC(5), //de servidor a cliente
    VINILO_ENVIADO_CS(6), //de cliente a servidor
    VINILO_ENVIADO_SC(7), //de servidor a cliente
    CERRAR_CONEXION(8), //de cliente a servidor
    CONF_CERRAR_CONEXION(9), //de servidor a cliente
    COMPROBAR_NOMBRE(10), //de cliente a servidor
    CONF_COMPROBAR_NOMBRE(11), //de servidor a cliente
    ADD_VINILO(12), //de cliente a servidor
    REMOVE_VINILO(13), //de cliente a servidor

    CONF_VINILO_CS(15), //de cliente a servidor
    CONF_VINILO_SC(16), //de servidor a cliente
    PEDIR_PUERTO_INI(17), //de cliente a servidor
    ENVIAR_PUERTO_INI(18), //de servidor a cliente
    PEDIR_PUERTO_P2P(19), //de cliente a servidor
    ENVIAR_PUERTO_P2P(20), //de servidor a cliente
    ENVIAR_ID_TEMPORAL(21), //de servidor a cliente
    PEDIR_LISTA_PROPIA(22), //de cliente a servidor
    LISTA_VINILOS_PROPIOS(23), //de servidor a cliente
    VINILO_ELIMINADO(24); //de servidor a cliente

    private final int numero;

    MensajesEnum(int numero) {
        this.numero = numero;
    }

    public int getNumero() {
        return numero;
    }

    // Método para obtener enum desde un número
    public static MensajesEnum fromCodigo(int numero) {
        for (MensajesEnum tipo : values()) {
            if (tipo.getNumero() == numero) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Código de mensaje inválido: " + numero);
    }
}

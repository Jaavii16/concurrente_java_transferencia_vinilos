package Mensajes;

public enum MensajesP2PEnum {
    RECIBIR_VINILO(1), CONF_CONEXION_P2P(2);
    private final int numero;

    MensajesP2PEnum(int numero) {
        this.numero = numero;
    }

    public int getNumero() {
        return numero;
    }

    // Método para obtener enum desde un número
    public static MensajesP2PEnum fromCodigo(int numero) {
        for (MensajesP2PEnum tipo : values()) {
            if (tipo.getNumero() == numero) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Código de mensaje inválido: " + numero);
    }
}

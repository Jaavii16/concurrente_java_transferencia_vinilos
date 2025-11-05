package Cliente;

public interface Almacen<T> {
    /**
     * Almacena (como último) un producto en el almacén. Si no hay
     * hueco, el proceso que ejecute el método se bloqueará hasta que lo haya.
     */
    void almacenar(T producto);

    /**
     * Extrae el primer producto disponible. Si no hay productos, el
     * proceso que ejecute el método se bloqueará hasta que se almacene uno.
     */
    T extraer();
}

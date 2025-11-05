package Cliente;

import Locks.Entero;

public class Trabajo<T> implements Almacen<T> {
    private T[] buffer;
    private int k;
    private Entero ini, fin;


    public Trabajo(T[] buffer, int size) {
        this.buffer = buffer;
        this.k = size;
        this.ini = new Entero();
        this.fin = new Entero();
    }

    @Override
    public synchronized void almacenar(T producto) {
        buffer[fin.entero] = producto;
        fin.entero = (fin.entero + 1) % k;
    }

    @Override
    public synchronized T extraer() {
        T item = buffer[ini.entero];
        ini.entero = (ini.entero + 1) % k;
        return item;
    }
}

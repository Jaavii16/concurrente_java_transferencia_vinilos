package Cliente;

import java.util.concurrent.Semaphore;
import Locks.Entero;

public class Productor extends Thread {

    private Semaphore mutexP;

    private Semaphore empty;
    private Semaphore full;
    private int id;
    private Trabajo t;
    private Entero cont;

    public Productor(Semaphore empty, Semaphore full, Semaphore mutexP, Trabajo t, int id, Entero cont) {
        this.mutexP = mutexP;
        this.full = full;
        this.empty = empty;
        this.t = t;
        this.id = id;
        this.cont = cont;
    }


    @Override
    public void run() {
        try {
            empty.acquire();
            mutexP.acquire();
            Producto p = new Producto(cont.entero);
            cont.entero = cont.entero + 1;
            almacenar(p);
            mutexP.release();
            full.release();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


    public void almacenar(Producto producto) {
        t.almacenar(producto);
    }


}

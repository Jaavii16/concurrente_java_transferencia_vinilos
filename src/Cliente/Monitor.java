package Cliente;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor {
    private int nr;
    private int nw;
    private final Lock l = new ReentrantLock();
    private final Condition writers = l.newCondition();
    private final Condition readers = l.newCondition();
    public void releaseReader(){
        l.lock();
        nr--;
        readers.signalAll();
        writers.signal();
        l.unlock();
    }
    public void releaseWriter(){
        l.lock();
        nw--;
        //despertamos todos los lectores y el escritor de la cabeza de la cola
        writers.signal();
        readers.signalAll();
        l.unlock();
    }

    public void requestReader() throws InterruptedException {
        l.lock();
        while(nw > 0){//si hay writers me espero en la cola (usamos while para espera activa; que no me quiten el turno)
            readers.await();
        }
        nr++;//cuando me hayan despertado aqui tendre implicitamente el lock
        l.unlock();
    }

    public void requestWriter() throws InterruptedException {
        l.lock();
        while(nw>0 || nr > 0){
            writers.await();
        }
        nw++;
        writers.signal();
        l.unlock();
    }
}


package Locks;

import java.util.concurrent.atomic.AtomicInteger;

public class LockTicket implements Locks {

    int turn;
    AtomicInteger number;
    Entero next;
    int N;

    public LockTicket(int N) {
        this.turn = 0;//esto es nuestro turno, da igual a que lo inicialicemos porque cuando hacemos takelock cogemos el ticket(number)
        this.number =  new AtomicInteger(1);//los tickets empiezan en 1
        this.next = new Entero(1);//el turno de la pantalla empieza en 1
        this.N = N;
    }

    @Override
    public void takeLock() {
        turn = number.getAndIncrement();//cogemos el ticket que haya en la maquinita
        if(turn == N){//si llega al ultimo turno reiniciamos
            number.getAndAdd(-N);//la maquina se vuelve a poner a 1
        }
        else if(turn > N){
            turn -= N;
        }

        while(turn != next.entero);//esto espera a que nuestro turno sea el que hay en la pantalla (nos toca)
    }

    @Override
    public void releaseLock(){
        next.entero = (next.entero%N)+1;
    }
}

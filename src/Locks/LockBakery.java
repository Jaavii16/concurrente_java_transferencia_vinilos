package Locks;
public class LockBakery implements Locks{
    private Entero[] turn;
    int i;
    int M;//numero de procesos

    public LockBakery(int M, int i) { //M es el numero de procesos
        this.M = M;
        this.turn = new Entero[M];
        for(int j = 0;j<M;j++)turn[j] = new Entero();
        this.i = i;
    }


    private boolean turnComparator (int a, int b, int c, int d){
        return(a > c || (a == c && b>d));
    }

    @Override
    public void takeLock() {
        int max = 0;
        for (int i = 0;i<M;i++) {
            max = Math.max(max, turn[i].entero);
        }
        turn[i].entero = (max + 1);

        for(int j = 0;j<M;j++){
            if(j != i){
                while(turnComparator(turn[i].entero, i, turn[j].entero, j) && turn[j].entero != 0);
            }
        }
    }

    @Override
    public void releaseLock(){
        turn[i].entero = 0;
    }

}

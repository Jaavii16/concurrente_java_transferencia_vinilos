package Cliente;

import Locks.LockBakery;
import Locks.LockTicket;
import Mensajes.*;
import Resto.VentanaEliminarVinilo;
import Resto.VentanaPrincipal;
import Resto.VentanaVinilos;
import Resto.Vinilo;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import Locks.Entero;
import Locks.Locks;


public class Cliente {

    //variables para los locks de las conexiones p2p
    private final static int CONEXIONESP2P_PERMITIDAS = 10;
    private int contadorLocks = 0;
    private int numP2P = 0;

    //vamos a usar lockTicket para proteger el contador
    private final LockTicket lockContador = new LockTicket(CONEXIONESP2P_PERMITIDAS);

    //solo si usamos LockRompeEmpate
    private final Entero[] in = new Entero[CONEXIONESP2P_PERMITIDAS+1];
    private final Entero[] last = new Entero[CONEXIONESP2P_PERMITIDAS+1];
    private final Entero n = new Entero();

    //---------------------------------------------------------------------
    private int idTemporal;

    private String nombre;
    private String ip;
    private int puerto;
    private Socket socketServidor;
    private ObjectOutputStream outServidor;
    private ObjectInputStream inServidor;
    private boolean existeNombre;
    private Usuario usuario;


    private int puertoP2P;
    private VentanaVinilos ventanaVinilos;
    private VentanaEliminarVinilo ventanaEliminar;
    private String receptorPendiente;
    private String nombreViniloPendiente;

    //lock para fout
    Locks lock;


    public Cliente() throws UnknownHostException {
        this.ip = InetAddress.getLocalHost().getHostAddress();
        this.puerto = -1;
        this.puertoP2P = -1;
        this.existeNombre = false;
        this.usuario = null;
        this.idTemporal = 0;
        this.ventanaVinilos = null;
        this.ventanaEliminar = null;

        this.lock = new LockTicket(CONEXIONESP2P_PERMITIDAS);

        //inicializamos arrays si usamos LockRompeEmpate
        for (int i = 0; i < CONEXIONESP2P_PERMITIDAS; i++) {
            in[i] = new Entero();
            last[i] = new Entero();
        }
    }

    public static void main(String[] args){
        new Thread(() -> {
            try {
                //creamos vinilos para las pruebas
                Vinilo v1 = new Vinilo("Abbey Road", "The Beatles", "Rock", 1960);
                Vinilo v2 = new Vinilo("Thriller", "Michael Jackson", "Pop", 1980);
                Vinilo v3 = new Vinilo("Back in Black", "AC/DC", "Hard Rock", 1980);
                Vinilo v4 = new Vinilo("The Dark Side of the Moon", "Pink Floyd", "Progressive Rock", 1970);
                Vinilo v5 = new Vinilo("Rumours", "Fleetwood Mac", "Rock", 1970);
                Vinilo v6 = new Vinilo("Nevermind", "Nirvana", "Grunge", 1990);
                Vinilo v7 = new Vinilo("Random Access Memories", "Daft Punk", "Electronic", 2010);
                Vinilo v8 = new Vinilo("To Pimp a Butterfly", "Kendrick Lamar", "Hip Hop", 2010);
                Vinilo v9 = new Vinilo("Hotel California", "Eagles", "Rock", 1970);
                Vinilo v10 = new Vinilo("Born to Run", "Bruce Springsteen", "Rock", 1970);
                Vinilo v11 = new Vinilo("OK Computer", "Radiohead", "Alternative Rock", 1990);
                Vinilo v12 = new Vinilo("21", "Adele", "Pop", 2010);
                Vinilo v13 = new Vinilo("Led Zeppelin IV", "Led Zeppelin", "Hard Rock", 1970);
                Vinilo v14 = new Vinilo("Good Kid, M.A.A.D City", "Kendrick Lamar", "Hip Hop", 2010); // distinto de "To Pimp a Butterfly"
                Vinilo v15 = new Vinilo("The Wall", "Pink Floyd", "Progressive Rock", 1970); // distinto de "Dark Side"
                Vinilo v16 = new Vinilo("In Rainbows", "Radiohead", "Alternative", 2000);
                Vinilo v17 = new Vinilo("DAMN.", "Kendrick Lamar", "Hip Hop", 2010);
                Vinilo v18 = new Vinilo("American Idiot", "Green Day", "Punk Rock", 2000);
                Vinilo v19 = new Vinilo("A Night at the Opera", "Queen", "Rock", 1970);
                Vinilo v20 = new Vinilo("Hounds of Love", "Kate Bush", "Art Pop", 1980);

                List<Vinilo> lista = new ArrayList<>();
                lista.add(v8);
                lista.add(v9);
                lista.add(v10);
                lista.add(v11);
                lista.add(v12);
                lista.add(v13);
                lista.add(v14);
                lista.add(v15);
                Cliente c1 = new Cliente();
                c1.conectarAlServidor("127.0.0.1",50000, lista);
            } catch (Exception e) {
                System.err.println("Error en Cliente: " + e.getMessage());
            }
        }).start();
    }

    public void enviarMensaje(Object msg) {
        lock.takeLock();
        try {
            outServidor.writeObject(msg);
            outServidor.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.releaseLock();
        }
    }


    public String getNombre() {
        return nombre;
    }

    public synchronized void setIdTemporal(int id){
        this.idTemporal = id;
        notifyAll();
    }

    public synchronized void setPuertoIni(int puerto) {
        this.puerto = puerto;
        notifyAll();
    }


    private void conectarAlServidor(String ipServidor, int puertoServidor, List<Vinilo> vinilosPropios) {
        try {
            socketServidor = new Socket(ipServidor, puertoServidor);
            outServidor = new ObjectOutputStream(socketServidor.getOutputStream());
            outServidor.flush();//importante
            inServidor = new ObjectInputStream(socketServidor.getInputStream());

            iniciarEscuchaServidor();

            synchronized (this){
                while(this.idTemporal == 0){//espera hasta que se le asigne una id temporal
                    wait();
                }
            }


            enviarMensaje(new MensajePedirPuertoIni(this.idTemporal));

            synchronized (this){
                while(this.puerto == -1){//espera hasta que se le asigne un puerto valido
                    wait();
                }
            }


            //comprobar si existe nombre
            while (true) {
                this.nombre = JOptionPane.showInputDialog(null, "Introduce tu nombre de usuario:");

                //el usuario ha pulsado "Cancelar" o ha cerrado la ventana
                if (this.nombre == null) {
                    JOptionPane.showMessageDialog(null, "Operación cancelada.");
                    return;
                }

                if (!comprobarNombre(nombre)) {
                    break; //como el nombre es válido, salimos del bucle
                } else {
                    JOptionPane.showMessageDialog(null, "Nombre de usuario ya existente, por favor elige otro.");
                }
            }

            //enviar mensaje de inicio de sesión
            enviarMensaje(new MensajeConexion(nombre,puerto, vinilosPropios, this.ip, this.idTemporal));

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void iniciarEscuchaServidor() {
        new Thread(new OyenteServidor(socketServidor, inServidor, outServidor, this)).start();
    }

    public void anyadirVinilo(Vinilo v) throws IOException {
        enviarMensaje(new MensajeAddVinilo(this.nombre, v));
        this.usuario.addVinilo(v);
    }

    public void eliminarVinilo(String vinilo) throws IOException {
        Vinilo viniloBuscado = null;
        for(Vinilo v: this.usuario.getVinilosPropios()){
            if(v.getNombre().equals(vinilo)){
                viniloBuscado = v;
            }
        }

        enviarMensaje(new MensajeRemoveVinilo(this.nombre, viniloBuscado.getNombre()));
        this.usuario.removeVinilo(viniloBuscado);
    }

    public void consultarVinilos() throws IOException {
        enviarMensaje(new MensajePedirLista(this.nombre));
    }

    protected void mostrarVinilos(Map<String, List<Vinilo>> vinilosPorUsuario) {
        if (this.ventanaVinilos != null) {
            this.ventanaVinilos.dispose(); //cierra la ventana anterior si existe
        }
        this.ventanaVinilos = new VentanaVinilos(vinilosPorUsuario, this.outServidor, this);
    }
    public synchronized boolean comprobarNombre(String nombre) throws IOException, InterruptedException {
        enviarMensaje(new MensajeComprobarNombre(nombre));
        wait();
        return existeNombre;
    }
    public synchronized void existe(boolean existe){
        this.existeNombre = existe;
        notifyAll();
    }


    protected void usuarioRegistrado(Usuario usuario){
        this.usuario = usuario;
        new VentanaPrincipal(this);
    }

    protected void enviarViniloCS(String receptor, String nombreVinilo) throws IOException {
        this.nombreViniloPendiente = nombreVinilo;
        this.receptorPendiente = receptor;
        //PRODUCTOR: reservar puerto libre
        enviarMensaje(new MensajePedirPuertoP2P(this.nombre));
    }



    //el receptor recibe un mensaje del servidor con la info necesaria para establecer la conexion con el emisor del vinilo que quiere
    protected void iniciarConexionP2P(String ip, int puerto, String nombreVinilo) {
        new Thread(new Receptor(ip, puerto, nombreVinilo, this)).start();
    }



    public synchronized void setPuertoP2P(int puerto) {
        this.puertoP2P = puerto;
        iniciarServidorP2P();
    }

    private void iniciarServidorP2P() {
        Locks lockP2P = crearNuevoLock();
        lockP2P.takeLock();
        try {
            if (numP2P >= CONEXIONESP2P_PERMITIDAS) {
                if (this.ventanaVinilos != null) {
                    JOptionPane.showMessageDialog(this.ventanaVinilos,"Número máximo de conexiones P2P alcanzado.");
                }
                return;
            }

            numP2P++;
            new Thread(new Emisor(puertoP2P, usuario)).start();

            enviarMensaje(new MensajeConfViniloCS(this.ip, puertoP2P, this.receptorPendiente, this.nombreViniloPendiente));

        } finally {
            lockP2P.releaseLock();
        }

    }

    public void consultarVinilosPropios() throws IOException {
        enviarMensaje(new MensajePedirListaPropia(this.nombre));
    }

    protected void mostrarVinilosPropios(List<Vinilo> vinilosPropios) {
        if (this.ventanaEliminar != null) {
            this.ventanaEliminar.dispose(); //cierra la ventana anterior si existe
        }
        this.ventanaEliminar = new VentanaEliminarVinilo(this.nombre, vinilosPropios, this);
    }

    public void cerrarConexion() throws IOException {
        enviarMensaje(new MensajeCerrarConexion());

        //cerramos flujos y socket
        if (inServidor != null) inServidor.close();
        if (outServidor != null) outServidor.close();
        if (socketServidor != null && !socketServidor.isClosed()) socketServidor.close();

        System.out.println("[Cliente] Conexión cerrada correctamente.");
    }

    public boolean comprobarVinilo(Vinilo nuevoVinilo) {
        for(Vinilo v: this.usuario.getVinilosPropios()){
            if(v.iguales(nuevoVinilo)){
                return true;
            }
        }
        return false;
    }

    private Locks crearNuevoLock() {
        lockContador.takeLock();
        int id;
        try {
            if (contadorLocks >= CONEXIONESP2P_PERMITIDAS) {
                throw new RuntimeException("Demasiados hilos compitiendo por el lock.");
            }
            id = contadorLocks++;
        } finally {
            lockContador.releaseLock();
        }

        return new LockBakery(CONEXIONESP2P_PERMITIDAS, id);
        //podriamos usar tambien el rompeEmpate
        // return new LockRompeEmpate(in, last, n, CONEXIONESP2P_PERMITIDAS, id);
    }

}

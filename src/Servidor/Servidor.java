package Servidor;

import Cliente.*;
import Locks.Entero;
import Mensajes.*;
import Resto.*;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Semaphore;
import Locks.Locks;
import Locks.LockTicket;

public class Servidor {
    private final static int PUERTO_SERVIDOR = 50000;
    private final static int CLIENTES_PERMITIDOS = 1000;
    private int contId;
    private Map<String, Usuario> mapaUsuarios;
    private static ServerSocket ss;
    private Map<String, InfoCliente> infoClientes;
    private Map<String, List<Vinilo>> listaVinilos;
    private Monitor mUsuarios;
    private Monitor mInfoClientes;
    private Monitor mVinilos;

    //variables de semaforos para puertos iniciales
    private Semaphore empty;
    private Semaphore full;
    private Semaphore mutex;
    private Trabajo<Producto> trabajoPuertos;
    private Entero contadorPuerto; // inicio de puertos disponibles
    private Producto[] buffer;

    //variables P2P
    private Semaphore emptyP2P;
    private Semaphore fullP2P ;
    private Semaphore mutexP2P ;
    private Trabajo<Producto> trabajoPuertosP2P;
    private Entero contadorPuertoP2P; // inicio de puertos disponibles
    private Producto[] bufferP2P;




    private class InfoCliente{//le pasaremos esto cuando se registre un cliente
        private Locks lock;
        private ObjectOutputStream fout;

        public InfoCliente(ObjectInputStream fin, ObjectOutputStream fout, String ip, int puerto) {
            this.lock = new LockTicket(CLIENTES_PERMITIDOS);
            this.fout = fout;
        }

        public void escribir(Object obj) throws IOException {
            lock.takeLock();
            try {
                fout.writeObject(obj);
                fout.flush();
            } finally {
                lock.releaseLock();
            }
        }
    }

    public Servidor(int puerto) throws IOException {
        mapaUsuarios = new HashMap<>();
        ss = new ServerSocket(puerto);
        this.contId = 1;
        this.listaVinilos = new HashMap<>();
        this.mUsuarios = new Monitor();
        this.mInfoClientes = new Monitor();
        this.mVinilos = new Monitor();
        //
        this.empty = new Semaphore(CLIENTES_PERMITIDOS);
        this.full = new Semaphore(0);
        this.mutex = new Semaphore(1);
        //inicializamos el almacenamiento de puertos P2P
        this.buffer = new Producto[CLIENTES_PERMITIDOS];
        trabajoPuertos = new Trabajo<>(buffer, CLIENTES_PERMITIDOS);
        this.contadorPuerto = new Entero(50101); // inicio de puertos disponibles

        this.emptyP2P = new Semaphore(100);
        this.fullP2P = new Semaphore(0);
        this.mutexP2P = new Semaphore(1);
        //inicializamos el almacenamiento de puertos P2P
        this.bufferP2P = new Producto[100];
        trabajoPuertosP2P = new Trabajo<>(bufferP2P, 100);
        this.contadorPuertoP2P = new Entero(50001); // inicio de puertos disponibles
    }

    public static void main(String[] args){
        new Thread(() -> {
            try {
                Servidor servidor = new Servidor(PUERTO_SERVIDOR);
                servidor.iniciar();
            } catch (IOException e) {
                System.err.println("Error al iniciar el servidor: " + e.getMessage());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    protected void enviarPuertoIni(int idTemporal) throws InterruptedException, IOException {
        int puerto = obtenerPuertoLibreIni();
        this.infoClientes.get("__TEMPORAL__"+ idTemporal).escribir(new MensajeEnviarPuertoIni(puerto));
    }

    private void iniciar() throws IOException, InterruptedException {
        System.out.println("Servidor iniciado...");

        while(true){//espera activa para esperar tod el rato si me envian un mensaje
            Socket s = ss.accept();//creamos un canal para cada cliente que envie un mensaje
            Thread hilo = new Thread(new OyenteCliente(s, this));//un hilo para cada cliente
            hilo.start();//lo corremos
        }
    }

    protected void mostrarLista(String cliente) throws IOException, InterruptedException {//pasamos por param el cliente que ha pedido la lista para no mostrar sus propios vinilos
        mInfoClientes.requestReader();
        InfoCliente infoAux = this.infoClientes.get(cliente);
        mInfoClientes.releaseReader();

        //mandamos el contenido del mapa fila por fila para que no de errores al serializar
        //primero  filtramos la lista de claves que no son el cliente
        mVinilos.requestReader();
        List<String> clavesAEnviar = this.listaVinilos.keySet().stream()
                .filter(nombre -> !nombre.equals(cliente))
                .toList();
        mVinilos.releaseReader();
        int totalAEnviar = clavesAEnviar.size();

        if(totalAEnviar == 0){
            JOptionPane optionPane = new JOptionPane(
                    "Los demás usuarios aún no han subido vinilos",
                    JOptionPane.WARNING_MESSAGE);

            JDialog dialog = optionPane.createDialog(null, "Advertencia");
            dialog.setAlwaysOnTop(true);
            dialog.setVisible(true);


        }
        for (int i = 0; i < totalAEnviar; i++) {
            String nombre = clavesAEnviar.get(i);
            mVinilos.requestReader();
            List<Vinilo> vinilos = listaVinilos.get(nombre);
            mVinilos.releaseReader();
            boolean esUltimo = (i == totalAEnviar - 1);
            infoAux.escribir(new MensajeFilaListaVinilos(nombre, new ArrayList<>(vinilos), esUltimo));
        }

    }

    protected void mostrarListaPropia(String cliente) throws IOException, InterruptedException {
        mInfoClientes.requestReader();
        InfoCliente infoAux = this.infoClientes.get(cliente);
        mInfoClientes.releaseReader();
        mVinilos.requestReader();
        List<Vinilo>listaPropia = this.listaVinilos.get(cliente);
        mVinilos.releaseReader();
        infoAux.escribir(new MensajeListaVinilosPropios(new ArrayList<>(listaPropia)));
    }

    public synchronized void anyadirVinilo(String nombreUsuario, Vinilo vinilo) throws InterruptedException {


        //como hemos marcado AtomicBoolean como transient hay que volverlo a poner porque vendrá como null
        try {
            if (vinilo.estaLibre() == null) {
                vinilo.reinicializarEstado();
            }
            mVinilos.requestWriter();
            this.listaVinilos.get(nombreUsuario).add(vinilo);
            mUsuarios.requestWriter();
            this.mapaUsuarios.get(nombreUsuario).addVinilo(vinilo);
            JOptionPane optionPane = new JOptionPane(
                    "Vinilo añadido correctamente",
                    JOptionPane.INFORMATION_MESSAGE);

            JDialog dialog = optionPane.createDialog(null, "Advertencia");
            dialog.setAlwaysOnTop(true);
            dialog.setVisible(true);

        } finally {
            mVinilos.releaseWriter();
            mUsuarios.releaseWriter();
        }
    }

    public synchronized void eliminarVinilo(String nombreUsuario, String vinilo) throws InterruptedException, IOException {
        mVinilos.releaseReader();
        for(Vinilo v: this.listaVinilos.get(nombreUsuario)){
            if(v.getNombre().equals(vinilo)){
                mVinilos.requestWriter();
                this.listaVinilos.get(nombreUsuario).remove(v);
                mVinilos.releaseWriter();
                mUsuarios.requestWriter();
                this.mapaUsuarios.get(nombreUsuario).removeVinilo(v);
                mUsuarios.releaseWriter();
                break;
            }
        }
        mInfoClientes.requestReader();
        InfoCliente infoAux = this.infoClientes.get(nombreUsuario);
        mInfoClientes.releaseReader();
        mVinilos.requestReader();
        infoAux.escribir(new MensajeViniloEliminado(new ArrayList<>(this.listaVinilos.get(nombreUsuario))));
        mVinilos.releaseReader();
    }


    public synchronized void anyadirUsuario(Usuario usuario, String nombre, ObjectInputStream fin, ObjectOutputStream fout, String ip, int puerto, List<Vinilo> listaVinilos, int idTemporal) throws IOException, InterruptedException {
        mUsuarios.requestWriter();
        this.mapaUsuarios.put(nombre, usuario);
        mUsuarios.releaseWriter();
        mInfoClientes.requestWriter();
        this.infoClientes.remove("__TEMPORAL__" + idTemporal);//borramos el cliente que habiamos añadido temporalmente sin nombre
        this.infoClientes.put(nombre, new InfoCliente(fin, fout, ip, puerto));
        mInfoClientes.releaseWriter();
        for(Vinilo v: listaVinilos){
            v.reinicializarEstado();
        }
        mVinilos.requestWriter();
        this.listaVinilos.put(nombre, listaVinilos);
        mVinilos.releaseWriter();
        mInfoClientes.requestReader();
        InfoCliente infoAux = this.infoClientes.get(nombre);
        mInfoClientes.releaseReader();
        infoAux.escribir(new MensajeConfConexion(usuario));
    }

    protected void eliminarUsuario(Usuario usuario) throws InterruptedException {
        mUsuarios.requestWriter();
        this.mapaUsuarios.remove(usuario.getNombre());
        mUsuarios.releaseWriter();
        mInfoClientes.requestWriter();
        this.infoClientes.remove(usuario.getNombre());
        mInfoClientes.releaseWriter();
        mVinilos.requestWriter();
        this.listaVinilos.remove(usuario.getNombre());
        mVinilos.releaseWriter();
    }
    protected void comprobarNombre(String nombre, ObjectOutputStream fout) throws IOException, InterruptedException {
        this.mUsuarios.requestReader();
        boolean existe = this.mapaUsuarios.containsKey(nombre);
        this.mUsuarios.releaseReader();
        if(existe){
            fout.writeObject(new MensajeConfComprobarCliente(true));
            fout.flush();
        }
        else{
            fout.writeObject(new MensajeConfComprobarCliente(false));
            fout.flush();
        }
    }

    protected boolean viniloPedido(String emisor, String nombreVinilo, String receptor) throws IOException, InterruptedException {
        boolean encontrado = false;
        mVinilos.requestReader();
        if(this.listaVinilos.containsKey(emisor)){
            for(Vinilo v: this.listaVinilos.get(emisor)){
                if(v.getNombre().equals(nombreVinilo)){
                    if (v.intentarDescargar()) {
                        try {
                            encontrado = true;
                            mInfoClientes.requestReader();
                            InfoCliente infoAux = infoClientes.get(emisor);
                            mInfoClientes.releaseReader();
                            infoAux.escribir(new MensajePedirViniloSC(receptor, nombreVinilo));
                        } finally {
                            v.liberar();
                        }
                    }
                    break;
                }
            }
        }
        mVinilos.releaseReader();

        return encontrado;

    }

    protected void registrarClienteTemporal(ObjectInputStream fin, ObjectOutputStream fout, String ip) throws InterruptedException, IOException {
        if (this.infoClientes == null)
            this.infoClientes = new HashMap<>();
        mInfoClientes.requestWriter();
        this.infoClientes.put("__TEMPORAL__" + contId, new InfoCliente(fin, fout, ip, -1)); // nombre temporal
        mInfoClientes.releaseWriter();
        fout.writeObject(new MensajeEnviarIdTemporal(contId));
        fout.flush();
        contId = (contId + 1) % CLIENTES_PERMITIDOS;
    }

    protected void infoEmisor(int puerto, String ip, String receptor, String nombreVinilo) throws IOException, InterruptedException {
        mInfoClientes.requestReader();
        InfoCliente infoAux = this.infoClientes.get(receptor);
        infoAux.escribir(new MensajeConfViniloSC(puerto, ip, nombreVinilo));
        mInfoClientes.releaseReader();
    }

    private int obtenerPuertoLibreIni() throws InterruptedException {
        empty.acquire();
        mutex.acquire();
        Producto p = new Producto(contadorPuerto.entero);
        contadorPuerto.entero++;
        trabajoPuertos.almacenar(p);
        mutex.release();
        full.release();

        full.acquire();
        mutex.acquire();
        Producto extraido = trabajoPuertos.extraer();
        mutex.release();
        empty.release();

        return extraido.getPuerto();
    }

    protected void enviarPuertoP2P(String nombre) throws InterruptedException, IOException {
        int puerto = obtenerPuertoLibreP2P();
        mInfoClientes.requestReader();
        InfoCliente infoAux = this.infoClientes.get(nombre);
        infoAux.escribir(new MensajeEnviarPuertoP2P(puerto));
        mInfoClientes.releaseReader();
    }
    private int obtenerPuertoLibreP2P() throws InterruptedException {
        emptyP2P.acquire();
        mutexP2P.acquire();
        Producto p = new Producto(contadorPuertoP2P.entero);
        contadorPuertoP2P.entero++;
        trabajoPuertosP2P.almacenar(p);
        mutexP2P.release();
        fullP2P.release();

        fullP2P.acquire();
        mutexP2P.acquire();
        Producto extraido = trabajoPuertosP2P.extraer();
        mutexP2P.release();
        emptyP2P.release();

        return extraido.getPuerto();
    }
}

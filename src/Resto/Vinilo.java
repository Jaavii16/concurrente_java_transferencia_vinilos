package Resto;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

public class Vinilo implements Serializable {
    private String nombre;
    private String artista;
    private String genero;
    private int decada;
    private transient AtomicBoolean libre = new AtomicBoolean(true);

    public Vinilo(String nombre, String artista, String genero, int decada) {
        this.nombre = nombre;
        this.artista = artista;
        this.genero = genero;
        this.decada = decada;
    }

    //logica de concurrencia para ver si podemos descargar un vinilo o no
    public boolean intentarDescargar() {
        return libre.compareAndSet(true, false);
    }

    public void liberar() {
        libre.set(true);
    }

    public AtomicBoolean estaLibre() {
        return libre;
    }

    //para restaurarlo cuando lo recibimos no serializado
    public void reinicializarEstado() {
        libre = new AtomicBoolean(true);
    }

    public String getNombre() {
        return nombre;
    }

    protected String getArtista() {
        return artista;
    }

    protected String getGenero() {
        return genero;
    }

    protected int getDecada() {
        return decada;
    }

    public boolean iguales(Vinilo otro) {
        if (otro == null) return false;
        return this.nombre.equals(otro.nombre) &&
                this.artista.equals(otro.artista) &&
                this.genero.equals(otro.genero) &&
                this.decada == otro.decada;
    }
}

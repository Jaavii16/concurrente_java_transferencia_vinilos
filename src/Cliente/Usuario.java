package Cliente;

import Resto.Vinilo;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Usuario implements Serializable {

    private String nombre;
    private List<Vinilo> vinilosPropios;

    public Usuario(String nombre, List<Vinilo> vinilosPropios)  {
        this.nombre = nombre;
        this.vinilosPropios = vinilosPropios;
    }

    public void addVinilo(Vinilo v) {
        vinilosPropios.add(v);
    }

    public void removeVinilo(Vinilo v) {
        vinilosPropios.remove(v);
    }

    protected List<Vinilo> getVinilosPropios() {
        return vinilosPropios;
    }

    public String getNombre() {
        return this.nombre;
    }
}

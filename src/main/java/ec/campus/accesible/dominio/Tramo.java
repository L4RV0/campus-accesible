package ec.campus.accesible.dominio;

import java.time.LocalDateTime;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

/**
 * Conexión entre dos lugares. Se puede recorrer en ambos sentidos.
 * Un tramo cerrado sigue existiendo en el mapa, pero no debe recomendarse.
 */
public class Tramo {

    private final String id;
    private final String extremoA;
    private final String extremoB;
    private final TipoTramo tipo;
    private final int metros;
    private boolean cerrado;
    /**
     * Lista segura entre hilos: el gestor puede programar un cierre mientras otros consultan rutas
     * (revisión de Erick en el PR #19). Se leen muchas más veces de las que se escriben.
     */
    private final List<CierreTemporal> cierres = new CopyOnWriteArrayList<>();

    public Tramo(String id, String extremoA, String extremoB, TipoTramo tipo, int metros) {
        if (metros <= 0) {
            throw new IllegalArgumentException("La longitud de un tramo debe ser positiva");
        }
        this.id = id;
        this.extremoA = extremoA;
        this.extremoB = extremoB;
        this.tipo = tipo;
        this.metros = metros;
    }

    public boolean conecta(String lugarId) {
        return extremoA.equals(lugarId) || extremoB.equals(lugarId);
    }

    public String otroExtremo(String lugarId) {
        if (extremoA.equals(lugarId)) {
            return extremoB;
        }
        if (extremoB.equals(lugarId)) {
            return extremoA;
        }
        throw new IllegalArgumentException("El tramo " + id + " no pasa por " + lugarId);
    }

    /** Regla 2: un tramo cerrado no se recomienda. */
    public boolean disponible() {
        return !cerrado;
    }

    /** Reglas 2 y HU-10: no se recomienda si está cerrado o si tiene un cierre temporal vigente. */
    public boolean disponibleEn(LocalDateTime momento) {
        return disponible() && cierres.stream().noneMatch(c -> c.vigenteEn(momento));
    }

    public void agregarCierre(CierreTemporal cierre) {
        cierres.add(cierre);
    }

    public List<CierreTemporal> cierres() {
        return List.copyOf(cierres);
    }

    public void cerrar() {
        cerrado = true;
    }

    public void abrir() {
        cerrado = false;
    }

    public String id() { return id; }
    public String extremoA() { return extremoA; }
    public String extremoB() { return extremoB; }
    public TipoTramo tipo() { return tipo; }
    public int metros() { return metros; }
    public boolean cerrado() { return cerrado; }
}

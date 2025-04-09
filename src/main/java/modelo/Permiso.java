package modelo;

/**
 * Clase que representa un permiso del sistema
 */
public class Permiso {
    private int id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String modulo;
    
    public Permiso() {
    }
    
    public Permiso(int id, String codigo, String nombre, String descripcion, String modulo) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.modulo = modulo;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }
    
    @Override
    public String toString() {
        return nombre;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Permiso permiso = (Permiso) obj;
        return id == permiso.id;
    }
    
    @Override
    public int hashCode() {
        return id;
    }
}
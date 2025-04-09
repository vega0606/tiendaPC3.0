package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa un rol de usuario con permisos asociados
 */
public class Rol {
    private int id;
    private String nombre;
    private String descripcion;
    private List<Permiso> permisos;
    
    public Rol() {
        this.permisos = new ArrayList<>();
    }
    
    public Rol(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.permisos = new ArrayList<>();
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public List<Permiso> getPermisos() {
        return permisos;
    }

    public void setPermisos(List<Permiso> permisos) {
        this.permisos = permisos;
    }
    
    /**
     * Agrega un permiso al rol
     * @param permiso Permiso a agregar
     */
    public void agregarPermiso(Permiso permiso) {
        if (!permisos.contains(permiso)) {
            permisos.add(permiso);
        }
    }
    
    /**
     * Elimina un permiso del rol
     * @param permiso Permiso a eliminar
     */
    public void eliminarPermiso(Permiso permiso) {
        permisos.remove(permiso);
    }
    
    /**
     * Verifica si el rol tiene un permiso específico
     * @param codigo Código del permiso
     * @return true si el rol tiene el permiso
     */
    public boolean tienePermiso(String codigo) {
        for (Permiso permiso : permisos) {
            if (permiso.getCodigo().equals(codigo)) {
                return true;
            }
        }
        return false;
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
        Rol rol = (Rol) obj;
        return id == rol.id;
    }
    
    @Override
    public int hashCode() {
        return id;
    }
}
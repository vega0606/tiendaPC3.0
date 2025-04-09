package modelo;

import java.time.LocalDate;

/**
 * Clase que representa a un cliente del sistema
 */
public class Cliente {
    private String id;
    private String nombre;
    private String email;
    private String telefono;
    private String direccion;
    private String ruc; // o NIT o documento fiscal
    private String estado;
    private LocalDate fechaRegistro;
    private double saldoPendiente;
    
    public Cliente() {
    }
    
    public Cliente(String id, String nombre, String email, String telefono, 
                  String direccion, String ruc, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.ruc = ruc;
        this.estado = estado;
        this.fechaRegistro = LocalDate.now();
        this.saldoPendiente = 0.0;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public double getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(double saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }
    
    @Override
    public String toString() {
        return nombre;
    }
}

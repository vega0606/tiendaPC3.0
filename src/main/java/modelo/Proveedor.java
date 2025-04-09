package modelo;

import java.time.LocalDate;

/**
 * Clase que representa a un proveedor del sistema
 */
public class Proveedor {
    private String id;
    private String empresa;
    private String contacto;
    private String email;
    private String telefono;
    private String direccion;
    private String ruc; // o NIT o documento fiscal
    private String categoria;
    private String estado;
    private LocalDate fechaRegistro;
    private double saldoPendiente;
    
    public Proveedor() {
    }
    
    public Proveedor(String id, String empresa, String contacto, String email, 
                    String telefono, String direccion, String ruc, 
                    String categoria, String estado) {
        this.id = id;
        this.empresa = empresa;
        this.contacto = contacto;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.ruc = ruc;
        this.categoria = categoria;
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

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
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
        return empresa + " (" + contacto + ")";
    }
    
    /**
     * Verifica si el proveedor está activo
     * @return true si el proveedor está activo
     */
    public boolean isActivo() {
        return "Activo".equals(estado);
    }
}
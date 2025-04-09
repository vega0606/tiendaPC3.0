package modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Clase que representa una alerta del sistema
 */
public class Alerta {
    private String id;
    private String tipo;
    private String descripcion;
    private LocalDate fecha;
    private String prioridad;
    private String estado;
    private String referencia;
    private LocalDateTime fechaCreacion;
    
    public Alerta() {
    }
    
    public Alerta(String id, String tipo, String descripcion, LocalDate fecha, 
                 String prioridad, String estado, String referencia) {
        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.prioridad = prioridad;
        this.estado = estado;
        this.referencia = referencia;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    @Override
    public String toString() {
        return "Alerta{" + "id=" + id + ", tipo=" + tipo + ", descripcion=" + descripcion + 
               ", fecha=" + fecha + ", prioridad=" + prioridad + ", estado=" + estado + '}';
    }
    
    /**
     * Obtiene el color asociado a la prioridad
     * @return Color en formato hexadecimal
     */
    public String getColorPrioridad() {
        switch (prioridad) {
            case "Alta":
                return "#F44336"; // Rojo
            case "Media":
                return "#FF9800"; // Naranja
            case "Baja":
                return "#4CAF50"; // Verde
            default:
                return "#9E9E9E"; // Gris
        }
    }
    
    /**
     * Obtiene el color asociado al estado
     * @return Color en formato hexadecimal
     */
    public String getColorEstado() {
        switch (estado) {
            case "Pendiente":
                return "#2196F3"; // Azul
            case "Atendida":
                return "#4CAF50"; // Verde
            case "Programada":
                return "#9C27B0"; // Púrpura
            default:
                return "#9E9E9E"; // Gris
        }
    }
}

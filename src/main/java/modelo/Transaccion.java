package modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Clase que representa una transacción financiera
 */
public class Transaccion {
    private String id;
    private LocalDate fecha;
    private String entidad;
    private String tipo;
    private String referencia;
    private BigDecimal total;
    private String estado;
    private int idUsuario;
    private LocalDateTime fechaCreacion;
    
    public Transaccion() {
        this.fecha = LocalDate.now();
        this.total = BigDecimal.ZERO;
        this.estado = "Completada";
    }
    
    public Transaccion(String id, LocalDate fecha, String entidad, String tipo, 
                      String referencia, BigDecimal total, String estado, int idUsuario) {
        this.id = id;
        this.fecha = fecha;
        this.entidad = entidad;
        this.tipo = tipo;
        this.referencia = referencia;
        this.total = total;
        this.estado = estado;
        this.idUsuario = idUsuario;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    /**
     * Verifica si la transacción es una venta
     * @return true si es una venta
     */
    public boolean isVenta() {
        return "Venta".equals(tipo);
    }
    
    /**
     * Verifica si la transacción es una compra
     * @return true si es una compra
     */
    public boolean isCompra() {
        return "Compra".equals(tipo);
    }
    
    /**
     * Verifica si la transacción es una devolución
     * @return true si es una devolución
     */
    public boolean isDevolucion() {
        return "Devolución".equals(tipo);
    }
    
    /**
     * Verifica si la transacción está completada
     * @return true si está completada
     */
    public boolean isCompletada() {
        return "Completada".equals(estado);
    }
    
    /**
     * Verifica si la transacción está pendiente
     * @return true si está pendiente
     */
    public boolean isPendiente() {
        return "Pendiente".equals(estado);
    }
    
    /**
     * Verifica si la transacción está cancelada
     * @return true si está cancelada
     */
    public boolean isCancelada() {
        return "Cancelada".equals(estado);
    }
    
    @Override
    public String toString() {
        return tipo + " #" + id + " - " + total;
    }
}
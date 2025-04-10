package modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa una devolución
 */
public class Devolucion {
    private String id;
    private String numeroFactura;
    private Factura factura;
    private LocalDate fecha;
    private String motivo;
    private String detalles;
    private BigDecimal total;
    private int idUsuario;
    private LocalDateTime fechaCreacion;
    private List<DetalleDevolucion> detalles_lista;
    
    public Devolucion() {
        // Inicialización mínima para la entidad
        this.total = BigDecimal.ZERO;
        this.fecha = LocalDate.now();
        // No inicializamos la lista para BD, se cargará mediante el DAO
    }
    
    // Getters y Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getNumeroFactura() {
        return numeroFactura;
    }
    
    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }
    
    public Factura getFactura() {
        return factura;
    }
    
    public void setFactura(Factura factura) {
        this.factura = factura;
        if (factura != null) {
            this.numeroFactura = factura.getNumero();
        }
    }
    
    public LocalDate getFecha() {
        return fecha;
    }
    
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    
    public String getMotivo() {
        return motivo;
    }
    
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
    
    public String getDetalles() {
        return detalles;
    }
    
    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public void setTotal(BigDecimal total) {
        this.total = total;
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
    
    public List<DetalleDevolucion> getDetallesLista() {
        if (detalles_lista == null) {
            detalles_lista = new ArrayList<>();
        }
        return detalles_lista;
    }
    
    public void setDetallesLista(List<DetalleDevolucion> detalles) {
        this.detalles_lista = detalles;
    }
    
    /**
     * Obtiene la lista de items de devolución (método para compatibilidad con el controlador)
     * @return Lista de detalles de devolución
     */
    public List<DetalleDevolucion> getItemsDevolucion() {
        return detalles_lista;
    }
    
    /**
     * Agrega un detalle a la devolución
     * @param detalle Detalle a agregar
     */
    public void agregarDetalle(DetalleDevolucion detalle) {
        if (detalles_lista == null) {
            detalles_lista = new ArrayList<>();
        }
        detalle.setIdDevolucion(this.id);
        this.detalles_lista.add(detalle);
        recalcularTotales();
    }
    
    /**
     * Elimina un detalle de la devolución
     * @param index Índice del detalle a eliminar
     */
    public void eliminarDetalle(int index) {
        if (detalles_lista != null && index >= 0 && index < detalles_lista.size()) {
            detalles_lista.remove(index);
            recalcularTotales();
        }
    }
    
    /**
     * Recalcula los totales de la devolución
     */
    public void recalcularTotales() {
        BigDecimal nuevoTotal = BigDecimal.ZERO;
        
        if (detalles_lista != null) {
            for (DetalleDevolucion detalle : detalles_lista) {
                if (detalle.getSubtotal() != null) {
                    nuevoTotal = nuevoTotal.add(detalle.getSubtotal());
                }
            }
        }
        
        this.total = nuevoTotal;
    }
    
    @Override
    public String toString() {
        return "Devolución #" + id;
    }
}
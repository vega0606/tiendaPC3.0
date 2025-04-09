package modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa una factura
 */
public class Factura {
    private String numero;
    private String idCliente;
    private Cliente cliente;
    private LocalDate fecha;
    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal total;
    private String estado;
    private int idUsuario;
    private String observaciones;
    private LocalDateTime fechaCreacion;
    private List<DetalleFactura> detalles;
    
    public Factura() {
        this.detalles = new ArrayList<>();
        this.subtotal = BigDecimal.ZERO;
        this.iva = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
        this.estado = "Emitida";
        this.fecha = LocalDate.now();
    }

    // Getters y Setters
    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        if (cliente != null) {
            this.idCliente = cliente.getId();
        }
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva(BigDecimal iva) {
        this.iva = iva;
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

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<DetalleFactura> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleFactura> detalles) {
        this.detalles = detalles;
    }
    
    /**
     * Agrega un detalle a la factura
     * @param detalle Detalle a agregar
     */
    public void agregarDetalle(DetalleFactura detalle) {
        detalle.setNumeroFactura(this.numero);
        this.detalles.add(detalle);
        recalcularTotales();
    }
    
    /**
     * Elimina un detalle de la factura
     * @param index Índice del detalle a eliminar
     */
    public void eliminarDetalle(int index) {
        if (index >= 0 && index < detalles.size()) {
            detalles.remove(index);
            recalcularTotales();
        }
    }
    
    /**
     * Recalcula los totales de la factura
     */
    public void recalcularTotales() {
        BigDecimal nuevoSubtotal = BigDecimal.ZERO;
        BigDecimal nuevoIva = BigDecimal.ZERO;
        
        for (DetalleFactura detalle : detalles) {
            nuevoSubtotal = nuevoSubtotal.add(detalle.getSubtotal());
            nuevoIva = nuevoIva.add(detalle.getIva());
        }
        
        this.subtotal = nuevoSubtotal;
        this.iva = nuevoIva;
        this.total = nuevoSubtotal.add(nuevoIva);
    }
    
    @Override
    public String toString() {
        return "Factura #" + numero;
    }
}

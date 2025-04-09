package modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa un pedido a proveedor
 */
public class Pedido {
    private String numero;
    private String idProveedor;
    private Proveedor proveedor;
    private LocalDate fecha;
    private LocalDate fechaEntrega;
    private BigDecimal total;
    private String estado;
    private int idUsuario;
    private String observaciones;
    private LocalDateTime fechaCreacion;
    private List<DetallePedido> detalles;
    
    public Pedido() {
        this.detalles = new ArrayList<>();
        this.total = BigDecimal.ZERO;
        this.estado = "Pendiente";
        this.fecha = LocalDate.now();
    }

    // Getters y Setters
    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(String idProveedor) {
        this.idProveedor = idProveedor;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
        if (proveedor != null) {
            this.idProveedor = proveedor.getId();
        }
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalDate getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDate fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
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

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = detalles;
    }
    
    /**
     * Agrega un detalle al pedido
     * @param detalle Detalle a agregar
     */
    public void agregarDetalle(DetallePedido detalle) {
        detalle.setNumeroPedido(this.numero);
        this.detalles.add(detalle);
        recalcularTotales();
    }
    
    /**
     * Elimina un detalle del pedido
     * @param index Índice del detalle a eliminar
     */
    public void eliminarDetalle(int index) {
        if (index >= 0 && index < detalles.size()) {
            detalles.remove(index);
            recalcularTotales();
        }
    }
    
    /**
     * Recalcula los totales del pedido
     */
    public void recalcularTotales() {
        BigDecimal nuevoTotal = BigDecimal.ZERO;
        
        for (DetallePedido detalle : detalles) {
            nuevoTotal = nuevoTotal.add(detalle.getSubtotal());
        }
        
        this.total = nuevoTotal;
    }
    
    /**
     * Verifica si el pedido está pendiente
     * @return true si el pedido está pendiente
     */
    public boolean isPendiente() {
        return "Pendiente".equals(estado);
    }
    
    /**
     * Verifica si el pedido está en proceso
     * @return true si el pedido está en proceso
     */
    public boolean isEnProceso() {
        return "En proceso".equals(estado);
    }
    
    /**
     * Verifica si el pedido está enviado
     * @return true si el pedido está enviado
     */
    public boolean isEnviado() {
        return "Enviado".equals(estado);
    }
    
    /**
     * Verifica si el pedido está entregado
     * @return true si el pedido está entregado
     */
    public boolean isEntregado() {
        return "Entregado".equals(estado);
    }
    
    /**
     * Verifica si el pedido está cancelado
     * @return true si el pedido está cancelado
     */
    public boolean isCancelado() {
        return "Cancelado".equals(estado);
    }
    
    @Override
    public String toString() {
        return "Pedido #" + numero;
    }
}
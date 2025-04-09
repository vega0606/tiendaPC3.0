package modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Clase que representa un detalle de devolución
 */
public class DetalleDevolucion {
    private int id;
    private String idDevolucion;
    private String codigoProducto;
    private Producto producto;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    
    public DetalleDevolucion() {
        this.cantidad = 1;
        this.precioUnitario = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
    }
    
    public DetalleDevolucion(Producto producto, int cantidad, BigDecimal precioUnitario) {
        this.codigoProducto = producto.getCodigo();
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        recalcular();
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdDevolucion() {
        return idDevolucion;
    }

    public void setIdDevolucion(String idDevolucion) {
        this.idDevolucion = idDevolucion;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
        if (producto != null) {
            this.codigoProducto = producto.getCodigo();
            recalcular();
        }
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        recalcular();
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
        recalcular();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    /**
     * Recalcula los valores del detalle
     */
    private void recalcular() {
        if (cantidad <= 0 || precioUnitario == null) {
            this.subtotal = BigDecimal.ZERO;
            return;
        }
        
        // Calcular subtotal
        this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad))
                         .setScale(2, RoundingMode.HALF_UP);
    }
    
    @Override
    public String toString() {
        if (producto != null) {
            return cantidad + " x " + producto.getNombre() + " - " + subtotal;
        } else {
            return cantidad + " x Producto - " + subtotal;
        }
    }
}
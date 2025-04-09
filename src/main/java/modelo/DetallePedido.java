package modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Clase que representa un detalle de pedido
 */
public class DetallePedido {
    private int id;
    private String numeroPedido;
    private String codigoProducto;
    private Producto producto;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    
    public DetallePedido() {
        this.cantidad = 1;
        this.precioUnitario = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
    }
    
    public DetallePedido(Producto producto, int cantidad) {
        this.codigoProducto = producto.getCodigo();
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecioCompra();
        recalcular();
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(String numeroPedido) {
        this.numeroPedido = numeroPedido;
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
            this.precioUnitario = producto.getPrecioCompra();
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
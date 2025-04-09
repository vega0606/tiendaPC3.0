package modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Clase que representa un detalle de factura
 */
public class DetalleFactura {
    private int id;
    private String numeroFactura;
    private String codigoProducto;
    private Producto producto;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal iva;
    private BigDecimal subtotal;
    
    public DetalleFactura() {
        this.cantidad = 1;
        this.precioUnitario = BigDecimal.ZERO;
        this.iva = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
    }
    
    public DetalleFactura(Producto producto, int cantidad) {
        this.codigoProducto = producto.getCodigo();
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecioVenta();
        
        // Calcular IVA (asumiendo 12% como ejemplo)
        BigDecimal porcentajeIva = new BigDecimal("0.12");
        BigDecimal subtotalSinIva = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        this.iva = subtotalSinIva.multiply(porcentajeIva).setScale(2, RoundingMode.HALF_UP);
        this.subtotal = subtotalSinIva;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
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
            this.precioUnitario = producto.getPrecioVenta();
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

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva(BigDecimal iva) {
        this.iva = iva;
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
    public void recalcular() {
        if (cantidad <= 0 || precioUnitario == null) {
            this.subtotal = BigDecimal.ZERO;
            this.iva = BigDecimal.ZERO;
            return;
        }
        
        // Calcular subtotal
        this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad))
                         .setScale(2, RoundingMode.HALF_UP);
        
        // Calcular IVA (asumiendo 12% como ejemplo)
        BigDecimal porcentajeIva = new BigDecimal("0.12");
        this.iva = subtotal.multiply(porcentajeIva).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Obtiene el subtotal con IVA incluido
     * @return Subtotal con IVA
     */
    public BigDecimal getSubtotalConIva() {
        return subtotal.add(iva);
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

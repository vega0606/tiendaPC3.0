package modelo;

import java.math.BigDecimal;

/**
 * Clase que representa un producto del inventario
 */
public class Producto {
    private String codigo;
    private String nombre;
    private String descripcion;
    private String categoria;
    private int stock;
    private int stockMinimo;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private String estado;
    private String unidad;
    
    public Producto() {
    }
    
    public Producto(String codigo, String nombre, String descripcion, String categoria, 
                    int stock, int stockMinimo, BigDecimal precioCompra, 
                    BigDecimal precioVenta, String estado, String unidad) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.estado = estado;
        this.unidad = unidad;
    }

    // Getters y Setters
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }
    
    /**
     * Determina si el producto está en estado de bajo stock
     * @return true si el stock es menor al stock mínimo
     */
    public boolean isBajoStock() {
        return stock <= stockMinimo;
    }
    
    /**
     * Calcula el margen de ganancia del producto
     * @return Porcentaje de ganancia
     */
    public double getMargenGanancia() {
        if (precioCompra.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        return precioVenta.subtract(precioCompra).divide(precioCompra, 4, BigDecimal.ROUND_HALF_UP)
               .multiply(BigDecimal.valueOf(100)).doubleValue();
    }
    
    @Override
    public String toString() {
        return nombre;
    }
}
package modelo;

import java.time.LocalDateTime;

/**
 * Clase que representa un movimiento en el inventario (entrada, salida o ajuste)
 */
public class MovimientoInventario {
    private int id;
    private String codigoProducto;
    private Producto producto;
    private String tipo;
    private int cantidad;
    private String referencia;
    private LocalDateTime fecha;
    private int idUsuario;
    
    public MovimientoInventario() {
        this.fecha = LocalDateTime.now();
    }
    
    public MovimientoInventario(String codigoProducto, String tipo, int cantidad, 
                               String referencia, int idUsuario) {
        this.codigoProducto = codigoProducto;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.referencia = referencia;
        this.idUsuario = idUsuario;
        this.fecha = LocalDateTime.now();
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        }
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    /**
     * Verifica si el movimiento es una entrada
     * @return true si es una entrada
     */
    public boolean isEntrada() {
        return "Entrada".equals(tipo);
    }
    
    /**
     * Verifica si el movimiento es una salida
     * @return true si es una salida
     */
    public boolean isSalida() {
        return "Salida".equals(tipo);
    }
    
    /**
     * Verifica si el movimiento es un ajuste
     * @return true si es un ajuste
     */
    public boolean isAjuste() {
        return "Ajuste".equals(tipo);
    }
    
    /**
     * Obtiene el valor neto del movimiento (positivo para entradas, negativo para salidas)
     * @return cantidad con signo según el tipo de movimiento
     */
    public int getValorNeto() {
        if (isSalida()) {
            return -cantidad;
        } else {
            return cantidad;
        }
    }
    
    @Override
    public String toString() {
        return tipo + " de " + cantidad + " unidades - " + fecha;
    }
}
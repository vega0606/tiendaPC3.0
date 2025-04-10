package controlador;

import java.util.Date;
import java.util.List;

import modelo.Transaccion;
import ventana.VistaTransacciones;

/**
 * Controlador para la vista de transacciones.
 * Gestiona la interacción entre la vista de transacciones y el modelo de datos.
 */
public class VistaTransaccionesController {
    
    private VistaTransacciones vista;
    private TransaccionController transaccionController;
    
    /**
     * Constructor del controlador de vista de transacciones.
     * 
     * @param vista La vista de transacciones
     * @param transaccionController El controlador de transacciones
     */
    public VistaTransaccionesController(VistaTransacciones vista, TransaccionController transaccionController) {
        this.vista = vista;
        this.transaccionController = transaccionController;
        
        // Inicializar los listeners y componentes de la vista
        inicializarVista();
    }
    
    /**
     * Inicializa los componentes de la vista y configura los listeners.
     */
    private void inicializarVista() {
        // Cargar las transacciones al iniciar la vista
        cargarTransacciones();
        
        // Configurar listeners para los botones de la vista
        configurarListeners();
    }
    
    /**
     * Configura los listeners para los botones y componentes interactivos de la vista.
     */
    private void configurarListeners() {
        // Listener para el botón de buscar transacción
        vista.getBtnBuscar().addActionListener(e -> buscarTransaccionPorId());
        
        // Listener para el botón de mostrar todas las transacciones
        vista.getBtnMostrarTodas().addActionListener(e -> cargarTransacciones());
        
        // Listener para el botón de nueva transacción
        vista.getBtnNuevaTransaccion().addActionListener(e -> abrirFormularioNuevaTransaccion());
        
        // Listener para el botón de editar transacción
        vista.getBtnEditar().addActionListener(e -> editarTransaccionSeleccionada());
        
        // Listener para el botón de eliminar transacción
        vista.getBtnEliminar().addActionListener(e -> eliminarTransaccionSeleccionada());
        
        // Listener para filtro por tipo de transacción
        vista.getBtnFiltrarPorTipo().addActionListener(e -> filtrarTransaccionesPorTipo());
        
        // Listener para filtro por fecha
        vista.getBtnFiltrarPorFecha().addActionListener(e -> filtrarTransaccionesPorFecha());
        
        // Listener para filtro por monto
        vista.getBtnFiltrarPorMonto().addActionListener(e -> filtrarTransaccionesPorMonto());
        
        // Listener para exportar a PDF
        vista.getBtnExportarPDF().addActionListener(e -> exportarAFormato("PDF"));
        
        // Listener para exportar a Excel
        vista.getBtnExportarExcel().addActionListener(e -> exportarAFormato("Excel"));
    }
    
    /**
     * Carga todas las transacciones en la tabla de la vista.
     */
    public void cargarTransacciones() {
        List<Transaccion> transacciones = transaccionController.obtenerTodasTransacciones();
        vista.mostrarTransacciones(transacciones);
    }
    
    /**
     * Busca una transacción por su ID y la muestra en la vista.
     */
    private void buscarTransaccionPorId() {
        try {
            String idTexto = vista.getTxtBusqueda().getText();
            if (idTexto.isEmpty()) {
                vista.mostrarMensaje("Ingrese un ID para buscar");
                return;
            }
            
            int id = Integer.parseInt(idTexto);
            Transaccion transaccion = transaccionController.buscarTransaccionPorId(id);
            
            if (transaccion != null) {
                vista.mostrarTransaccion(transaccion);
            } else {
                vista.mostrarMensaje("No se encontró ninguna transacción con el ID: " + id);
            }
        } catch (NumberFormatException e) {
            vista.mostrarMensaje("El ID debe ser un número entero");
        }
    }
    
    /**
     * Abre el formulario para crear una nueva transacción.
     */
    private void abrirFormularioNuevaTransaccion() {
        vista.mostrarFormularioTransaccion(null);
    }
    
    /**
     * Edita la transacción seleccionada en la tabla.
     */
    private void editarTransaccionSeleccionada() {
        Transaccion transaccionSeleccionada = vista.obtenerTransaccionSeleccionada();
        
        if (transaccionSeleccionada != null) {
            vista.mostrarFormularioTransaccion(transaccionSeleccionada);
        } else {
            vista.mostrarMensaje("Seleccione una transacción para editar");
        }
    }
    
    /**
     * Elimina la transacción seleccionada en la tabla.
     */
    private void eliminarTransaccionSeleccionada() {
        Transaccion transaccionSeleccionada = vista.obtenerTransaccionSeleccionada();
        
        if (transaccionSeleccionada != null) {
            boolean confirmacion = vista.mostrarConfirmacion("¿Está seguro de eliminar esta transacción?");
            
            if (confirmacion) {
                boolean eliminado = transaccionController.eliminarTransaccion(transaccionSeleccionada.getId());
                
                if (eliminado) {
                    vista.mostrarMensaje("Transacción eliminada correctamente");
                    cargarTransacciones();
                } else {
                    vista.mostrarMensaje("No se pudo eliminar la transacción");
                }
            }
        } else {
            vista.mostrarMensaje("Seleccione una transacción para eliminar");
        }
    }
    
    /**
     * Filtra las transacciones por tipo.
     */
    private void filtrarTransaccionesPorTipo() {
        String tipo = vista.getComboTipoTransaccion().getSelectedItem().toString();
        
        if (tipo.equals("Todas")) {
            cargarTransacciones();
            return;
        }
        
        List<Transaccion> transaccionesFiltradas = transaccionController.filtrarTransaccionesPorTipo(tipo);
        vista.mostrarTransacciones(transaccionesFiltradas);
    }
    
    /**
     * Filtra las transacciones por un rango de fechas.
     */
    private void filtrarTransaccionesPorFecha() {
        try {
            Date fechaInicio = vista.getFechaInicio().getDate();
            Date fechaFin = vista.getFechaFin().getDate();
            
            if (fechaInicio == null || fechaFin == null) {
                vista.mostrarMensaje("Seleccione un rango de fechas válido");
                return;
            }
            
            if (fechaInicio.after(fechaFin)) {
                vista.mostrarMensaje("La fecha de inicio debe ser anterior a la fecha de fin");
                return;
            }
            
            List<Transaccion> transaccionesFiltradas = transaccionController.filtrarTransaccionesPorFecha(fechaInicio, fechaFin);
            vista.mostrarTransacciones(transaccionesFiltradas);
            
        } catch (Exception e) {
            vista.mostrarMensaje("Error al filtrar por fecha: " + e.getMessage());
        }
    }
    
    /**
     * Filtra las transacciones por un rango de montos.
     */
    private void filtrarTransaccionesPorMonto() {
        try {
            String montoMinimoTexto = vista.getTxtMontoMinimo().getText();
            String montoMaximoTexto = vista.getTxtMontoMaximo().getText();
            
            if (montoMinimoTexto.isEmpty() || montoMaximoTexto.isEmpty()) {
                vista.mostrarMensaje("Ingrese un rango de montos válido");
                return;
            }
            
            double montoMinimo = Double.parseDouble(montoMinimoTexto);
            double montoMaximo = Double.parseDouble(montoMaximoTexto);
            
            if (montoMinimo > montoMaximo) {
                vista.mostrarMensaje("El monto mínimo debe ser menor que el monto máximo");
                return;
            }
            
            List<Transaccion> transaccionesFiltradas = transaccionController.filtrarTransaccionesPorMonto(montoMinimo, montoMaximo);
            vista.mostrarTransacciones(transaccionesFiltradas);
            
        } catch (NumberFormatException e) {
            vista.mostrarMensaje("Los montos deben ser valores numéricos");
        } catch (Exception e) {
            vista.mostrarMensaje("Error al filtrar por monto: " + e.getMessage());
        }
    }
    
    /**
     * Exporta las transacciones mostradas a un formato específico.
     * 
     * @param formato El formato de exportación ("PDF" o "Excel")
     */
    private void exportarAFormato(String formato) {
        List<Transaccion> transacciones = vista.obtenerTransaccionesMostradas();
        
        if (transacciones.isEmpty()) {
            vista.mostrarMensaje("No hay datos para exportar");
            return;
        }
        
        String rutaArchivo = vista.seleccionarRutaGuardado(formato);
        
        if (rutaArchivo != null) {
            boolean exportado = false;
            
            if ("PDF".equals(formato)) {
                exportado = transaccionController.exportarAPDF(transacciones, rutaArchivo);
            } else if ("Excel".equals(formato)) {
                exportado = transaccionController.exportarAExcel(transacciones, rutaArchivo);
            }
            
            if (exportado) {
                vista.mostrarMensaje("Datos exportados correctamente a " + formato);
            } else {
                vista.mostrarMensaje("Error al exportar a " + formato);
            }
        }
    }
    
    /**
     * Guarda una transacción nueva o actualizada.
     * 
     * @param transaccion La transacción a guardar
     * @param esNueva Indica si es una nueva transacción o una actualización
     * @return true si se guardó correctamente, false en caso contrario
     */
    public boolean guardarTransaccion(Transaccion transaccion, boolean esNueva) {
        boolean resultado;
        
        if (esNueva) {
            resultado = transaccionController.crearTransaccion(transaccion);
        } else {
            resultado = transaccionController.actualizarTransaccion(transaccion);
        }
        
        if (resultado) {
            cargarTransacciones();
        }
        
        return resultado;
    }
}

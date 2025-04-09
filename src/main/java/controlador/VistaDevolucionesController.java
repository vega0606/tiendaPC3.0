package controlador;

import java.util.List;

import modelo.Devolucion;
import ventana.VistaDevoluciones;

/**
 * Controlador para la vista de devoluciones.
 * Gestiona la interacción entre la vista de devoluciones y el modelo de datos.
 */
public class VistaDevolucionesController {
    
    private VistaDevolucionesView vista;
    private DevolucionController devolucionController;
    
    /**
     * Constructor del controlador de vista de devoluciones.
     * 
     * @param vista La vista de devoluciones
     * @param devolucionController El controlador de devoluciones
     */
    public VistaDevolucionesController(VistaDevolucionesView vista, DevolucionController devolucionController) {
        this.vista = vista;
        this.devolucionController = devolucionController;
        
        // Inicializar los listeners y componentes de la vista
        inicializarVista();
    }
    
    /**
     * Inicializa los componentes de la vista y configura los listeners.
     */
    private void inicializarVista() {
        // Cargar las devoluciones al iniciar la vista
        cargarDevoluciones();
        
        // Configurar listeners para los botones de la vista
        configurarListeners();
    }
    
    /**
     * Configura los listeners para los botones y componentes interactivos de la vista.
     */
    private void configurarListeners() {
        // Listener para el botón de buscar devolución
        vista.getBtnBuscar().addActionListener(e -> buscarDevolucionPorId());
        
        // Listener para el botón de mostrar todas las devoluciones
        vista.getBtnMostrarTodas().addActionListener(e -> cargarDevoluciones());
        
        // Listener para el botón de nueva devolución
        vista.getBtnNuevaDevolucion().addActionListener(e -> abrirFormularioNuevaDevolucion());
        
        // Listener para el botón de editar devolución
        vista.getBtnEditar().addActionListener(e -> editarDevolucionSeleccionada());
        
        // Listener para el botón de eliminar devolución
        vista.getBtnEliminar().addActionListener(e -> eliminarDevolucionSeleccionada());
        
        // Listener para filtro por fecha
        vista.getBtnFiltrarPorFecha().addActionListener(e -> filtrarDevolucionesPorFecha());
        
        // Listener para exportar a PDF
        vista.getBtnExportarPDF().addActionListener(e -> exportarAFormato("PDF"));
        
        // Listener para exportar a Excel
        vista.getBtnExportarExcel().addActionListener(e -> exportarAFormato("Excel"));
    }
    
    /**
     * Carga todas las devoluciones en la tabla de la vista.
     */
    public void cargarDevoluciones() {
        List<Devolucion> devoluciones = devolucionController.obtenerTodasDevoluciones();
        vista.mostrarDevoluciones(devoluciones);
    }
    
    /**
     * Busca una devolución por su ID y la muestra en la vista.
     */
    private void buscarDevolucionPorId() {
        try {
            String idTexto = vista.getTxtBusqueda().getText();
            if (idTexto.isEmpty()) {
                vista.mostrarMensaje("Ingrese un ID para buscar");
                return;
            }
            
            int id = Integer.parseInt(idTexto);
            Devolucion devolucion = devolucionController.buscarDevolucionPorId(id);
            
            if (devolucion != null) {
                vista.mostrarDevolucion(devolucion);
            } else {
                vista.mostrarMensaje("No se encontró ninguna devolución con el ID: " + id);
            }
        } catch (NumberFormatException e) {
            vista.mostrarMensaje("El ID debe ser un número entero");
        }
    }
    
    /**
     * Abre el formulario para crear una nueva devolución.
     */
    private void abrirFormularioNuevaDevolucion() {
        vista.mostrarFormularioDevolucion(null);
    }
    
    /**
     * Edita la devolución seleccionada en la tabla.
     */
    private void editarDevolucionSeleccionada() {
        Devolucion devolucionSeleccionada = vista.obtenerDevolucionSeleccionada();
        
        if (devolucionSeleccionada != null) {
            vista.mostrarFormularioDevolucion(devolucionSeleccionada);
        } else {
            vista.mostrarMensaje("Seleccione una devolución para editar");
        }
    }
    
    /**
     * Elimina la devolución seleccionada en la tabla.
     */
    private void eliminarDevolucionSeleccionada() {
        Devolucion devolucionSeleccionada = vista.obtenerDevolucionSeleccionada();
        
        if (devolucionSeleccionada != null) {
            boolean confirmacion = vista.mostrarConfirmacion("¿Está seguro de eliminar esta devolución?");
            
            if (confirmacion) {
                boolean eliminado = devolucionController.eliminarDevolucion(devolucionSeleccionada.getId());
                
                if (eliminado) {
                    vista.mostrarMensaje("Devolución eliminada correctamente");
                    cargarDevoluciones();
                } else {
                    vista.mostrarMensaje("No se pudo eliminar la devolución");
                }
            }
        } else {
            vista.mostrarMensaje("Seleccione una devolución para eliminar");
        }
    }
    
    /**
     * Filtra las devoluciones por un rango de fechas.
     */
    private void filtrarDevolucionesPorFecha() {
        try {
            java.util.Date fechaInicio = vista.getFechaInicio().getDate();
            java.util.Date fechaFin = vista.getFechaFin().getDate();
            
            if (fechaInicio == null || fechaFin == null) {
                vista.mostrarMensaje("Seleccione un rango de fechas válido");
                return;
            }
            
            List<Devolucion> devolucionesFiltradas = devolucionController.filtrarDevolucionesPorFecha(fechaInicio, fechaFin);
            vista.mostrarDevoluciones(devolucionesFiltradas);
            
        } catch (Exception e) {
            vista.mostrarMensaje("Error al filtrar por fecha: " + e.getMessage());
        }
    }
    
    /**
     * Exporta las devoluciones mostradas a un formato específico.
     * 
     * @param formato El formato de exportación ("PDF" o "Excel")
     */
    private void exportarAFormato(String formato) {
        List<Devolucion> devoluciones = vista.obtenerDevolucionesMostradas();
        
        if (devoluciones.isEmpty()) {
            vista.mostrarMensaje("No hay datos para exportar");
            return;
        }
        
        String rutaArchivo = vista.seleccionarRutaGuardado(formato);
        
        if (rutaArchivo != null) {
            boolean exportado = false;
            
            if ("PDF".equals(formato)) {
                exportado = devolucionController.exportarAPDF(devoluciones, rutaArchivo);
            } else if ("Excel".equals(formato)) {
                exportado = devolucionController.exportarAExcel(devoluciones, rutaArchivo);
            }
            
            if (exportado) {
                vista.mostrarMensaje("Datos exportados correctamente a " + formato);
            } else {
                vista.mostrarMensaje("Error al exportar a " + formato);
            }
        }
    }
    
    /**
     * Guarda una devolución nueva o actualizada.
     * 
     * @param devolucion La devolución a guardar
     * @param esNueva Indica si es una nueva devolución o una actualización
     * @return true si se guardó correctamente, false en caso contrario
     */
    public boolean guardarDevolucion(Devolucion devolucion, boolean esNueva) {
        boolean resultado;
        
        if (esNueva) {
            resultado = devolucionController.crearDevolucion(devolucion);
        } else {
            resultado = devolucionController.actualizarDevolucion(devolucion);
        }
        
        if (resultado) {
            cargarDevoluciones();
        }
        
        return resultado;
    }
}

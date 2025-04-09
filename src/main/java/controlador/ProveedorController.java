package controlador;

import modelo.Proveedor;
import DAO.ProveedorDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para la gestión de proveedores
 */
public class ProveedorController {
    private static final Logger logger = LoggerFactory.getLogger(ProveedorController.class);
    private ProveedorDAO proveedorDAO;
    
    public ProveedorController() {
        proveedorDAO = new ProveedorDAO();
    }
    
    /**
     * Crea un nuevo proveedor
     * @param empresa Nombre de la empresa
     * @param contacto Nombre del contacto
     * @param email Email del contacto
     * @param telefono Teléfono del contacto
     * @param direccion Dirección de la empresa
     * @param ruc RUC/NIT de la empresa
     * @param categoria Categoría del proveedor
     * @return El proveedor creado o null si ocurre un error
     */
    public Proveedor crearProveedor(String empresa, String contacto, String email, 
                                  String telefono, String direccion, String ruc, 
                                  String categoria) {
        try {
            // Generar ID para el nuevo proveedor
            String id = proveedorDAO.generarNuevoId();
            
            Proveedor proveedor = new Proveedor();
            proveedor.setId(id);
            proveedor.setEmpresa(empresa);
            proveedor.setContacto(contacto);
            proveedor.setEmail(email);
            proveedor.setTelefono(telefono);
            proveedor.setDireccion(direccion);
            proveedor.setRuc(ruc);
            proveedor.setCategoria(categoria);
            proveedor.setEstado("Activo");
            
            return proveedorDAO.crear(proveedor);
        } catch (Exception e) {
            logger.error("Error al crear proveedor: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Obtiene un proveedor por su ID
     * @param id ID del proveedor
     * @return El proveedor encontrado o null si no existe
     */
    public Proveedor obtenerProveedor(String id) {
        try {
            return proveedorDAO.buscarPorId(id);
        } catch (Exception e) {
            logger.error("Error al obtener proveedor: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Obtiene todos los proveedores
     * @return Lista de proveedores
     */
    public List<Proveedor> listarProveedores() {
        try {
            return proveedorDAO.listarTodos();
        } catch (Exception e) {
            logger.error("Error al listar proveedores: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Actualiza un proveedor existente
     * @param proveedor Proveedor con los datos actualizados
     * @return true si la actualización fue exitosa
     */
    public boolean actualizarProveedor(Proveedor proveedor) {
        try {
            proveedorDAO.actualizar(proveedor);
            return true;
        } catch (Exception e) {
            logger.error("Error al actualizar proveedor: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Elimina un proveedor
     * @param id ID del proveedor
     * @return true si la eliminación fue exitosa
     */
    public boolean eliminarProveedor(String id) {
        try {
            return proveedorDAO.eliminar(id);
        } catch (Exception e) {
            logger.error("Error al eliminar proveedor: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Busca proveedores por nombre o empresa
     * @param termino Término de búsqueda
     * @return Lista de proveedores que coinciden con la búsqueda
     */
    public List<Proveedor> buscarProveedoresPorNombre(String termino) {
        try {
            return proveedorDAO.buscarPorNombre(termino);
        } catch (Exception e) {
            logger.error("Error al buscar proveedores por nombre: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Busca un proveedor por RUC/NIT
     * @param ruc RUC/NIT del proveedor
     * @return Proveedor encontrado o null si no existe
     */
    public Proveedor buscarProveedorPorRuc(String ruc) {
        try {
            return proveedorDAO.buscarPorRuc(ruc);
        } catch (Exception e) {
            logger.error("Error al buscar proveedor por RUC: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Obtiene proveedores por categoría
     * @param categoria Categoría de los proveedores
     * @return Lista de proveedores de la categoría
     */
    public List<Proveedor> listarProveedoresPorCategoria(String categoria) {
        try {
            return proveedorDAO.buscarPorCategoria(categoria);
        } catch (Exception e) {
            logger.error("Error al listar proveedores por categoría: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene proveedores por estado
     * @param estado Estado de los proveedores ('Activo', 'Inactivo', etc.)
     * @return Lista de proveedores con el estado especificado
     */
    public List<Proveedor> listarProveedoresPorEstado(String estado) {
        try {
            return proveedorDAO.listarPorEstado(estado);
        } catch (Exception e) {
            logger.error("Error al listar proveedores por estado: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Verifica si existe un proveedor con el ID dado
     * @param id ID a verificar
     * @return true si el proveedor existe
     */
    public boolean existeProveedor(String id) {
        try {
            return proveedorDAO.buscarPorId(id) != null;
        } catch (Exception e) {
            logger.error("Error al verificar existencia del proveedor: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Cambia el estado de un proveedor
     * @param id ID del proveedor
     * @param nuevoEstado Nuevo estado
     * @return true si el cambio fue exitoso
     */
    public boolean cambiarEstadoProveedor(String id, String nuevoEstado) {
        try {
            Proveedor proveedor = proveedorDAO.buscarPorId(id);
            if (proveedor == null) {
                return false;
            }
            
            proveedor.setEstado(nuevoEstado);
            proveedorDAO.actualizar(proveedor);
            return true;
        } catch (Exception e) {
            logger.error("Error al cambiar estado del proveedor: {}", e.getMessage(), e);
            return false;
        }
    }
}
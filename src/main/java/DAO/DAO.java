package DAO;

import java.util.List;

/**
 * Interface genérica para operaciones CRUD (Create, Read, Update, Delete)
 * @param <T> tipo de entidad
 * @param <K> tipo de clave primaria
 */
public interface DAO<T, K> {
    
    /**
     * Crear un nuevo registro
     * @param entity entidad a guardar
     * @return entidad guardada con ID generado
     */
    T crear(T entity) throws Exception;
    
    /**
     * Buscar entidad por ID
     * @param id identificador de la entidad
     * @return entidad encontrada o null si no existe
     */
    T buscarPorId(K id) throws Exception;
    
    /**
     * Obtener todos los registros
     * @return lista de entidades
     */
    List<T> listarTodos() throws Exception;
    
    /**
     * Actualizar un registro existente
     * @param entity entidad con datos actualizados
     * @return entidad actualizada
     */
    T actualizar(T entity) throws Exception;
    
    /**
     * Eliminar un registro por ID
     * @param id identificador de la entidad a eliminar
     * @return true si se eliminó correctamente
     */
    boolean eliminar(K id) throws Exception;
}

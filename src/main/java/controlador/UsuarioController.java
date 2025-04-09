package controlador;

import modelo.Usuario;
import modelo.Rol;
import modelo.Permiso;
import DAO.UsuarioDAO;
import DAO.RolDAO;
import DAO.PermisoDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



/**
 * Controlador para la gestión de usuarios
 */
public class UsuarioController {
    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);
    private UsuarioDAO usuarioDAO;
    private RolDAO rolDAO;
    private PermisoDAO permisoDAO;
    private static Usuario usuarioActual;
    
    public UsuarioController() {
        usuarioDAO = new UsuarioDAO();
        rolDAO = new RolDAO();
        permisoDAO = new PermisoDAO();
    }
    
    /**
     * Crea un nuevo usuario
     * @param nombre Nombre completo del usuario
     * @param username Nombre de usuario para login
     * @param password Contraseña
     * @param email Email
     * @param rol Rol del usuario
     * @return El usuario creado o null si ocurre un error
     */
    public boolean crearUsuario(Usuario usuarioNuevo, String password) {
        try {
            // Validar que el username no exista
            Usuario existente = usuarioDAO.buscarPorUsername(usuarioNuevo.getUsername());
            if (existente != null) {
                logger.error("Ya existe un usuario con el username: {}", usuarioNuevo.getUsername());
                return false;
            }
            
            // Establecer la contraseña
            usuarioNuevo.setPassword(password); // En un sistema real, se debería encriptar
            
            // Establecer estado activo por defecto
            usuarioNuevo.setActivo(true);
            
            // Crear el usuario
            Usuario usuarioCreado = usuarioDAO.crear(usuarioNuevo);
            
            return usuarioCreado != null;
        } catch (Exception e) {
            logger.error("Error al crear usuario: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Obtiene un usuario por su ID
     * @param id ID del usuario
     * @return El usuario encontrado o null si no existe
     */
    public Usuario obtenerUsuario(int id) {
        try {
            return usuarioDAO.buscarPorId(id);
        } catch (Exception e) {
            logger.error("Error al obtener usuario: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Obtiene un usuario por su nombre de usuario
     * @param username Nombre de usuario
     * @return El usuario encontrado o null si no existe
     */
    public Usuario obtenerUsuarioPorUsername(String username) {
        try {
            return usuarioDAO.buscarPorUsername(username);
        } catch (Exception e) {
            logger.error("Error al obtener usuario por username: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Obtiene todos los usuarios
     * @return Lista de usuarios
     */
    public List<Usuario> listarUsuarios() {
        try {
            return usuarioDAO.listarTodos();
        } catch (Exception e) {
            logger.error("Error al listar usuarios: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Actualiza un usuario existente
     * @param usuario Usuario con los datos actualizados
     * @return true si la actualización fue exitosa
     */
    public boolean actualizarUsuario(Usuario usuario) {
        try {
            usuarioDAO.actualizar(usuario);
            return true;
        } catch (Exception e) {
            logger.error("Error al actualizar usuario: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Cambia la contraseña de un usuario
     * @param id ID del usuario
     * @param passwordActual Contraseña actual
     * @param nuevaPassword Nueva contraseña
     * @return true si el cambio fue exitoso
     */
    public boolean cambiarPassword(int id, String passwordActual, String nuevaPassword) {
        try {
            Usuario usuario = usuarioDAO.buscarPorId(id);
            if (usuario == null) {
                logger.error("Usuario no encontrado con ID: {}", id);
                return false;
            }
            
            // Verificar contraseña actual
            if (!usuario.getPassword().equals(passwordActual)) {
                logger.error("Contraseña actual incorrecta para usuario ID: {}", id);
                return false;
            }
            
            // Actualizar contraseña
            usuario.setPassword(nuevaPassword); // En un sistema real, se debería encriptar
            usuarioDAO.actualizar(usuario);
            
            return true;
        } catch (Exception e) {
            logger.error("Error al cambiar contraseña: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Activa o desactiva un usuario
     * @param id ID del usuario
     * @param activo true para activar, false para desactivar
     * @return true si el cambio fue exitoso
     */
    public boolean cambiarEstadoUsuario(int id, boolean activo) {
        try {
            Usuario usuario = usuarioDAO.buscarPorId(id);
            if (usuario == null) {
                logger.error("Usuario no encontrado con ID: {}", id);
                return false;
            }
            
            usuario.setActivo(activo);
            usuarioDAO.actualizar(usuario);
            
            return true;
        } catch (Exception e) {
            logger.error("Error al cambiar estado de usuario: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Elimina un usuario
     * @param id ID del usuario
     * @return true si la eliminación fue exitosa
     */
    public boolean eliminarUsuario(int id) {
        try {
            return usuarioDAO.eliminar(id);
        } catch (Exception e) {
            logger.error("Error al eliminar usuario: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Obtiene usuarios por rol
     * @param rol Rol de los usuarios
     * @return Lista de usuarios con el rol especificado
     */
    public List<Usuario> obtenerUsuariosPorRol(String rol) {
        try {
            return usuarioDAO.buscarPorRol(rol);
        } catch (Exception e) {
            logger.error("Error al obtener usuarios por rol: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Verifica si un usuario tiene un permiso específico
     * @param username Nombre de usuario
     * @param codigoPermiso Código del permiso a verificar
     * @return true si el usuario tiene el permiso
     */
    public boolean tienePermiso(String username, String codigoPermiso) {
        try {
            Usuario usuario = usuarioDAO.buscarPorUsername(username);
            if (usuario == null || !usuario.isActivo()) {
                return false;
            }
            
            // Obtener rol del usuario
            Rol rol = rolDAO.buscarPorNombre(usuario.getRol());
            if (rol == null) {
                return false;
            }
            
            // Verificar si el rol tiene el permiso
            return rol.tienePermiso(codigoPermiso);
        } catch (Exception e) {
            logger.error("Error al verificar permiso: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Actualiza la fecha de último acceso de un usuario
     * @param id ID del usuario
     */
    public void actualizarUltimoAcceso(int id) {
        try {
            usuarioDAO.actualizarUltimoAcceso(id, LocalDateTime.now());
        } catch (Exception e) {
            logger.error("Error al actualizar último acceso: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene todos los roles disponibles
     * @return Lista de roles
     */
    public List<Rol> listarRoles() {
        try {
            return rolDAO.listarTodos();
        } catch (Exception e) {
            logger.error("Error al listar roles: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Crea un nuevo rol
     * @param nombre Nombre del rol
     * @param descripcion Descripción del rol
     * @return El rol creado o null si ocurre un error
     */
    public Rol crearRol(String nombre, String descripcion) {
        try {
            Rol rol = new Rol();
            rol.setNombre(nombre);
            rol.setDescripcion(descripcion);
            
            return rolDAO.crear(rol);
        } catch (Exception e) {
            logger.error("Error al crear rol: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Asigna un permiso a un rol
     * @param idRol ID del rol
     * @param codigoPermiso Código del permiso
     * @return true si la asignación fue exitosa
     */
    public boolean asignarPermisoARol(int idRol, String codigoPermiso) {
        try {
            Rol rol = rolDAO.buscarPorId(idRol);
            if (rol == null) {
                logger.error("Rol no encontrado con ID: {}", idRol);
                return false;
            }
            
            Permiso permiso = permisoDAO.buscarPorCodigo(codigoPermiso);
            if (permiso == null) {
                logger.error("Permiso no encontrado con código: {}", codigoPermiso);
                return false;
            }
            
            rol.agregarPermiso(permiso);
            rolDAO.actualizar(rol);
            
            return true;
        } catch (Exception e) {
            logger.error("Error al asignar permiso a rol: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Revoca un permiso de un rol
     * @param idRol ID del rol
     * @param codigoPermiso Código del permiso
     * @return true si la revocación fue exitosa
     */
    public boolean revocarPermisoDeRol(int idRol, String codigoPermiso) {
        try {
            Rol rol = rolDAO.buscarPorId(idRol);
            if (rol == null) {
                logger.error("Rol no encontrado con ID: {}", idRol);
                return false;
            }
            
            Permiso permiso = permisoDAO.buscarPorCodigo(codigoPermiso);
            if (permiso == null) {
                logger.error("Permiso no encontrado con código: {}", codigoPermiso);
                return false;
            }
            
            rol.eliminarPermiso(permiso);
            rolDAO.actualizar(rol);
            
            return true;
        } catch (Exception e) {
            logger.error("Error al revocar permiso de rol: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Obtiene todos los permisos disponibles
     * @return Lista de permisos
     */
    public List<Permiso> listarPermisos() {
        try {
            return permisoDAO.listarTodos();
        } catch (Exception e) {
            logger.error("Error al listar permisos: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene los permisos de un rol
     * @param idRol ID del rol
     * @return Lista de permisos del rol
     */
    public List<Permiso> listarPermisosDeRol(int idRol) {
        try {
            Rol rol = rolDAO.buscarPorId(idRol);
            if (rol == null) {
                logger.error("Rol no encontrado con ID: {}", idRol);
                return new ArrayList<>();
            }
            
            return rol.getPermisos();
        } catch (Exception e) {
            logger.error("Error al listar permisos de rol: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Crea un nuevo permiso
     * @param codigo Código único del permiso
     * @param nombre Nombre descriptivo
     * @param descripcion Descripción detallada
     * @param modulo Módulo del sistema al que pertenece
     * @return El permiso creado o null si ocurre un error
     */
    public Permiso crearPermiso(String codigo, String nombre, String descripcion, String modulo) {
        try {
            // Verificar que el código no exista
            Permiso existente = permisoDAO.buscarPorCodigo(codigo);
            if (existente != null) {
                logger.error("Ya existe un permiso con el código: {}", codigo);
                return null;
            }
            
            Permiso permiso = new Permiso();
            permiso.setCodigo(codigo);
            permiso.setNombre(nombre);
            permiso.setDescripcion(descripcion);
            permiso.setModulo(modulo);
            
            return permisoDAO.crear(permiso);
        } catch (Exception e) {
            logger.error("Error al crear permiso: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Verifica si existe un usuario con el username dado
     * @param username Nombre de usuario a verificar
     * @return true si el usuario existe
     */
    public boolean existeUsuario(String username) {
        try {
            return usuarioDAO.buscarPorUsername(username) != null;
        } catch (Exception e) {
            logger.error("Error al verificar existencia de usuario: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Verifica si existe un rol con el nombre dado
     * @param nombre Nombre del rol a verificar
     * @return true si el rol existe
     */
    public boolean existeRol(String nombre) {
        try {
            return rolDAO.buscarPorNombre(nombre) != null;
        } catch (Exception e) {
            logger.error("Error al verificar existencia de rol: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Verifica si existe un permiso con el código dado
     * @param codigo Código del permiso a verificar
     * @return true si el permiso existe
     */
    public boolean existePermiso(String codigo) {
        try {
            return permisoDAO.buscarPorCodigo(codigo) != null;
        } catch (Exception e) {
            logger.error("Error al verificar existencia de permiso: {}", e.getMessage(), e);
            return false;
        }
    }
    
    public List<Usuario> obtenerTodosUsuarios() {
        try {
            return usuarioDAO.listarTodos();
        } catch (Exception e) {
            logger.error("Error al obtener todos los usuarios: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    public Usuario buscarUsuarioPorUsername(String username) {
        try {
            return usuarioDAO.buscarPorUsername(username);
        } catch (Exception e) {
            logger.error("Error al buscar usuario por username: {}", e.getMessage(), e);
            return null;
        }
    }
   
    public boolean restablecerPassword(int id) {
        try {
            Usuario usuario = usuarioDAO.buscarPorId(id);
            if (usuario == null) {
                logger.error("Usuario no encontrado con ID: {}", id);
                return false;
            }
            String passwordPredeterminada = "Temporal123!";
            usuario.setPassword(passwordPredeterminada);
            
            usuarioDAO.actualizar(usuario);
            return true;
        } catch (Exception e) {
            logger.error("Error al restablecer contraseña: {}", e.getMessage(), e);
            return false;
        }
    }
    public List<Usuario> filtrarUsuariosPorRol(String rol) {
        try {
            return usuarioDAO.buscarPorRol(rol);
        } catch (Exception e) {
            logger.error("Error al filtrar usuarios por rol: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    public static Usuario obtenerUsuarioActual() {
        if (usuarioActual == null) {
            throw new IllegalStateException("No hay usuario logeado");
        }
        return usuarioActual;
    }
    
    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }
    
    public static void cerrarSesion() {
        usuarioActual = null;
    }
}


package controlador;

import modelo.Usuario;
import DAO.UsuarioDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * Controlador para la gestión de login
 */
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private UsuarioDAO usuarioDAO;
    
    // Variable para almacenar el usuario que inició sesión
    private static Usuario usuarioActual;
    
    public LoginController() {
        usuarioDAO = new UsuarioDAO();
    }
    
    /**
     * Valida las credenciales del usuario
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return true si las credenciales son válidas, false en caso contrario
     */
    public boolean validarLogin(String username, String password) {
        try {
            Usuario usuario = usuarioDAO.buscarPorUsername(username);
            
            if (usuario == null) {
                logger.warn("Intento de inicio de sesión con usuario inexistente: {}", username);
                return false;
            }
            
            // En un sistema real se usaría encriptación (BCrypt, etc.)
            boolean credencialesValidas = usuario.getPassword().equals(password) && usuario.isActivo();
            
            if (credencialesValidas) {
                // Registrar fecha de acceso y guardar usuario en sesión
                usuario.setUltimoAcceso(LocalDateTime.now());
                usuarioDAO.actualizarUltimoAcceso(usuario.getId(), usuario.getUltimoAcceso());
                usuarioActual = usuario;
                logger.info("Usuario '{}' inició sesión exitosamente", username);
            } else {
                logger.warn("Intento de inicio de sesión fallido para el usuario: {}", username);
            }
            
            return credencialesValidas;
        } catch (Exception e) {
            logger.error("Error al validar login: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Recupera información del usuario luego del login
     * @param username Nombre de usuario
     * @return Objeto Usuario con la información completa
     */
    public Usuario getUsuarioInfo(String username) {
        try {
            return usuarioDAO.buscarPorUsername(username);
        } catch (Exception e) {
            logger.error("Error al obtener información del usuario: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Obtiene el usuario que ha iniciado sesión
     * @return Usuario actual o null si no hay sesión
     */
    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }
    
    /**
     * Cierra la sesión del usuario
     */
    public void cerrarSesion() {
        logger.info("Usuario '{}' cerró sesión", usuarioActual != null ? usuarioActual.getUsername() : "desconocido");
        usuarioActual = null;
    }
    
    /**
     * Verifica si el usuario actual tiene un rol específico
     * @param rol Rol a verificar
     * @return true si el usuario tiene el rol especificado
     */
    public static boolean tieneRol(String rol) {
        return usuarioActual != null && usuarioActual.getRol().equals(rol);
    }
    
    /**
     * Verifica si hay un usuario con sesión activa
     * @return true si hay un usuario con sesión activa
     */
    public static boolean haySesionActiva() {
        return usuarioActual != null;
    }
}
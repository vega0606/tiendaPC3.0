package util;

import modelo.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase de utilidad para gestionar la sesión del usuario actual en el sistema
 */
public class SesionUsuario {
    private static final Logger logger = LoggerFactory.getLogger(SesionUsuario.class);
    private static Usuario usuarioActual = null;
    
    /**
     * Inicia sesión con un usuario
     * @param usuario Usuario que inicia sesión
     */
    public static void iniciarSesion(Usuario usuario) {
        if (usuario != null) {
            usuarioActual = usuario;
            logger.info("Usuario {} ha iniciado sesión", usuario.getUsername());
        } else {
            logger.warn("Intento de iniciar sesión con usuario nulo");
        }
    }
    
    /**
     * Cierra la sesión actual
     */
    public static void cerrarSesion() {
        if (usuarioActual != null) {
            logger.info("Usuario {} ha cerrado sesión", usuarioActual.getUsername());
            usuarioActual = null;
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
     * Verifica si hay un usuario con sesión iniciada
     * @return true si hay una sesión activa
     */
    public static boolean hayUsuarioActivo() {
        return usuarioActual != null;
    }
}
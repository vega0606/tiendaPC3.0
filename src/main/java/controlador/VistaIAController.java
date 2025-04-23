package controlador;

import servicio.IAService;
import ventana.VistaIA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Map;

/**
 * Controlador para la vista de IA
 */
public class VistaIAController {
    private static final Logger logger = LoggerFactory.getLogger(VistaIAController.class);
    
    private VistaIA vista;
    private IAService iaService;
    
    /**
     * Constructor del controlador
     * @param vista La vista de IA
     */
    public VistaIAController(VistaIA vista) {
        this.vista = vista;
        this.iaService = new IAService();
        
        // Verificar que la API key funciona
        verificarApiKey();
    }
    
    /**
     * Verifica si la API key es válida
     */
    private void verificarApiKey() {
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return iaService.testApiKey();
            }
            
            @Override
            protected void done() {
                try {
                    boolean apiKeyValida = get();
                    if (apiKeyValida) {
                        vista.actualizarEstado("API Key válida. El servicio está listo para usar.");
                    } else {
                        vista.actualizarEstado("API Key inválida. No se podrán utilizar los servicios de IA.");
                    }
                } catch (Exception e) {
                    logger.error("Error al verificar API Key", e);
                    vista.actualizarEstado("Error al verificar API Key: " + e.getMessage());
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Analiza el sentimiento de un texto
     * @param texto El texto a analizar
     */
    public void analizarSentimiento(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            vista.mostrarMensaje("El texto no puede estar vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        vista.actualizarEstado("Analizando sentimiento...");
        
        SwingWorker<Map<String, Object>, Void> worker = new SwingWorker<Map<String, Object>, Void>() {
            @Override
            protected Map<String, Object> doInBackground() throws Exception {
                return iaService.analizarSentimiento(texto);
            }
     
            
            
            @Override 
            protected void done() { 
                try { 
                    Map<String, Object> resultado = get(); 
                    
                    if ("success".equals(resultado.get("status"))) { 
                        float score = (float) resultado.get("score"); 
                        float magnitude = (float) resultado.get("magnitude"); 
                        
                        StringBuilder mensaje = new StringBuilder(); 
                        mensaje.append("Análisis de sentimiento:\n\n"); 
                        mensaje.append("Score: ").append(String.format("%.2f", score)).append("\n"); 
                        mensaje.append("Magnitud: ").append(String.format("%.2f", magnitude)).append("\n\n"); 
                        
                        if (score > 0.25) { 
                            mensaje.append("El texto tiene un sentimiento positivo."); 
                        } else if (score < -0.25) { 
                            mensaje.append("El texto tiene un sentimiento negativo."); 
                        } else { 
                            mensaje.append("El texto tiene un sentimiento neutral."); 
                        } 
                        
                        vista.mostrarResultado(mensaje.toString()); 
                        vista.actualizarEstado("Análisis de sentimiento completado"); 
                    } else { 
                        vista.mostrarMensaje("Error al analizar sentimiento: " + resultado.get("message"), 
                                          JOptionPane.ERROR_MESSAGE); 
                        vista.actualizarEstado("Error en el análisis de sentimiento"); 
                    }
                    // El resto del método...
                } catch (Exception e) {
                    logger.error("Error al procesar resultado de análisis de sentimiento", e);
                    vista.mostrarMensaje("Error al procesar resultado: " + e.getMessage(), 
                                      JOptionPane.ERROR_MESSAGE);
                    vista.actualizarEstado("Error en el análisis de sentimiento");
                }
            }
        };
        
        worker.execute();
    }
    /**
     * Traduce un texto a otro idioma
     * @param texto El texto a traducir
     * @param idiomaDestino El código del idioma de destino
     */
    public void traducirTexto(String texto, String idiomaDestino) {
        if (texto == null || texto.trim().isEmpty()) {
            vista.mostrarMensaje("El texto no puede estar vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (idiomaDestino == null || idiomaDestino.trim().isEmpty()) {
            vista.mostrarMensaje("Debe seleccionar un idioma de destino", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        vista.actualizarEstado("Traduciendo texto...");
        
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return iaService.traducirTexto(texto, idiomaDestino);
            }
            
            @Override
            protected void done() {
                try {
                    String textoTraducido = get();
                    
                    if (textoTraducido.startsWith("Error:")) {
                        vista.mostrarMensaje(textoTraducido, JOptionPane.ERROR_MESSAGE);
                        vista.actualizarEstado("Error en la traducción");
                    } else {
                        vista.mostrarResultado("Texto traducido a " + obtenerNombreIdioma(idiomaDestino) + 
                                          ":\n\n" + textoTraducido);
                        vista.actualizarEstado("Traducción completada");
                    }
                } catch (Exception e) {
                    logger.error("Error al procesar traducción", e);
                    vista.mostrarMensaje("Error al procesar traducción: " + e.getMessage(), 
                                      JOptionPane.ERROR_MESSAGE);
                    vista.actualizarEstado("Error en la traducción");
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Genera una respuesta utilizando IA
     * @param consulta La consulta del usuario
     * @param contexto Contexto adicional (opcional)
     */
    public void generarRespuestaIA(String consulta, String contexto) {
        if (consulta == null || consulta.trim().isEmpty()) {
            vista.mostrarMensaje("La consulta no puede estar vacía", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        vista.actualizarEstado("Generando respuesta...");
        
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return iaService.generarRespuestaIA(consulta, contexto);
            }
            
            @Override
            protected void done() {
                try {
                    String respuesta = get();
                    
                    if (respuesta.startsWith("Error:")) {
                        vista.mostrarMensaje(respuesta, JOptionPane.ERROR_MESSAGE);
                        vista.actualizarEstado("Error al generar respuesta");
                    } else {
                        vista.mostrarResultado("Respuesta de la IA:\n\n" + respuesta);
                        vista.actualizarEstado("Respuesta generada correctamente");
                    }
                } catch (Exception e) {
                    logger.error("Error al procesar respuesta de IA", e);
                    vista.mostrarMensaje("Error al procesar respuesta: " + e.getMessage(), 
                                      JOptionPane.ERROR_MESSAGE);
                    vista.actualizarEstado("Error al generar respuesta");
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Obtiene el nombre completo de un idioma a partir de su código
     * @param codigoIdioma El código del idioma (ej. "en", "es")
     * @return El nombre completo del idioma
     */
    private String obtenerNombreIdioma(String codigoIdioma) {
        switch (codigoIdioma) {
            case "es": return "Español";
            case "en": return "Inglés";
            case "fr": return "Francés";
            case "de": return "Alemán";
            case "it": return "Italiano";
            case "pt": return "Portugués";
            case "ru": return "Ruso";
            case "zh": return "Chino";
            case "ja": return "Japonés";
            case "ko": return "Coreano";
            case "ar": return "Árabe";
            default: return codigoIdioma;
        }
    }
}
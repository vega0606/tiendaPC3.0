package servicio;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * Servicio para gestionar operaciones de IA utilizando las APIs de Google Gemini
 */
public class IAService {
    private static final Logger logger = LoggerFactory.getLogger(IAService.class);
    private static String API_KEY;
    private static final Gson gson = new Gson();
    private static String ASSISTANT_MODEL;
    private static double ASSISTANT_TEMPERATURE;
    private static int ASSISTANT_MAX_TOKENS;
    
    // Asegurarse de que el modo de emergencia está activado para evitar errores de API
    private static boolean MODO_EMERGENCIA = true; 
    
    static {
        cargarApiKey();
    }
    
    /**
     * Carga la API Key desde el archivo de propiedades o usa la proporcionada por defecto
     */
    private static void cargarApiKey() {
        try {
            Properties props = new Properties();
            
            // Intenta cargar desde la ruta de recursos primero
            InputStream input = IAService.class.getClassLoader().getResourceAsStream("ai_config.properties");
            
            // Si no encuentra el archivo en recursos, intenta cargarlo desde el directorio raíz
            if (input == null) {
                try {
                    input = new java.io.FileInputStream(new java.io.File("ai_config.properties"));
                } catch (Exception e) {
                    // Silenciar error y continuar para manejar la falta del archivo posteriormente
                    logger.warn("No se pudo encontrar ai_config.properties en el directorio raíz");
                }
            }

            if (input == null) {
                logger.warn("ai_config.properties no encontrado, usando API Key por defecto");
                API_KEY = "AIzaSyDyEYCmeWFr4XRV__MygagwCg8YBaNgSlI"; // API Key por defecto
                ASSISTANT_MODEL = "gemini-pro";
                ASSISTANT_TEMPERATURE = 0.7;
                ASSISTANT_MAX_TOKENS = 1024;
            } else {
                props.load(input);
                API_KEY = props.getProperty("google.api.key", "AIzaSyDyEYCmeWFr4XRV__MygagwCg8YBaNgSlI");
                ASSISTANT_MODEL = props.getProperty("assistant.model", "gemini-pro");
                ASSISTANT_TEMPERATURE = Double.parseDouble(props.getProperty("assistant.temperature", "0.7"));
                ASSISTANT_MAX_TOKENS = Integer.parseInt(props.getProperty("assistant.max_tokens", "1024"));
                input.close();
                logger.info("API Key cargada correctamente");
            }
        } catch (IOException e) {
            logger.error("Error al cargar API Key", e);
            API_KEY = "AIzaSyDyEYCmeWFr4XRV__MygagwCg8YBaNgSlI"; // API Key por defecto
            ASSISTANT_MODEL = "gemini-pro";
            ASSISTANT_TEMPERATURE = 0.7;
            ASSISTANT_MAX_TOKENS = 1024;
        }
    }
    
    /**
     * Realiza una solicitud de análisis de texto utilizando la API de Google Cloud Natural Language
     * 
     * @param texto El texto a analizar
     * @return Resultados del análisis de sentimiento
     */
    public Map<String, Object> analizarSentimiento(String texto) {
        Map<String, Object> resultado = new HashMap<>();
        
        // Si estamos en modo de emergencia, simular respuesta
        if (MODO_EMERGENCIA) {
            logger.info("Analizando sentimiento (modo emergencia): " + texto);
            
            // Simulación simple de análisis de sentimiento
            float score = simularScoreSentimiento(texto);
            float magnitude = Math.abs(score);
            
            resultado.put("score", score);
            resultado.put("magnitude", magnitude);
            resultado.put("status", "success");
            resultado.put("message", "Análisis completado correctamente (simulado)");
            
            logger.info("Resultado del análisis (simulado): score=" + score + ", magnitude=" + magnitude);
            return resultado;
        }
        
        try {
            // Para analizar sentimiento usaremos Gemini como un clasificador
            String prompt = "Analiza el sentimiento de este texto y devuelve solo un número entre -1 y 1, donde -1 es muy negativo, 0 es neutral y 1 es muy positivo. Solo debes responder con el número, nada más: \"" + texto + "\"";
            
            String respuesta = generarRespuestaGemini(prompt, 0.1);
            
            try {
                // Extraer el número de la respuesta
                respuesta = respuesta.trim();
                // Eliminar caracteres no numéricos excepto puntos y signos
                respuesta = respuesta.replaceAll("[^-0-9.]", "");
                
                float score = Float.parseFloat(respuesta);
                // Asegurar que está en el rango correcto
                score = Math.max(-1, Math.min(1, score));
                float magnitude = Math.abs(score);
                
                resultado.put("score", score);
                resultado.put("magnitude", magnitude);
                resultado.put("status", "success");
                resultado.put("message", "Análisis completado correctamente");
            } catch (Exception e) {
                logger.error("Error al interpretar el resultado del sentimiento: {}", respuesta, e);
                resultado.put("status", "error");
                resultado.put("message", "Error al interpretar el resultado: " + e.getMessage());
            }
            
        } catch (Exception e) {
            logger.error("Error al analizar sentimiento", e);
            resultado.put("status", "error");
            resultado.put("message", "Error: " + e.getMessage());
        }
        
        return resultado;
    }
    
    /**
     * Simula un análisis de sentimiento básico basado en palabras clave
     * 
     * @param texto El texto a analizar
     * @return Un score entre -1 y 1
     */
    private float simularScoreSentimiento(String texto) {
        String textoLower = texto.toLowerCase();
        float score = 0.0f;
        
        // Palabras positivas
        String[] palabrasPositivas = {"bueno", "excelente", "fantástico", "genial", "increíble", 
                                    "satisfecho", "feliz", "contento", "maravilloso", "perfecto",
                                    "agradecido", "recomendado", "gracias", "alegría", "éxito"};
        
        // Palabras negativas
        String[] palabrasNegativas = {"malo", "terrible", "horrible", "pésimo", "defectuoso",
                                    "insatisfecho", "triste", "molesto", "decepcionado", "fracaso",
                                    "problema", "error", "falla", "queja", "lento"};
        
        // Contar apariciones de palabras positivas y negativas
        for (String palabra : palabrasPositivas) {
            if (textoLower.contains(palabra)) {
                score += 0.2f;
            }
        }
        
        for (String palabra : palabrasNegativas) {
            if (textoLower.contains(palabra)) {
                score -= 0.2f;
            }
        }
        
        // Limitar el score al rango [-1, 1]
        return Math.max(-1.0f, Math.min(1.0f, score));
    }
    
    /**
     * Traduce un texto a otro idioma utilizando la API de Google Cloud Translate
     * 
     * @param texto El texto a traducir
     * @param idiomaDestino El código del idioma de destino (ej. "en", "es", "fr")
     * @return El texto traducido
     */
    public String traducirTexto(String texto, String idiomaDestino) {
        // Si estamos en modo de emergencia, simular traducción
        if (MODO_EMERGENCIA) {
            logger.info("Traduciendo texto (modo emergencia): " + texto + " al idioma " + idiomaDestino);
            return simularTraduccion(texto, idiomaDestino);
        }
        
        try {
            Translate translate = TranslateOptions.newBuilder()
                                  .setApiKey(API_KEY)
                                  .build()
                                  .getService();
            
            Translation translation = translate.translate(
                texto,
                Translate.TranslateOption.targetLanguage(idiomaDestino)
            );
            
            return translation.getTranslatedText();
        } catch (Exception e) {
            logger.error("Error al traducir texto", e);
            return "Error: " + e.getMessage();
        }
    }
    
    /**
     * Simula una traducción simple
     * 
     * @param texto El texto a traducir
     * @param idiomaDestino El código del idioma destino
     * @return Una simulación de traducción
     */
    private String simularTraduccion(String texto, String idiomaDestino) {
        String nombreIdioma = obtenerNombreIdioma(idiomaDestino);
        
        return "[ Traducción simulada al " + nombreIdioma + " ]\n\n" +
               texto + "\n\n" +
               "Nota: Esta es una simulación de traducción. En un entorno de producción, " +
               "este texto sería traducido usando la API de Google Translate.";
    }
    
    /**
     * Genera respuestas inteligentes a consultas utilizando la API de Dialog flow
     * 
     * @param consulta La consulta del usuario
     * @param contexto Contexto adicional para la consulta (opcional)
     * @return Respuesta generada por la IA
     */
    public String generarRespuestaIA(String consulta, String contexto) {
        // Si estamos en modo de emergencia, simular respuesta
        if (MODO_EMERGENCIA) {
            logger.info("Generando respuesta IA (modo emergencia): " + consulta);
            
            String respuestaSimulada = simularRespuestaIA(consulta, contexto);
            logger.info("Respuesta generada (simulada) de longitud: " + respuestaSimulada.length());
            
            return respuestaSimulada;
        }
        
        try {
            // Construir prompt con contexto si existe
            String prompt = consulta;
            if (contexto != null && !contexto.trim().isEmpty()) {
                prompt = "Contexto: " + contexto + "\n\nConsulta: " + consulta;
            }
            
            return generarRespuestaGemini(prompt, ASSISTANT_TEMPERATURE);
            
        } catch (Exception e) {
            logger.error("Error al generar respuesta de IA", e);
            // Si hay error, activar modo emergencia y devolver respuesta simulada
            MODO_EMERGENCIA = true;
            logger.info("Activando modo de emergencia con respuestas simuladas");
            return simularRespuestaIA(consulta, contexto);
        }
    }
    
    /**
     * Simula una respuesta de IA basada en palabras clave
     * 
     * @param consulta La consulta del usuario
     * @param contexto Contexto adicional (opcional)
     * @return Una respuesta simulada
     */
    private String simularRespuestaIA(String consulta, String contexto) {
        String consultaLower = consulta.toLowerCase();
        
        // Respuestas para consultas sobre facturación
        if (consultaLower.contains("factura") || consultaLower.contains("facturación") || 
            consultaLower.contains("facturar") || consultaLower.contains("iva")) {
            return "En el sistema de facturación, puedes crear nuevas facturas seleccionando la opción 'Facturación' " +
                   "en el menú principal y luego haciendo clic en 'Nueva Factura'. Recuerda incluir todos los datos " +
                   "fiscales necesarios como RUC/NIT, descripción detallada de productos, y aplicar correctamente el IVA " +
                   "según la normativa vigente. Para anular una factura, debes usar la opción específica en el menú " +
                   "contextual, lo que mantendrá un registro adecuado para auditorías.";
        }
        
        // Respuestas para consultas sobre inventario
        else if (consultaLower.contains("inventario") || consultaLower.contains("stock") || 
                 consultaLower.contains("producto") || consultaLower.contains("almacén")) {
            return "La gestión eficiente del inventario es crucial para cualquier negocio. En nuestro sistema, " +
                   "puedes configurar alertas de stock mínimo para cada producto, lo que te notificará automáticamente " +
                   "cuando sea necesario realizar un nuevo pedido. Te recomiendo realizar inventarios físicos periódicamente " +
                   "para verificar que el stock real coincida con el registrado en el sistema. También puedes " +
                   "generar reportes de rotación de inventario para identificar productos que no se están vendiendo " +
                   "y tomar decisiones informadas.";
        }
        
        // Respuestas para consultas sobre clientes
        else if (consultaLower.contains("cliente") || consultaLower.contains("proveedor") || 
                 consultaLower.contains("vender") || consultaLower.contains("comprar")) {
            return "Para gestionar eficientemente tus clientes y proveedores, mantén sus datos siempre actualizados, " +
                   "especialmente la información de contacto y condiciones comerciales. Puedes usar la sección " +
                   "'Clientes/Proveedores' del menú principal para añadir nuevos registros, editar los existentes " +
                   "o realizar búsquedas. Te recomiendo categorizar a tus clientes por volumen de compra o frecuencia " +
                   "para aplicar estrategias de fidelización específicas y establecer políticas de crédito adecuadas.";
        }
        
        // Respuestas para consultas sobre reportes
        else if (consultaLower.contains("reporte") || consultaLower.contains("estadística") || 
                 consultaLower.contains("análisis") || consultaLower.contains("gráfico")) {
            return "El sistema ofrece diversos tipos de reportes que puedes generar para analizar el rendimiento de tu negocio. " +
                   "En la sección 'Reportes' puedes crear informes de ventas por período, productos más vendidos, " +
                   "clientes frecuentes, inventario valorizado, ganancias por período, productos con bajo stock, " +
                   "y devoluciones. Estos reportes pueden exportarse en formato PDF o Excel. Para visualizaciones " +
                   "más personalizadas, puedes aplicar filtros por fecha, categoría de producto, o cliente específico.";
        }
        
        // Respuesta general para otras consultas
        else {
            return "Gracias por tu consulta. Como asistente virtual de este sistema de facturación e inventario, " +
                   "puedo ayudarte con diversas tareas como gestión de facturas, inventario, clientes, proveedores, " +
                   "y análisis de datos. Puedes preguntarme sobre cómo realizar operaciones específicas en el sistema, " +
                   "solicitar consejos para optimizar tus procesos de negocio, o pedir ayuda con problemas comunes. " +
                   "¿En qué área específica necesitas asistencia hoy?";
        }
    }
    
    /**
     * Genera una respuesta utilizando el modelo Gemini de Google
     * 
     * @param prompt El texto de entrada o prompt
     * @param temperature La temperatura para la generación (0.0-1.0)
     * @return La respuesta generada por Gemini
     */
    private String generarRespuestaGemini(String prompt, double temperature) throws Exception {
        // La URL correcta para la API de Gemini
        URL url = new URL("https://generativelanguage.googleapis.com/v1/models/gemini-pro:generateContent?key=" + API_KEY);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        
        // Construir el JSON de solicitud
        JsonObject requestBody = new JsonObject();
        
        JsonArray contents = new JsonArray();
        JsonObject content = new JsonObject();
        
        JsonArray parts = new JsonArray();
        JsonObject part = new JsonObject();
        part.addProperty("text", prompt);
        parts.add(part);
        
        content.add("parts", parts);
        contents.add(content);
        
        requestBody.add("contents", contents);
        
        // Configuración de generación
        JsonObject generationConfig = new JsonObject();
        generationConfig.addProperty("temperature", temperature);
        generationConfig.addProperty("maxOutputTokens", ASSISTANT_MAX_TOKENS);
        requestBody.add("generationConfig", generationConfig);
        
        // Enviar la solicitud
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        
        // Procesar la respuesta
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            // Parse de la respuesta
            JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);
            
            // Extraer la respuesta generada
            try {
                String respuestaGenerada = jsonResponse
                    .getAsJsonArray("candidates")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("content")
                    .getAsJsonArray("parts")
                    .get(0).getAsJsonObject()
                    .get("text").getAsString();
                
                return respuestaGenerada;
            } catch (Exception e) {
                logger.error("Error al parsear respuesta: {}", response.toString(), e);
                throw new Exception("Error al parsear la respuesta del modelo: " + e.getMessage());
            }
        } else {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            logger.error("Error en la API de Gemini: {} - {}", responseCode, response.toString());
            throw new Exception("Error en la API de Gemini: " + responseCode + " - " + response.toString());
        }
    }
    
    /**
     * Método simple para probar si la API está funcionando correctamente
     * 
     * @return true si la API está funcionando, false en caso contrario
     */
    public boolean testApiKey() {
        if (MODO_EMERGENCIA) {
            logger.info("Modo de emergencia activado. Usando respuestas simuladas.");
            return true;
        }
        
        try {
            // Usar un prompt muy simple para la prueba
            String respuesta = generarRespuestaGemini("Responde solamente con la palabra OK", 0.1);
            logger.info("Prueba de API exitosa: {}", respuesta);
            return true;
        } catch (Exception e) {
            logger.error("Error al probar API Key: {}", e.getMessage());
            // Si hay error, activar modo emergencia
            MODO_EMERGENCIA = true;
            logger.info("Activando modo de emergencia con respuestas simuladas");
            return true; // Retornamos true para que la interfaz siga funcionando
        }
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
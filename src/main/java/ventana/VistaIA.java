package ventana;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import controlador.VistaIAController;

/**
 * Vista para la integración de Inteligencia Artificial
 */
public class VistaIA extends Vista {
    
    // Componentes principales
    private JPanel cardsPanel;
    private CardLayout cardLayout;
    private JComboBox<String> funcionalidadCombo;
    private JTextArea textAreaInput;
    private JTextArea textAreaResultado;
    private JButton btnProcesar;
    private JLabel estadoLabel;
    
    // Paneles específicos para cada funcionalidad
    private JPanel panelAnalisisSentimiento;
    private JPanel panelTraduccion;
    private JPanel panelAsistente;
    
    // Componentes específicos para la traducción
    private JComboBox<String> idiomaDestinoCombo;
    
    // Componentes específicos para el asistente virtual
    private JTextArea textAreaContexto;
    
    // Controlador
    private VistaIAController controller;
    
    /**
     * Constructor de la vista de IA
     */
    public VistaIA() {
        super("Asistente de Inteligencia Artificial");
    }
    
    @Override
    protected void inicializarPanel() {
        // Header
        JPanel headerPanel = crearHeaderPanel();
        
        // Panel izquierdo - Opciones
        String[] sidebarItems = {
            "Análisis de Sentimiento", "Traducción", "Asistente Virtual"
        };
        JPanel sidebarPanel = crearSidebarPanel(sidebarItems);
        
        // Panel principal - Funcionalidades de IA
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainContentPanel.setLayout(new BorderLayout());
        
        // Selector de funcionalidad
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Seleccione una funcionalidad:"));
        
        funcionalidadCombo = new JComboBox<>(new String[] {
            "Análisis de Sentimiento", "Traducción", "Asistente Virtual"
        });
        
        funcionalidadCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cambiarFuncionalidad();
            }
        });
        
        topPanel.add(funcionalidadCombo);
        
        // Paneles para cada funcionalidad
        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);
        
        // Panel de análisis de sentimiento
        panelAnalisisSentimiento = crearPanelAnalisisSentimiento();
        cardsPanel.add(panelAnalisisSentimiento, "Análisis de Sentimiento");
        
        // Panel de traducción
        panelTraduccion = crearPanelTraduccion();
        cardsPanel.add(panelTraduccion, "Traducción");
        
        // Panel de asistente virtual
        panelAsistente = crearPanelAsistenteVirtual();
        cardsPanel.add(panelAsistente, "Asistente Virtual");
        
        // Panel de resultados - MODIFICADO PARA HACERLO MÁS VISIBLE
        JPanel panelResultados = new JPanel(new BorderLayout());
        panelResultados.setBorder(BorderFactory.createTitledBorder("Resultados"));
        
        textAreaResultado = new JTextArea();
        textAreaResultado.setEditable(false);
        textAreaResultado.setWrapStyleWord(true);
        textAreaResultado.setLineWrap(true);
        textAreaResultado.setFont(new Font("Arial", Font.PLAIN, 14));
        textAreaResultado.setBackground(new Color(250, 250, 250)); // Fondo más claro para asegurar contraste
        textAreaResultado.setForeground(Color.BLACK); // Texto negro para asegurar visibilidad
        
        JScrollPane scrollResultados = new JScrollPane(textAreaResultado);
        scrollResultados.setPreferredSize(new Dimension(0, 250)); // AUMENTADO TAMAÑO VERTICAL
        panelResultados.add(scrollResultados, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        btnProcesar = new JButton("Procesar");
        btnProcesar.setBackground(new Color(74, 134, 232));
        btnProcesar.setForeground(Color.WHITE);
        btnProcesar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                procesarSolicitud();
            }
        });
        
        JButton btnLimpiar = new JButton("Limpiar"); // AÑADIDO BOTÓN LIMPIAR
        btnLimpiar.setBackground(new Color(180, 180, 180));
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textAreaInput.setText("");
                textAreaResultado.setText("");
                if (textAreaContexto != null) {
                    textAreaContexto.setText("");
                }
            }
        });
        
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnProcesar);
        
        // Panel de estado
        JPanel panelEstado = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelEstado.setBorder(BorderFactory.createEtchedBorder());
        
        estadoLabel = new JLabel("Inicializando...");
        panelEstado.add(estadoLabel);
        
        // Añadir todo al panel principal - MODIFICADO LAYOUT PARA MEJOR VISIBILIDAD
        mainContentPanel.add(topPanel, BorderLayout.NORTH);
        
        // Crear un panel con separador vertical para contenido y resultados
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, cardsPanel, panelResultados);
        splitPane.setResizeWeight(0.5); // División equitativa del espacio
        mainContentPanel.add(splitPane, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(panelBotones, BorderLayout.NORTH);
        bottomPanel.add(panelEstado, BorderLayout.SOUTH);
        
        mainContentPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Añadir todo al panel principal
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(sidebarPanel, BorderLayout.WEST);
        panel.add(mainContentPanel, BorderLayout.CENTER);
        
        // Inicializar controlador
        controller = new VistaIAController(this);
    }
    
    /**
     * Crea el panel para el análisis de sentimiento
     * @return Panel configurado
     */
    private JPanel crearPanelAnalisisSentimiento() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Análisis de Sentimiento"));
        
        JLabel infoLabel = new JLabel("<html>Esta funcionalidad analiza el sentimiento (positivo, negativo o neutral) de un texto.</html>");
        infoLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(infoLabel, BorderLayout.NORTH);
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(new JLabel("Ingrese el texto a analizar:"), BorderLayout.NORTH);
        
        textAreaInput = new JTextArea();
        textAreaInput.setWrapStyleWord(true);
        textAreaInput.setLineWrap(true);
        textAreaInput.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JScrollPane scrollInput = new JScrollPane(textAreaInput);
        scrollInput.setPreferredSize(new Dimension(0, 150));
        inputPanel.add(scrollInput, BorderLayout.CENTER);
        
        panel.add(inputPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Crea el panel para la traducción
     * @return Panel configurado
     */
    private JPanel crearPanelTraduccion() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Traducción de Texto"));
        
        JLabel infoLabel = new JLabel("<html>Esta funcionalidad traduce texto entre diferentes idiomas utilizando la API de Google Translate.</html>");
        infoLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(infoLabel, BorderLayout.NORTH);
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.add(new JLabel("Ingrese el texto a traducir:"), BorderLayout.NORTH);
        
        textAreaInput = new JTextArea();
        textAreaInput.setWrapStyleWord(true);
        textAreaInput.setLineWrap(true);
        textAreaInput.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JScrollPane scrollInput = new JScrollPane(textAreaInput);
        scrollInput.setPreferredSize(new Dimension(0, 150));
        textPanel.add(scrollInput, BorderLayout.CENTER);
        
        JPanel idiomaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        idiomaPanel.add(new JLabel("Traducir a:"));
        
        idiomaDestinoCombo = new JComboBox<>(new String[] {
            "en - Inglés", "es - Español", "fr - Francés", "de - Alemán", 
            "it - Italiano", "pt - Portugués", "ru - Ruso", "zh - Chino",
            "ja - Japonés", "ko - Coreano", "ar - Árabe"
        });
        idiomaPanel.add(idiomaDestinoCombo);
        
        inputPanel.add(textPanel, BorderLayout.CENTER);
        inputPanel.add(idiomaPanel, BorderLayout.SOUTH);
        
        panel.add(inputPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Crea el panel para el asistente virtual
     * @return Panel configurado
     */
    private JPanel crearPanelAsistenteVirtual() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Asistente Virtual"));
        
        JLabel infoLabel = new JLabel("<html>Esta funcionalidad utiliza inteligencia artificial para responder preguntas y generar contenido basado en texto.</html>");
        infoLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(infoLabel, BorderLayout.NORTH);
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        
        JPanel consultaPanel = new JPanel(new BorderLayout());
        consultaPanel.add(new JLabel("Ingrese su consulta:"), BorderLayout.NORTH);
        
        textAreaInput = new JTextArea();
        textAreaInput.setWrapStyleWord(true);
        textAreaInput.setLineWrap(true);
        textAreaInput.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JScrollPane scrollInput = new JScrollPane(textAreaInput);
        scrollInput.setPreferredSize(new Dimension(0, 100));
        consultaPanel.add(scrollInput, BorderLayout.CENTER);
        
        JPanel contextoPanel = new JPanel(new BorderLayout());
        contextoPanel.add(new JLabel("Contexto adicional (opcional):"), BorderLayout.NORTH);
        
        textAreaContexto = new JTextArea();
        textAreaContexto.setWrapStyleWord(true);
        textAreaContexto.setLineWrap(true);
        textAreaContexto.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JScrollPane scrollContexto = new JScrollPane(textAreaContexto);
        scrollContexto.setPreferredSize(new Dimension(0, 80));
        contextoPanel.add(scrollContexto, BorderLayout.CENTER);
        
        inputPanel.add(consultaPanel, BorderLayout.CENTER);
        inputPanel.add(contextoPanel, BorderLayout.SOUTH);
        
        panel.add(inputPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Cambia la funcionalidad actual según la selección del combo box
     */
    private void cambiarFuncionalidad() {
        String funcionalidad = (String) funcionalidadCombo.getSelectedItem();
        cardLayout.show(cardsPanel, funcionalidad);
        
        // Limpiar resultados
        textAreaResultado.setText("");
        
        // Ajustar el texto del botón según la funcionalidad
        if ("Análisis de Sentimiento".equals(funcionalidad)) {
            btnProcesar.setText("Analizar");
        } else if ("Traducción".equals(funcionalidad)) {
            btnProcesar.setText("Traducir");
        } else if ("Asistente Virtual".equals(funcionalidad)) {
            btnProcesar.setText("Enviar Consulta");
        }
    }
    
    /**
     * Procesa la solicitud actual según la funcionalidad seleccionada
     */
    private void procesarSolicitud() {
        String funcionalidad = (String) funcionalidadCombo.getSelectedItem();
        String texto = textAreaInput.getText();
        
        if (texto == null || texto.trim().isEmpty()) {
            mostrarMensaje("Por favor ingrese un texto para procesar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Limpiar el área de resultados antes de procesar
        textAreaResultado.setText("");
        
        // Añadir texto de espera mientras se procesa
        textAreaResultado.setText("Procesando solicitud...");
        
        if ("Análisis de Sentimiento".equals(funcionalidad)) {
            controller.analizarSentimiento(texto);
        } else if ("Traducción".equals(funcionalidad)) {
            String idiomaSeleccionado = (String) idiomaDestinoCombo.getSelectedItem();
            String codigoIdioma = idiomaSeleccionado.split(" - ")[0];
            controller.traducirTexto(texto, codigoIdioma);
        } else if ("Asistente Virtual".equals(funcionalidad)) {
            String contexto = textAreaContexto.getText();
            controller.generarRespuestaIA(texto, contexto);
        }
    }
    
    /**
     * Muestra un mensaje al usuario
     * @param mensaje El mensaje a mostrar
     * @param tipo El tipo de mensaje
     */
    public void mostrarMensaje(String mensaje, int tipo) {
        JOptionPane.showMessageDialog(panel, mensaje, "Asistente IA", tipo);
    }
    
    /**
     * Muestra el resultado de la operación en el área de texto
     * @param resultado El resultado a mostrar
     */
    public void mostrarResultado(String resultado) {
        textAreaResultado.setText(resultado);
        // Mover al inicio del texto
        textAreaResultado.setCaretPosition(0);
        
        // Añadir log para confirmar que se está llamando a este método
        System.out.println("Mostrando resultado: " + (resultado.length() > 50 ? resultado.substring(0, 50) + "..." : resultado));
    }
    
    /**
     * Actualiza el estado de la aplicación
     * @param estado Texto de estado a mostrar
     */
    public void actualizarEstado(String estado) {
        estadoLabel.setText(estado);
        System.out.println("Estado actualizado: " + estado);
    }
}
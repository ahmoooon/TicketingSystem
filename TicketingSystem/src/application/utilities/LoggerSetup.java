/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package application.utilities;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
/**
 *
 * @author zhili
 */
public class LoggerSetup {
    private static final Logger LOGGER = Logger.getLogger(presentation.gui.CinemaApplication.class.getName());

    private LoggerSetup() {
        // Prevent instantiation
    }

    public static Logger getLogger() {
        // Guard clause: Configure only if not already configured (e.g., handlers are empty)
        if (LOGGER.getHandlers().length == 0) {
            // Set level to INFO to capture important application flow
            LOGGER.setLevel(Level.INFO);

            // Create console handler
            ConsoleHandler handler = new ConsoleHandler();
            // Set handler level
            handler.setLevel(Level.INFO);
            // Use a simple formatter for clean output
            handler.setFormatter(new SimpleFormatter());

            // Add handler to the logger
            LOGGER.addHandler(handler);
            
            // Prevent logs from being passed up to the root logger which might duplicate output
            LOGGER.setUseParentHandlers(false);
        }
        return LOGGER;
    }
}

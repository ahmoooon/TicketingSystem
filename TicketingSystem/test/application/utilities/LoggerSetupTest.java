package application.utilities;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Unit tests for LoggerSetup class
 */
public class LoggerSetupTest {
    
    @Test
    public void testGetLogger_ReturnsNonNull() {
        Logger logger = LoggerSetup.getLogger();
        
        assertNotNull("Logger should not be null", logger);
    }
    
    @Test
    public void testGetLogger_ReturnsSameInstance() {
        Logger logger1 = LoggerSetup.getLogger();
        Logger logger2 = LoggerSetup.getLogger();
        
        assertSame("Should return same logger instance", logger1, logger2);
    }
    
    @Test
    public void testGetLogger_HasCorrectLevel() {
        Logger logger = LoggerSetup.getLogger();
        
        assertEquals("Logger should be set to INFO level", 
                     Level.INFO, logger.getLevel());
    }
    
    @Test
    public void testGetLogger_HasHandlers() {
        Logger logger = LoggerSetup.getLogger();
        Handler[] handlers = logger.getHandlers();
        
        assertTrue("Logger should have at least one handler", 
                   handlers.length > 0);
    }
    
    @Test
    public void testGetLogger_HandlerHasCorrectLevel() {
        Logger logger = LoggerSetup.getLogger();
        Handler[] handlers = logger.getHandlers();
        
        if (handlers.length > 0) {
            assertEquals("Handler should be set to INFO level",
                        Level.INFO, handlers[0].getLevel());
        }
    }
    
    @Test
    public void testGetLogger_DoesNotUseParentHandlers() {
        Logger logger = LoggerSetup.getLogger();
        
        assertFalse("Should not use parent handlers to avoid duplication",
                   logger.getUseParentHandlers());
    }
    
    @Test
    public void testGetLogger_CanLogInfo() {
        Logger logger = LoggerSetup.getLogger();
        
        // This should not throw exception
        try {
            logger.info("Test info message");
            assertTrue("Info logging should work", true);
        } catch (Exception e) {
            fail("Should be able to log INFO messages: " + e.getMessage());
        }
    }
    
    @Test
    public void testGetLogger_CanLogWarning() {
        Logger logger = LoggerSetup.getLogger();
        
        try {
            logger.warning("Test warning message");
            assertTrue("Warning logging should work", true);
        } catch (Exception e) {
            fail("Should be able to log WARNING messages: " + e.getMessage());
        }
    }
    
    @Test
    public void testGetLogger_CanLogSevere() {
        Logger logger = LoggerSetup.getLogger();
        
        try {
            logger.severe("Test severe message");
            assertTrue("Severe logging should work", true);
        } catch (Exception e) {
            fail("Should be able to log SEVERE messages: " + e.getMessage());
        }
    }
    
    @Test
    public void testGetLogger_ConfigurationIsIdempotent() {
        // Call multiple times
        Logger logger1 = LoggerSetup.getLogger();
        int handlerCount1 = logger1.getHandlers().length;
        
        Logger logger2 = LoggerSetup.getLogger();
        int handlerCount2 = logger2.getHandlers().length;
        
        Logger logger3 = LoggerSetup.getLogger();
        int handlerCount3 = logger3.getHandlers().length;
        
        assertEquals("Handler count should remain stable", 
                     handlerCount1, handlerCount2);
        assertEquals("Handler count should remain stable", 
                     handlerCount2, handlerCount3);
    }
}
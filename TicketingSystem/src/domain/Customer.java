/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package domain;

/**
 *
 * @author Cason Soh
 */
// Customer.java - Domain/Data Layer
// NOTE: This class holds the HASHED password in the 'password' field.

import application.utilities.LoggerSetup;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Customer { 
    // Match your original variable names
    private String name;
    private String password; // Stores the HASHED password
    private int id;
    
    // Match your original static counter logic
    private static int assignid = 1; // Start at 1 instead of 0001 for int type

    // Empty Constructor (Updated to increment ID if used, matching original)
    public Customer() {
        assignid++;
    }

    // Main Constructor (Accepts HASHED password from AuthService)
    public Customer(String name, String hashedPassword) {
        this.name = name;
        this.password = hashedPassword;
        // Match your original ID assignment logic
        id = assignid; 
        assignid++;
    }
    
    // --- Getters (Matching Original Names) ---
    
    public String getName() { 
        return name; 
    }

    // IMPORTANT: This returns the HASHED password for internal use (e.g., AuthService verification)
    public String getPassword() { 
        return password; 
    }
    
    public int getId(){
        return id;
    }

    // --- Setters (Matching Original Names) ---

    public void setName(String name) { 
        this.name = name; 
    }

    // WARNING: This setter should only be called by the system to set the HASHED password
    public void setPassword(String hashedPassword) { 
        this.password = hashedPassword; 
    }

    // --- JSON Conversion Methods (Updated to use original variable names) ---
    
    /**
     * Converts the Customer object to a single-line JSON string.
     * @return 
     */
    public String toJsonString() {
        return String.format(
            "{\"name\":\"%s\", \"password\":\"%s\", \"id\":%d, \"assignid\":%d}", 
            this.name, 
            this.password,
            this.id,
            assignid // Include static counter value for persistence
        );
    }
    
    /**
     * Parses a JSON string back into a Customer object.NOTE: This manual parsing is highly brittle and for demonstration only.
     * @param json
     * @return 
     */
    public static Customer fromJsonString(String json) {
        Logger logger = LoggerSetup.getLogger();
        try {
            // Find the values by parsing the string based on known keys (Hashed password is found first)
            int passStart = json.indexOf("\"password\":\"") + 12;
            int passEnd = json.indexOf("\", \"id\":");
            String password = json.substring(passStart, passEnd);
            
            int nameStart = json.indexOf("\"name\":\"") + 8;
            int nameEnd = json.indexOf("\", \"password\":");
            String name = json.substring(nameStart, nameEnd);
            
            // Extract the ID and static assignid value
            int idStart = json.indexOf("\"id\":") + 5;
            int idEnd = json.indexOf(", \"assignid\":");
            int id = Integer.parseInt(json.substring(idStart, idEnd));
            
            int assignidStart = json.indexOf("\"assignid\":") + 11;
            int assignidValue = Integer.parseInt(json.substring(assignidStart, json.lastIndexOf("}")));
            
            // Set the static counter to the highest value found across all loaded customers
            Customer.assignid = Math.max(Customer.assignid, assignidValue);

            // Manually create the customer object and set the ID, bypassing the constructor's increment
            Customer c = new Customer(name, password);
            c.id = id; // Override the ID assigned by the constructor
            
            return c;
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "Failed to parse JSON string: {0}. Error: {1}", new Object[]{json, e.getMessage()});
            return null;
        }
    }

    // --- Standard Java Methods ---
    
    @Override
    public String toString() {
        return name + " | " + password + " | ID: " + id;
    }
    
    // Removed cusLogin() and cusReg() methods, as their logic is now correctly placed in AuthService.
}

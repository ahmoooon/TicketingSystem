/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package application.services;

/**
 *
 * @author zhili
 */
import application.utilities.LoggerSetup;
import domain.Customer;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthService {
    private static final Logger logger = LoggerSetup.getLogger();
    
    // Fixed Staff Credentials
    public static final String STAFF_ID = "admin";
    private static final String STAFF_PASSWORD = "staff"; // Plaintext config
    private final String HASHED_STAFF_PASSWORD;

    private final PasswordService passwordService;
    private final OtpService otpService;

    public AuthService(PasswordService passwordService, OtpService otpService) {
        this.passwordService = passwordService;
        this.otpService = otpService;
        this.HASHED_STAFF_PASSWORD = passwordService.hashPassword(STAFF_PASSWORD);
    }

    /**
     * Authenticates the Staff user.
     * @param id The ID entered (must be STAFF_ID).
     * @param password The plaintext password entered.
     * @return Optional<String> containing STAFF_ID if successful, otherwise Optional.empty().
     */
    public Optional<String> authenticateStaff(String id, String password) {
        if (!id.equalsIgnoreCase(STAFF_ID)) {
             logger.warning("Staff login failed: ID entered is not STAFF_ID.");
             return Optional.empty();
        }
        
        if (passwordService.verifyPassword(password, HASHED_STAFF_PASSWORD)) {
            logger.info("Staff logged in successfully: " + STAFF_ID);
            return Optional.of(STAFF_ID);
        }
        
        logger.warning(() -> "Staff login failed: Incorrect password for ID: " + id);
        return Optional.empty();
    }

    /**
     * Authenticates a Customer against stored credentials.
     * @param customerList The list of registered customers.
     * @param name The name entered.
     * @param password The plaintext password entered.
     * @return Optional<Customer> object if successful, otherwise Optional.empty().
     */
    public Optional<Customer> authenticateCustomer(ArrayList<Customer> customerList, String name, String password) {
        // CRITICAL CHECK: Staff ID is explicitly forbidden here.
        if (name.equalsIgnoreCase(STAFF_ID)) {
             logger.warning(() -> "Staff ID attempted customer login: " + name);
             return Optional.empty();
        }

        Optional<Customer> foundCustomer = customerList.stream()
            .filter(c -> c.getName().equals(name.trim()))
            .findFirst();

        if (foundCustomer.isPresent()) {
            Customer customer = foundCustomer.get();

            if (passwordService.verifyPassword(password, customer.getPassword())) { 
                logger.log(Level.INFO, "Customer logged in successfully: {0}", customer.getName());
                // Return the actual Customer object
                return foundCustomer; 
            }
        }
        
        logger.log(Level.WARNING, "Customer login failed: User not found or incorrect password for {0}", name);
        return Optional.empty();
    }

    // --- Registration Methods ---
    
    /**
     * Performs initial validation and generates an OTP for registration.
     * @param userList The list of registered customers.
     * @param name The desired username.
     * @param password The desired password (validation done in AuthController).
     * @return The generated OTP string.
     * @throws IllegalArgumentException if validation fails (name is taken or is STAFF_ID).
     */
    public String startRegistration(ArrayList<Customer> userList, String name, String password) throws IllegalArgumentException {
        // Check for duplicate names (including STAFF_ID)
        if (name.equalsIgnoreCase(STAFF_ID)) {
            throw new IllegalArgumentException("The name '" + name + "' is reserved.");
        }
        if (userList.stream().anyMatch(c -> c.getName().equalsIgnoreCase(name))) {
            throw new IllegalArgumentException("This name has been used.");
        }
        
        return otpService.generateOtp();
    }
    
    public static boolean isPasswordValid(String password) {
        
        String regex = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[^a-zA-Z0-9]).{8,16}$";
        
        return password.matches(regex);
    }
    
    /**
     * Finalizes registration by hashing the password and creating the new Customer object.
     * @param name The customer name.
     * @param password The plaintext password.
     * @return The newly created Customer object.
     */
    public Customer finalizeCustomerRegistration(String name, String password) {
        String hashedPassword = passwordService.hashPassword(password);
        // The password passed here is the plaintext password from the UI
        return new Customer(name, hashedPassword); 
    }
}

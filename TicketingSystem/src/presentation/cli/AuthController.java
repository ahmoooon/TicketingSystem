/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package presentation.cli;

/**
 *
 * @author zhili
 */
// AuthController.java - Presentation Layer (I/O & Flow Control)
import application.services.AuthService;
import application.services.CustomerService;
import application.services.OtpService;
import application.utilities.LoggerSetup;
import application.utilities.Utility;
import domain.Customer;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Logger;
// Assuming imports for Customer, Utility, AuthService, CustomerService, OtpService, StaffController, LoggerSetup, etc.

public class AuthController {
    private final Scanner sc;
    private final AuthService authService;
    private final CustomerService customerService;
    private final OtpService otpService;
    private final StaffController staffController;
    private static final Logger logger = LoggerSetup.getLogger();

    public AuthController(Scanner sc, AuthService authService, CustomerService customerService, OtpService otpService, StaffController staffController) {
        this.sc = sc;
        this.authService = authService;
        this.customerService = customerService;
        this.otpService = otpService;
        this.staffController = staffController;
    }
    
    /**
     * Orchestrates the top-level Login and Registration menu loop.
     * @return true if a Customer successfully logs in; false if the program is exited.
     */
    public boolean startAuthFlow() {
        int loginChoice = 0;

        do {
            System.out.println("     =============================");
            System.out.println("     // Welcome to YSCM Cinema! //");
            System.out.println("     =============================");
            System.out.println("\n\t|| 1 | Login         ||\n\t|| 2 | Register      ||\n\t|| 3 | Exit program  ||\n");
            System.out.print("Choose one of the option from menu above ~ ");
            
            loginChoice = Utility.checkError(sc, 1, 3);

            switch (loginChoice) {
                case 1 -> {
                    // Login
                    Optional<String> result = handleLoginTypeSelection();
                    if (result.isPresent()) {
                        String name = result.get();
                        if (!name.equalsIgnoreCase(AuthService.STAFF_ID)) {
                            System.out.println("Proceeding to main menu...");
                            return true; // Customer successfully logged in
                        }
                        // Staff was logged in, staffController handles its own loop
                        // Execution continues the loop (do-while) after staff logs out.
                    }
                }
                case 2 -> // Register
                    handleRegistrationUI();
                case 3 -> {
                    // Exit program
                    System.out.println("Exiting application...");
                    return false; // Indicates exit
                }
            }
        } while (true);
    }
    
    /**
     * Handles the choice between Customer and Staff login.
     * @return Optional<String> with the logged-in name (Customer or Staff ID), or Optional.empty().
     */
    private Optional<String> handleLoginTypeSelection() {
        System.out.println("\n\tLogin as > \n\t|| 1 | Customer      ||\n\t|| 2 | Staff         ||\n\t|| 3 | Back          ||\n");
        System.out.print("Choose one of the option from menu above ~ ");
        
        int staffOrCust = Utility.checkError(sc, 1, 3);

        switch (staffOrCust) {
            case 1: // Customer Login
                return handleCustomerLoginUI();
            case 2: // Staff Login
                return handleStaffLoginUI();
            case 3: // Back
            default:
                return Optional.empty();
        }
    }

    /**
     * Handles the user interaction for Customer login.
     * Calls authenticateCustomer (returns Optional<Customer>) and returns the name (String).
     * @return The name of the authenticated customer (String), or Optional.empty().
     */
    private Optional<String> handleCustomerLoginUI() {
        System.out.println("\n----------< Welcome to Customer Login >----------");

        System.out.print("\nPlease enter your name (0 for Back): ");
        String name = sc.nextLine().trim();
        if (name.equals("0")) return Optional.empty();
        
        System.out.print("Please enter your 6-character password: ");
        String password = sc.nextLine().trim();
        
        // Delegate core logic to AuthService (Now returns Optional<Customer>)
        Optional<Customer> customerResult = authService.authenticateCustomer(customerService.getCustomerList(), name, password);

        if (customerResult.isPresent()) {
            Customer customer = customerResult.get();
            // Store the logged-in customer object
            customerService.setLoggedInCustomer(customer); 

            System.out.println("\nWelcome, " + customer.getName() + "!");
            // Return the customer name (String) to match the expected flow
            return Optional.of(customer.getName());
        } else {
            // Error message covers both user not found and password incorrect
            System.out.println("\n<!> Wrong username or wrong password. Please reenter. <!>");
            return Optional.empty();
        }
    }
    
    /**
     * Handles the user interaction for Staff login.
     * Calls authenticateStaff (returns Optional<String>).
     * @return The name of the authenticated staff ("admin"), or Optional.empty().
     */
    private Optional<String> handleStaffLoginUI() {
        System.out.println("\n----------< Welcome to Staff Login >----------");

        System.out.print("\nEnter login ID (0 for Back): ");
        String id = sc.nextLine().trim();
        if (id.equals("0")) return Optional.empty();
        
        System.out.print("Enter password: ");
        String password = sc.nextLine().trim();
        
        // Delegate core logic to AuthService (Calls authenticateStaff)
        Optional<String> authResult = authService.authenticateStaff(id, password);
        
        if (authResult.isPresent() && authResult.get().equalsIgnoreCase(AuthService.STAFF_ID)) {
            System.out.println("\nWelcome Staff!");
            
            // Staff is logged in, transfer control to StaffController menu loop
            staffController.staffMainMenu(null, customerService.getCustomerList());
            
            // Execution returns here when the staff logs out.
            return Optional.of(AuthService.STAFF_ID); 
        } else {
            System.out.println("\n <!> Invalid ID or password! <!>");
            return Optional.empty();
        }
    }

    /**
     * Handles customer registration flow (I/O, validation feedback, OTP).
     * @return An Optional containing the newly registered Customer, or Optional.empty().
     */
    public Optional<Customer> handleRegistrationUI() {
        System.out.println("\n----------< Welcome to Registration >----------");
        String name = "";
        String password = "";
        String generatedOtp = "";

        while(true) {
            System.out.print("\nPlease enter your name for registration: ");
            name = sc.nextLine().trim();
            
            // --- Password Input and Validation Loop ---
            boolean validPassword = false;
            while (!validPassword) {
                System.out.print("Please enter a password (Must be 8-16 characters long and include at least 1 alphabet, 1 number, and 1 symbol): ");
                password = sc.nextLine().trim();
                
                if (AuthService.isPasswordValid(password)) {
                    validPassword = true;
                } else {
                    System.out.println(" <!> Password does not meet criteria: Must be 8-16 characters long and include at least one alphabet, one number, and one symbol. Please re-enter. <!>");
                }
            }
            
            try {
                // Delegate validation and OTP generation to AuthService
                generatedOtp = authService.startRegistration(customerService.getCustomerList(), name, password);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(" <!> " + e.getMessage() + ". Please try again. <!>");
            }
        }

        System.out.println("Generated OTP: " + generatedOtp); 
        System.out.print("Please enter the 6-digit OTP: ");
        String enteredOtp = sc.nextLine().trim();

        if (!otpService.verifyOtp(enteredOtp, generatedOtp)) {
            System.out.println("\n <!> Invalid OTP. Registration failed. <!>");
            return Optional.empty();
        }
        
        // Finalize Registration (Logic handled by AuthService, persistence by CustomerService)
        Customer newCustomer = authService.finalizeCustomerRegistration(name, password);
        customerService.addCustomer(newCustomer); // Save the new customer immediately
        
        System.out.println("Registration successful for " + newCustomer.getName() + "!");
        return Optional.of(newCustomer);
    }
}

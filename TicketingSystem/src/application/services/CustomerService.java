/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package application.services;

/**
 *
 * @author zhili
 */
// CustomerService.java - Business Logic Layer (Domain Management)
// CustomerService.java - Business Logic Layer (Domain Management)
import application.utilities.LoggerSetup;
import domain.Customer;
import infrastructure.repositories.DataFileHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CustomerService {
    private final String customerFile;
    // <<< Logger Initialization added >>>
    private static final Logger logger = LoggerSetup.getLogger();
    private Customer loggedInCustomer = null;
    private ArrayList<Customer> customerList;

    public CustomerService() {
        this("customer_data.json");
    }

    // 👇 test-friendly constructor
    public CustomerService(String customerFile) {
        this.customerFile = customerFile;
        this.customerList = loadCustomers();
        logger.log(Level.INFO, "CustomerService initialized with {0} existing customers.", this.customerList.size());
        this.customerList.removeIf(c -> c.getName().equalsIgnoreCase(AuthService.STAFF_ID));
    }
    
    // --- Persistence Logic ---
    private ArrayList<Customer> loadCustomers() {
        List<String> jsonLines = DataFileHandler.loadFromJsonFile(customerFile);
        return jsonLines.stream()
            .map(Customer::fromJsonString) 
            .filter(c -> c != null)       
            .collect(Collectors.toCollection(ArrayList::new));
    }
    
    public void setLoggedInCustomer(Customer customer) {
        this.loggedInCustomer = customer;
        if (customer != null) {
            logger.log(Level.INFO, "Current user set to: {0}", customer.getName());
        }
    }

    // NEW: Getter for the currently logged-in user
    public Optional<Customer> getLoggedInCustomer() {
        return Optional.ofNullable(loggedInCustomer);
    }
    
    private void saveCustomers() {
        List<String> jsonLines = this.customerList.stream()
            .map(Customer::toJsonString) 
            .collect(Collectors.toList());
            
        DataFileHandler.saveToJsonFile(jsonLines, customerFile);
    }
    
    // --- Core Management Logic ---
    public ArrayList<Customer> getCustomerList() {
        return this.customerList;
    }

    public boolean deleteCustomer(String nameToDelete) {
        if (nameToDelete == null || nameToDelete.trim().isEmpty() || nameToDelete.equalsIgnoreCase(AuthService.STAFF_ID)) {
            logger.warning("Attempted to delete customer with invalid name or tried to delete Staff account.");
            return false;
        }
        
        boolean removed = this.customerList.removeIf(c -> c.getName().equalsIgnoreCase(nameToDelete.trim()));

        if (removed) {
            saveCustomers();
            logger.log(Level.INFO, "Customer deleted successfully: {0}", nameToDelete);
        } else {
            logger.log(Level.WARNING, "Deletion failed: Customer not found: {0}", nameToDelete);
        }
        return removed;
    }
    
    public void addCustomer(Customer newCustomer) {
        this.customerList.add(newCustomer);
        saveCustomers();
        logger.info(() -> "New customer added and data saved: " + newCustomer.getName());
    }
}

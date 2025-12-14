/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package application.services;

import application.utilities.LoggerSetup;
import domain.Customer;
import domain.Payment;
import java.util.ArrayList;

/**
 *
 * @author zhili
 */
// StaffService.java - Business Logic Layer (Domain Management)
// Assuming ReportService exists
// StaffService.java - Business Logic Layer (Domain Management)
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

// Assuming ReportService and CustomerService are imported

public class StaffService {
    // <<< Logger Initialization added >>>
    private static final Logger logger = LoggerSetup.getLogger();
    
    private final ReportService reportService;
    private final CustomerService customerService; 
    
    public StaffService(ReportService reportService, CustomerService customerService) {
        this.reportService = reportService;
        this.customerService = customerService;
        logger.info("StaffService initialized.");
    }

    // New Adapter methods for the reports:
    public String getCustomerListReport(ArrayList<Customer> customer) {
        logger.info("Retrieving Customer List Report.");
        return reportService.getCustomerListReport(customer);
    }

    public String getMoviePurchaseReport(ArrayList<Payment> payment) {
        logger.info("Retrieving Movie Purchase Report.");
        return reportService.getMoviePurchaseReport(payment);
    }

    public String getFoodPurchaseReport(ArrayList<Payment> payment) {
        logger.info("Retrieving Food Purchase Report.");
        return reportService.getFoodPurchaseReport(payment);
    }
    
//    public String getSalesSummaryReport(ArrayList<Payment> payment) {
//        logger.info("Retrieving Sales Summary Report (NEW).");
//        return reportService.generateSalesSummaryReport(payment);
//    }
    
    public boolean deleteCustomerAccount(String name) {
        logger.log(Level.INFO, "Attempting to delete customer account: {0}", name);
        return customerService.deleteCustomer(name);
    }
}

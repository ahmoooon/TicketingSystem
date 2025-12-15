package application.services;

import application.utilities.LoggerSetup;
import domain.Customer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service layer for staff operations.
 * Handles customer management and report generation.
 */
public class StaffService {
    private static final Logger logger = LoggerSetup.getLogger();
    
    private final ReportService reportService;
    private final CustomerService customerService; 
    
    public StaffService(ReportService reportService, CustomerService customerService) {
        this.reportService = reportService;
        this.customerService = customerService;
        logger.info("StaffService initialized.");
    }

    // ========== TEXT REPORT METHODS (Existing) ==========
    
    public String getCustomerListReport(ArrayList<Customer> customer) {
        logger.info("Retrieving Customer List Report.");
        return reportService.getCustomerListReport(customer);
    }

    public String getMoviePurchaseReport() {
        logger.info("Retrieving Movie Purchase Report.");
        return reportService.getMoviePurchaseReport();
    }

    public String getFoodPurchaseReport() {
        logger.info("Retrieving Food Purchase Report.");
        return reportService.getFoodPurchaseReport();
    }
    
    public String getSalesSummaryReport() {
        logger.info("Retrieving Sales Summary Report.");
        return reportService.generateSalesSummaryReport();
    }
    
    // ========== PDF EXPORT METHODS (New) ==========
    
    /**
     * Exports Customer List Report to PDF file.
     * 
     * @param customers List of customers to include in report
     * @param outputFile Target PDF file
     * @throws IOException if PDF generation or file writing fails
     */
    public void exportCustomerListToPdf(ArrayList<Customer> customers, File outputFile) 
            throws IOException {
        logger.log(Level.INFO, "Exporting Customer List Report to PDF: {0}", outputFile.getName());
        reportService.exportCustomerListToPdf(customers, outputFile);
    }
    
    /**
     * Exports Movie Purchase Report to PDF file.
     * 
     * @param outputFile Target PDF file
     * @throws IOException if PDF generation or file writing fails
     */
    public void exportMoviePurchaseToPdf(File outputFile) throws IOException {
        logger.log(Level.INFO, "Exporting Movie Purchase Report to PDF: {0}", outputFile.getName());
        reportService.exportMoviePurchaseToPdf(outputFile);
    }
    
    /**
     * Exports Food Purchase Report to PDF file.
     * 
     * @param outputFile Target PDF file
     * @throws IOException if PDF generation or file writing fails
     */
    public void exportFoodPurchaseToPdf(File outputFile) throws IOException {
        logger.log(Level.INFO, "Exporting Food Purchase Report to PDF: {0}", outputFile.getName());
        reportService.exportFoodPurchaseToPdf(outputFile);
    }
    
    /**
     * Exports Sales Summary Report to PDF file.
     * 
     * @param outputFile Target PDF file
     * @throws IOException if PDF generation or file writing fails
     */
    public void exportSalesSummaryToPdf(File outputFile) throws IOException {
        logger.log(Level.INFO, "Exporting Sales Summary Report to PDF: {0}", outputFile.getName());
        reportService.exportSalesSummaryToPdf(outputFile);
    }
    
    // ========== CUSTOMER MANAGEMENT (Existing) ==========
    
    public boolean deleteCustomerAccount(String name) {
        logger.log(Level.INFO, "Attempting to delete customer account: {0}", name);
        return customerService.deleteCustomer(name);
    }
}
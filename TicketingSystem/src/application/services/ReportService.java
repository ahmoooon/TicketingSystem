package application.services;

import application.utilities.LoggerSetup;
import application.utilities.PdfReportGenerator;
import domain.Customer;
import domain.Food;
import domain.Payment;
import domain.Ticket;
import infrastructure.repositories.PaymentRepository;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for generating business reports.
 * Supports both text and PDF output formats.
 */
public class ReportService {
    private static final Logger logger = LoggerSetup.getLogger();
    private final PaymentRepository paymentRepository;
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    public ReportService(PaymentRepository paymentRepository) {
        logger.info("ReportService initialized.");
        this.paymentRepository = paymentRepository;
    }

    // ========== TEXT REPORT METHODS (Existing) ==========
    
    /**
     * Generates a detailed list of all registered customers.
     */
    public String getCustomerListReport(ArrayList<Customer> customer) {
        if (customer.isEmpty()) {
            return "\n <!> There are no registered customer records! <!>";
        }
        
        String todayDate = dateFormat.format(new Date());
        StringBuilder report = new StringBuilder();
        int count = 0;
        
        report.append("\n---< Customer List Report >---\n\n");
        report.append("Date    : ").append(todayDate).append("\n");
        report.append("< Customer List Registered > \n\n");
        report.append("=========================================================\n");
        report.append("No. Customer Name                        Customer Password\n");
        report.append("=========================================================\n");
        
        for (Customer c : customer) {
            report.append(String.format("%d   %s\t\t\t\t%s\n", ++count, c.getName(), c.getPassword()));
        }
        
        report.append("=========================================================\n");
        report.append(String.format("Total Number of Customer: %d\n", count));
        report.append("=========================================================");
        
        logger.info("Customer list report generated.");
        return report.toString();
    }

    /**
     * Generates a detailed report of all movie ticket purchases.
     */
    public String getMoviePurchaseReport() {
        ArrayList<Ticket> ticketArr = new ArrayList<>();
        ArrayList<Payment> payments = paymentRepository.getAllPayments();
        for (Payment p : payments) {
            ticketArr.addAll(p.getTicket());
        }
        
        if (ticketArr.isEmpty()) {
            return "\n <!> There are no movie purchase records! <!>";
        }

        String todayDate = dateFormat.format(new Date());
        StringBuilder report = new StringBuilder();
        int count = 0;
        double ticketSum = 0;

        report.append("\n---< Movie Purchase Report >---\n\n");
        report.append("Date    : ").append(todayDate).append("\n");
        report.append("< Movie Purchase Record >\n\n");
        report.append("=============================================================\n");
        report.append("No. Movie Name                         Unit             Total Price\n");
        report.append("=============================================================\n");

        for (Ticket t : ticketArr) {
            if (t.getMovieName() != null) {
                double totalPrice = t.ticketPrice() * t.getTicketAmt();
                report.append(String.format("%d    %s\t\t%d\t\t\t%.2f\n", ++count, t.getMovieName(), t.getTicketAmt(), totalPrice));
                ticketSum += totalPrice;
            }
        }

        report.append("=============================================================\n");
        report.append(String.format("Sum of Price: %.2f\n", ticketSum));
        report.append("=============================================================");

        logger.info("Movie purchase report generated.");
        return report.toString();
    }

    /**
     * Generates a detailed report of all F&B purchases.
     */
    public String getFoodPurchaseReport() {
        ArrayList<Food> foodArr = new ArrayList<>();
        ArrayList<Payment> payments = paymentRepository.getAllPayments();
        for (Payment p : payments) {
            foodArr.addAll(p.getFood());
        }
        
        if (foodArr.isEmpty()) {
            return "\n <!> There are no food purchase records! <!>";
        }

        String todayDate = dateFormat.format(new Date());
        StringBuilder report = new StringBuilder();
        int count = 0;
        double foodSum = 0;

        report.append("\n---< Food Purchase Report >---\n\n");
        report.append("Date    : ").append(todayDate).append("\n");
        report.append("< Food Purchase Record >\n\n");
        report.append("=============================================================\n");
        report.append("No. Food Name                          Unit             Total Price\n");
        report.append("=============================================================\n");

        for (Food f : foodArr) {
            if (f.getName() != null) {
                report.append(String.format("%d    %s\t\t%d\t\t%.2f\n", ++count, f.getName(), f.getQty(), f.getPrice()));
                foodSum += f.getPrice();
            }
        }

        report.append("=============================================================\n");
        report.append(String.format("Sum of Price: %.2f\n", foodSum));
        report.append("=============================================================");

        logger.info("Food purchase report generated.");
        return report.toString();
    }
    
    /**
     * Generates the consolidated Sales Summary Report.
     */
    public String generateSalesSummaryReport() {
        ArrayList<Payment> payments = paymentRepository.getAllPayments();
        if (payments == null || payments.isEmpty()) {
            return "\n*** No Payment Data Available to Generate Report ***\n";
        }

        double totalRevenue = 0.0;
        double totalTicketSales = 0.0;
        double totalFoodBeverageSales = 0.0;
        int totalTransactions = payments.size();
        
        for (Payment payment : payments) {
            double ticketPrice = payment.getTotalTicketPrice();
            double fbPrice = payment.getTotalFoodPrice();
            double grandTotal = payment.getTotalPrice();

            totalTicketSales += ticketPrice;
            totalFoodBeverageSales += fbPrice;
            totalRevenue += grandTotal;
        }

        String report = String.format("""
                                      
                                      ==============================================
                                             CINEMA SALES SUMMARY REPORT
                                      ==============================================
                                      Date: %s
                                      Total Number of Transactions: %d
                                      ----------------------------------------------
                                      Total Ticket Sales:   RM %.2f
                                      Total F&B Sales:      RM %.2f
                                      ----------------------------------------------
                                      GRAND TOTAL REVENUE:  RM %.2f
                                      ==============================================""",
            dateFormat.format(new Date()),
            totalTransactions,
            totalTicketSales,
            totalFoodBeverageSales,
            totalRevenue
        );

        logger.info("Sales summary report generated.");
        return report;
    }
    
    // ========== PDF EXPORT METHODS (New) ==========
    
    /**
     * Exports Customer List Report to PDF.
     * 
     * @param customer List of customers
     * @param outputFile Target PDF file
     * @throws IOException if PDF generation fails
     */
    public void exportCustomerListToPdf(ArrayList<Customer> customer, File outputFile) 
            throws IOException {
        String reportContent = getCustomerListReport(customer);
        PdfReportGenerator.generatePdfReport(reportContent, outputFile, "Customer List Report");
        logger.info("Customer list exported to PDF: " + outputFile.getName());
    }
    
    /**
     * Exports Movie Purchase Report to PDF.
     * 
     * @param outputFile Target PDF file
     * @throws IOException if PDF generation fails
     */
    public void exportMoviePurchaseToPdf(File outputFile) throws IOException {
        String reportContent = getMoviePurchaseReport();
        PdfReportGenerator.generatePdfReport(reportContent, outputFile, "Movie Purchase Report");
        logger.info("Movie purchase report exported to PDF: " + outputFile.getName());
    }
    
    /**
     * Exports Food Purchase Report to PDF.
     * 
     * @param outputFile Target PDF file
     * @throws IOException if PDF generation fails
     */
    public void exportFoodPurchaseToPdf(File outputFile) throws IOException {
        String reportContent = getFoodPurchaseReport();
        PdfReportGenerator.generatePdfReport(reportContent, outputFile, "Food Purchase Report");
        logger.info("Food purchase report exported to PDF: " + outputFile.getName());
    }
    
    /**
     * Exports Sales Summary Report to PDF.
     * 
     * @param outputFile Target PDF file
     * @throws IOException if PDF generation fails
     */
    public void exportSalesSummaryToPdf(File outputFile) throws IOException {
        String reportContent = generateSalesSummaryReport();
        PdfReportGenerator.generatePdfReport(reportContent, outputFile, "Sales Summary Report");
        logger.info("Sales summary report exported to PDF: " + outputFile.getName());
    }
}
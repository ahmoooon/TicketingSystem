/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package application.services;

/**
 *
 * @author zhili
 */
// ReportService.java - Business Logic Layer (Reporting Logic and Formatting)

import application.utilities.LoggerSetup;
import domain.Customer;
import domain.Food;
import domain.Payment;
import domain.Ticket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

// Assuming Customer, Payment, Ticket, and Food classes exist
// and that 'checkError' is handled by the Controller.

public class ReportService {
    private static final Logger logger = LoggerSetup.getLogger();
    
    // Date format for report headers
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    public ReportService() {
        logger.info("ReportService initialized.");
    }

    /**
     * Generates a detailed list of all registered customers (Original Case 1).
     * @param customer The list of all customers.
     * @return The formatted report string.
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
            // Using printf for alignment, matching your original format
            report.append(String.format("%d   %s\t\t\t\t%s\n", ++count, c.getName(), c.getPassword()));
        }
        
        report.append("=========================================================\n");
        report.append(String.format("Total Number of Customer: %d\n", count));
        report.append("=========================================================");
        
        logger.info("Customer list report generated.");
        return report.toString();
    }

    /**
     * Generates a detailed report of all movie ticket purchases (Original Case 2).
     * @param payment The list of all payments (used to extract tickets).
     * @return The formatted report string.
     */
    public String getMoviePurchaseReport(ArrayList<Payment> payment) {
        ArrayList<Ticket> ticketArr = new ArrayList<>();
        // Assuming Payment has getTicket() which returns ArrayList<Ticket>
        for (Payment p : payment) {
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

        // NOTE: Assuming Ticket class has getMovieName(), getTicketAmt(), and ticketPrice()
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
     * Generates a detailed report of all F&B purchases (Original Case 3).
     * @param payment The list of all payments (used to extract F&B items).
     * @return The formatted report string.
     */
    public String getFoodPurchaseReport(ArrayList<Payment> payment) {
        ArrayList<Food> foodArr = new ArrayList<>();
        // Assuming Payment has getFood() which returns ArrayList<Food>
        for (Payment p : payment) {
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

        // NOTE: Assuming Food class has getName(), getQty(), and getPrice() (which returns total price for that item)
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
    
//    /**
//     * Generates the new consolidated Sales Summary Report (New Feature).
//     * @param paymentArr The list of all payments.
//     * @return The formatted sales summary report string.
//     */
//    public String generateSalesSummaryReport(ArrayList<Payment> paymentArr) {
//        if (paymentArr == null || paymentArr.isEmpty()) {
//            return "\n*** No Payment Data Available to Generate Report ***\n";
//        }
//
//        double totalRevenue = 0.0;
//        double totalTicketSales = 0.0;
//        double totalFoodBeverageSales = 0.0;
//        int totalTransactions = paymentArr.size();
//        
//        // Assuming Payment class has methods like getTicketPrice() and getFoodBeveragePrice()
//        for (Payment payment : paymentArr) {
//            double ticketPrice = payment.getTicketPrice();
//            double fbPrice = payment.getFoodBeveragePrice();
//            double grandTotal = payment.getTotalAmount();
//
//            totalTicketSales += ticketPrice;
//            totalFoodBeverageSales += fbPrice;
//            totalRevenue += grandTotal;
//        }
//
//        String report = String.format("""
//                                      
//                                      ==============================================
//                                             CINEMA SALES SUMMARY REPORT
//                                      ==============================================
//                                      Date: %s
//                                      Total Number of Transactions: %d
//                                      ----------------------------------------------
//                                      Total Ticket Sales:   RM %.2f
//                                      Total F&B Sales:      RM %.2f
//                                      ----------------------------------------------
//                                      GRAND TOTAL REVENUE:  RM %.2f
//                                      ==============================================""",
//            dateFormat.format(new Date()),
//            totalTransactions,
//            totalTicketSales,
//            totalFoodBeverageSales,
//            totalRevenue
//        );
//
//        logger.info("Sales summary report generated.");
//        return report;
//    }
}

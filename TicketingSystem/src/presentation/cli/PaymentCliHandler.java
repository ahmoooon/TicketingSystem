/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package presentation.cli;

import application.dto.PaymentRequest;
import application.dto.PaymentResult;
import application.services.PaymentService;
import application.utilities.Utility;
import domain.Customer;
import domain.Ticket;
import domain.Food;
import domain.Payment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.Scanner;

/**
 *
 * @author MOON
 */
public class PaymentCliHandler {

    private final PaymentService paymentService;
    private final Scanner scanner;

    public PaymentCliHandler(PaymentService paymentService, Scanner scanner) {
        this.paymentService = paymentService;
        this.scanner = scanner;
    }

    public Payment handlePaymentModule(ArrayList<Ticket> ticketArr, ArrayList<Food> foodArr, Optional<Customer> currentUser, ArrayList<Payment> oldPayment) {
        // 1. Calculate Totals (Logic preserved from original)
        double sumOfPrices = calculateTotal(ticketArr, foodArr);

        // 2. Print Receipt
        int receiptId = getNextReceiptId(oldPayment);
        printReceipt(ticketArr, foodArr, receiptId, sumOfPrices);

        // 3. Payment Loop
        if (ticketArr.isEmpty() && foodArr.isEmpty()) {
            return null;
        }

        boolean back = false;
        Payment payment = null;

        do {
            System.out.println("\n\tWould you like to pay by > ");
            System.out.println("\n\t|| 1 | Bank Transfer      ||\n\t|| 2 | Cash               ||\n\t|| 3 | Back               ||\n");
            System.out.print("Choose one of the option from menu above ~ ");
            
            int payChoice = Utility.checkError(scanner, 1, 3);

            switch (payChoice) {
                case 1: // Bank Transfer
                    payment = handleBankTransfer(sumOfPrices, ticketArr, foodArr, currentUser);
                    if (payment != null) back = true;
                    break;

                case 2: // Cash
                    payment = handleCashPayment(sumOfPrices, ticketArr, foodArr, currentUser);
                    if (payment != null) back = true;
                    break;

                case 3: // Back
                    return null;
            }
        } while (!back);

        return payment;
    }

    // --- Extracted Helper Methods (SRP: Separation of Logic) ---

    private Payment handleBankTransfer(double amount, ArrayList<Ticket> tickets, ArrayList<Food> foodOrders, Optional<Customer> currentUser) {
        boolean valid = false;
        String bankNum = "";
        
        // UI Loop for validation
        while (!valid) {
            System.out.print("\nEnter bank account number: ");
            bankNum = scanner.next();

            // Create Request
            PaymentRequest request = new PaymentRequest(amount, "Bank Transfer", bankNum, tickets, foodOrders, currentUser);
            
            // Call Service
            PaymentResult result = paymentService.processPayment(request);

            if (result.isSuccess()) {
                System.out.println("\n\t<$> Payment successful! <$>\n-----< Thank you! Come Again! >-----");
                return new Payment(currentUser, tickets, foodOrders, amount, true);
            } else {
                System.out.println("\n <!> Invalid account number! Please try again! <!>");
                // Loop continues
            }
        }
        return null;
    }

    private Payment handleCashPayment(double amount, ArrayList<Ticket> tickets, ArrayList<Food> foodOrders, Optional<Customer> currentUser) {
        System.out.printf("\nIs the amount: RM%.2f correct?(Y: Yes/N: No) > ", amount);
        char correct = scanner.next().toUpperCase().charAt(0);

        if (correct == 'Y') {
            // Cash technically always succeeds if user says Yes, but we still route through service for consistency
            PaymentRequest request = new PaymentRequest(amount, "Cash", null, tickets, foodOrders, currentUser);
            paymentService.processPayment(request); // We can ignore result as we verified via UI
            
            System.out.println("\n\t<$> Payment successful! <$>\n-----< Thank you! Come Again! >-----");
            return new Payment(currentUser, tickets, foodOrders, amount, true);
        } else {
            System.out.println("\n <!> Sorry for the inconvenience! <!>");
            return null;
        }
    }

    private double calculateTotal(ArrayList<Ticket> tickets, ArrayList<Food> foods) {
        double sum = 0;
        for (Ticket t : tickets) sum += t.ticketPrice() * t.getTicketAmt();
        for (Food f : foods) sum += f.getPrice(); // Assuming getPrice() on Food returns total for that line item
        return sum;
    }

    private int getNextReceiptId(ArrayList<Payment> oldPayments) {
        int id = 0;
        for (Payment p : oldPayments) {
            if (p != null) id = p.getPaymentID();
        }
        return ++id;
    }

    private void printReceipt(ArrayList<Ticket> tickets, ArrayList<Food> foods, int receiptId, double total) {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String todayDate = dateFormat.format(currentDate);
        int productNo = 0;

        System.out.println("\n---< Receipt >--- \n");
        System.out.printf("=============================================================================================\n");
        System.out.printf("Receipt No      : %d\n", receiptId);
        System.out.printf("Date            : %s\n", todayDate);
        System.out.printf("---------------------------------------------------------------------------------------------\n");
        System.out.printf("Product No.   Product Name                   Qty                 Unit Price       Sum Price\n");

        for (Ticket t : tickets) {
            if (t.getMovieName() != null) {
                System.out.printf("%-12d%14s%20d%25.2f%17.2f\n", ++productNo, t.getMovieName(), t.getTicketAmt(), t.ticketPrice(), t.ticketPrice() * t.getTicketAmt());
            }
        }

        for (Food f : foods) {
            if (f.getName() != null) {
                // Assuming f.getPrice() is total price, and we calculate unit price
                double unitPrice = (f.getQty() > 0) ? f.getPrice() / f.getQty() : 0;
                System.out.printf("%-12d%17s%17d%25.2f%17.2f\n", ++productNo, f.getName(), f.getQty(), unitPrice, f.getPrice());
            }
        }
        System.out.printf("---------------------------------------------------------------------------------------------\n");
        System.out.printf("Total Price: %74.2f\n", total);
    }
}
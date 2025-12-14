// src/presentation/cli/PaymentCliHandler.java
package presentation.cli;

import application.dto.PaymentRequest;
import application.dto.PaymentResult;
import application.services.PaymentService;
import application.utilities.Utility;
import domain.Customer;
import domain.Ticket;
import domain.Food;
import domain.Payment;
import infrastructure.repositories.PaymentRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.Scanner;

public class PaymentCliHandler {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final Scanner scanner;

    public PaymentCliHandler(PaymentService paymentService, PaymentRepository paymentRepository, Scanner scanner) {
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
        this.scanner = scanner;
    }

    public Payment handlePaymentMenu(ArrayList<Ticket> ticketArr, ArrayList<Food> foodArr, 
                                     Optional<Customer> currentUser, ArrayList<Payment> paymentHistory) {
        boolean back = false;
        Payment completedPayment = null;
        
        do {
            System.out.println("\n----------< Payment Module >----------");
            System.out.println("\t|| 1 | Make Payment           ||");
            System.out.println("\t|| 2 | View Current Cart      ||");
            System.out.println("\t|| 3 | View Payment History   ||");
            System.out.println("\t|| 4 | Back to Main Menu      ||");
            System.out.print("\nChoose option ~ ");
            
            int choice = Utility.checkError(scanner, 1, 4);
            
            switch (choice) {
                case 1: 
                    completedPayment = handlePaymentModule(ticketArr, foodArr, currentUser, paymentHistory);
                    if (completedPayment != null && completedPayment.getPaymentMade()) {
                        paymentRepository.savePayment(completedPayment);
                        System.out.println("\n Payment history saved successfully!");
                        return completedPayment;
                    }
                    break;
                case 2: displayCurrentCart(ticketArr, foodArr); break;
                case 3: displayPaymentHistory(); break;
                case 4: back = true; break;
            }
        } while (!back);
        return null;
    }
    
    private void displayCurrentCart(ArrayList<Ticket> ticketArr, ArrayList<Food> foodArr) {
        System.out.println("\n---< Current Cart (Unpaid) >---");
        if (ticketArr.isEmpty() && foodArr.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        // ... (Keep existing display logic if desired) ...
        double total = calculateTotal(ticketArr, foodArr);
        System.out.printf("%nTOTAL: RM%.2f%n", total);
    }
    
    private void displayPaymentHistory() {
        ArrayList<Payment> history = paymentRepository.getAllPayments();
        System.out.println("\n---< Payment History >---");
        
        if (history.isEmpty()) {
            System.out.println("No payment records found.");
            return;
        }
        
        System.out.println("=======================================================================");
        System.out.printf("%-12s %-15s %-12s %-12s %-15s%n", 
            "Payment ID", "Customer", "Tickets", "Food Items", "Total Amount");
        System.out.println("=======================================================================");
        
        for (Payment p : history) {
            String customerName = p.getCustomer().map(Customer::getName).orElse("Guest");
            
            // CLEANER: Using the smart getters that work for both New and History payments
            System.out.printf("%-12d %-15s %-12d %-12d RM %.2f%n",
                p.getPaymentID(),
                customerName,
                p.getTicketAmt(), // Uses summaryTicketCount if list is empty
                p.getFoodQty(),   // Uses summaryFoodCount if list is empty
                p.getTotalPrice()); // Uses stored totalPricing
        }
        System.out.println("=======================================================================");
        
        System.out.print("\nEnter Payment ID to view details (0 to back): ");
        int id = Utility.checkError(scanner, 0, 9999);
        if (id != 0) displayPaymentDetails(history, id);
    }
    
    private void displayPaymentDetails(ArrayList<Payment> history, int paymentId) {
        Optional<Payment> paymentOpt = history.stream().filter(p -> p.getPaymentID() == paymentId).findFirst();
        
        if (paymentOpt.isEmpty()) {
            System.out.println("\n <!> Payment ID not found! <!>");
            return;
        }
        
        Payment p = paymentOpt.get();
        System.out.println("\n========== Payment Details ==========");
        System.out.println("Payment ID: " + p.getPaymentID());
        System.out.println("Customer: " + p.getCustomer().map(Customer::getName).orElse("Guest"));
        
        // HANDLING HISTORY VS NEW DATA
        if (p.getTicket().isEmpty() && p.getFood().isEmpty()) {
            System.out.println("\n[NOTICE] Detailed item list is not available for historical records.");
            System.out.println("Summary:");
            System.out.println("  Total Tickets: " + p.getTicketAmt());
            System.out.println("  Total Food:    " + p.getFoodQty());
        } else {
            System.out.println("\nTICKETS:");
            for (Ticket t : p.getTicket()) {
                System.out.printf("  %s (x%d) - RM%.2f%n", t.getMovieName(), t.getTicketAmt(), t.getTotalPrice());
            }
            System.out.println("\nFOOD:");
            for (Food f : p.getFood()) {
                System.out.printf("  %s (x%d) - RM%.2f%n", f.getName(), f.getQty(), f.getPrice());
            }
        }
        
        System.out.printf("%nGRAND TOTAL: RM%.2f%n", p.getTotalPrice());
        System.out.println("=====================================");
    }

    public Payment handlePaymentModule(ArrayList<Ticket> ticketArr, ArrayList<Food> foodArr, 
                                       Optional<Customer> currentUser, ArrayList<Payment> oldPayment) {
        double sumOfPrices = calculateTotal(ticketArr, foodArr);
        int receiptId = getNextReceiptId(oldPayment);
        printReceipt(ticketArr, foodArr, receiptId, sumOfPrices);

        if (ticketArr.isEmpty() && foodArr.isEmpty()) {
            System.out.println("\n <!> Cart is empty! Nothing to pay. <!>");
            return null;
        }

        boolean back = false;
        Payment payment = null;

        do {
            System.out.println("\n\tWould you like to pay by > ");
            System.out.println("\n\t|| 1 | Bank Transfer      ||");
            System.out.println("\n\t|| 2 | Cash               ||");
            System.out.println("\n\t|| 3 | Cancel Payment     ||");
            System.out.print("\nChoose one of the option from menu above ~ ");
            
            int payChoice = Utility.checkError(scanner, 1, 3);

            switch (payChoice) {
                case 1: payment = handleBankTransfer(sumOfPrices, ticketArr, foodArr, currentUser); if(payment!=null) back=true; break;
                case 2: payment = handleCashPayment(sumOfPrices, ticketArr, foodArr, currentUser); if(payment!=null) back=true; break;
                case 3: return null;
            }
        } while (!back);

        return payment;
    }

    // ... (Keep handleBankTransfer, handleCashPayment, calculateTotal, printReceipt methods exactly as before) ...
    
    private Payment handleBankTransfer(double amount, ArrayList<Ticket> tickets, ArrayList<Food> foodOrders, Optional<Customer> currentUser) {
        boolean valid = false;
        String bankNum = "";
        while (!valid) {
            System.out.print("\nEnter bank account number (9 digits): ");
            bankNum = scanner.next();
            scanner.nextLine(); 
            PaymentRequest request = new PaymentRequest(amount, "Bank Transfer", bankNum, tickets, foodOrders, currentUser);
            PaymentResult result = paymentService.processPayment(request);
            if (result.isSuccess()) {
                System.out.println("\n\t<$> Payment successful! <$>\n-----< Thank you! Come Again! >-----");
                return new Payment(currentUser, tickets, foodOrders, amount, true);
            } else {
                System.out.println("\n <!> " + result.getMessage() + " <!>");
                System.out.print("Try again? (Y/N): ");
                if (scanner.next().toUpperCase().charAt(0) != 'Y') return null;
            }
        }
        return null;
    }

    private Payment handleCashPayment(double amount, ArrayList<Ticket> tickets, ArrayList<Food> foodOrders, Optional<Customer> currentUser) {
        System.out.printf("\nIs the amount: RM%.2f correct? (Y: Yes/N: No) > ", amount);
        char correct = scanner.next().toUpperCase().charAt(0);
        scanner.nextLine();
        if (correct == 'Y') {
            paymentService.processPayment(new PaymentRequest(amount, "Cash", null, tickets, foodOrders, currentUser));
            System.out.println("\n\t<$> Payment successful! <$>\n-----< Thank you! Come Again! >-----");
            return new Payment(currentUser, tickets, foodOrders, amount, true);
        }
        System.out.println("\n <!> Payment cancelled. <!>");
        return null;
    }

    private double calculateTotal(ArrayList<Ticket> tickets, ArrayList<Food> foods) {
        double sum = 0;
        for (Ticket t : tickets) sum += t.getTotalPrice();
        for (Food f : foods) sum += f.getPrice();
        return sum;
    }

    private int getNextReceiptId(ArrayList<Payment> oldPayments) {
        return paymentRepository.getAllPayments().size() + 1;
    }
    
    private void printReceipt(ArrayList<Ticket> tickets, ArrayList<Food> foods, int receiptId, double total) {
        // ... (Keep existing receipt printing logic) ...
        // Ensure you copy the printing logic from your previous file here
        System.out.println("\n---< Receipt >--- \nTotal: RM" + String.format("%.2f", total));
    }
}
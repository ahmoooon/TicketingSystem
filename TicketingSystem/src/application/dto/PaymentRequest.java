// src/application/dto/PaymentRequest.java
package application.dto;

import domain.Ticket;
import domain.Food; 
import domain.Customer;
import java.util.List;
import java.util.Optional;

public class PaymentRequest {
    
    // Data needed for transaction
    private final double totalAmount;
    private final String paymentMethod; // e.g., "Bank Transfer", "Cash"
    private final String bankAccountNumber; // Nullable if payment method is cash
    
    // Entities involved in the transaction (needed for saving the record)
    private final List<Ticket> tickets;
    private final List<Food> foodOrders;
    private final Optional<Customer> customer; // Assuming the logged-in user is one of these

    public PaymentRequest(double totalAmount, String paymentMethod, String bankAccountNumber, List<Ticket> tickets, List<Food> foodOrders, Optional<Customer> customer) {
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.bankAccountNumber = bankAccountNumber;
        this.tickets = tickets;
        this.foodOrders = foodOrders;
        this.customer = customer;
    }

    // Getters only (DTOs should be immutable)
    public double getTotalAmount() { return totalAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getBankAccountNumber() { return bankAccountNumber; }
    public List<Ticket> getTickets() { return tickets; }
    public List<Food> getFoodOrders() { return foodOrders; }
    public Optional<Customer> getCustomer() { return customer; }
}
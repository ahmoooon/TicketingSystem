/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package application.services;

import application.dto.PaymentRequest;
import application.dto.PaymentResult;
import application.strategies.PaymentStrategy;
import infrastructure.strategies.BankTransferStrategy;
import infrastructure.strategies.CashPaymentStrategy;

/**
 *
 * @author MOON
 */
public class PaymentService {
    public PaymentResult processPayment(PaymentRequest request){
        PaymentStrategy strategy;
        // 1. Factory Logic: Choose the Strategy
        // (This switch replaces the switch case in the old main method)
        if (request.getPaymentMethod().equalsIgnoreCase("Bank Transfer")) {
            strategy = new BankTransferStrategy(request.getBankAccountNumber());
        } else if (request.getPaymentMethod().equalsIgnoreCase("Cash")) {
            strategy = new CashPaymentStrategy();
        } else {
            return new PaymentResult(false, "Invalid Payment Method");
        }

        // 2. Validate using the Strategy
        if (!strategy.validate()) {
            return new PaymentResult(false, "Invalid details provided (e.g., Account Number).");
        }

        // 3. Execute Payment
        strategy.pay(request.getTotalAmount());

        return new PaymentResult(true, "Payment successful!");
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package infrastructure.strategies;
import application.strategies.PaymentStrategy;

/**
 *
 * @author MOON
 */
public class BankTransferStrategy implements PaymentStrategy {
    private final String accountNumber;

    public BankTransferStrategy(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Override
    public boolean validate() {
        // Logic extracted from original code: length 9 and numeric
        return accountNumber != null && accountNumber.length() == 9 && accountNumber.matches("\\d+");
    }

    @Override
    public void pay(double amount) {
        // Business logic for bank transfer (logging, API calls, etc.)
        // For CLI simulation, the output is handled by the result message, 
        // but we can simulate the "processing" here if needed.
    }
}
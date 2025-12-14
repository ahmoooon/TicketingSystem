/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package application.strategies;

/**
 *
 * @author MOON
 */
public interface PaymentStrategy {
    /**
     * Validates if the payment details (e.g., account number) are correct.
     * @return true if valid, false otherwise.
     */
    boolean validate();

    /**
     * Executes the payment logic.
     * @param amount The total amount to pay.
     */
    void pay(double amount);
}
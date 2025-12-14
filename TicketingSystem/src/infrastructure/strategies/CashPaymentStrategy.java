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
public class CashPaymentStrategy implements PaymentStrategy{
    
    @Override
    public boolean validate(){
        return true;
    }
    
    @Override
    public void pay(double amount) {
        // Cash is always successful if the staff verifies it
    }
}

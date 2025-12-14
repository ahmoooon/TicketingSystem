/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package domain;

/**
 *
 * @author MOON
 */
// Food.java - ADAPTED Domain Model (Abstract Base Class)
import java.util.Scanner;
import java.util.ArrayList;

public abstract class Food {
    protected int foodNum;
    protected String name;
    protected double price; // Note: This field is used for both Unit Price (in inventory) and Total Price (in order) in your logic.
    protected int qty;
    protected static int lastNum = 1;
    
    // Constructor 1: Default (Used when loading menu items)
    Food(String name, double unitPrice) {
        foodNum = lastNum;
        lastNum++;
        this.name = name;
        // In the inventory, price stores the unit price
        this.price = unitPrice; 
        this.qty = 1; 
    }

    // Constructor 2: For merging existing orders (kept minimal)
    Food() { 
        this.qty = 0;
    }
    
    // Constructor 3: Used when creating a new item from menu choice
    Food(int num) {
        foodNum = num;
    }
    
    // --- Getters and Setters (Retaining your exact methods) ---
    public int getFoodNum() { return foodNum; }
    public void setFoodNum(int foodNum) { this.foodNum = foodNum; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }

    public static int getLastNum() { return lastNum; }
    public static void setLastNum(int lastNum) { Food.lastNum = lastNum; }
    
    // --- Logic Methods ---
    
    // Calculates the total price and stores it back in the 'price' field
    public void calPrice() {
        // NOTE: This logic assumes 'price' currently holds the unit price before this call.
        // In the new architecture, we will manage unit price separate from total price.
        // For compatibility with your final object state, we calculate the total here.
        this.price = this.qty * this.price; 
    }

    // Matches your original logic
    public void incrementQty(int amount) {
        this.qty += amount;
        // We will update the total price in the controller/service layer to avoid ambiguity
    }
    
    // Matches your original logic
    public void incrementPrice(double amount) {
        this.price += amount;
    }
    
    // Abstract method to differentiate types for the factory/service
    public abstract String getFoodType(); 
    
    // --- Display Methods (Retaining your exact formatting) ---
    public String toString() {
        // Used to display the menu items (number, name, unit price)
        return String.format("%3d %-35s RM %6.2f", foodNum, name, price);
    }
    
    public String printOrder() {
        // Used to display items in the order list (name, qty, total price)
        return String.format("%-35s %3d RM %6.2f", name, qty, price);
    }
}

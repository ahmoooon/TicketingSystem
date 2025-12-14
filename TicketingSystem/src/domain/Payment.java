// src/domain/Payment.java
package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Optional;

public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<Ticket> ticket;
    private ArrayList<Food> food;
    private double totalPricing;
    private int paymentID = 0;
    private static int lastID = 0;
    private boolean paymentMade;
    private Optional<Customer> customer;
    
    public Payment(){
        this.ticket = new ArrayList<>();
        this.food = new ArrayList<>();
        this.customer = Optional.empty();
    }
    
    public Payment(Optional<Customer> customer, ArrayList<Ticket> ticket, ArrayList<Food> food, double totalPricing, boolean paymentMade){
        // Create copies of the lists to prevent data loss when cart is cleared
        this.ticket = (ticket != null) ? new ArrayList<>(ticket) : new ArrayList<>();
        this.food = (food != null) ? new ArrayList<>(food) : new ArrayList<>();
        this.customer = customer;
        this.totalPricing = totalPricing;
        this.paymentMade = paymentMade;
        this.paymentID = ++lastID;
    }
    
    // --- STATIC SETTERS ---
    public static void setLastID(int id){
        lastID = id;
    }
    
    // --- SETTERS FOR HISTORY LOADING ---
    public void setPaymentID(int paymentId) {
        this.paymentID = paymentId;
        // Ensure static counter doesn't lag behind if we load a high ID
        if (paymentId > lastID) {
            lastID = paymentId;
        }
    }

    // --- GETTERS ---
    
    public int getPaymentID(){
        return paymentID;
    }
    
    public boolean getPaymentMade(){
        return paymentMade;
    }
    
    public Optional<Customer> getCustomer(){
        return customer;
    }
    
    public ArrayList<Ticket> getTicket(){
        return ticket;   
    }
    
    public ArrayList<Food> getFood(){
        return food;
    }
    
    /**
     * CLEAN CODE FIX: Returns the stored total directly. 
     * No need to recalculate, which prevents errors if lists are empty.
     */
    public double getTotalPrice(){
        return totalPricing;
    }
    
    // --- SMART GETTERS (No separate summary variables needed) ---
    
    public int getTicketAmt() {
        if (ticket == null || ticket.isEmpty()) return 0;
        
        int total = 0;
        for(Ticket t : ticket) {
            if(t != null) total += t.getTicketAmt();
        }
        return total;
    }
    
    public int getTotalTicketPrice() {
        if (ticket == null || ticket.isEmpty()) return 0;
        
        int total = 0;
        for(Ticket t : ticket) {
            if(t != null) total += t.getTotalPrice();
        }
        return total;
    }
    
    
    public int getFoodQty() {
        if (food == null || food.isEmpty()) return 0;
        
        int total = 0;
        for(Food f : food) {
            if(f != null) total += f.getQty();
        }
        return total;
    }
    
    public double getTotalFoodPrice() {
        if (food == null || food.isEmpty()) return 0;
        
        double total = 0;
        for(Food f : food) {
            if(f != null) total += f.getPrice();
        }
        return total;
    }
    
    public String getMovieName(){
        for(Ticket t: ticket){
            if(t != null && t.getMovieName() != null){
              return t.getMovieName();  
            }
        }
        return "N/A";
    }
    
    @Override
    public String toString(){
        return "Payment ID: " + paymentID + " | Total: " + totalPricing;
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ticketingsystem;

import java.util.ArrayList;

/**
 *
 * @author User
 */
public class Payment {
    private ArrayList<Ticket> ticket;
    private ArrayList<Food> food;
    private double totalPricing;
    private int paymentID = 0;
    private static int lastID = 0;
    private boolean paymentMade;
    private ArrayList<Customer> customer;
    
    public Payment(){
        
    }
    
    public Payment(ArrayList<Customer> customer,ArrayList<Ticket> ticket, ArrayList<Food> food, double totalPricing, boolean paymentMade){
        this.ticket = ticket;
        this.paymentMade = paymentMade;
        this.customer = customer;
        this.food = food;
        this.totalPricing = totalPricing;
        this.paymentMade = paymentMade;
        this.paymentID = lastID;
        lastID++;
    }
    
    public int getPaymentID(){
        return paymentID;
    }
    
    public void setPaymentMade(boolean paymentMade){
        this.paymentMade = paymentMade;
    }
    
    public boolean getPaymentMade(){
        return paymentMade;
    }
    
    public boolean getPaid(){
        return paymentMade;
    }
    
    public ArrayList<Customer> getCustomer(){
        return customer;
    }
    
    public String getName(){
        for(Customer c: customer){
            return c.getname();
        }
        return null;
    }
    
    public ArrayList<Ticket> getTicket(){
     return ticket;   
    }
    
    public String getMovieName(){
        String movieName = null;
        for(Ticket t: ticket){
            if(t.getMovieName() != null){
              movieName = t.getMovieName();  
            }
            
        }
        return movieName;
    }
    
    public int getTicketAmt(){
        for(Ticket t: ticket){
            if(t != null){
                return t.getTicketAmt();
            }
        }
        return 0;
    }
    
    public double ticketPrice(){
        for(Ticket t: ticket){
            if(t != null){
                return t.ticketPrice();
            }
        }
        return 0;
    }
    
    public int getTicketID(){
        for(Ticket t: ticket){
          return t.getTicketID();
        }
        return 0;
    }
    
    public ArrayList<Food> getFood(){
        return food;
    }
    
    public String toString(){
        return "Ticket List:\n" + ticket + "Food List:\n" + food;
    }
}

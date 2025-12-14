/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package domain;

/**
 *
 * @author MOON
 */
public class Popcorn extends Food {
    public Popcorn(String name, double unitPrice) {
        super(name, unitPrice);
    }
    // Added for compatibility with the Food(int num) constructor
    public Popcorn() { super(); }
    public Popcorn(int num) { super(num); } 
    
    @Override
    public String getFoodType() {
        return "Popcorn";
    }
}

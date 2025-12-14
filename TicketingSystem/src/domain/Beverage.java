/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package domain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author MOON
 */
public class Beverage extends Food {
    public Beverage(String name, double unitPrice) {
        super(name, unitPrice);
    }
    // Added for compatibility with the Food() and Food(int num) constructors
    public Beverage() { super(); } 
    public Beverage(int num) { super(num); }
    
    @Override
    public String getFoodType() {
        return "Beverage";
    }
}

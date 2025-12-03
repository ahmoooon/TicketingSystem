/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ticketingsystem;

/**
 *
 * @author MOON
 */
import java.util.ArrayList;
public abstract class Food {
    protected int foodNum;
    protected String name;
    protected double price;
    protected int qty;
    protected static int lastNum= 1;
    
    Food(){
        foodNum = lastNum;
        lastNum++;
    }
    
    Food(int num){
        foodNum = num;
    }
    
    Food(String name,double price){
        foodNum = lastNum;
        lastNum++;
        this.name = name;
        this.price = price;
    }

    public int getFoodNum() {
        return foodNum;
    }

    public void setFoodNum(int foodNum) {
        this.foodNum = foodNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public static int getLastNum() {
        return lastNum;
    }

    public static void setLastNum(int lastNum) {
        Food.lastNum = lastNum;
    }
    
    public void calPrice(){
        price = qty * price;
    }

    public abstract ArrayList getMenu();
    public abstract Food getOrder();
    
    public String toString(){
        return String.format("%3d %-35s RM %6.2f",foodNum,name,price);
    }
    
    public String printOrder(){
        return String.format("%-35s %3d RM %6.2f",name,qty,price);
    }
    
    public void incrementQty(int amount){
        this.qty += amount;
    }
    public void incrementPrice(double amount){
        this.price += amount;
    }
}

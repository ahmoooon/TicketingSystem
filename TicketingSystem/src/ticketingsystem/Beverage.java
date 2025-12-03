/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ticketingsystem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author MOON
 */
public class Beverage extends Food{
    public Beverage(){
        super();
    }    
    public Beverage(int num){
        super(num);
    }
        
    public Beverage(String name,double price){
        super(name,price);
    }
    
    @Override
    public String toString(){
        return super.toString();
    }
    
    @Override
    public ArrayList getMenu(){
        setLastNum(1);
        String filePath = "src/assignment/beverage.txt";
        ArrayList<Beverage> beverageList = new ArrayList<>();
        
        try(BufferedReader read = new BufferedReader(new FileReader(filePath))){
            String line;
            while((line = read.readLine()) != null){
                String[] parts = line.split(",");
                if(parts.length == 2){
                    String tname = parts[0];
                    double tprice = Double.parseDouble(parts[1]);
                    
                    Beverage b = new Beverage(tname,tprice);
                    beverageList.add(b);
                }
            }
        } catch(IOException e){
        }
        return beverageList;
    }
    
    @Override
    public Beverage getOrder(){
        Scanner sc = new Scanner(System.in);
        Beverage beverage = new Beverage();
        int num;
            ArrayList<Beverage> beverageList = beverage.getMenu();
            for(Beverage B : beverageList){
                System.out.println(B.toString());
            }
            do{
                System.out.print("Enter Choice (0: Back): ");
                num = sc.nextInt();
                if(num < 0 && num >=beverageList.size()){
                    System.out.println("No. " + num + "Not Found");
                }
            }while(num < 0 && num >= beverageList.size());
            
            Beverage b = new Beverage(num);
            for(Beverage B : beverageList){
                if(num == B.getFoodNum())
                {
                    b.setName(B.getName());
                    b.setPrice(B.getPrice());
                    System.out.print("\nEnter Quantity: ");
                    int tqty = sc.nextInt();
                    b.setQty(tqty);
                    b.calPrice();
                }
            }

        return b;
    }
    
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ticketingsystem;

import static ticketingsystem.Food.setLastNum;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class HotFood extends Food{
    public HotFood(){
        super();
    }    
    public HotFood(int num){
        super(num);
    }
        
    public HotFood(String name,double price){
        super(name,price);
    }

    @Override
    public String toString(){
        return super.toString();
    }
    
    @Override
    public ArrayList getMenu(){
        setLastNum(1);
        String filePath = "src/assignment/hotfood.txt";
        ArrayList<HotFood> hotFoodList = new ArrayList<>();
        
        try(BufferedReader read = new BufferedReader(new FileReader(filePath))){
            String line;
            while((line = read.readLine()) != null){
                String[] parts = line.split(",");
                if(parts.length == 2){
                    String tname = parts[0];
                    double tprice = Double.parseDouble(parts[1]);
                    
                    HotFood hotFood = new HotFood(tname,tprice);
                    hotFoodList.add(hotFood);
                }
            }
        } catch(IOException e){
        }
        return hotFoodList;
    }
    
    public HotFood getOrder(){
        Scanner sc = new Scanner(System.in);
        HotFood hotFood = new HotFood();
        int num;
            ArrayList<HotFood> hotFoodList = hotFood.getMenu();
            for(HotFood H : hotFoodList){
                System.out.println(H.toString());
            }
            do{
                System.out.print("Enter Choice (0: Back): ");
                num = sc.nextInt();
                if(num < 0 && num >=hotFoodList.size()){
                    System.out.println("No. " + num + "Not Found");
                }
            }while(num < 0 && num >=hotFoodList.size());
            
            HotFood h = new HotFood(num);
            for(HotFood H : hotFoodList){
                if(num == H.getFoodNum())
                {
                    h.setName(H.getName());
                    h.setPrice(H.getPrice());
                    System.out.print("\nEnter Quantity: ");
                    int qty = sc.nextInt();
                    h.setQty(qty);
                    h.calPrice();
                }
            }

        return h;
    }
}

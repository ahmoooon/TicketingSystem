/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ticketingsystem;

/**
 *
 * @author MOON
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class Popcorn extends Food{
    
    public Popcorn(){
        super();
    }    
    public Popcorn(int num){
        super(num);
    }
        
    public Popcorn(String name,double price){
        super(name,price);
    }

    @Override
    public String toString(){
        return super.toString();
    }
    
    @Override
    public ArrayList getMenu(){
        setLastNum(1);
        String filePath = "src/assignment/popcorn.txt";
        ArrayList<Popcorn> popcornList = new ArrayList<>();
        
        try(BufferedReader read = new BufferedReader(new FileReader(filePath))){
            String line;
            while((line = read.readLine()) != null){
                String[] parts = line.split(",");
                if(parts.length == 2){
                    String tname = parts[0];
                    double tprice = Double.parseDouble(parts[1]);
                    
                    Popcorn popcorn = new Popcorn(tname,tprice);
                    popcornList.add(popcorn);
                }
            }
        } catch(IOException e){
        }
        return popcornList;
    }
    
    @Override
    public Popcorn getOrder(){
        Scanner sc = new Scanner(System.in);
        Popcorn popcorn = new Popcorn();
        int num;
            ArrayList<Popcorn> popcornList = popcorn.getMenu();
            for(Popcorn P : popcornList){
                System.out.println(P.toString());
            }
            do{
                System.out.print("Enter Choice (0: Back): ");
                num = sc.nextInt();
                if(num < 0 && num >=popcornList.size()){
                    System.out.println("No. " + num + "Not Found");
                }
            }while(num < 0 && num >=popcornList.size());
            
            Popcorn p = new Popcorn(num);
            for(Popcorn P : popcornList){
                if(num == P.getFoodNum())
                {
                    p.setName(P.getName());
                    p.setPrice(P.getPrice());
                    System.out.print("\nEnter Quantity: ");
                    int tqty = sc.nextInt();
                    p.setQty(tqty);
                    p.calPrice();
                }
            }

        return p;
    }
}

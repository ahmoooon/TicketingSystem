/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ticketingsystem;

/**
 *
 * @author User
 */
public class Seat {
    private char seatRow;
    private int seatCol;
    private String seatType;
    private String seatStatus;
    private CinemaHall hall;
    
    public Seat(){}
    
    public Seat(int seatCol, char seatRow, String seatType, String seatStatus, CinemaHall hall){
        this.seatCol = seatCol;
        this.seatRow = seatRow;
        this.seatType = seatType;
        this.seatStatus = seatStatus;
        this.hall = hall;
    }
    
    //GETTERS
    //-----------------------------------------------------------------------------------------------
    public char getSeatRow(){
        return seatRow;
    }
    public int getSeatCol(){
        return seatCol;
    }
    public String getSeatType(){
        return seatType;
    }
    public String getSeatStatus(){
        return seatStatus;
    }
    
    public CinemaHall getCinemaHall(){
        return hall;
    }
    //-----------------------------------------------------------------------------------------------

    //SETTERS
    //-----------------------------------------------------------------------------------------------
    public void setSeatRow(char seatRow){
        this.seatRow = seatRow;
    }
    
    public void setSeatCol(int seatCol){
        this.seatCol = seatCol;
    }
    
    public void setSeatType(String seatType){
        this.seatType = seatType;
    }
    
    public void setSeatStatus(String seatStatus){
        this.seatStatus = seatStatus;
    }
    
    public void setCinemaHall(CinemaHall hall){
        this.hall = hall;
    }
    //-----------------------------------------------------------------------------------------------    
    
    public double calculatePrice() {
        double seatPrice;
        if (hall != null) {
            if (hall.getHallType().equals("IMAX")) {
                return seatPrice = 30.00;
            } else if (hall.getHallType().equals("Standard")) {
                return seatPrice = 15.00;
            } else if (hall.getHallType().equals("Lounge")) {
                return seatPrice = 80.00;
            } else {
                return 0.0;
            }
        }
        return 0;
    }
    
    public String toString(){
        return "\nSeat Row & Column   : " + seatRow + seatCol +
                "\nSeat Type          : " + seatType +
                "\nSeat Status        : " + seatStatus +
                "\nSeat Price         : " + calculatePrice();
     }

}

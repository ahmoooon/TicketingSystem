/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ticketingsystem;

public class CinemaHall {
    private int hallNum;
    private String hallType;
    private int rowAmt;
    private int colAmt;
    private Seat[] seat;
    
    public CinemaHall(){
    }
    
    public CinemaHall(int hallNum, String hallType, int rowAmt, int colAmt){
        this.hallNum = hallNum;
        this.hallType = hallType;
        this.rowAmt = rowAmt;
        this.colAmt = colAmt;
    }
    
    //GETTERS
    //-----------------------------------------------------------------------------------------------
    public int getHallNum(){
        return hallNum;
    }
    
    public String getHallType(){
        return hallType;
    }
    
    public int getRowAmt(){
        return rowAmt;
    }
    
    public int getColAmt(){
        return colAmt;
    }
    //-----------------------------------------------------------------------------------------------
    
    //SETTERS
    //-----------------------------------------------------------------------------------------------
    public void setHallNum(int hallNum){
        this.hallNum = hallNum;
    }
    
    public void setHallType(String hallType){
        this.hallType = hallType;
    }
    
    public void setRowAmt(int rowAmt){
        this.rowAmt = rowAmt;
    }
    
    public void setColAmt(int colAmt){
        this.colAmt = colAmt;
    }
    
    public String toString(){
        return  "\nHall ID       : " + hallNum +
                "\nHall Type     : " + hallType +
                "\nHall Column   : " + colAmt +
                "\nHall Row      : " + rowAmt;
    }
}

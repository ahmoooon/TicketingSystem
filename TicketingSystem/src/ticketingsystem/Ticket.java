/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ticketingsystem;

import java.time.LocalDate;
import java.util.ArrayList;

public class Ticket {

    private static int lastTicketID = 1;
    private int ticketID = 0;
    private int ticketAmt;
    private Showtime showtime;
    private CinemaHall cinema;
    private ArrayList<Seat> seat;

    public Ticket() {
    }

    public Ticket(Showtime showtime, int ticketAmt, CinemaHall cinema, ArrayList<Seat> seat) {
        this.showtime = showtime;
        this.ticketAmt = ticketAmt;
        this.cinema = cinema;
        this.seat = seat;
        this.ticketID = lastTicketID;
        lastTicketID++;
    }

    public int getTicketID() {
        return ticketID;
    }

    public Showtime getShowtime() {
        return showtime;
    }
    

    public LocalDate getSchedule() {
        return showtime.getDate();
    }

    public String time() {
        return showtime.getShowtime();
    }

    public int getTicketAmt() {
        return ticketAmt;
    }

    public CinemaHall getCinemaHall() {
        return cinema;
    }
    
    public String getHallType(){
        return cinema.getHallType();
    }

    public int getHallId() {
        return cinema.getHallNum();
    }

    public ArrayList<Seat> getSeat() {
        return seat;
    }

    public double ticketPrice() {
        for(Seat s: seat){
            return s.calculatePrice();
        }
        return 0;
    }

    public String getMovieName() {
        return showtime.getMovieName();
    }

    public String toString() {
        return "\nTicket ID       : " + ticketID
                + "\nTicket amount   : " + ticketAmt
                + showtime
                + "\n\nHall          : " + cinema;
    }

    public void displaySeatArray() {
        for(Seat s: seat){
            System.out.println(s.toString());
        }
    }
}

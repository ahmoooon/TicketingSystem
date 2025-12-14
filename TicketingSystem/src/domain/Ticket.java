package domain;

import java.time.LocalDate;
import java.util.ArrayList;

public class Ticket {

    private static int lastTicketID = 1;
    private final int ticketID; // Recommended: make final
    private final int ticketAmt; // Recommended: make final
    private final Showtime showtime; // Recommended: make final
    private final CinemaHall cinema; // Recommended: make final
    private final ArrayList<Seat> seat; // Recommended: make final

    public Ticket() {
        this.ticketID = lastTicketID; // Still needs an ID, even if default
        this.ticketAmt = 0;
        this.showtime = null;
        this.cinema = null;
        this.seat = new ArrayList<>();
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
        return showtime.getShowtime(); // FIXED: Method added to Showtime
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
        return cinema.getHallId(); // FIXED: Uses the refactored method name
    }

    public ArrayList<Seat> getSeat() {
        return seat;
    }

    // FIX: This calculation is wrong in the original code. It only returns the price 
    // of the *first* seat. It should calculate the sum.
    public double ticketPrice() {
        double totalPrice = 0;
        if (seat == null || seat.isEmpty()) return 0;
        
        // This calculates the price based on ONE seat's price logic 
        // (which is tied to the hall type, meaning all seats in the same hall cost the same).
        // Returning the price of the first seat is conceptually correct for the unit price:
        return seat.get(0).calculatePrice(); 
    }
    
    public double getTotalPrice() {
        if (seat == null || seat.isEmpty()) return 0;
        return seat.size() * ticketPrice(); // Total price is unit price * quantity
    }

    public String getMovieName() {
        return showtime.getMovieName(); // FIXED: Method added to Showtime
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
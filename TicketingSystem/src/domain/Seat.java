package domain;

import domain.valueobjects.SeatId; // New import
// Assume CinemaHall is also in the 'domain' package

/**
 * Represents a single seat in a cinema hall.
 * It is an Entity identified by its SeatId.
 */
public class Seat {
    
    // Changed primitives to Value Object
    private final SeatId id; 
    private String seatType;
    private String seatStatus;
    private CinemaHall hall;
    
    // Note: Removed default constructor (less necessary for entities)
    
    public Seat(SeatId id, String seatType, String seatStatus, CinemaHall hall){
        this.id = id;
        this.seatType = seatType;
        this.seatStatus = seatStatus;
        this.hall = hall;
    }
    
    // You can now access the row/col via the ID object
    public char getSeatRow(){
        return id.getRow();
    }
    public int getSeatCol(){
        return id.getColumn();
    }
    
    // The main identifier getter
    public SeatId getId() {
        return id;
    }
    
    // Existing Getters for other fields (no changes)
    public String getSeatType(){
        return seatType;
    }
    public String getSeatStatus(){
        return seatStatus;
    }
    public CinemaHall getCinemaHall(){
        return hall;
    }
    
    // Setters: Only include business-valid state changes. 
    // We remove setters for row/col as they are now immutable parts of the SeatId.
    // The Seat ID should not change after creation.
    
    public void setSeatStatus(String seatStatus){
        this.seatStatus = seatStatus;
    }
    
    // Note: Removed setSeatRow/Col/Type as they should generally be immutable or 
    // changed via a business method (e.g., upgradeSeat()).

    // Business Logic (Price calculation is fine here as it's directly tied to the seat's state/hall)
    public double calculatePrice() {
        if (hall != null) {
            return hall.getBasePrice(); 
        }
        return 0.0;
    }
    
    @Override
    public String toString(){
        return "\nSeat Row & Column    : " + id.toDisplayString() +
                 "\nSeat Type          : " + seatType +
                 "\nSeat Status        : " + seatStatus +
                 "\nSeat Price         : " + calculatePrice();
    }
}
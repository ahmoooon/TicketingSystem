package domain.repositories;

import domain.Seat;
import domain.Showtime; // Assuming Showtime is in 'domain'
import domain.valueobjects.SeatId;
import java.util.List;
import java.util.Optional;

public interface SeatRepository {
    
    /**
     * Finds a seat object for a specific hall/showtime combination.
     * @param showtime The showtime being booked.
     * @param seatId The specific seat identifier.
     * @return The Seat object.
     */
    Optional<Seat> findSeat(Showtime showtime, SeatId seatId);
    
    /**
     * Reserves a list of seats for a specific showtime. 
     * This is the core transactional logic.
     * @param showtime The showtime.
     * @param seatIds The list of SeatIds to reserve.
     * @return The list of successfully reserved Seat objects.
     * @throws SeatUnavailableException if any seat is already booked.
     */
    List<Seat> reserveSeats(Showtime showtime, List<SeatId> seatIds);
    
    /**
     * Retrieves all seats for a given showtime to display availability.
     * @param showtime The showtime.
     * @return A list of all Seat objects (Booked/Available).
     */
    List<Seat> findSeatsByShowtime(Showtime showtime);
}

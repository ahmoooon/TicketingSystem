package application.dto;

import domain.valueobjects.SeatId;
import java.time.LocalDate;
import java.util.List;

public class BookingRequest {
    private final int movieId;
    private final LocalDate date;
    private final String showtimeTime;
    private final int hallId; 
    private final List<SeatId> seatIds;
    
    public BookingRequest(int movieId, LocalDate date, String showtimeTime, int hallId, List<SeatId> seatIds) {
        this.movieId = movieId;
        this.date = date;
        this.showtimeTime = showtimeTime;
        this.hallId = hallId;
        this.seatIds = seatIds;
    }

    // Getters
    public int getMovieId() { return movieId; }
    public LocalDate getDate() { return date; }
    public String getShowtimeTime() { return showtimeTime; }
    public int getHallId() { return hallId; }
    public List<SeatId> getSeatIds() { return seatIds; }
}
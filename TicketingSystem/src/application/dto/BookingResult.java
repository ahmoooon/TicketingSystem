package application.dto;

import domain.Movie;
import domain.Seat;
import domain.Showtime;
import java.util.List;

public class BookingResult {
    private final Movie movie;
    private final Showtime showtime;
    private final List<Seat> seats;

    public BookingResult(Movie movie, Showtime showtime, List<Seat> seats) {
        this.movie = movie;
        this.showtime = showtime;
        this.seats = seats;
    }

    // Getters
    public Movie getMovie() { return movie; }
    public Showtime getShowtime() { return showtime; }
    public List<Seat> getSeats() { return seats; }
    
    // Helper method for the old cart logic (Optional)
    public String getMovieName() {
        return movie.getMovieName();
    }
}

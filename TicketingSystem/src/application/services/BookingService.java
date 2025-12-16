// src/application/services/BookingService.java
package application.services;

import application.dto.BookingRequest;
import application.dto.BookingResult;
import domain.CinemaHall;
import domain.Movie;
import domain.Showtime;
import domain.repositories.MovieRepository;
import domain.repositories.ShowtimeRepository;
import domain.repositories.SeatRepository;
import infrastructure.repositories.FileSeatRepository;
import infrastructure.repositories.SeatUnavailableException;

import java.time.LocalDate;
import java.util.List;
import domain.Seat;

public class BookingService {
    
    private final MovieRepository movieRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;

    public BookingService(MovieRepository movieRepository, 
                          ShowtimeRepository showtimeRepository, 
                          SeatRepository seatRepository) {
        this.movieRepository = movieRepository;
        this.showtimeRepository = showtimeRepository;
        this.seatRepository = seatRepository;
    }
    
    /**
     * The core use case: Orchestrates movie, showtime, and seat reservation.
     */
    public BookingResult bookTickets(BookingRequest request) {
        // 1. Validation
        validateBookingRequest(request);
        
        // 2. Get Movie
        Movie movie = movieRepository.findById(request.getMovieId())
            .orElseThrow(() -> new IllegalArgumentException("Movie with ID " + request.getMovieId() + " not found."));
        
        // 3. Get Showtime
        Showtime showtime = showtimeRepository.findAvailableShowtime(
            movie,
            request.getDate(), 
            request.getShowtimeTime(),
            request.getHallId()
        ).orElseThrow(() -> new IllegalArgumentException("Showtime on " + request.getDate() + " at " + request.getShowtimeTime() + " is not available."));
        
        // 4. Reserve Seats (The critical transaction)
        try {
            List<Seat> reservedSeats = seatRepository.reserveSeats(
                showtime, 
                request.getSeatIds()
            );
            
            // 5. Create Booking Result (Success)
            return new BookingResult(movie, showtime, reservedSeats); 
            
        } catch (SeatUnavailableException e) {
            // Re-throw the explicit infrastructure exception
            throw e; 
        }
    }
    
    /**
     * Retrieves all available cinema halls.
     */
    public List<CinemaHall> getAllHalls() {
        if (seatRepository instanceof FileSeatRepository) {
            return ((FileSeatRepository) seatRepository).getAllHalls();
        }
        return List.of(); // Empty list if wrong repository type
    }
    
    /**
     * Retrieves halls of a specific type.
     */
    public List<CinemaHall> getHallsByType(String hallType) {
        if (seatRepository instanceof FileSeatRepository) {
            return ((FileSeatRepository) seatRepository).getHallsByType(hallType);
        }
        return List.of();
    }
    
    /**
     * Retrieves seat availability for a specific showtime.
     */
    public List<Seat> getSeatsByShowtime(Movie movie, LocalDate date, String time, int hallId) {
        Showtime showtime = showtimeRepository.findAvailableShowtime(
            movie, date, time, hallId
        ).orElseThrow(() -> new IllegalArgumentException("Showtime not found"));
        
        return seatRepository.findSeatsByShowtime(showtime);
    }
    
    /**
     * Retrieves a single movie by its ID.
     */
    public Movie getMovieById(int movieId) {
        return movieRepository.findById(movieId)
            .orElseThrow(() -> new IllegalArgumentException("Movie with ID " + movieId + " not found."));
    }
    
    /**
     * Retrieves all movies for display.
     */
    public List<Movie> getAvailableMovies() {
        return movieRepository.findAll();
    }
    
    /**
     * Retrieves available dates for a given movie.
     */
    public List<Showtime> getAvailableShowtimeDates(Movie movie) {
        return showtimeRepository.findAvailableDates(movie);
    }

    private void validateBookingRequest(BookingRequest request) {
        if (request.getSeatIds() == null || request.getSeatIds().isEmpty()) {
            throw new IllegalArgumentException("No seats selected for booking.");
        }
        if (request.getMovieId() <= 0) {
            throw new IllegalArgumentException("Invalid movie ID.");
        }
        
        // Check for duplicate seats in request
        java.util.Set<domain.valueobjects.SeatId> uniqueSeats = new java.util.HashSet<>(request.getSeatIds());
        if (uniqueSeats.size() != request.getSeatIds().size()) {
            throw new IllegalArgumentException("Duplicate seats detected in selection.");
        }
    }
}
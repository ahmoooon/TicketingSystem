// src/application/services/BookingService.java
package application.services;

import application.dto.BookingRequest;
import application.dto.BookingResult;
import domain.Movie;
import domain.Showtime;
import domain.repositories.MovieRepository;
import domain.repositories.ShowtimeRepository;
import domain.repositories.SeatRepository;
import infrastructure.repositories.SeatUnavailableException;

import java.time.LocalDate;
import java.util.List;
import domain.Seat;

public class BookingService {
    
    // Dependencies are defined as interfaces (DIP)
    private final MovieRepository movieRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;

    // Dependency Injection via Constructor
    public BookingService(MovieRepository movieRepository, 
                          ShowtimeRepository showtimeRepository, 
                          SeatRepository seatRepository) {
        this.movieRepository = movieRepository;
        this.showtimeRepository = showtimeRepository;
        this.seatRepository = seatRepository;
    }
    
    /**
     * The core use case: Orchestrates movie, showtime, and seat reservation.
     * Replaces the business logic originally in TicketingSystem.bookingModule().
     */
    public BookingResult bookTickets(BookingRequest request) {
        // 1. Validation (Initial simple check - can be expanded)
        validateBookingRequest(request);
        
        // 2. Get Movie (Failure is communicated via Exception/Optional)
        Movie movie = movieRepository.findById(request.getMovieId())
            .orElseThrow(() -> new IllegalArgumentException("Movie with ID " + request.getMovieId() + " not found."));
        
        // 3. Get Showtime (Ensures the date, time, and hall combination is valid)
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
            // Re-throw the explicit infrastructure exception (critical for testing)
            throw e; 
        }
    }
    /**
     * Retrieves a single movie by its ID, serving the Presentation layer (CLI).
     * This method was missing and is now added.
     */
    public Movie getMovieById(int movieId) {
        return movieRepository.findById(movieId)
            .orElseThrow(() -> new IllegalArgumentException("Movie with ID " + movieId + " not found."));
    }
    
    /**
     * Retrieves all movies for display (used by the CLI handler).
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
        java.util.Set<domain.valueobjects.SeatId> uniqueSeats = new java.util.HashSet<>(request.getSeatIds());
    
        if (uniqueSeats.size() != request.getSeatIds().size()) {
            throw new IllegalArgumentException("Duplicate seats detected in selection. You cannot book the same seat twice.");
        }
    }
}
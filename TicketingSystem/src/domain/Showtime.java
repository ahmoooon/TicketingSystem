package domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a scheduled screening of a Movie in a specific CinemaHall.
 */
public class Showtime {
    private final Movie movie; 
    private final LocalDate date; 
    private final String showtime; 
    private final CinemaHall hall; 

    // Primary Constructor
    public Showtime(Movie movie, int year, int month, int day, String showtime, CinemaHall hall) {
        this.movie = movie;
        this.date = LocalDate.of(year, month, day);
        this.showtime = showtime;
        this.hall = hall;
    }
    
    // Legacy constructor
    public Showtime(Movie movie, int year, int month, int day, String showtime) {
        this(movie, year, month, day, showtime, null); 
    }
    
    // --- Getters (as fixed previously) ---
    
    public String getShowtime() {
        return showtime;
    }
    
    public String getMovieName() {
        if (movie == null) return "N/A";
        return movie.getMovieName();
    }
    
    public CinemaHall getCinemaHall(){
        return hall;
    }
    
    public int getHallId() {
        if (hall == null) {
            throw new IllegalStateException("Showtime object has not been assigned a CinemaHall.");
        }
        return hall.getHallId();
    }
    
    public LocalDate getDate() {
        return date;
    }

    public String time() {
        return showtime;
    }
    
    public String getHallType() {
        if (hall == null) return null;
        return hall.getHallType();
    }
    
    // --- FIX: Method restored from original monolithic code ---
    /**
     * Generates a list of possible showtimes based on a start time, end time, and interval.
     * This logic was extracted from the old MainApplication.
     */
    public List<LocalTime> generateShowtimes(LocalTime startTime, LocalTime endTime, int intervalMinutes) {
        List<LocalTime> generatedShowtimes = new ArrayList<>();
        LocalTime currentTime = startTime;
        
        while (currentTime.isBefore(endTime) || currentTime.equals(endTime)) {
            generatedShowtimes.add(currentTime);
            currentTime = currentTime.plusMinutes(intervalMinutes);
        }
        return generatedShowtimes;
    }

    @Override
    public String toString() {
        return "\nMovie: " + getMovieName() + 
               "\nDate: " + date + 
               "\nTime: " + showtime + 
               "\nHall: " + (hall != null ? hall.getHallType() : "N/A");
    }
}
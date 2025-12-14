// src/domain/repositories/ShowtimeRepository.java
package domain.repositories;

import domain.Movie;
import domain.Showtime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ShowtimeRepository {

    /**
     * Finds a list of available dates for a specific movie.
     * @param movie The movie object.
     * @return A list of Showtime objects (primarily holding the date).
     */
    List<Showtime> findAvailableDates(Movie movie);

    /**
     * Finds the specific showtime slot and links it to the Hall.
     * This orchestrates the selection process.
     * @param movie The movie.
     * @param date The selected date.
     * @param timeString The user-selected time (e.g., "10:00 AM").
     * @param hallId The ID of the hall selected.
     * @return The specific, fully instantiated Showtime object.
     */
    Optional<Showtime> findAvailableShowtime(
        Movie movie, 
        LocalDate date, 
        String timeString,
        int hallId
    );
}
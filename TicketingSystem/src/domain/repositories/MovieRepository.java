/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package domain.repositories;

import domain.Movie;
import java.util.List;
import java.util.Optional;

public interface MovieRepository {
    /**
     * Retrieves all available movies.
     * @return A list of all Movie objects.
     */
    List<Movie> findAll();
    
    /**
     * Finds a Movie by its logical ID (used as array index + 1 in the legacy system).
     * We use Optional to explicitly handle cases where the movie is not found, 
     * promoting cleaner error handling in the application layer.
     * * @param id The ID of the movie to find (e.g., 1 for the first movie).
     * @return An Optional containing the Movie if found, or an empty Optional otherwise.
     */
    Optional<Movie> findById(int id);
    
    // Future methods could include:
    // Optional<Movie> findByTitle(String title);
    // void save(Movie movie);
}
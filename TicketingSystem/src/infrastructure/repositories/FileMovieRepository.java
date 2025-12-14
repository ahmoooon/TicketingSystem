/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package infrastructure.repositories;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import domain.Movie;
import domain.repositories.MovieRepository;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;

/**
 * Concrete implementation of MovieRepository that retrieves data from a hardcoded 
 * list (simulating file I/O for the 2-day plan). 
 * This class belongs to the Infrastructure Layer.
 */
public class FileMovieRepository implements MovieRepository {
    
    private final String MOVIE_FILE = "movies.json";
    private List<Movie> movieList;
    private final Gson gson = new Gson();

    public FileMovieRepository() {
        loadData();
    }

    private void loadData() {
        try (Reader reader = new FileReader(MOVIE_FILE)) {
            // Converts JSON text file directly into List<Movie> objects
            movieList = gson.fromJson(reader, new TypeToken<List<Movie>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace(); // Handle file not found
        }
    }
    
    @Override
    public List<Movie> findAll() {
        return movieList;
    }

    @Override
    public Optional<Movie> findById(int id) {
        return movieList.stream()
                .filter(m -> m.getId() == id)
                .findFirst();
    }
}

package infrastructure.repositories;

import application.utilities.LoggerSetup;
import domain.Movie;
import domain.repositories.MovieRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FileMovieRepository implements MovieRepository {
    
    private static final String MOVIE_FILE = "movies.json";
    private static final Logger logger = LoggerSetup.getLogger();
    
    private List<Movie> movieList;

    public FileMovieRepository() {
        loadData();
        // --- ADD DEBUG LINE 1 HERE ---
        System.out.println("[DEBUG] Movies loaded from file: " + (movieList != null ? movieList.size() : 0)); 
    }

    private void loadData() {
        List<String> jsonLines = DataFileHandler.loadFromJsonFile(MOVIE_FILE);
        
        if (jsonLines.isEmpty()) {
            // --- ADD DEBUG LINE 2 HERE ---
            System.out.println("[DEBUG] List is empty. Seeding default data...");
            
            logger.warning("No movies found in file. Creating default movies.");
            movieList = createDefaultMovies();
            saveMovies(); 
        } else {
            movieList = jsonLines.stream()
                .map(this::parseMovieFromJson)
                .filter(m -> m != null)
                .collect(Collectors.toList());
        }
        
        logger.log(Level.INFO, "Loaded {0} movies from {1}", 
                   new Object[]{movieList.size(), MOVIE_FILE});
    }
    
    // ... (Keep the rest of the methods extractInt, extractString etc. exactly the same) ...

    private Movie parseMovieFromJson(String json) {
        try {
            int id = extractInt(json, "id");
            String movieName = extractString(json, "movieName");
            double movieLength = extractDouble(json, "movieLength");
            String director = extractString(json, "director");
            String releaseDate = extractString(json, "releaseDate");
            
            return new Movie(id, movieName, movieLength, director, releaseDate);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to parse movie JSON: {0}. Error: {1}", 
                        new Object[]{json, e.getMessage()});
            return null;
        }
    }
    
    private List<Movie> createDefaultMovies() {
        List<Movie> defaults = new ArrayList<>();
        defaults.add(new Movie(1, "Dune: Part 1", 2.35, "Denis Villeneuve", "October 22, 2021"));
        defaults.add(new Movie(2, "Blade Runner 2049", 2.43, "Denis Villeneuve", "October 5, 2017"));
        defaults.add(new Movie(3, "Infinity Pool", 1.58, "Brandon Cronenberg", "January 27, 2023"));
        defaults.add(new Movie(4, "Crimes of the Future", 1.47, "David Cronenberg", "May 25, 2022"));
        defaults.add(new Movie(5, "Asteroid City", 1.45, "Wes Anderson", "June 15, 2023"));
        return defaults;
    }
    
    private void saveMovies() {
        List<String> jsonLines = movieList.stream()
            .map(this::movieToJsonString)
            .collect(Collectors.toList());
        
        DataFileHandler.saveToJsonFile(jsonLines, MOVIE_FILE);
    }
    
    private String movieToJsonString(Movie movie) {
        return String.format(
            "{\"id\":%d,\"movieName\":\"%s\",\"movieLength\":%.2f,\"director\":\"%s\",\"releaseDate\":\"%s\"}",
            movie.getId(),
            movie.getMovieName(),
            movie.getMovieLength(),
            movie.getDirector(),
            movie.releaseDate()
        );
    }
    
    @Override
    public List<Movie> findAll() {
        return new ArrayList<>(movieList);
    }

    @Override
    public Optional<Movie> findById(int id) {
        return movieList.stream()
                .filter(m -> m.getId() == id)
                .findFirst();
    }
    
    private int extractInt(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int start = json.indexOf(searchKey) + searchKey.length();
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        return Integer.parseInt(json.substring(start, end).trim());
    }
    
    private double extractDouble(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int start = json.indexOf(searchKey) + searchKey.length();
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        return Double.parseDouble(json.substring(start, end).trim());
    }
    
    private String extractString(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int start = json.indexOf(searchKey) + searchKey.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}
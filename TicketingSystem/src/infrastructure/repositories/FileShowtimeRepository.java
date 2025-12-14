package infrastructure.repositories;

import application.utilities.LoggerSetup;
import domain.Movie;
import domain.Showtime;
import domain.CinemaHall;
import domain.repositories.ShowtimeRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * REFACTORED: Uses DataFileHandler approach instead of Gson.
 * Manages hall configurations and showtime generation logic.
 */
public class FileShowtimeRepository implements ShowtimeRepository {
    
    private static final String HALLS_FILE = "halls.json";
    private static final int SHOWTIME_INTERVAL_MINUTES = 100;
    private static final Logger logger = LoggerSetup.getLogger();
    
    private final List<CinemaHall> hallConfigurations;

    public FileShowtimeRepository() {
        this.hallConfigurations = loadHalls();
        
        // Seed default halls if file doesn't exist
        if (this.hallConfigurations.isEmpty()) {
            seedHalls();
        }
        
        logger.log(Level.INFO, "FileShowtimeRepository initialized with {0} halls.", 
                   hallConfigurations.size());
    }
    
    // === HALL CONFIGURATION LOADING ===
    
    /**
     * Loads hall configurations from JSON file.
     * Format: {"hallNum":1,"hallType":"Standard","rowAmt":5,"colAmt":10}
     */
    private List<CinemaHall> loadHalls() {
        List<String> jsonLines = DataFileHandler.loadFromJsonFile(HALLS_FILE);
        
        return jsonLines.stream()
            .map(this::parseHallFromJson)
            .filter(h -> h != null)
            .collect(Collectors.toList());
    }
    
    /**
     * Parses a single CinemaHall from JSON string.
     */
    private CinemaHall parseHallFromJson(String json) {
        try {
            int hallNum = extractInt(json, "hallNum");
            String hallType = extractString(json, "hallType");
            int rowAmt = extractInt(json, "rowAmt");
            int colAmt = extractInt(json, "colAmt");
            
            return new CinemaHall(hallNum, hallType, rowAmt, colAmt);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to parse hall JSON: {0}", json);
            return null;
        }
    }
    
    /**
     * Creates and saves default hall configuration.
     */
    private void seedHalls() {
        hallConfigurations.add(new CinemaHall(1, CinemaHall.HALL_TYPE_STANDARD, 5, 10));
        hallConfigurations.add(new CinemaHall(2, CinemaHall.HALL_TYPE_IMAX, 8, 15));
        hallConfigurations.add(new CinemaHall(3, CinemaHall.HALL_TYPE_LOUNGE, 5, 5));
        
        saveHalls();
        logger.info("Default halls seeded and saved.");
    }
    
    /**
     * Saves hall configurations to JSON file.
     */
    private void saveHalls() {
        List<String> jsonLines = hallConfigurations.stream()
            .map(this::hallToJsonString)
            .collect(Collectors.toList());
        
        DataFileHandler.saveToJsonFile(jsonLines, HALLS_FILE);
    }
    
    /**
     * Converts CinemaHall to JSON string.
     */
    private String hallToJsonString(CinemaHall hall) {
        return String.format(
            "{\"hallNum\":%d,\"hallType\":\"%s\",\"rowAmt\":%d,\"colAmt\":%d}",
            hall.getHallId(),
            hall.getHallType(),
            hall.getRowAmt(),
            hall.getColAmt()
        );
    }
    
    // === SHOWTIME BUSINESS LOGIC ===
    
    /**
     * Calculates a future date by adding days to current date.
     */
    private LocalDate calculateFutureDate(int daysFromNow) {
        return LocalDate.now().plusDays(daysFromNow);
    }
    
    /**
     * Retrieves hall configuration by hall ID.
     */
    private CinemaHall getHallById(int hallId) {
        return hallConfigurations.stream()
            .filter(h -> h.getHallId() == hallId)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Hall ID not found: " + hallId));
    }
    
    @Override
    public List<Showtime> findAvailableDates(Movie movie) {
        List<Showtime> availableDates = new ArrayList<>();
        
        // Generate next 3 days as available dates
        for (int i = 1; i <= 3; i++) {
            LocalDate date = calculateFutureDate(i);
            availableDates.add(new Showtime(
                movie, 
                date.getYear(), 
                date.getMonthValue(), 
                date.getDayOfMonth(), 
                null, 
                null
            ));
        }
        
        return availableDates;
    }

    @Override
    public Optional<Showtime> findAvailableShowtime(Movie movie, LocalDate date, String timeString, int hallId) {
        CinemaHall hall = getHallById(hallId);
        
        // Generate valid times for the day
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(22, 0);
        
        List<LocalTime> generatedTimes = new Showtime(null, 1900, 1, 1, null, null)
                .generateShowtimes(startTime, endTime, SHOWTIME_INTERVAL_MINUTES);
            
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        // Validate that the requested time is in the generated list
        boolean timeIsValid = generatedTimes.stream()
            .anyMatch(t -> t.format(timeFormatter).equals(timeString));
            
        if (!timeIsValid) {
            logger.log(Level.WARNING, "Invalid showtime requested: {0}", timeString);
            return Optional.empty(); 
        }

        // Create and return the showtime
        Showtime showtime = new Showtime(
            movie, 
            date.getYear(), 
            date.getMonthValue(), 
            date.getDayOfMonth(), 
            timeString, 
            hall 
        );
        
        logger.log(Level.INFO, "Showtime created: {0} at {1} in Hall {2}", 
                   new Object[]{movie.getMovieName(), timeString, hallId});
        
        return Optional.of(showtime);
    }
    
    // === Helper Methods for JSON Parsing ===
    
    private int extractInt(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int start = json.indexOf(searchKey) + searchKey.length();
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        
        return Integer.parseInt(json.substring(start, end).trim());
    }
    
    private String extractString(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int start = json.indexOf(searchKey) + searchKey.length();
        int end = json.indexOf("\"", start);
        
        return json.substring(start, end);
    }
}
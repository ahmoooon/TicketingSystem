package infrastructure.repositories;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import domain.Movie;
import domain.Showtime;
import domain.CinemaHall;
import domain.repositories.ShowtimeRepository;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileShowtimeRepository implements ShowtimeRepository {
    
    private static final String HALLS_FILE = "halls.json";
    private static final int SHOWTIME_INTERVAL_MINUTES = 100;
    
    private final List<CinemaHall> hallConfigurations;
    private final Gson gson;

    public FileShowtimeRepository() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.hallConfigurations = loadHalls();
        
        // Seed default halls if file doesn't exist
        if (this.hallConfigurations.isEmpty()) {
            seedHalls();
        }
    }
    
    // --- JSON Persistence Methods ---
    
    private List<CinemaHall> loadHalls() {
        File file = new File(HALLS_FILE);
        if (!file.exists()) return new ArrayList<>();
        
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<ArrayList<CinemaHall>>(){}.getType();
            List<CinemaHall> data = gson.fromJson(reader, listType);
            return (data != null) ? data : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    private void seedHalls() {
        // Default configuration (Standard, IMAX, Lounge)
        hallConfigurations.add(new CinemaHall(1, CinemaHall.HALL_TYPE_STANDARD, 5, 10));
        hallConfigurations.add(new CinemaHall(2, CinemaHall.HALL_TYPE_IMAX, 8, 15));
        hallConfigurations.add(new CinemaHall(3, CinemaHall.HALL_TYPE_LOUNGE, 5, 5));
        
        try (Writer writer = new FileWriter(HALLS_FILE)) {
            gson.toJson(hallConfigurations, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // --- Existing Business Logic ---

    private LocalDate calculateFutureDate(int daysFromNow) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        int day = currentDateTime.getDayOfMonth() + daysFromNow;
        int month = currentDateTime.getMonthValue();
        int year = currentDateTime.getYear();

        // Simple overflow logic (improved version would use .plusDays)
        while (day > 30) {
            month += 1;
            day -= 30;
        }
        return LocalDate.of(year, month, day); 
    }
    
    private CinemaHall getHallById(int hallId) {
        return hallConfigurations.stream()
            .filter(h -> h.getHallId() == hallId)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Hall ID not found: " + hallId));
    }
    
    @Override
    public List<Showtime> findAvailableDates(Movie movie) {
        List<Showtime> availableDates = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            LocalDate date = calculateFutureDate(i);
            availableDates.add(new Showtime(movie, date.getYear(), date.getMonthValue(), date.getDayOfMonth(), null, null));
        }
        return availableDates;
    }

    @Override
    public Optional<Showtime> findAvailableShowtime(Movie movie, LocalDate date, String timeString, int hallId) {
        CinemaHall hall = getHallById(hallId);
        
        // Generate valid times
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(22, 0);
        
        List<LocalTime> generatedTimes = new Showtime(null, 1900, 1, 1, null, null)
                .generateShowtimes(startTime, endTime, SHOWTIME_INTERVAL_MINUTES);
            
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        boolean timeIsValid = generatedTimes.stream()
            .anyMatch(t -> t.format(timeFormatter).equals(timeString));
            
        if (!timeIsValid) {
            return Optional.empty(); 
        }

        return Optional.of(new Showtime(
            movie, 
            date.getYear(), 
            date.getMonthValue(), 
            date.getDayOfMonth(), 
            timeString, 
            hall 
        ));
    }
}
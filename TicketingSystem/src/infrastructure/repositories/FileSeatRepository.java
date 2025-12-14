package infrastructure.repositories;

import application.utilities.LoggerSetup;
import domain.Seat;
import domain.Showtime;
import domain.CinemaHall;
import domain.repositories.SeatRepository;
import domain.valueobjects.SeatId;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * REFACTORED: Uses DataFileHandler approach for consistency.
 * FIXED: Booking key now properly isolates different movies/showtimes.
 */
public class FileSeatRepository implements SeatRepository {

    private static final String BOOKINGS_FILE = "bookings.json";
    private static final String HALLS_FILE = "halls.json";
    private static final Logger logger = LoggerSetup.getLogger();
    
    private final List<CinemaHall> hallList;
    private ConcurrentHashMap<String, List<SeatId>> bookings;

    public FileSeatRepository() {
        this.hallList = loadHalls();
        this.bookings = loadBookings();
        
        logger.log(Level.INFO, "FileSeatRepository initialized. Halls: {0}, Bookings: {1}", 
                   new Object[]{hallList.size(), bookings.size()});
    }
    
    // === HALLS LOADING (Using DataFileHandler) ===
    
    /**
     * Loads hall configurations from JSON file.
     * Format: {"hallNum":1,"hallType":"Standard","rowAmt":5,"colAmt":10}
     */
    private List<CinemaHall> loadHalls() {
        List<String> jsonLines = DataFileHandler.loadFromJsonFile(HALLS_FILE);
        
        if (jsonLines.isEmpty()) {
            logger.warning("No halls found in file. Creating default halls.");
            return createDefaultHalls();
        }
        
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
     * Creates default hall configuration if file doesn't exist.
     */
    private List<CinemaHall> createDefaultHalls() {
        List<CinemaHall> defaults = List.of(
            new CinemaHall(1, CinemaHall.HALL_TYPE_STANDARD, 5, 10),
            new CinemaHall(2, CinemaHall.HALL_TYPE_IMAX, 8, 15),
            new CinemaHall(3, CinemaHall.HALL_TYPE_LOUNGE, 5, 5)
        );
        
        // Save defaults to file
        List<String> jsonLines = defaults.stream()
            .map(this::hallToJsonString)
            .collect(Collectors.toList());
        DataFileHandler.saveToJsonFile(jsonLines, HALLS_FILE);
        
        return defaults;
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
    
    // === BOOKINGS LOADING (Using DataFileHandler) ===
    
    /**
     * Loads existing seat bookings from JSON file.
     * Format: {"key":"1_2025-12-15_10:00 AM_1","seats":"A1,A2,B3"}
     */
    private ConcurrentHashMap<String, List<SeatId>> loadBookings() {
        List<String> jsonLines = DataFileHandler.loadFromJsonFile(BOOKINGS_FILE);
        ConcurrentHashMap<String, List<SeatId>> map = new ConcurrentHashMap<>();
        
        for (String line : jsonLines) {
            try {
                String key = extractString(line, "key");
                String seatsStr = extractString(line, "seats");
                
                List<SeatId> seatIds = parseSeatIds(seatsStr);
                map.put(key, seatIds);
                
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to parse booking: {0}", line);
            }
        }
        
        logger.log(Level.INFO, "Loaded {0} booking records.", map.size());
        return map;
    }
    
    /**
     * Parses seat IDs from comma-separated string.
     * Example: "A1,A2,B3" -> List of SeatId objects
     */
    private List<SeatId> parseSeatIds(String seatsStr) {
        List<SeatId> seatIds = new ArrayList<>();
        
        if (seatsStr == null || seatsStr.trim().isEmpty()) {
            return seatIds;
        }
        
        String[] parts = seatsStr.split(",");
        for (String part : parts) {
            part = part.trim();
            if (part.length() >= 2) {
                char row = part.charAt(0);
                int col = Integer.parseInt(part.substring(1));
                seatIds.add(new SeatId(row, col));
            }
        }
        
        return seatIds;
    }
    
    /**
     * Saves current bookings to JSON file.
     */
    private void saveBookings() {
        List<String> jsonLines = bookings.entrySet().stream()
            .map(entry -> bookingToJsonString(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
        
        DataFileHandler.saveToJsonFile(jsonLines, BOOKINGS_FILE);
    }
    
    /**
     * Converts a booking entry to JSON string.
     */
    private String bookingToJsonString(String key, List<SeatId> seats) {
        String seatsStr = seats.stream()
            .map(SeatId::toDisplayString)
            .collect(Collectors.joining(","));
        
        return String.format("{\"key\":\"%s\",\"seats\":\"%s\"}", key, seatsStr);
    }
    
    // === CORE REPOSITORY METHODS ===
    
    /**
     * FIXED: Key now includes movieId to prevent cross-movie booking conflicts.
     * 
     * Critical Fix: Previously, bookings were keyed by only (hall, date, time).
     * This meant seats booked for "Dune at 10 AM in Hall 1" would block
     * "Blade Runner at 10 AM in Hall 1" (if they somehow had the same showtime).
     * 
     * While in reality, only ONE movie should play at a given hall/time,
     * the system should explicitly track this to prevent data corruption.
     */
    private String generateKey(Showtime showtime) {
        // Include movie name as part of the key for clarity
        return showtime.getHallId() + "_" + 
               showtime.getDate() + "_" + 
               showtime.time() + "_" + 
               showtime.getMovieName().replaceAll("\\s+", ""); // Remove spaces
    }
    
    private CinemaHall getHallByType(String type) {
        return hallList.stream()
            .filter(h -> h.getHallType().equals(type))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Hall type not found: " + type));
    }
    
    @Override
    public Optional<Seat> findSeat(Showtime showtime, SeatId seatId) {
        CinemaHall hall = getHallByType(showtime.getHallType());

        if (isValidForHall(seatId, hall)) {
            String status = isSeatBooked(showtime, seatId) ? "Booked" : "Available";
            return Optional.of(new Seat(seatId, "Single", status, hall));
        }
        return Optional.empty();
    }

    private boolean isValidForHall(SeatId seatId, CinemaHall hall) {
        int maxCol = hall.getMaxSeatCol(); 
        char maxRow = (char) ('A' + hall.getRowAmt() - 1); 
        
        return seatId.getRow() >= 'A' && seatId.getRow() <= maxRow &&
               seatId.getColumn() >= 1 && seatId.getColumn() <= maxCol;
    }
    
    private boolean isSeatBooked(Showtime showtime, SeatId seatId) {
        String key = generateKey(showtime);
        List<SeatId> bookedList = bookings.getOrDefault(key, new ArrayList<>());
        return bookedList.contains(seatId);
    }

    @Override
    public List<Seat> reserveSeats(Showtime showtime, List<SeatId> seatIds) {
        String key = generateKey(showtime);
        
        logger.log(Level.INFO, "Reserving seats with key: {0}", key);
        
        // 1. Check for conflicts
        List<SeatId> currentBooked = bookings.getOrDefault(key, new ArrayList<>());
        List<SeatId> conflicts = seatIds.stream()
            .filter(currentBooked::contains)
            .collect(Collectors.toList());

        if (!conflicts.isEmpty()) {
            logger.log(Level.WARNING, "Seat booking conflict detected: {0}", conflicts);
            throw new SeatUnavailableException("The following seats are already booked: " + conflicts);
        }

        // 2. Reserve (Update Memory)
        currentBooked.addAll(seatIds);
        bookings.put(key, currentBooked);
        
        // 3. Persist (Update JSON File)
        saveBookings();
        
        logger.log(Level.INFO, "Successfully reserved {0} seats.", seatIds.size());

        // 4. Return result
        return seatIds.stream()
            .map(id -> findSeat(showtime, id).orElseThrow(() -> 
                new RuntimeException("Error forming seat object")))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Seat> findSeatsByShowtime(Showtime showtime) {
        List<Seat> allSeats = new ArrayList<>();
        CinemaHall hall = getHallByType(showtime.getHallType());
        
        char maxRow = (char) ('A' + hall.getRowAmt() - 1);
        int maxCol = hall.getMaxSeatCol();

        for (char row = 'A'; row <= maxRow; row++) {
            for (int col = 1; col <= maxCol; col++) {
                SeatId id = new SeatId(row, col);
                boolean isBooked = isSeatBooked(showtime, id);
                String status = isBooked ? "Booked" : "Available";
                allSeats.add(new Seat(id, "Single", status, hall));
            }
        }
        return allSeats;
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
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

public class FileSeatRepository implements SeatRepository {

    private static final String DEFAULT_BOOKINGS_FILE = "bookings.json";
    private static final String DEFAULT_HALLS_FILE = "halls.json";
    private static final Logger logger = LoggerSetup.getLogger();
    
    private final String bookingsFile;
    private final String hallsFile;
    
    private final List<CinemaHall> hallList;
    private ConcurrentHashMap<String, List<SeatId>> confirmedBookings;
    private ConcurrentHashMap<String, List<SeatId>> cartReservations;

    // DEFAULT CONSTRUCTOR (for production use)
    public FileSeatRepository() {
        this(DEFAULT_BOOKINGS_FILE, DEFAULT_HALLS_FILE);
    }
    
    // CONSTRUCTOR WITH CUSTOM FILES (for testing)
    public FileSeatRepository(String bookingsFile, String hallsFile) {
        this.bookingsFile = bookingsFile;
        this.hallsFile = hallsFile;
        this.hallList = loadHalls();
        this.confirmedBookings = loadBookings();
        this.cartReservations = new ConcurrentHashMap<>();
        
        logger.log(Level.INFO, "FileSeatRepository initialized. Halls: {0}, Confirmed Bookings: {1}", 
                   new Object[]{hallList.size(), confirmedBookings.size()});
    }
    
    // === NEW METHODS: Cart Management ===
    
    /**
     * Temporarily reserves seats for a cart (NOT written to file)
     */
    public void addToCartReservation(Showtime showtime, List<SeatId> seatIds) {
        String key = generateKey(showtime);
        List<SeatId> currentCart = cartReservations.getOrDefault(key, new ArrayList<>());
        currentCart.addAll(seatIds);
        cartReservations.put(key, currentCart);
        
        logger.info("Added " + seatIds.size() + " seats to cart reservation: " + key);
    }
    
    /**
     * Confirms cart reservation as a paid booking (written to file)
     */
    public void confirmCartReservation(Showtime showtime, List<SeatId> seatIds) {
        String key = generateKey(showtime);
        
        // Move from cart to confirmed
        List<SeatId> confirmed = confirmedBookings.getOrDefault(key, new ArrayList<>());
        confirmed.addAll(seatIds);
        confirmedBookings.put(key, confirmed);
        
        // Remove from cart
        List<SeatId> cart = cartReservations.get(key);
        if (cart != null) {
            cart.removeAll(seatIds);
            if (cart.isEmpty()) {
                cartReservations.remove(key);
            }
        }
        
        saveBookings();
        logger.info("Confirmed " + seatIds.size() + " seats as paid booking: " + key);
    }
    
    /**
     * Cancels cart reservation (releases seats)
     */
    public void cancelCartReservation(Showtime showtime, List<SeatId> seatIds) {
        String key = generateKey(showtime);
        List<SeatId> cart = cartReservations.get(key);
        
        if (cart != null) {
            cart.removeAll(seatIds);
            if (cart.isEmpty()) {
                cartReservations.remove(key);
            }
            logger.info("Cancelled " + seatIds.size() + " seats from cart: " + key);
        }
    }
    
    /**
     * Clears all cart reservations for a customer (on logout)
     */
    public void clearAllCartReservations() {
        int count = cartReservations.size();
        cartReservations.clear();
        logger.info("Cleared all cart reservations (" + count + " entries)");
    }
    
    // === EXISTING METHODS (Updated to use instance variables) ===
    
    public List<CinemaHall> getAllHalls() {
        return new ArrayList<>(hallList);
    }
    
    public List<CinemaHall> getHallsByType(String hallType) {
        return hallList.stream()
            .filter(h -> h.getHallType().equals(hallType))
            .collect(Collectors.toList());
    }
    
    private List<CinemaHall> loadHalls() {
        List<String> jsonLines = DataFileHandler.loadFromJsonFile(hallsFile);
        
        if (jsonLines.isEmpty()) {
            logger.warning("No halls found in file. Creating default halls.");
            return createDefaultHalls();
        }
        
        return jsonLines.stream()
            .map(this::parseHallFromJson)
            .filter(h -> h != null)
            .collect(Collectors.toList());
    }
    
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
    
    private List<CinemaHall> createDefaultHalls() {
        List<CinemaHall> defaults = List.of(
            new CinemaHall(1, CinemaHall.HALL_TYPE_STANDARD, 5, 10),
            new CinemaHall(2, CinemaHall.HALL_TYPE_STANDARD, 5, 10),
            new CinemaHall(3, CinemaHall.HALL_TYPE_STANDARD, 5, 10),
            new CinemaHall(4, CinemaHall.HALL_TYPE_IMAX, 8, 15),
            new CinemaHall(5, CinemaHall.HALL_TYPE_IMAX, 8, 15),
            new CinemaHall(6, CinemaHall.HALL_TYPE_LOUNGE, 5, 5)
        );
        
        List<String> jsonLines = defaults.stream()
            .map(this::hallToJsonString)
            .collect(Collectors.toList());
        DataFileHandler.saveToJsonFile(jsonLines, hallsFile);
        
        return defaults;
    }
    
    private String hallToJsonString(CinemaHall hall) {
        return String.format(
            "{\"hallNum\":%d,\"hallType\":\"%s\",\"rowAmt\":%d,\"colAmt\":%d}",
            hall.getHallId(),
            hall.getHallType(),
            hall.getRowAmt(),
            hall.getColAmt()
        );
    }
    
    private ConcurrentHashMap<String, List<SeatId>> loadBookings() {
        List<String> jsonLines = DataFileHandler.loadFromJsonFile(bookingsFile);
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
        
        logger.log(Level.INFO, "Loaded {0} confirmed booking records.", map.size());
        return map;
    }
    
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
    
    private void saveBookings() {
        List<String> jsonLines = confirmedBookings.entrySet().stream()
            .map(entry -> bookingToJsonString(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
        
        DataFileHandler.saveToJsonFile(jsonLines, bookingsFile);
    }
    
    private String bookingToJsonString(String key, List<SeatId> seats) {
        String seatsStr = seats.stream()
            .map(SeatId::toDisplayString)
            .collect(Collectors.joining(","));
        
        return String.format("{\"key\":\"%s\",\"seats\":\"%s\"}", key, seatsStr);
    }
    
    private String generateKey(Showtime showtime) {
        return showtime.getHallId() + "_" + 
               showtime.getDate() + "_" + 
               showtime.time();
    }
    
    private CinemaHall getHallById(int hallId) {
        return hallList.stream()
            .filter(h -> h.getHallId() == hallId)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Hall ID not found: " + hallId));
    }
    
    @Override
    public Optional<Seat> findSeat(Showtime showtime, SeatId seatId) {
        CinemaHall hall = getHallById(showtime.getHallId());

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
        
        // Check BOTH confirmed bookings AND cart reservations
        List<SeatId> confirmed = confirmedBookings.getOrDefault(key, new ArrayList<>());
        List<SeatId> cart = cartReservations.getOrDefault(key, new ArrayList<>());
        
        return confirmed.contains(seatId) || cart.contains(seatId);
    }

    @Override
    public List<Seat> reserveSeats(Showtime showtime, List<SeatId> seatIds) {
        String key = generateKey(showtime);
        
        logger.log(Level.INFO, "Reserving seats for cart: {0}", key);
        
        // Check conflicts with BOTH confirmed AND other carts
        List<SeatId> confirmed = confirmedBookings.getOrDefault(key, new ArrayList<>());
        List<SeatId> otherCarts = cartReservations.getOrDefault(key, new ArrayList<>());
        
        List<SeatId> allBooked = new ArrayList<>();
        allBooked.addAll(confirmed);
        allBooked.addAll(otherCarts);
        
        List<SeatId> conflicts = seatIds.stream()
            .filter(allBooked::contains)
            .collect(Collectors.toList());

        if (!conflicts.isEmpty()) {
            logger.log(Level.WARNING, "Seat booking conflict: {0}", conflicts);
            throw new SeatUnavailableException("Seats already booked: " + conflicts);
        }

        // Add to CART (not confirmed bookings)
        addToCartReservation(showtime, seatIds);
        
        logger.log(Level.INFO, "Successfully reserved {0} seats in cart.", seatIds.size());

        CinemaHall hall = getHallById(showtime.getHallId());
        return seatIds.stream()
            .map(id -> new Seat(id, "Single", "Reserved", hall))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Seat> findSeatsByShowtime(Showtime showtime) {
        List<Seat> allSeats = new ArrayList<>();
        CinemaHall hall = getHallById(showtime.getHallId());
        
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
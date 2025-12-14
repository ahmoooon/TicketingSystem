package infrastructure.repositories;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import domain.Seat;
import domain.Showtime;
import domain.CinemaHall;
import domain.repositories.SeatRepository;
import domain.valueobjects.SeatId;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FileSeatRepository implements SeatRepository {

    private static final String BOOKINGS_FILE = "bookings.json";
    private static final String HALLS_FILE = "halls.json";
    
    private final Gson gson;
    private final List<CinemaHall> hallList;
    private ConcurrentHashMap<String, List<SeatId>> bookings;

    public FileSeatRepository() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.hallList = loadHalls(); // Load halls config
        this.bookings = loadBookings(); // Load saved bookings
    }
    
    // --- JSON Persistence Methods ---

    private List<CinemaHall> loadHalls() {
        File file = new File(HALLS_FILE);
        if (!file.exists()) {
            // Fallback if file missing (or let ShowtimeRepository seed it)
            return List.of(
                new CinemaHall(1, CinemaHall.HALL_TYPE_STANDARD, 5, 10),
                new CinemaHall(2, CinemaHall.HALL_TYPE_IMAX, 8, 15),
                new CinemaHall(3, CinemaHall.HALL_TYPE_LOUNGE, 5, 5)
            );
        }
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<ArrayList<CinemaHall>>(){}.getType();
            List<CinemaHall> data = gson.fromJson(reader, listType);
            return (data != null) ? data : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private ConcurrentHashMap<String, List<SeatId>> loadBookings() {
        File file = new File(BOOKINGS_FILE);
        if (!file.exists()) return new ConcurrentHashMap<>();

        try (Reader reader = new FileReader(file)) {
            Type mapType = new TypeToken<ConcurrentHashMap<String, List<SeatId>>>(){}.getType();
            ConcurrentHashMap<String, List<SeatId>> data = gson.fromJson(reader, mapType);
            return (data != null) ? data : new ConcurrentHashMap<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ConcurrentHashMap<>();
        }
    }

    private void saveBookings() {
        try (Writer writer = new FileWriter(BOOKINGS_FILE)) {
            gson.toJson(bookings, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // --- Helper Methods ---

    private String generateKey(Showtime showtime) {
        return showtime.getHallId() + "_" + showtime.getDate() + "_" + showtime.time(); 
    }
    
    private CinemaHall getHallByType(String type) {
        return hallList.stream()
            .filter(h -> h.getHallType().equals(type))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Hall type not found: " + type));
    }
    
    // --- Repository Contract Implementations ---

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
        // Use loaded hall configuration for dimensions
        int maxCol = hall.getMaxSeatCol(); 
        
        // For Rows, we can infer from hall or use hardcoded logic if rowAmt isn't sufficient char
        // (Assuming rowAmt is 5 -> 'E', 8 -> 'H')
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
        
        // 1. Transactional Lock
        List<SeatId> currentBooked = bookings.getOrDefault(key, new ArrayList<>());
        List<SeatId> conflicts = seatIds.stream()
            .filter(currentBooked::contains)
            .collect(Collectors.toList());

        if (!conflicts.isEmpty()) {
            throw new SeatUnavailableException("The following seats are already booked: " + conflicts);
        }

        // 2. Reserve (Update Memory)
        currentBooked.addAll(seatIds);
        bookings.put(key, currentBooked);
        
        // 3. Persist (Update JSON File)
        saveBookings();

        // 4. Return result
        return seatIds.stream()
            .map(id -> findSeat(showtime, id).orElseThrow(() -> new RuntimeException("Error forming seat object")))
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
}
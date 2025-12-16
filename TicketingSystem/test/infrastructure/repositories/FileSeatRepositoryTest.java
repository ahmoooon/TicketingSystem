package infrastructure.repositories;

import domain.*;
import domain.valueobjects.SeatId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FileSeatRepositoryTest {
    
    private FileSeatRepository repository;
    private Movie testMovie;
    private CinemaHall standardHall;
    private CinemaHall imaxHall;
    private Showtime testShowtime;
    
    private static final String TEST_BOOKINGS_FILE = "bookings_test.json";
    private static final String TEST_HALLS_FILE = "halls_test.json";
    
    @Before
    public void setUp() {
        // Clean test files FIRST
        DataFileHandler.saveToJsonFile(java.util.Collections.emptyList(), TEST_BOOKINGS_FILE);
        DataFileHandler.saveToJsonFile(java.util.Collections.emptyList(), TEST_HALLS_FILE);
        
        // NOW initialize repository with custom test files
        repository = new FileSeatRepository(TEST_BOOKINGS_FILE, TEST_HALLS_FILE);
        
        // Setup test data
        testMovie = new Movie(1, "Test Movie", 2.0, "Test Director", "2025-01-01");
        standardHall = new CinemaHall(1, CinemaHall.HALL_TYPE_STANDARD, 5, 10);
        imaxHall = new CinemaHall(4, CinemaHall.HALL_TYPE_IMAX, 8, 15);
        testShowtime = new Showtime(testMovie, 2025, 1, 15, "10:00 AM", standardHall);
    }
    
    @After
    public void tearDown() {
        // Clear all cart reservations
        repository.clearAllCartReservations();
        
        // Clean test files
        DataFileHandler.saveToJsonFile(java.util.Collections.emptyList(), TEST_BOOKINGS_FILE);
        DataFileHandler.saveToJsonFile(java.util.Collections.emptyList(), TEST_HALLS_FILE);
    }
    
    // ========== HALL MANAGEMENT TESTS ==========
    
    @Test
    public void testGetAllHalls_ReturnsHalls() {
        List<CinemaHall> halls = repository.getAllHalls();
        
        assertNotNull("Halls list should not be null", halls);
        assertFalse("Should have default halls", halls.isEmpty());
    }
    
    @Test
    public void testGetAllHalls_ContainsStandardHall() {
        List<CinemaHall> halls = repository.getAllHalls();
        
        boolean hasStandard = halls.stream()
            .anyMatch(h -> h.getHallType().equals(CinemaHall.HALL_TYPE_STANDARD));
        
        assertTrue("Should contain at least one Standard hall", hasStandard);
    }
    
    @Test
    public void testGetAllHalls_ContainsIMAXHall() {
        List<CinemaHall> halls = repository.getAllHalls();
        
        boolean hasIMAX = halls.stream()
            .anyMatch(h -> h.getHallType().equals(CinemaHall.HALL_TYPE_IMAX));
        
        assertTrue("Should contain at least one IMAX hall", hasIMAX);
    }
    
    @Test
    public void testGetAllHalls_ContainsLoungeHall() {
        List<CinemaHall> halls = repository.getAllHalls();
        
        boolean hasLounge = halls.stream()
            .anyMatch(h -> h.getHallType().equals(CinemaHall.HALL_TYPE_LOUNGE));
        
        assertTrue("Should contain at least one Lounge hall", hasLounge);
    }
    
    @Test
    public void testGetHallsByType_Standard() {
        List<CinemaHall> standardHalls = repository.getHallsByType(CinemaHall.HALL_TYPE_STANDARD);
        
        assertNotNull("Standard halls list should not be null", standardHalls);
        assertFalse("Should have Standard halls", standardHalls.isEmpty());
        
        for (CinemaHall hall : standardHalls) {
            assertEquals("All halls should be Standard type", 
                        CinemaHall.HALL_TYPE_STANDARD, hall.getHallType());
        }
    }
    
    @Test
    public void testGetHallsByType_IMAX() {
        List<CinemaHall> imaxHalls = repository.getHallsByType(CinemaHall.HALL_TYPE_IMAX);
        
        assertNotNull("IMAX halls list should not be null", imaxHalls);
        assertFalse("Should have IMAX halls", imaxHalls.isEmpty());
        
        for (CinemaHall hall : imaxHalls) {
            assertEquals("All halls should be IMAX type", 
                        CinemaHall.HALL_TYPE_IMAX, hall.getHallType());
        }
    }
    
    @Test
    public void testGetHallsByType_Lounge() {
        List<CinemaHall> loungeHalls = repository.getHallsByType(CinemaHall.HALL_TYPE_LOUNGE);
        
        assertNotNull("Lounge halls list should not be null", loungeHalls);
        assertFalse("Should have Lounge halls", loungeHalls.isEmpty());
        
        for (CinemaHall hall : loungeHalls) {
            assertEquals("All halls should be Lounge type", 
                        CinemaHall.HALL_TYPE_LOUNGE, hall.getHallType());
        }
    }
    
    @Test
    public void testGetHallsByType_InvalidType() {
        List<CinemaHall> halls = repository.getHallsByType("InvalidType");
        
        assertNotNull("Should return empty list, not null", halls);
        assertTrue("Should return empty list for invalid type", halls.isEmpty());
    }
    
    // ========== FIND SEAT TESTS ==========
    
    @Test
    public void testFindSeat_ValidSeat() {
        SeatId seatId = new SeatId('A', 1);
        
        Optional<Seat> seat = repository.findSeat(testShowtime, seatId);
        
        assertTrue("Should find valid seat", seat.isPresent());
        assertEquals("Seat ID should match", seatId, seat.get().getId());
        assertEquals("Initial status should be Available", "Available", seat.get().getSeatStatus());
    }
    
    @Test
    public void testFindSeat_ValidRowAndColumn() {
        SeatId seatId = new SeatId('C', 5);
        
        Optional<Seat> seat = repository.findSeat(testShowtime, seatId);
        
        assertTrue("Should find seat C5", seat.isPresent());
        assertEquals('C', seat.get().getSeatRow());
        assertEquals(5, seat.get().getSeatCol());
    }
    
    @Test
    public void testFindSeat_InvalidRow_TooHigh() {
        // Standard hall has 5 rows (A-E)
        SeatId seatId = new SeatId('Z', 1);
        
        Optional<Seat> seat = repository.findSeat(testShowtime, seatId);
        
        assertFalse("Should not find seat beyond hall rows", seat.isPresent());
    }
    
    @Test
    public void testFindSeat_InvalidColumn_TooHigh() {
        // Standard hall has 10 columns
        SeatId seatId = new SeatId('A', 99);
        
        Optional<Seat> seat = repository.findSeat(testShowtime, seatId);
        
        assertFalse("Should not find seat beyond hall columns", seat.isPresent());
    }
    
    @Test
    public void testFindSeat_MinimumValidSeat() {
        SeatId seatId = new SeatId('A', 1);
        
        Optional<Seat> seat = repository.findSeat(testShowtime, seatId);
        
        assertTrue("Should find minimum valid seat A1", seat.isPresent());
    }
    
    @Test
    public void testFindSeat_MaximumValidSeat() {
        // Standard hall: 5 rows × 10 columns = E10
        SeatId seatId = new SeatId('E', 10);
        
        Optional<Seat> seat = repository.findSeat(testShowtime, seatId);
        
        assertTrue("Should find maximum valid seat E10", seat.isPresent());
    }
    
    // ========== RESERVE SEATS (CART) TESTS ==========
    
    @Test
    public void testReserveSeats_SingleSeat_Success() {
        List<SeatId> seatIds = Arrays.asList(new SeatId('A', 1));
        
        List<Seat> reserved = repository.reserveSeats(testShowtime, seatIds);
        
        assertNotNull("Reserved seats should not be null", reserved);
        assertEquals("Should reserve 1 seat", 1, reserved.size());
        assertEquals("Reserved", reserved.get(0).getSeatStatus());
    }
    
    @Test
    public void testReserveSeats_MultipleSeats_Success() {
        List<SeatId> seatIds = Arrays.asList(
            new SeatId('A', 1),
            new SeatId('A', 2),
            new SeatId('A', 3)
        );
        
        List<Seat> reserved = repository.reserveSeats(testShowtime, seatIds);
        
        assertEquals("Should reserve 3 seats", 3, reserved.size());
        for (Seat seat : reserved) {
            assertEquals("All seats should be Reserved", "Reserved", seat.getSeatStatus());
        }
    }
    
    @Test(expected = SeatUnavailableException.class)
    public void testReserveSeats_AlreadyInCart_ThrowsException() {
        List<SeatId> seatIds = Arrays.asList(new SeatId('A', 1));
        
        // Reserve once
        repository.reserveSeats(testShowtime, seatIds);
        
        // Try to reserve again (should fail)
        repository.reserveSeats(testShowtime, seatIds);
    }
    
    @Test(expected = SeatUnavailableException.class)
    public void testReserveSeats_AlreadyConfirmed_ThrowsException() {
        List<SeatId> seatIds = Arrays.asList(new SeatId('B', 1));
        
        // Reserve and confirm
        repository.reserveSeats(testShowtime, seatIds);
        repository.confirmCartReservation(testShowtime, seatIds);
        
        // Try to reserve again (should fail)
        repository.reserveSeats(testShowtime, seatIds);
    }
    
    @Test
    public void testReserveSeats_DifferentShowtimes_Success() {
        Showtime showtime1 = new Showtime(testMovie, 2025, 1, 15, "10:00 AM", standardHall);
        Showtime showtime2 = new Showtime(testMovie, 2025, 1, 15, "02:00 PM", standardHall);
        
        List<SeatId> seatIds = Arrays.asList(new SeatId('A', 1));
        
        // Should be able to reserve same seat for different showtimes
        List<Seat> reserved1 = repository.reserveSeats(showtime1, seatIds);
        List<Seat> reserved2 = repository.reserveSeats(showtime2, seatIds);
        
        assertEquals(1, reserved1.size());
        assertEquals(1, reserved2.size());
    }
    
    // ========== CONFIRM RESERVATION TESTS ==========
    
    @Test
    public void testConfirmCartReservation_Success() {
        List<SeatId> seatIds = Arrays.asList(new SeatId('C', 1));
        
        // Reserve seats
        repository.reserveSeats(testShowtime, seatIds);
        
        // Confirm reservation
        repository.confirmCartReservation(testShowtime, seatIds);
        
        // Verify seats are now booked
        List<Seat> seats = repository.findSeatsByShowtime(testShowtime);
        Optional<Seat> confirmedSeat = seats.stream()
            .filter(s -> s.getId().equals(new SeatId('C', 1)))
            .findFirst();
        
        assertTrue("Confirmed seat should be found", confirmedSeat.isPresent());
        assertEquals("Seat should be Booked", "Booked", confirmedSeat.get().getSeatStatus());
    }
    
    @Test
    public void testConfirmCartReservation_RemovesFromCart() {
        List<SeatId> seatIds = Arrays.asList(new SeatId('D', 1));
        
        // Reserve and confirm
        repository.reserveSeats(testShowtime, seatIds);
        repository.confirmCartReservation(testShowtime, seatIds);
        
        // Try to reserve again - should FAIL because confirmed seats are booked
        try {
            repository.reserveSeats(testShowtime, seatIds);
            fail("Should not be able to reserve confirmed seats");
        } catch (SeatUnavailableException e) {
            // Expected
            assertTrue(true);
        }
    }
    
    // ========== CANCEL RESERVATION TESTS ==========
    
    @Test
    public void testCancelCartReservation_Success() {
        List<SeatId> seatIds = Arrays.asList(new SeatId('E', 1));
        
        // Reserve seats
        repository.reserveSeats(testShowtime, seatIds);
        
        // Cancel reservation
        repository.cancelCartReservation(testShowtime, seatIds);
        
        // Should be able to reserve again
        List<Seat> reserved = repository.reserveSeats(testShowtime, seatIds);
        assertEquals("Should be able to reserve cancelled seats", 1, reserved.size());
    }
    
    @Test
    public void testCancelCartReservation_NonExistent_NoError() {
        List<SeatId> seatIds = Arrays.asList(new SeatId('A', 5));
        
        // Cancel non-existent reservation (should not throw exception)
        repository.cancelCartReservation(testShowtime, seatIds);
        
        // Should still be able to reserve
        List<Seat> reserved = repository.reserveSeats(testShowtime, seatIds);
        assertEquals(1, reserved.size());
    }
    
    // ========== CLEAR ALL CART RESERVATIONS TESTS ==========
    
    @Test
    public void testClearAllCartReservations() {
        // Reserve multiple seats in cart
        List<SeatId> seats1 = Arrays.asList(new SeatId('A', 1), new SeatId('A', 2));
        List<SeatId> seats2 = Arrays.asList(new SeatId('B', 1), new SeatId('B', 2));
        
        repository.reserveSeats(testShowtime, seats1);
        repository.reserveSeats(testShowtime, seats2);
        
        // Clear all cart reservations
        repository.clearAllCartReservations();
        
        // Should be able to reserve all seats again
        List<Seat> reserved1 = repository.reserveSeats(testShowtime, seats1);
        List<Seat> reserved2 = repository.reserveSeats(testShowtime, seats2);
        
        assertEquals(2, reserved1.size());
        assertEquals(2, reserved2.size());
    }
    
    // ========== FIND SEATS BY SHOWTIME TESTS ==========
    
    @Test
    public void testFindSeatsByShowtime_ReturnsAllSeats() {
        List<Seat> seats = repository.findSeatsByShowtime(testShowtime);
        
        assertNotNull("Seats list should not be null", seats);
        
        // Standard hall: 5 rows × 10 columns = 50 seats
        assertEquals("Should return 50 seats for standard hall", 50, seats.size());
    }
    
    @Test
    public void testFindSeatsByShowtime_AllInitiallyAvailable() {
        List<Seat> seats = repository.findSeatsByShowtime(testShowtime);
        
        long availableCount = seats.stream()
            .filter(s -> s.getSeatStatus().equals("Available"))
            .count();
        
        assertEquals("All seats should be initially available", 
                    seats.size(), availableCount);
    }
    
    @Test
    public void testFindSeatsByShowtime_AfterReservation() {
        // Reserve some seats
        List<SeatId> seatIds = Arrays.asList(new SeatId('A', 1), new SeatId('A', 2));
        repository.reserveSeats(testShowtime, seatIds);
        
        List<Seat> seats = repository.findSeatsByShowtime(testShowtime);
        
        long bookedCount = seats.stream()
            .filter(s -> s.getSeatStatus().equals("Booked"))
            .count();
        
        assertEquals("Should have 2 booked seats", 2, bookedCount);
    }
    
    @Test
    public void testFindSeatsByShowtime_AfterConfirmation() {
        // Reserve and confirm
        List<SeatId> seatIds = Arrays.asList(new SeatId('B', 5));
        repository.reserveSeats(testShowtime, seatIds);
        repository.confirmCartReservation(testShowtime, seatIds);
        
        List<Seat> seats = repository.findSeatsByShowtime(testShowtime);
        
        Optional<Seat> confirmedSeat = seats.stream()
            .filter(s -> s.getId().equals(new SeatId('B', 5)))
            .findFirst();
        
        assertTrue("Confirmed seat should be in list", confirmedSeat.isPresent());
        assertEquals("Confirmed seat should be Booked", "Booked", confirmedSeat.get().getSeatStatus());
    }
    
    // ========== INTEGRATION TESTS ==========
    
    @Test
    public void testCompleteBookingFlow() {
        List<SeatId> seatIds = Arrays.asList(
            new SeatId('C', 3),
            new SeatId('C', 4),
            new SeatId('C', 5)
        );
        
        // 1. Reserve seats (add to cart)
        List<Seat> reserved = repository.reserveSeats(testShowtime, seatIds);
        assertEquals(3, reserved.size());
        
        // 2. Verify seats are in cart
        List<Seat> allSeats = repository.findSeatsByShowtime(testShowtime);
        long bookedInCart = allSeats.stream()
            .filter(s -> seatIds.contains(s.getId()))
            .filter(s -> s.getSeatStatus().equals("Booked"))
            .count();
        assertEquals("Seats should be booked in cart", 3, bookedInCart);
        
        // 3. Confirm booking (payment successful)
        repository.confirmCartReservation(testShowtime, seatIds);
        
        // 4. Verify seats are permanently booked
        try {
            repository.reserveSeats(testShowtime, seatIds);
            fail("Should not be able to book confirmed seats");
        } catch (SeatUnavailableException e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testCartCancellationFlow() {
        List<SeatId> seatIds = Arrays.asList(new SeatId('D', 8));
        
        // 1. Reserve seats
        repository.reserveSeats(testShowtime, seatIds);
        
        // 2. Cancel (user abandons cart)
        repository.cancelCartReservation(testShowtime, seatIds);
        
        // 3. Verify seats are available again
        List<Seat> reserved = repository.reserveSeats(testShowtime, seatIds);
        assertEquals("Should be able to reserve cancelled seats", 1, reserved.size());
    }
    
    // ========== EDGE CASE TESTS ==========
    
    @Test
    public void testReserveSeats_EmptyList() {
        List<SeatId> emptySeatIds = new ArrayList<>();
        
        List<Seat> reserved = repository.reserveSeats(testShowtime, emptySeatIds);
        
        assertNotNull("Should return empty list", reserved);
        assertTrue("Reserved list should be empty", reserved.isEmpty());
    }
    
    @Test
    public void testConfirmCartReservation_EmptyList() {
        List<SeatId> emptySeatIds = new ArrayList<>();
        
        // Should not throw exception
        repository.confirmCartReservation(testShowtime, emptySeatIds);
    }
    
    @Test
    public void testCancelCartReservation_EmptyList() {
        List<SeatId> emptySeatIds = new ArrayList<>();
        
        // Should not throw exception
        repository.cancelCartReservation(testShowtime, emptySeatIds);
    }
    
    @Test
    public void testReserveSeats_AllSeatsInHall() {
        // Try to reserve all 50 seats
        List<SeatId> allSeats = new ArrayList<>();
        for (char row = 'A'; row <= 'E'; row++) {
            for (int col = 1; col <= 10; col++) {
                allSeats.add(new SeatId(row, col));
            }
        }
        
        List<Seat> reserved = repository.reserveSeats(testShowtime, allSeats);
        
        assertEquals("Should reserve all 50 seats", 50, reserved.size());
    }
}
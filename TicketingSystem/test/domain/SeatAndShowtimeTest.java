package domain;

import domain.valueobjects.SeatId;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Unit tests for Seat and Showtime classes
 */
public class SeatAndShowtimeTest {
    
    private CinemaHall testHall;
    private Movie testMovie;
    
    @Before
    public void setUp() {
        testHall = new CinemaHall(1, CinemaHall.HALL_TYPE_STANDARD, 5, 10);
        testMovie = new Movie(1, "Dune: Part 1", 2.35, "Denis Villeneuve", "October 22, 2021");
    }
    
    // ========== SEAT TESTS ==========
    
    @Test
    public void testSeat_Constructor() {
        SeatId seatId = new SeatId('A', 1);
        Seat seat = new Seat(seatId, "Single", "Available", testHall);
        
        assertNotNull(seat);
        assertEquals(seatId, seat.getId());
        assertEquals("Single", seat.getSeatType());
        assertEquals("Available", seat.getSeatStatus());
        assertEquals(testHall, seat.getCinemaHall());
    }
    
    @Test
    public void testSeat_GetSeatRow() {
        SeatId seatId = new SeatId('B', 5);
        Seat seat = new Seat(seatId, "Single", "Available", testHall);
        
        assertEquals('B', seat.getSeatRow());
    }
    
    @Test
    public void testSeat_GetSeatCol() {
        SeatId seatId = new SeatId('C', 8);
        Seat seat = new Seat(seatId, "Single", "Available", testHall);
        
        assertEquals(8, seat.getSeatCol());
    }
    
    @Test
    public void testSeat_GetId() {
        SeatId seatId = new SeatId('D', 3);
        Seat seat = new Seat(seatId, "Single", "Available", testHall);
        
        assertEquals(seatId, seat.getId());
        assertEquals('D', seat.getId().getRow());
        assertEquals(3, seat.getId().getColumn());
    }
    
    @Test
    public void testSeat_GetSeatType() {
        SeatId seatId = new SeatId('A', 1);
        Seat seat = new Seat(seatId, "VIP", "Available", testHall);
        
        assertEquals("VIP", seat.getSeatType());
    }
    
    @Test
    public void testSeat_GetSeatStatus() {
        SeatId seatId = new SeatId('A', 1);
        Seat seat = new Seat(seatId, "Single", "Booked", testHall);
        
        assertEquals("Booked", seat.getSeatStatus());
    }
    
    @Test
    public void testSeat_SetSeatStatus() {
        SeatId seatId = new SeatId('A', 1);
        Seat seat = new Seat(seatId, "Single", "Available", testHall);
        
        seat.setSeatStatus("Booked");
        
        assertEquals("Booked", seat.getSeatStatus());
    }
    
    @Test
    public void testSeat_GetCinemaHall() {
        SeatId seatId = new SeatId('A', 1);
        Seat seat = new Seat(seatId, "Single", "Available", testHall);
        
        assertEquals(testHall, seat.getCinemaHall());
    }
    
    @Test
    public void testSeat_CalculatePrice_StandardHall() {
        SeatId seatId = new SeatId('A', 1);
        Seat seat = new Seat(seatId, "Single", "Available", testHall);
        
        double price = seat.calculatePrice();
        
        assertEquals(15.00, price, 0.001);
    }
    
    @Test
    public void testSeat_CalculatePrice_IMAXHall() {
        CinemaHall imaxHall = new CinemaHall(2, CinemaHall.HALL_TYPE_IMAX, 8, 15);
        SeatId seatId = new SeatId('A', 1);
        Seat seat = new Seat(seatId, "Single", "Available", imaxHall);
        
        double price = seat.calculatePrice();
        
        assertEquals(30.00, price, 0.001);
    }
    
    @Test
    public void testSeat_CalculatePrice_LoungeHall() {
        CinemaHall loungeHall = new CinemaHall(3, CinemaHall.HALL_TYPE_LOUNGE, 5, 5);
        SeatId seatId = new SeatId('A', 1);
        Seat seat = new Seat(seatId, "Single", "Available", loungeHall);
        
        double price = seat.calculatePrice();
        
        assertEquals(80.00, price, 0.001);
    }
    
    @Test
    public void testSeat_CalculatePrice_NullHall() {
        SeatId seatId = new SeatId('A', 1);
        Seat seat = new Seat(seatId, "Single", "Available", null);
        
        double price = seat.calculatePrice();
        
        assertEquals(0.0, price, 0.001);
    }
    
    @Test
    public void testSeat_ToString() {
        SeatId seatId = new SeatId('A', 5);
        Seat seat = new Seat(seatId, "Single", "Available", testHall);
        
        String result = seat.toString();
        
        assertTrue(result.contains("A5"));
        assertTrue(result.contains("Single"));
        assertTrue(result.contains("Available"));
    }
    
    @Test
    public void testSeat_DifferentStatuses() {
        SeatId seatId = new SeatId('A', 1);
        Seat available = new Seat(seatId, "Single", "Available", testHall);
        Seat booked = new Seat(seatId, "Single", "Booked", testHall);
        Seat reserved = new Seat(seatId, "Single", "Reserved", testHall);
        
        assertEquals("Available", available.getSeatStatus());
        assertEquals("Booked", booked.getSeatStatus());
        assertEquals("Reserved", reserved.getSeatStatus());
    }
    
    // ========== SHOWTIME TESTS ==========
    
    @Test
    public void testShowtime_Constructor_WithHall() {
        Showtime showtime = new Showtime(testMovie, 2025, 12, 16, "10:00 AM", testHall);
        
        assertNotNull(showtime);
        assertEquals("10:00 AM", showtime.getShowtime());
        assertEquals(LocalDate.of(2025, 12, 16), showtime.getDate());
        assertEquals(testHall, showtime.getCinemaHall());
    }
    
    @Test
    public void testShowtime_Constructor_WithoutHall() {
        Showtime showtime = new Showtime(testMovie, 2025, 12, 16, "10:00 AM");
        
        assertNotNull(showtime);
        assertEquals("10:00 AM", showtime.getShowtime());
        assertEquals(LocalDate.of(2025, 12, 16), showtime.getDate());
        assertNull(showtime.getCinemaHall());
    }
    
    @Test
    public void testShowtime_GetShowtime() {
        Showtime showtime = new Showtime(testMovie, 2025, 12, 16, "02:30 PM", testHall);
        
        assertEquals("02:30 PM", showtime.getShowtime());
    }
    
    @Test
    public void testShowtime_GetMovieName() {
        Showtime showtime = new Showtime(testMovie, 2025, 12, 16, "10:00 AM", testHall);
        
        assertEquals("Dune: Part 1", showtime.getMovieName());
    }
    
    @Test
    public void testShowtime_GetMovieName_NullMovie() {
        Showtime showtime = new Showtime(null, 2025, 12, 16, "10:00 AM", testHall);
        
        assertEquals("N/A", showtime.getMovieName());
    }
    
    @Test
    public void testShowtime_GetCinemaHall() {
        Showtime showtime = new Showtime(testMovie, 2025, 12, 16, "10:00 AM", testHall);
        
        assertEquals(testHall, showtime.getCinemaHall());
    }
    
    @Test
    public void testShowtime_GetHallId() {
        Showtime showtime = new Showtime(testMovie, 2025, 12, 16, "10:00 AM", testHall);
        
        assertEquals(1, showtime.getHallId());
    }
    
    @Test(expected = IllegalStateException.class)
    public void testShowtime_GetHallId_NullHall_ThrowsException() {
        Showtime showtime = new Showtime(testMovie, 2025, 12, 16, "10:00 AM");
        
        showtime.getHallId();
    }
    
    @Test
    public void testShowtime_GetDate() {
        Showtime showtime = new Showtime(testMovie, 2025, 12, 16, "10:00 AM", testHall);
        
        assertEquals(LocalDate.of(2025, 12, 16), showtime.getDate());
    }
    
    @Test
    public void testShowtime_Time() {
        Showtime showtime = new Showtime(testMovie, 2025, 12, 16, "03:45 PM", testHall);
        
        assertEquals("03:45 PM", showtime.time());
    }
    
    @Test
    public void testShowtime_GetHallType() {
        Showtime showtime = new Showtime(testMovie, 2025, 12, 16, "10:00 AM", testHall);
        
        assertEquals(CinemaHall.HALL_TYPE_STANDARD, showtime.getHallType());
    }
    
    @Test
    public void testShowtime_GetHallType_NullHall() {
        Showtime showtime = new Showtime(testMovie, 2025, 12, 16, "10:00 AM");
        
        assertNull(showtime.getHallType());
    }
    
    @Test
    public void testShowtime_GenerateShowtimes() {
        Showtime showtime = new Showtime(null, 2025, 1, 1, null, null);
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(14, 0);
        int interval = 100; // 1 hour 40 minutes
        
        List<LocalTime> times = showtime.generateShowtimes(startTime, endTime, interval);
        
        assertNotNull(times);
        assertFalse(times.isEmpty());
        assertTrue(times.contains(LocalTime.of(10, 0)));
        assertTrue(times.contains(LocalTime.of(11, 40)));
        assertTrue(times.contains(LocalTime.of(13, 20)));
    }
    
    @Test
    public void testShowtime_GenerateShowtimes_ShortInterval() {
        Showtime showtime = new Showtime(null, 2025, 1, 1, null, null);
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(11, 0);
        int interval = 30;
        
        List<LocalTime> times = showtime.generateShowtimes(startTime, endTime, interval);
        
        assertEquals(3, times.size()); // 10:00, 10:30, 11:00
    }
    
    @Test
    public void testShowtime_ToString() {
        Showtime showtime = new Showtime(testMovie, 2025, 12, 16, "10:00 AM", testHall);
        
        String result = showtime.toString();
        
        assertTrue(result.contains("Dune: Part 1"));
        assertTrue(result.contains("2025-12-16"));
        assertTrue(result.contains("10:00 AM"));
        assertTrue(result.contains("Standard"));
    }
    
    @Test
    public void testShowtime_DifferentDates() {
        Showtime s1 = new Showtime(testMovie, 2025, 1, 1, "10:00 AM", testHall);
        Showtime s2 = new Showtime(testMovie, 2025, 12, 31, "10:00 AM", testHall);
        
        assertEquals(LocalDate.of(2025, 1, 1), s1.getDate());
        assertEquals(LocalDate.of(2025, 12, 31), s2.getDate());
    }
    
    @Test
    public void testShowtime_LeapYear() {
        Showtime showtime = new Showtime(testMovie, 2024, 2, 29, "10:00 AM", testHall);
        
        assertEquals(LocalDate.of(2024, 2, 29), showtime.getDate());
    }
}
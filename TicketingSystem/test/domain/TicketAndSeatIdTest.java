package domain;

import domain.valueobjects.SeatId;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Unit tests for Ticket and SeatId classes
 */
public class TicketAndSeatIdTest {
    
    private Movie testMovie;
    private CinemaHall testHall;
    private Showtime testShowtime;
    private ArrayList<Seat> testSeats;
    
    @Before
    public void setUp() {
        testMovie = new Movie(1, "Dune: Part 1", 2.35, "Denis Villeneuve", "October 22, 2021");
        testHall = new CinemaHall(1, CinemaHall.HALL_TYPE_STANDARD, 5, 10);
        testShowtime = new Showtime(testMovie, 2025, 12, 16, "10:00 AM", testHall);
        
        testSeats = new ArrayList<>();
        testSeats.add(new Seat(new SeatId('A', 1), "Single", "Booked", testHall));
        testSeats.add(new Seat(new SeatId('A', 2), "Single", "Booked", testHall));
    }
    
    // ========== TICKET TESTS ==========
    
    @Test
    public void testTicket_Constructor_WithParameters() {
        Ticket ticket = new Ticket(testShowtime, 2, testHall, testSeats);
        
        assertNotNull(ticket);
        assertEquals(testShowtime, ticket.getShowtime());
        assertEquals(2, ticket.getTicketAmt());
        assertEquals(testHall, ticket.getCinemaHall());
        assertEquals(testSeats, ticket.getSeat());
        assertTrue(ticket.getTicketID() > 0);
    }
    
    @Test
    public void testTicket_Constructor_Default() {
        Ticket ticket = new Ticket();
        
        assertNotNull(ticket);
        assertEquals(0, ticket.getTicketAmt());
        assertNull(ticket.getShowtime());
        assertNull(ticket.getCinemaHall());
        assertNotNull(ticket.getSeat());
        assertTrue(ticket.getSeat().isEmpty());
    }
    
    @Test
    public void testTicket_GetTicketID() {
        Ticket ticket = new Ticket(testShowtime, 2, testHall, testSeats);
        
        assertTrue(ticket.getTicketID() > 0);
    }
    
    @Test
    public void testTicket_GetShowtime() {
        Ticket ticket = new Ticket(testShowtime, 2, testHall, testSeats);
        
        assertEquals(testShowtime, ticket.getShowtime());
    }
    
    @Test
    public void testTicket_GetSchedule() {
        Ticket ticket = new Ticket(testShowtime, 2, testHall, testSeats);
        
        assertEquals(LocalDate.of(2025, 12, 16), ticket.getSchedule());
    }
    
    @Test
    public void testTicket_Time() {
        Ticket ticket = new Ticket(testShowtime, 2, testHall, testSeats);
        
        assertEquals("10:00 AM", ticket.time());
    }
    
    @Test
    public void testTicket_GetTicketAmt() {
        Ticket ticket = new Ticket(testShowtime, 3, testHall, testSeats);
        
        assertEquals(3, ticket.getTicketAmt());
    }
    
    @Test
    public void testTicket_GetCinemaHall() {
        Ticket ticket = new Ticket(testShowtime, 2, testHall, testSeats);
        
        assertEquals(testHall, ticket.getCinemaHall());
    }
    
    @Test
    public void testTicket_GetHallType() {
        Ticket ticket = new Ticket(testShowtime, 2, testHall, testSeats);
        
        assertEquals(CinemaHall.HALL_TYPE_STANDARD, ticket.getHallType());
    }
    
    @Test
    public void testTicket_GetHallId() {
        Ticket ticket = new Ticket(testShowtime, 2, testHall, testSeats);
        
        assertEquals(1, ticket.getHallId());
    }
    
    @Test
    public void testTicket_GetSeat() {
        Ticket ticket = new Ticket(testShowtime, 2, testHall, testSeats);
        
        assertEquals(testSeats, ticket.getSeat());
        assertEquals(2, ticket.getSeat().size());
    }
    
    @Test
    public void testTicket_TicketPrice_Standard() {
        Ticket ticket = new Ticket(testShowtime, 2, testHall, testSeats);
        
        double unitPrice = ticket.ticketPrice();
        
        assertEquals(15.00, unitPrice, 0.001);
    }
    
    @Test
    public void testTicket_TicketPrice_IMAX() {
        CinemaHall imaxHall = new CinemaHall(2, CinemaHall.HALL_TYPE_IMAX, 8, 15);
        ArrayList<Seat> imaxSeats = new ArrayList<>();
        imaxSeats.add(new Seat(new SeatId('A', 1), "Single", "Booked", imaxHall));
        Showtime imaxShowtime = new Showtime(testMovie, 2025, 12, 16, "10:00 AM", imaxHall);
        
        Ticket ticket = new Ticket(imaxShowtime, 1, imaxHall, imaxSeats);
        
        assertEquals(30.00, ticket.ticketPrice(), 0.001);
    }
    
    @Test
    public void testTicket_TicketPrice_Lounge() {
        CinemaHall loungeHall = new CinemaHall(3, CinemaHall.HALL_TYPE_LOUNGE, 5, 5);
        ArrayList<Seat> loungeSeats = new ArrayList<>();
        loungeSeats.add(new Seat(new SeatId('A', 1), "Single", "Booked", loungeHall));
        Showtime loungeShowtime = new Showtime(testMovie, 2025, 12, 16, "10:00 AM", loungeHall);
        
        Ticket ticket = new Ticket(loungeShowtime, 1, loungeHall, loungeSeats);
        
        assertEquals(80.00, ticket.ticketPrice(), 0.001);
    }
    
    @Test
    public void testTicket_TicketPrice_EmptySeats() {
        Ticket ticket = new Ticket(testShowtime, 0, testHall, new ArrayList<>());
        
        assertEquals(0.0, ticket.ticketPrice(), 0.001);
    }
    
    @Test
    public void testTicket_TicketPrice_NullSeats() {
        Ticket ticket = new Ticket(testShowtime, 0, testHall, null);
        
        assertEquals(0.0, ticket.ticketPrice(), 0.001);
    }
    
    @Test
    public void testTicket_GetTotalPrice() {
        Ticket ticket = new Ticket(testShowtime, 2, testHall, testSeats);
        
        double totalPrice = ticket.getTotalPrice();
        
        assertEquals(30.00, totalPrice, 0.001); // 2 seats × 15.00
    }
    
    @Test
    public void testTicket_GetTotalPrice_MultipleSeats() {
        ArrayList<Seat> manySeats = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            manySeats.add(new Seat(new SeatId('A', i), "Single", "Booked", testHall));
        }
        
        Ticket ticket = new Ticket(testShowtime, 5, testHall, manySeats);
        
        assertEquals(75.00, ticket.getTotalPrice(), 0.001); // 5 × 15.00
    }
    
    @Test
    public void testTicket_GetMovieName() {
        Ticket ticket = new Ticket(testShowtime, 2, testHall, testSeats);
        
        assertEquals("Dune: Part 1", ticket.getMovieName());
    }
    
    @Test
    public void testTicket_ToString() {
        Ticket ticket = new Ticket(testShowtime, 2, testHall, testSeats);
        
        String result = ticket.toString();
        
        assertTrue(result.contains("Ticket ID"));
        assertTrue(result.contains("Ticket amount"));
        assertTrue(result.contains("2"));
    }
    
    @Test
    public void testTicket_AutoIncrementID() {
        Ticket t1 = new Ticket(testShowtime, 2, testHall, testSeats);
        Ticket t2 = new Ticket(testShowtime, 2, testHall, testSeats);
        
        assertTrue(t2.getTicketID() > t1.getTicketID());
        assertEquals(t1.getTicketID() + 1, t2.getTicketID());
    }
    
    // ========== SEATID VALUE OBJECT TESTS ==========
    
    @Test
    public void testSeatId_Constructor() {
        SeatId seatId = new SeatId('B', 5);
        
        assertNotNull(seatId);
        assertEquals('B', seatId.getRow());
        assertEquals(5, seatId.getColumn());
    }
    
    @Test
    public void testSeatId_GetRow() {
        SeatId seatId = new SeatId('C', 3);
        
        assertEquals('C', seatId.getRow());
    }
    
    @Test
    public void testSeatId_GetColumn() {
        SeatId seatId = new SeatId('D', 7);
        
        assertEquals(7, seatId.getColumn());
    }
    
    @Test
    public void testSeatId_ToDisplayString() {
        SeatId seatId = new SeatId('A', 10);
        
        assertEquals("A10", seatId.toDisplayString());
    }
    
    @Test
    public void testSeatId_ToString() {
        SeatId seatId = new SeatId('E', 2);
        
        assertEquals("E2", seatId.toString());
    }
    
    @Test
    public void testSeatId_Equals_SameObject() {
        SeatId seatId = new SeatId('A', 1);
        
        assertTrue(seatId.equals(seatId));
    }
    
    @Test
    public void testSeatId_Equals_EqualObjects() {
        SeatId seatId1 = new SeatId('A', 1);
        SeatId seatId2 = new SeatId('A', 1);
        
        assertTrue(seatId1.equals(seatId2));
    }
    
    @Test
    public void testSeatId_Equals_DifferentRow() {
        SeatId seatId1 = new SeatId('A', 1);
        SeatId seatId2 = new SeatId('B', 1);
        
        assertFalse(seatId1.equals(seatId2));
    }
    
    @Test
    public void testSeatId_Equals_DifferentColumn() {
        SeatId seatId1 = new SeatId('A', 1);
        SeatId seatId2 = new SeatId('A', 2);
        
        assertFalse(seatId1.equals(seatId2));
    }
    
    @Test
    public void testSeatId_Equals_Null() {
        SeatId seatId = new SeatId('A', 1);
        
        assertFalse(seatId.equals(null));
    }
    
    @Test
    public void testSeatId_Equals_DifferentClass() {
        SeatId seatId = new SeatId('A', 1);
        String notASeatId = "A1";
        
        assertFalse(seatId.equals(notASeatId));
    }
    
    @Test
    public void testSeatId_HashCode_Consistent() {
        SeatId seatId = new SeatId('A', 1);
        
        int hash1 = seatId.hashCode();
        int hash2 = seatId.hashCode();
        
        assertEquals(hash1, hash2);
    }
    
    @Test
    public void testSeatId_HashCode_EqualObjects() {
        SeatId seatId1 = new SeatId('A', 1);
        SeatId seatId2 = new SeatId('A', 1);
        
        assertEquals(seatId1.hashCode(), seatId2.hashCode());
    }
    
    @Test
    public void testSeatId_HashCode_DifferentObjects() {
        SeatId seatId1 = new SeatId('A', 1);
        SeatId seatId2 = new SeatId('B', 2);
        
        assertNotEquals(seatId1.hashCode(), seatId2.hashCode());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSeatId_InvalidRow_TooLow() {
        new SeatId('@', 1); // '@' is before 'A'
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSeatId_InvalidRow_TooHigh() {
        new SeatId('[', 1); // '[' is after 'Z'
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSeatId_InvalidColumn_Zero() {
        new SeatId('A', 0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSeatId_InvalidColumn_Negative() {
        new SeatId('A', -1);
    }
    
    @Test
    public void testSeatId_ValidBoundaries() {
        SeatId minSeat = new SeatId('A', 1);
        SeatId maxSeat = new SeatId('Z', 100);
        
        assertEquals('A', minSeat.getRow());
        assertEquals(1, minSeat.getColumn());
        assertEquals('Z', maxSeat.getRow());
        assertEquals(100, maxSeat.getColumn());
    }
}

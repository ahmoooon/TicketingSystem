package application.services;

import application.dto.BookingRequest;
import application.dto.BookingResult;
import domain.*;
import domain.repositories.MovieRepository;
import domain.repositories.SeatRepository;
import domain.repositories.ShowtimeRepository;
import domain.valueobjects.SeatId;
import infrastructure.repositories.SeatUnavailableException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BookingServiceTest {
    
    @Mock
    private MovieRepository movieRepository;
    
    @Mock
    private ShowtimeRepository showtimeRepository;
    
    @Mock
    private SeatRepository seatRepository;
    
    private BookingService bookingService;
    
    // Test data
    private Movie testMovie;
    private Showtime testShowtime;
    private CinemaHall testHall;
    private List<SeatId> testSeatIds;
    private List<Seat> testSeats;
    
    @Before
    public void setUp() {
        // Initialize the service with mocked dependencies
        bookingService = new BookingService(movieRepository, showtimeRepository, seatRepository);
        
        // Setup test data
        testMovie = new Movie(1, "Dune: Part 1", 2.35, "Denis Villeneuve", "October 22, 2021");
        testHall = new CinemaHall(1, CinemaHall.HALL_TYPE_STANDARD, 5, 10);
        testShowtime = new Showtime(testMovie, 2025, 12, 16, "10:00 AM", testHall);
        
        // Create test seat IDs
        testSeatIds = Arrays.asList(
            new SeatId('A', 1),
            new SeatId('A', 2)
        );
        
        // Create test seats
        testSeats = new ArrayList<>();
        for (SeatId seatId : testSeatIds) {
            testSeats.add(new Seat(seatId, "Single", "Available", testHall));
        }
    }
    
    // ========== SUCCESS CASES ==========
    
    @Test
    public void testBookTickets_Success() {
        // Arrange
        LocalDate testDate = LocalDate.of(2025, 12, 16);
        BookingRequest request = new BookingRequest(
            1, testDate, "10:00 AM", 1, testSeatIds
        );
        
        // Mock repository responses
        when(movieRepository.findById(1)).thenReturn(Optional.of(testMovie));
        when(showtimeRepository.findAvailableShowtime(testMovie, testDate, "10:00 AM", 1))
            .thenReturn(Optional.of(testShowtime));
        when(seatRepository.reserveSeats(testShowtime, testSeatIds))
            .thenReturn(testSeats);
        
        // Act
        BookingResult result = bookingService.bookTickets(request);
        
        // Assert
        assertNotNull("Result should not be null", result);
        assertEquals("Should return correct movie", testMovie, result.getMovie());
        assertEquals("Should return correct showtime", testShowtime, result.getShowtime());
        assertEquals("Should return correct number of seats", 2, result.getSeats().size());
        
        // Verify interactions
        verify(movieRepository, times(1)).findById(1);
        verify(showtimeRepository, times(1)).findAvailableShowtime(testMovie, testDate, "10:00 AM", 1);
        verify(seatRepository, times(1)).reserveSeats(testShowtime, testSeatIds);
    }
    
    @Test
    public void testBookTickets_SingleSeat_Success() {
        // Arrange
        List<SeatId> singleSeat = Arrays.asList(new SeatId('B', 5));
        LocalDate testDate = LocalDate.of(2025, 12, 16);
        BookingRequest request = new BookingRequest(
            1, testDate, "10:00 AM", 1, singleSeat
        );
        
        List<Seat> singleSeatList = Arrays.asList(
            new Seat(new SeatId('B', 5), "Single", "Available", testHall)
        );
        
        when(movieRepository.findById(1)).thenReturn(Optional.of(testMovie));
        when(showtimeRepository.findAvailableShowtime(any(), any(), any(), anyInt()))
            .thenReturn(Optional.of(testShowtime));
        when(seatRepository.reserveSeats(any(), anyList()))
            .thenReturn(singleSeatList);
        
        // Act
        BookingResult result = bookingService.bookTickets(request);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getSeats().size());
    }
    
    @Test
    public void testGetAvailableMovies_Success() {
        // Arrange
        List<Movie> expectedMovies = Arrays.asList(
            testMovie,
            new Movie(2, "Blade Runner 2049", 2.43, "Denis Villeneuve", "October 5, 2017")
        );
        when(movieRepository.findAll()).thenReturn(expectedMovies);
        
        // Act
        List<Movie> result = bookingService.getAvailableMovies();
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Dune: Part 1", result.get(0).getMovieName());
        verify(movieRepository, times(1)).findAll();
    }
    
    @Test
    public void testGetMovieById_Success() {
        // Arrange
        when(movieRepository.findById(1)).thenReturn(Optional.of(testMovie));
        
        // Act
        Movie result = bookingService.getMovieById(1);
        
        // Assert
        assertNotNull(result);
        assertEquals("Dune: Part 1", result.getMovieName());
        assertEquals(1, result.getId());
        verify(movieRepository, times(1)).findById(1);
    }
    
    @Test
    public void testGetAvailableShowtimeDates_Success() {
        // Arrange
        List<Showtime> expectedShowtimes = Arrays.asList(
            testShowtime,
            new Showtime(testMovie, 2025, 12, 17, "10:00 AM", testHall)
        );
        when(showtimeRepository.findAvailableDates(testMovie)).thenReturn(expectedShowtimes);
        
        // Act
        List<Showtime> result = bookingService.getAvailableShowtimeDates(testMovie);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(showtimeRepository, times(1)).findAvailableDates(testMovie);
    }
    
    // ========== VALIDATION FAILURE CASES ==========
    
    @Test(expected = IllegalArgumentException.class)
    public void testBookTickets_NoSeatsSelected_ThrowsException() {
        // Arrange
        LocalDate testDate = LocalDate.of(2025, 12, 16);
        BookingRequest request = new BookingRequest(
            1, testDate, "10:00 AM", 1, new ArrayList<>() // Empty seat list
        );
        
        // Act
        bookingService.bookTickets(request);
        
        // Assert - Exception expected
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testBookTickets_NullSeatList_ThrowsException() {
        // Arrange
        LocalDate testDate = LocalDate.of(2025, 12, 16);
        BookingRequest request = new BookingRequest(
            1, testDate, "10:00 AM", 1, null // Null seat list
        );
        
        // Act
        bookingService.bookTickets(request);
        
        // Assert - Exception expected
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testBookTickets_InvalidMovieId_ThrowsException() {
        // Arrange
        LocalDate testDate = LocalDate.of(2025, 12, 16);
        BookingRequest request = new BookingRequest(
            0, testDate, "10:00 AM", 1, testSeatIds // Invalid movie ID (0)
        );
        
        // Act
        bookingService.bookTickets(request);
        
        // Assert - Exception expected
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testBookTickets_DuplicateSeats_ThrowsException() {
        // Arrange
        List<SeatId> duplicateSeats = Arrays.asList(
            new SeatId('A', 1),
            new SeatId('A', 1),  // Duplicate!
            new SeatId('A', 2)
        );
        LocalDate testDate = LocalDate.of(2025, 12, 16);
        BookingRequest request = new BookingRequest(
            1, testDate, "10:00 AM", 1, duplicateSeats
        );
        
        // Act
        bookingService.bookTickets(request);
        
        // Assert - Exception expected with message about duplicates
    }
    
    // ========== REPOSITORY FAILURE CASES ==========
    
    @Test(expected = IllegalArgumentException.class)
    public void testBookTickets_MovieNotFound_ThrowsException() {
        // Arrange
        LocalDate testDate = LocalDate.of(2025, 12, 16);
        BookingRequest request = new BookingRequest(
            999, testDate, "10:00 AM", 1, testSeatIds
        );
        
        when(movieRepository.findById(999)).thenReturn(Optional.empty());
        
        // Act
        bookingService.bookTickets(request);
        
        // Assert - Exception expected
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testBookTickets_ShowtimeNotAvailable_ThrowsException() {
        // Arrange
        LocalDate testDate = LocalDate.of(2025, 12, 16);
        BookingRequest request = new BookingRequest(
            1, testDate, "10:00 AM", 1, testSeatIds
        );
        
        when(movieRepository.findById(1)).thenReturn(Optional.of(testMovie));
        when(showtimeRepository.findAvailableShowtime(testMovie, testDate, "10:00 AM", 1))
            .thenReturn(Optional.empty());
        
        // Act
        bookingService.bookTickets(request);
        
        // Assert - Exception expected
    }
    
    @Test(expected = SeatUnavailableException.class)
    public void testBookTickets_SeatsAlreadyBooked_ThrowsException() {
        // Arrange
        LocalDate testDate = LocalDate.of(2025, 12, 16);
        BookingRequest request = new BookingRequest(
            1, testDate, "10:00 AM", 1, testSeatIds
        );
        
        when(movieRepository.findById(1)).thenReturn(Optional.of(testMovie));
        when(showtimeRepository.findAvailableShowtime(testMovie, testDate, "10:00 AM", 1))
            .thenReturn(Optional.of(testShowtime));
        when(seatRepository.reserveSeats(testShowtime, testSeatIds))
            .thenThrow(new SeatUnavailableException("Seats A1, A2 are already booked"));
        
        // Act
        bookingService.bookTickets(request);
        
        // Assert - SeatUnavailableException expected
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetMovieById_MovieNotFound_ThrowsException() {
        // Arrange
        when(movieRepository.findById(999)).thenReturn(Optional.empty());
        
        // Act
        bookingService.getMovieById(999);
        
        // Assert - Exception expected
    }
    
    // ========== EDGE CASES ==========
    
    @Test
    public void testGetAvailableMovies_EmptyList() {
        // Arrange
        when(movieRepository.findAll()).thenReturn(new ArrayList<>());
        
        // Act
        List<Movie> result = bookingService.getAvailableMovies();
        
        // Assert
        assertNotNull(result);
        assertTrue("Movie list should be empty", result.isEmpty());
    }
    
    @Test
    public void testGetAvailableShowtimeDates_EmptyList() {
        // Arrange
        when(showtimeRepository.findAvailableDates(testMovie)).thenReturn(new ArrayList<>());
        
        // Act
        List<Showtime> result = bookingService.getAvailableShowtimeDates(testMovie);
        
        // Assert
        assertNotNull(result);
        assertTrue("Showtime list should be empty", result.isEmpty());
    }
    
    @Test
    public void testBookTickets_MaximumSeats_Success() {
        // Arrange - Book 10 seats
        List<SeatId> manySeatIds = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            manySeatIds.add(new SeatId('A', i));
        }
        
        List<Seat> manySeats = new ArrayList<>();
        for (SeatId seatId : manySeatIds) {
            manySeats.add(new Seat(seatId, "Single", "Available", testHall));
        }
        
        LocalDate testDate = LocalDate.of(2025, 12, 16);
        BookingRequest request = new BookingRequest(
            1, testDate, "10:00 AM", 1, manySeatIds
        );
        
        when(movieRepository.findById(1)).thenReturn(Optional.of(testMovie));
        when(showtimeRepository.findAvailableShowtime(any(), any(), any(), anyInt()))
            .thenReturn(Optional.of(testShowtime));
        when(seatRepository.reserveSeats(any(), eq(manySeatIds)))
            .thenReturn(manySeats);
        
        // Act
        BookingResult result = bookingService.bookTickets(request);
        
        // Assert
        assertNotNull(result);
        assertEquals(10, result.getSeats().size());
    }
    
    // ========== INTEGRATION-LIKE TESTS ==========
    
    @Test
    public void testCompleteBookingFlow_Success() {
        // Arrange - Simulate complete booking flow
        LocalDate testDate = LocalDate.of(2025, 12, 16);
        
        // Step 1: Get available movies
        List<Movie> movies = Arrays.asList(testMovie);
        when(movieRepository.findAll()).thenReturn(movies);
        
        // Step 2: Get available dates
        List<Showtime> showtimes = Arrays.asList(testShowtime);
        when(showtimeRepository.findAvailableDates(testMovie)).thenReturn(showtimes);
        
        // Step 3: Book tickets
        BookingRequest request = new BookingRequest(
            1, testDate, "10:00 AM", 1, testSeatIds
        );
        when(movieRepository.findById(1)).thenReturn(Optional.of(testMovie));
        when(showtimeRepository.findAvailableShowtime(testMovie, testDate, "10:00 AM", 1))
            .thenReturn(Optional.of(testShowtime));
        when(seatRepository.reserveSeats(testShowtime, testSeatIds))
            .thenReturn(testSeats);
        
        // Act
        List<Movie> availableMovies = bookingService.getAvailableMovies();
        List<Showtime> availableDates = bookingService.getAvailableShowtimeDates(testMovie);
        BookingResult result = bookingService.bookTickets(request);
        
        // Assert
        assertEquals(1, availableMovies.size());
        assertEquals(1, availableDates.size());
        assertNotNull(result);
        assertEquals(2, result.getSeats().size());
    }
    
    // ========== NEGATIVE SCENARIO TESTS ==========
    
    @Test
    public void testBookTickets_PartialSeatAvailability_ThrowsException() {
        // Arrange - User tries to book A1, A2, but only A2 is available
        LocalDate testDate = LocalDate.of(2025, 12, 16);
        BookingRequest request = new BookingRequest(
            1, testDate, "10:00 AM", 1, testSeatIds
        );
        
        when(movieRepository.findById(1)).thenReturn(Optional.of(testMovie));
        when(showtimeRepository.findAvailableShowtime(testMovie, testDate, "10:00 AM", 1))
            .thenReturn(Optional.of(testShowtime));
        when(seatRepository.reserveSeats(testShowtime, testSeatIds))
            .thenThrow(new SeatUnavailableException("Seat A1 is already booked"));
        
        // Act & Assert
        try {
            bookingService.bookTickets(request);
            fail("Should throw SeatUnavailableException");
        } catch (SeatUnavailableException e) {
            assertTrue(e.getMessage().contains("A1"));
        }
    }
    
    @Test
    public void testBookTickets_DifferentHallTypes_Success() {
        // Arrange - Test IMAX hall
        CinemaHall imaxHall = new CinemaHall(4, CinemaHall.HALL_TYPE_IMAX, 8, 15);
        Showtime imaxShowtime = new Showtime(testMovie, 2025, 12, 16, "02:00 PM", imaxHall);
        
        LocalDate testDate = LocalDate.of(2025, 12, 16);
        BookingRequest request = new BookingRequest(
            1, testDate, "02:00 PM", 4, testSeatIds
        );
        
        when(movieRepository.findById(1)).thenReturn(Optional.of(testMovie));
        when(showtimeRepository.findAvailableShowtime(testMovie, testDate, "02:00 PM", 4))
            .thenReturn(Optional.of(imaxShowtime));
        when(seatRepository.reserveSeats(eq(imaxShowtime), anyList()))
            .thenReturn(testSeats);
        
        // Act
        BookingResult result = bookingService.bookTickets(request);
        
        // Assert
        assertNotNull(result);
        assertEquals(CinemaHall.HALL_TYPE_IMAX, result.getShowtime().getHallType());
    }
}
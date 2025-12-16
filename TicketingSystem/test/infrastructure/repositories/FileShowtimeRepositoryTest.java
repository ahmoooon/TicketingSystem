package infrastructure.repositories;

import domain.CinemaHall;
import domain.Movie;
import domain.Showtime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class FileShowtimeRepositoryTest {
    
    private FileShowtimeRepository repository;
    private Movie testMovie;
    private static final String TEST_HALLS_FILE = "halls_showtime_test.json";
    
    @Before
    public void setUp() {
        // Clean test file
        DataFileHandler.saveToJsonFile(java.util.Collections.emptyList(), TEST_HALLS_FILE);
        
        // Initialize repository
        repository = new FileShowtimeRepository();
        
        // Setup test movie
        testMovie = new Movie(1, "Test Movie", 2.35, "Test Director", "2025-01-01");
    }
    
    @After
    public void tearDown() {
        DataFileHandler.saveToJsonFile(java.util.Collections.emptyList(), TEST_HALLS_FILE);
    }
    
    // ========== FIND AVAILABLE DATES TESTS ==========
    
    @Test
    public void testFindAvailableDates_ReturnsThreeDates() {
        List<Showtime> dates = repository.findAvailableDates(testMovie);
        
        assertNotNull("Dates list should not be null", dates);
        assertEquals("Should return 3 future dates", 3, dates.size());
    }
    
    @Test
    public void testFindAvailableDates_AllInFuture() {
        LocalDate today = LocalDate.now();
        List<Showtime> dates = repository.findAvailableDates(testMovie);
        
        for (Showtime showtime : dates) {
            assertTrue("All dates should be in the future", 
                      showtime.getDate().isAfter(today));
        }
    }
    
    @Test
    public void testFindAvailableDates_SequentialDates() {
        List<Showtime> dates = repository.findAvailableDates(testMovie);
        
        assertEquals("Should have 3 dates", 3, dates.size());
        
        // Verify dates are sequential
        LocalDate date1 = dates.get(0).getDate();
        LocalDate date2 = dates.get(1).getDate();
        LocalDate date3 = dates.get(2).getDate();
        
        assertEquals("Second date should be 1 day after first", 
                    date1.plusDays(1), date2);
        assertEquals("Third date should be 1 day after second", 
                    date2.plusDays(1), date3);
    }
    
    @Test
    public void testFindAvailableDates_ContainsMovieInfo() {
        List<Showtime> dates = repository.findAvailableDates(testMovie);
        
        for (Showtime showtime : dates) {
            assertEquals("Should contain correct movie name", 
                        testMovie.getMovieName(), showtime.getMovieName());
        }
    }
    
    @Test
    public void testFindAvailableDates_DifferentMovies() {
        Movie movie1 = new Movie(1, "Movie 1", 2.0, "Director 1", "2025-01-01");
        Movie movie2 = new Movie(2, "Movie 2", 2.5, "Director 2", "2025-01-02");
        
        List<Showtime> dates1 = repository.findAvailableDates(movie1);
        List<Showtime> dates2 = repository.findAvailableDates(movie2);
        
        assertEquals(3, dates1.size());
        assertEquals(3, dates2.size());
        
        // Dates should be the same, only movie differs
        assertEquals("Same dates for different movies", 
                    dates1.get(0).getDate(), dates2.get(0).getDate());
    }
    
    // ========== FIND AVAILABLE SHOWTIME TESTS ==========
    
    @Test
    public void testFindAvailableShowtime_ValidTime_Success() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        String validTime = "10:00 AM";
        int hallId = 1;
        
        Optional<Showtime> showtime = repository.findAvailableShowtime(
            testMovie, futureDate, validTime, hallId
        );
        
        assertTrue("Should find valid showtime", showtime.isPresent());
        assertEquals("Date should match", futureDate, showtime.get().getDate());
        assertEquals("Time should match", validTime, showtime.get().getShowtime());
    }
    
    @Test
    public void testFindAvailableShowtime_AllValidTimes() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        int hallId = 1;
        
        String[] validTimes = {
            "10:00 AM", "11:40 AM", "01:20 PM", "03:00 PM",
            "04:40 PM", "06:20 PM", "08:00 PM"
        };
        
        for (String time : validTimes) {
            Optional<Showtime> showtime = repository.findAvailableShowtime(
                testMovie, futureDate, time, hallId
            );
            assertTrue("Should find showtime for " + time, showtime.isPresent());
        }
    }
    
    @Test
    public void testFindAvailableShowtime_InvalidTime_Failure() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        String invalidTime = "09:30 AM"; // Not in generated list
        int hallId = 1;
        
        Optional<Showtime> showtime = repository.findAvailableShowtime(
            testMovie, futureDate, invalidTime, hallId
        );
        
        assertFalse("Should not find invalid showtime", showtime.isPresent());
    }
    
    @Test
    public void testFindAvailableShowtime_ContainsHallInfo() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        String validTime = "10:00 AM";
        int hallId = 1;
        
        Optional<Showtime> showtime = repository.findAvailableShowtime(
            testMovie, futureDate, validTime, hallId
        );
        
        assertTrue("Should find showtime", showtime.isPresent());
        assertNotNull("Should have hall info", showtime.get().getCinemaHall());
        assertEquals("Hall ID should match", hallId, showtime.get().getHallId());
    }
    
    @Test
    public void testFindAvailableShowtime_DifferentHallTypes() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        String validTime = "10:00 AM";
        
        // Test different hall IDs (which have different types)
        Optional<Showtime> standard = repository.findAvailableShowtime(
            testMovie, futureDate, validTime, 1
        );
        Optional<Showtime> imax = repository.findAvailableShowtime(
            testMovie, futureDate, validTime, 4
        );
        
        assertTrue("Should find Standard hall showtime", standard.isPresent());
        assertTrue("Should find IMAX hall showtime", imax.isPresent());
        
        assertNotEquals("Hall types should be different",
                       standard.get().getHallType(), 
                       imax.get().getHallType());
    }
    
    @Test
    public void testFindAvailableShowtime_InvalidHallId() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        String validTime = "10:00 AM";
        int invalidHallId = 999;
        
        try {
            repository.findAvailableShowtime(
                testMovie, futureDate, validTime, invalidHallId
            );
            fail("Should throw exception for invalid hall ID");
        } catch (IllegalArgumentException e) {
            assertTrue("Should throw IllegalArgumentException", true);
        }
    }
    
    // ========== HALL CONFIGURATION TESTS ==========
    
    @Test
    public void testHallConfiguration_LoadedCorrectly() {
        // Verify default halls are loaded
        LocalDate futureDate = LocalDate.now().plusDays(1);
        String validTime = "10:00 AM";
        
        // Try to get showtimes for all default hall IDs
        for (int hallId = 1; hallId <= 6; hallId++) {
            try {
                Optional<Showtime> showtime = repository.findAvailableShowtime(
                    testMovie, futureDate, validTime, hallId
                );
                assertTrue("Should find showtime for hall " + hallId, 
                          showtime.isPresent());
            } catch (IllegalArgumentException e) {
                fail("Hall " + hallId + " should exist in configuration");
            }
        }
    }
    
    @Test
    public void testHallConfiguration_HasValidProperties() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        String validTime = "10:00 AM";
        
        Optional<Showtime> showtime = repository.findAvailableShowtime(
            testMovie, futureDate, validTime, 1
        );
        
        assertTrue("Should find showtime", showtime.isPresent());
        CinemaHall hall = showtime.get().getCinemaHall();
        
        assertNotNull("Hall should not be null", hall);
        assertTrue("Hall ID should be positive", hall.getHallId() > 0);
        assertNotNull("Hall type should not be null", hall.getHallType());
        assertTrue("Row amount should be positive", hall.getRowAmt() > 0);
        assertTrue("Column amount should be positive", hall.getColAmt() > 0);
    }
    
    // ========== TIME GENERATION TESTS ==========
    
    @Test
    public void testTimeGeneration_100MinuteInterval() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        
        // 10:00 AM + 100 minutes = 11:40 AM
        // 11:40 AM + 100 minutes = 01:20 PM
        Optional<Showtime> time1 = repository.findAvailableShowtime(
            testMovie, futureDate, "10:00 AM", 1
        );
        Optional<Showtime> time2 = repository.findAvailableShowtime(
            testMovie, futureDate, "11:40 AM", 1
        );
        Optional<Showtime> time3 = repository.findAvailableShowtime(
            testMovie, futureDate, "01:20 PM", 1
        );
        
        assertTrue("10:00 AM should be valid", time1.isPresent());
        assertTrue("11:40 AM should be valid", time2.isPresent());
        assertTrue("01:20 PM should be valid", time3.isPresent());
    }
    
    @Test
    public void testTimeGeneration_WithinOperatingHours() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        
        // Operating hours: 10:00 AM to 10:00 PM
        Optional<Showtime> tooEarly = repository.findAvailableShowtime(
            testMovie, futureDate, "09:00 AM", 1
        );
        Optional<Showtime> tooLate = repository.findAvailableShowtime(
            testMovie, futureDate, "11:00 PM", 1
        );
        
        assertFalse("09:00 AM should be too early", tooEarly.isPresent());
        assertFalse("11:00 PM should be too late", tooLate.isPresent());
    }
    
    // ========== INTEGRATION TESTS ==========
    
    @Test
    public void testCompleteShowtimeFlow() {
        // 1. Get available dates
        List<Showtime> dates = repository.findAvailableDates(testMovie);
        assertEquals(3, dates.size());
        
        // 2. Pick a date
        LocalDate selectedDate = dates.get(0).getDate();
        
        // 3. Get showtime for that date
        Optional<Showtime> showtime = repository.findAvailableShowtime(
            testMovie, selectedDate, "10:00 AM", 1
        );
        
        assertTrue("Should find showtime", showtime.isPresent());
        assertEquals("Date should match", selectedDate, showtime.get().getDate());
    }
    
    @Test
    public void testMultipleMoviesSameTime() {
        Movie movie1 = new Movie(1, "Movie 1", 2.0, "Director 1", "2025-01-01");
        Movie movie2 = new Movie(2, "Movie 2", 2.5, "Director 2", "2025-01-02");
        
        LocalDate date = LocalDate.now().plusDays(1);
        String time = "10:00 AM";
        
        // Same time, different halls
        Optional<Showtime> show1 = repository.findAvailableShowtime(
            movie1, date, time, 1
        );
        Optional<Showtime> show2 = repository.findAvailableShowtime(
            movie2, date, time, 2
        );
        
        assertTrue("Movie 1 showtime should exist", show1.isPresent());
        assertTrue("Movie 2 showtime should exist", show2.isPresent());
        assertNotEquals("Should be in different halls",
                       show1.get().getHallId(), 
                       show2.get().getHallId());
    }
    
    @Test
    public void testSameMovieDifferentTimes() {
        LocalDate date = LocalDate.now().plusDays(1);
        int hallId = 1;
        
        Optional<Showtime> morning = repository.findAvailableShowtime(
            testMovie, date, "10:00 AM", hallId
        );
        Optional<Showtime> afternoon = repository.findAvailableShowtime(
            testMovie, date, "03:00 PM", hallId
        );
        Optional<Showtime> evening = repository.findAvailableShowtime(
            testMovie, date, "08:00 PM", hallId
        );
        
        assertTrue("Morning showtime should exist", morning.isPresent());
        assertTrue("Afternoon showtime should exist", afternoon.isPresent());
        assertTrue("Evening showtime should exist", evening.isPresent());
        
        // All should be for same movie, date, and hall
        assertEquals(testMovie.getMovieName(), morning.get().getMovieName());
        assertEquals(date, morning.get().getDate());
        assertEquals(hallId, morning.get().getHallId());
    }
    
    // ========== EDGE CASE TESTS ==========
    
    @Test
    public void testFindAvailableDates_NullMovie() {
        try {
            repository.findAvailableDates(null);
            // If no exception, test passes
        } catch (NullPointerException e) {
            // Also acceptable
            assertTrue(true);
        }
    }
    
    @Test
    public void testFindAvailableShowtime_NullMovie() {
        LocalDate date = LocalDate.now().plusDays(1);
        
        try {
            repository.findAvailableShowtime(null, date, "10:00 AM", 1);
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testFindAvailableShowtime_NullDate() {
        try {
            repository.findAvailableShowtime(testMovie, null, "10:00 AM", 1);
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testFindAvailableShowtime_NullTime() {
        LocalDate date = LocalDate.now().plusDays(1);
        
        Optional<Showtime> showtime = repository.findAvailableShowtime(
            testMovie, date, null, 1
        );
        
        assertFalse("Null time should not be found", showtime.isPresent());
    }
    
    @Test
    public void testFindAvailableShowtime_EmptyTime() {
        LocalDate date = LocalDate.now().plusDays(1);
        
        Optional<Showtime> showtime = repository.findAvailableShowtime(
            testMovie, date, "", 1
        );
        
        assertFalse("Empty time should not be found", showtime.isPresent());
    }
    
    @Test
    public void testFindAvailableShowtime_InvalidTimeFormat() {
        LocalDate date = LocalDate.now().plusDays(1);
        
        String[] invalidTimes = {
            "10:00",      // Missing AM/PM
            "10 AM",      // Missing colon
            "25:00 AM",   // Invalid hour
            "10:60 AM",   // Invalid minute
            "abc",        // Not a time
            "12:00 XM"    // Invalid period
        };
        
        for (String time : invalidTimes) {
            Optional<Showtime> showtime = repository.findAvailableShowtime(
                testMovie, date, time, 1
            );
            assertFalse("Invalid time '" + time + "' should not be found", 
                       showtime.isPresent());
        }
    }
    
    @Test
    public void testFindAvailableShowtime_ZeroHallId() {
        LocalDate date = LocalDate.now().plusDays(1);
        
        try {
            repository.findAvailableShowtime(testMovie, date, "10:00 AM", 0);
            fail("Should throw exception for hall ID 0");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testFindAvailableShowtime_NegativeHallId() {
        LocalDate date = LocalDate.now().plusDays(1);
        
        try {
            repository.findAvailableShowtime(testMovie, date, "10:00 AM", -1);
            fail("Should throw exception for negative hall ID");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }
    
    // ========== CONSISTENCY TESTS ==========
    
    @Test
    public void testConsistency_SameParametersSameResult() {
        LocalDate date = LocalDate.now().plusDays(1);
        String time = "10:00 AM";
        int hallId = 1;
        
        Optional<Showtime> show1 = repository.findAvailableShowtime(
            testMovie, date, time, hallId
        );
        Optional<Showtime> show2 = repository.findAvailableShowtime(
            testMovie, date, time, hallId
        );
        
        assertEquals("Should return same availability", 
                    show1.isPresent(), show2.isPresent());
        
        if (show1.isPresent() && show2.isPresent()) {
            assertEquals("Should return same showtime details",
                        show1.get().getShowtime(), 
                        show2.get().getShowtime());
        }
    }
    
    @Test
    public void testConsistency_DateGenerationStable() {
        List<Showtime> dates1 = repository.findAvailableDates(testMovie);
        List<Showtime> dates2 = repository.findAvailableDates(testMovie);
        
        assertEquals("Should generate same number of dates", 
                    dates1.size(), dates2.size());
        
        for (int i = 0; i < dates1.size(); i++) {
            assertEquals("Dates should match at index " + i,
                        dates1.get(i).getDate(), 
                        dates2.get(i).getDate());
        }
    }
}
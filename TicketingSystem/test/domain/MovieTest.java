package domain;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for Movie class
 */
public class MovieTest {
    
    private Movie movie;
    
    @Before
    public void setUp() {
        movie = new Movie(1, "Dune: Part 1", 2.35, "Denis Villeneuve", "October 22, 2021");
    }
    
    @Test
    public void testConstructor_WithParameters() {
        assertNotNull(movie);
        assertEquals(1, movie.getId());
        assertEquals("Dune: Part 1", movie.getMovieName());
        assertEquals(2.35, movie.getMovieLength(), 0.001);
        assertEquals("Denis Villeneuve", movie.getDirector());
        assertEquals("October 22, 2021", movie.releaseDate());
    }
    
    @Test
    public void testConstructor_DefaultConstructor() {
        Movie emptyMovie = new Movie();
        
        assertNotNull(emptyMovie);
    }
    
    @Test
    public void testGetId() {
        assertEquals(1, movie.getId());
    }
    
    @Test
    public void testGetMovieName() {
        assertEquals("Dune: Part 1", movie.getMovieName());
    }
    
    @Test
    public void testSetMovieName() {
        movie.setMovieName("Blade Runner 2049");
        
        assertEquals("Blade Runner 2049", movie.getMovieName());
    }
    
    @Test
    public void testGetMovieLength() {
        assertEquals(2.35, movie.getMovieLength(), 0.001);
    }
    
    @Test
    public void testSetMovieLength() {
        movie.setMovieLength(2.43);
        
        assertEquals(2.43, movie.getMovieLength(), 0.001);
    }
    
    @Test
    public void testGetDirector() {
        assertEquals("Denis Villeneuve", movie.getDirector());
    }
    
    @Test
    public void testSetDirector() {
        movie.setDirector("Christopher Nolan");
        
        assertEquals("Christopher Nolan", movie.getDirector());
    }
    
    @Test
    public void testReleaseDate() {
        assertEquals("October 22, 2021", movie.releaseDate());
    }
    
    @Test
    public void testToString() {
        String result = movie.toString();
        
        assertTrue(result.contains("Dune: Part 1"));
        assertTrue(result.contains("2.35"));
        assertTrue(result.contains("Denis Villeneuve"));
        assertTrue(result.contains("October 22, 2021"));
    }
    
    @Test
    public void testZeroId() {
        Movie zeroIdMovie = new Movie(0, "Test Movie", 2.0, "Test Director", "2021");
        
        assertEquals(0, zeroIdMovie.getId());
    }
    
    @Test
    public void testNegativeId() {
        Movie negativeIdMovie = new Movie(-1, "Test Movie", 2.0, "Test Director", "2021");
        
        assertEquals(-1, negativeIdMovie.getId());
    }
    
    @Test
    public void testVeryShortMovie() {
        Movie shortMovie = new Movie(2, "Short Film", 0.5, "Director", "2021");
        
        assertEquals(0.5, shortMovie.getMovieLength(), 0.001);
    }
    
    @Test
    public void testVeryLongMovie() {
        Movie longMovie = new Movie(3, "Epic Movie", 5.5, "Director", "2021");
        
        assertEquals(5.5, longMovie.getMovieLength(), 0.001);
    }
    
    @Test
    public void testZeroLengthMovie() {
        Movie zeroLength = new Movie(4, "No Duration", 0.0, "Director", "2021");
        
        assertEquals(0.0, zeroLength.getMovieLength(), 0.001);
    }
    
    @Test
    public void testEmptyMovieName() {
        Movie emptyName = new Movie(5, "", 2.0, "Director", "2021");
        
        assertEquals("", emptyName.getMovieName());
    }
    
    @Test
    public void testNullMovieName() {
        Movie nullName = new Movie(6, null, 2.0, "Director", "2021");
        
        assertNull(nullName.getMovieName());
    }
    
    @Test
    public void testEmptyDirector() {
        Movie emptyDirector = new Movie(7, "Movie", 2.0, "", "2021");
        
        assertEquals("", emptyDirector.getDirector());
    }
    
    @Test
    public void testNullDirector() {
        Movie nullDirector = new Movie(8, "Movie", 2.0, null, "2021");
        
        assertNull(nullDirector.getDirector());
    }
    
    @Test
    public void testEmptyReleaseDate() {
        Movie emptyDate = new Movie(9, "Movie", 2.0, "Director", "");
        
        assertEquals("", emptyDate.releaseDate());
    }
    
    @Test
    public void testNullReleaseDate() {
        Movie nullDate = new Movie(10, "Movie", 2.0, "Director", null);
        
        assertNull(nullDate.releaseDate());
    }
    
    @Test
    public void testLongMovieName() {
        String longName = "A Very Long Movie Title That Exceeds Normal Length".repeat(5);
        Movie longNameMovie = new Movie(11, longName, 2.0, "Director", "2021");
        
        assertEquals(longName, longNameMovie.getMovieName());
    }
    
    @Test
    public void testSpecialCharactersInName() {
        Movie specialChars = new Movie(12, "Movie: Part 1 - The Beginning!", 2.0, "Director", "2021");
        
        assertEquals("Movie: Part 1 - The Beginning!", specialChars.getMovieName());
    }
    
    @Test
    public void testSpecialCharactersInDirector() {
        Movie specialChars = new Movie(13, "Movie", 2.0, "Director-Name O'Brien", "2021");
        
        assertEquals("Director-Name O'Brien", specialChars.getDirector());
    }
    
    @Test
    public void testDifferentDateFormats() {
        Movie date1 = new Movie(14, "Movie1", 2.0, "Director", "October 22, 2021");
        Movie date2 = new Movie(15, "Movie2", 2.0, "Director", "22/10/2021");
        Movie date3 = new Movie(16, "Movie3", 2.0, "Director", "2021-10-22");
        
        assertEquals("October 22, 2021", date1.releaseDate());
        assertEquals("22/10/2021", date2.releaseDate());
        assertEquals("2021-10-22", date3.releaseDate());
    }
    
    @Test
    public void testMovieLengthPrecision() {
        Movie precise = new Movie(17, "Movie", 2.123456789, "Director", "2021");
        
        assertEquals(2.123456789, precise.getMovieLength(), 0.000001);
    }
    
    @Test
    public void testMultipleSetters() {
        movie.setMovieName("New Name");
        movie.setMovieLength(3.0);
        movie.setDirector("New Director");
        
        assertEquals("New Name", movie.getMovieName());
        assertEquals(3.0, movie.getMovieLength(), 0.001);
        assertEquals("New Director", movie.getDirector());
        assertEquals("October 22, 2021", movie.releaseDate()); // Unchanged
    }
}

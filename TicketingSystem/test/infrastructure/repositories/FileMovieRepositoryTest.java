package infrastructure.repositories;

import domain.Movie;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Optional;

public class FileMovieRepositoryTest {
    
    private FileMovieRepository repository;
    private static final String TEST_FILE = "movies_test.json";
    
    @Before
    public void setUp() {
        // Clean test file
        DataFileHandler.saveToJsonFile(java.util.Collections.emptyList(), TEST_FILE);
        
        // Note: FileMovieRepository doesn't have a constructor with custom filename yet
        // You'll need to add it for testing. For now, tests will use the default file.
        repository = new FileMovieRepository();
    }
    
    @After
    public void tearDown() {
        // Clean up
        DataFileHandler.saveToJsonFile(java.util.Collections.emptyList(), TEST_FILE);
    }
    
    // ========== FIND ALL TESTS ==========
    
    @Test
    public void testFindAll_ReturnsMovies() {
        List<Movie> movies = repository.findAll();
        
        assertNotNull("Movie list should not be null", movies);
        assertFalse("Movie list should not be empty (default movies loaded)", movies.isEmpty());
    }
    
    @Test
    public void testFindAll_ReturnsDefensiveCopy() {
        List<Movie> movies1 = repository.findAll();
        List<Movie> movies2 = repository.findAll();
        
        assertNotSame("Should return different list instances", movies1, movies2);
        assertEquals("Lists should have same size", movies1.size(), movies2.size());
    }
    
    @Test
    public void testFindAll_ContainsDefaultMovies() {
        List<Movie> movies = repository.findAll();
        
        // Check for some default movies
        assertTrue("Should contain at least 5 default movies", movies.size() >= 5);
        
        boolean foundDune = movies.stream()
            .anyMatch(m -> m.getMovieName().contains("Dune"));
        assertTrue("Should contain Dune movie", foundDune);
    }
    
    @Test
    public void testFindAll_ModifyingListDoesntAffectRepository() {
        List<Movie> movies = repository.findAll();
        int originalSize = movies.size();
        
        // Try to modify the returned list
        movies.clear();
        
        // Get list again
        List<Movie> moviesAgain = repository.findAll();
        
        assertEquals("Repository should not be affected", 
                     originalSize, moviesAgain.size());
    }
    
    // ========== FIND BY ID TESTS ==========
    
    @Test
    public void testFindById_ExistingMovie() {
        // First movie should have ID 1
        Optional<Movie> movie = repository.findById(1);
        
        assertTrue("Should find movie with ID 1", movie.isPresent());
        assertEquals("Movie ID should match", 1, movie.get().getId());
    }
    
    @Test
    public void testFindById_MultipleValidIds() {
        Optional<Movie> movie1 = repository.findById(1);
        Optional<Movie> movie2 = repository.findById(2);
        Optional<Movie> movie3 = repository.findById(3);
        
        assertTrue("Should find movie 1", movie1.isPresent());
        assertTrue("Should find movie 2", movie2.isPresent());
        assertTrue("Should find movie 3", movie3.isPresent());
        
        assertNotEquals("Movies should be different", 
                       movie1.get().getMovieName(), 
                       movie2.get().getMovieName());
    }
    
    @Test
    public void testFindById_NonExistentId() {
        Optional<Movie> movie = repository.findById(999);
        
        assertFalse("Should not find non-existent movie", movie.isPresent());
    }
    
    @Test
    public void testFindById_ZeroId() {
        Optional<Movie> movie = repository.findById(0);
        
        assertFalse("Should not find movie with ID 0", movie.isPresent());
    }
    
    @Test
    public void testFindById_NegativeId() {
        Optional<Movie> movie = repository.findById(-1);
        
        assertFalse("Should not find movie with negative ID", movie.isPresent());
    }
    
    @Test
    public void testFindById_AllDefaultMovies() {
        List<Movie> allMovies = repository.findAll();
        
        // Verify we can find each movie by its ID
        for (Movie m : allMovies) {
            Optional<Movie> found = repository.findById(m.getId());
            assertTrue("Should find movie with ID " + m.getId(), found.isPresent());
            assertEquals("Found movie should match", m.getId(), found.get().getId());
        }
    }
    
    // ========== MOVIE DATA INTEGRITY TESTS ==========
    
    @Test
    public void testDefaultMovies_HaveValidData() {
        List<Movie> movies = repository.findAll();
        
        for (Movie movie : movies) {
            assertNotNull("Movie should not be null", movie);
            assertTrue("Movie ID should be positive", movie.getId() > 0);
            assertNotNull("Movie name should not be null", movie.getMovieName());
            assertFalse("Movie name should not be empty", 
                       movie.getMovieName().trim().isEmpty());
            assertTrue("Movie length should be positive", movie.getMovieLength() > 0);
            assertNotNull("Director should not be null", movie.getDirector());
            assertNotNull("Release date should not be null", movie.releaseDate());
        }
    }
    
    @Test
    public void testDefaultMovies_HaveUniqueIds() {
        List<Movie> movies = repository.findAll();
        java.util.Set<Integer> ids = new java.util.HashSet<>();
        
        for (Movie movie : movies) {
            assertTrue("Movie IDs should be unique", ids.add(movie.getId()));
        }
    }
    
    @Test
    public void testDefaultMovies_DuneMovie() {
        Optional<Movie> dune = repository.findById(1);
        
        assertTrue("Dune should be movie ID 1", dune.isPresent());
        assertTrue("Movie name should contain 'Dune'", 
                  dune.get().getMovieName().contains("Dune"));
        assertEquals("Director should be Denis Villeneuve", 
                    "Denis Villeneuve", dune.get().getDirector());
    }
    
    @Test
    public void testDefaultMovies_BladeRunner() {
        Optional<Movie> bladeRunner = repository.findById(2);
        
        assertTrue("Blade Runner should be movie ID 2", bladeRunner.isPresent());
        assertTrue("Movie name should contain 'Blade Runner'", 
                  bladeRunner.get().getMovieName().contains("Blade Runner"));
    }
    
    // ========== PERSISTENCE TESTS ==========
    
    @Test
    public void testPersistence_DefaultMoviesSaved() {
        // Repository should save default movies on first load
        // Create new instance to verify persistence
        FileMovieRepository newRepo = new FileMovieRepository();
        
        List<Movie> movies = newRepo.findAll();
        
        assertFalse("Persisted movies should be loaded", movies.isEmpty());
    }
    
    // ========== CONCURRENT ACCESS TESTS ==========
    
    @Test
    public void testFindAll_MultipleCalls_Consistent() {
        List<Movie> movies1 = repository.findAll();
        List<Movie> movies2 = repository.findAll();
        List<Movie> movies3 = repository.findAll();
        
        assertEquals("All calls should return same size", 
                    movies1.size(), movies2.size());
        assertEquals("All calls should return same size", 
                    movies2.size(), movies3.size());
    }
    
    @Test
    public void testFindById_MultipleCalls_SameMovie() {
        Optional<Movie> movie1 = repository.findById(1);
        Optional<Movie> movie2 = repository.findById(1);
        Optional<Movie> movie3 = repository.findById(1);
        
        assertTrue("All calls should find movie", movie1.isPresent());
        assertTrue("All calls should find movie", movie2.isPresent());
        assertTrue("All calls should find movie", movie3.isPresent());
        
        assertEquals("All calls should return same movie name",
                    movie1.get().getMovieName(), 
                    movie2.get().getMovieName());
    }
    
    // ========== EDGE CASE TESTS ==========
    
    @Test
    public void testFindAll_EmptyModification() {
        List<Movie> movies = repository.findAll();
        int size = movies.size();
        
        // Try to add movie to returned list (should not affect repository)
        Movie newMovie = new Movie(999, "Test Movie", 2.0, "Test", "2025");
        try {
            movies.add(newMovie);
        } catch (UnsupportedOperationException e) {
            // If list is immutable, that's fine
        }
        
        // Verify repository unchanged
        List<Movie> moviesAgain = repository.findAll();
        assertEquals("Repository should be unchanged", size, moviesAgain.size());
    }
    
    @Test
    public void testFindById_LargeId() {
        Optional<Movie> movie = repository.findById(Integer.MAX_VALUE);
        
        assertFalse("Should not find movie with very large ID", movie.isPresent());
    }
    
    @Test
    public void testFindById_MinimumId() {
        Optional<Movie> movie = repository.findById(Integer.MIN_VALUE);
        
        assertFalse("Should not find movie with minimum int ID", movie.isPresent());
    }
    
    // ========== SEARCH PATTERN TESTS ==========
    
    @Test
    public void testFindAll_ThenFindById() {
        // Get all movies
        List<Movie> allMovies = repository.findAll();
        assertTrue("Should have movies", !allMovies.isEmpty());
        
        // Try to find each by ID
        for (Movie m : allMovies) {
            Optional<Movie> found = repository.findById(m.getId());
            assertTrue("Should find movie " + m.getMovieName(), found.isPresent());
        }
    }
    
    @Test
    public void testFindById_ThenVerifyInFindAll() {
        // Find specific movie
        Optional<Movie> specificMovie = repository.findById(1);
        assertTrue("Should find movie", specificMovie.isPresent());
        
        // Verify it exists in findAll
        List<Movie> allMovies = repository.findAll();
        boolean found = allMovies.stream()
            .anyMatch(m -> m.getId() == specificMovie.get().getId());
        
        assertTrue("Movie should be in findAll results", found);
    }
    
    // ========== DATA VALIDATION TESTS ==========
    
    @Test
    public void testMovieData_NoNullNames() {
        List<Movie> movies = repository.findAll();
        
        for (Movie m : movies) {
            assertNotNull("Movie name should not be null", m.getMovieName());
        }
    }
    
    @Test
    public void testMovieData_NoEmptyNames() {
        List<Movie> movies = repository.findAll();
        
        for (Movie m : movies) {
            assertFalse("Movie name should not be empty", 
                       m.getMovieName().trim().isEmpty());
        }
    }
    
    @Test
    public void testMovieData_ValidLengths() {
        List<Movie> movies = repository.findAll();
        
        for (Movie m : movies) {
            assertTrue("Movie length should be positive: " + m.getMovieName(), 
                      m.getMovieLength() > 0);
            assertTrue("Movie length should be reasonable (< 10 hours): " + m.getMovieName(), 
                      m.getMovieLength() < 10.0);
        }
    }
    
    @Test
    public void testMovieData_HasDirectors() {
        List<Movie> movies = repository.findAll();
        
        for (Movie m : movies) {
            assertNotNull("Director should not be null", m.getDirector());
            assertFalse("Director should not be empty", 
                       m.getDirector().trim().isEmpty());
        }
    }
    
    @Test
    public void testMovieData_HasReleaseDates() {
        List<Movie> movies = repository.findAll();
        
        for (Movie m : movies) {
            assertNotNull("Release date should not be null", m.releaseDate());
            assertFalse("Release date should not be empty", 
                       m.releaseDate().trim().isEmpty());
        }
    }
}
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

public class CartManagerTest {
    
    private CartManager cartManager;
    private Customer testCustomer;
    private ArrayList<Ticket> testTickets;
    private ArrayList<Food> testFood;
    private Movie testMovie;
    private CinemaHall testHall;
    private Showtime testShowtime;
    
    private static final String TEST_CART_FILE = "test_carts.json";
    
    @Before
    public void setUp() {
        // Clean test file
        DataFileHandler.saveToJsonFile(java.util.Collections.emptyList(), TEST_CART_FILE);
        
        // Note: CartManager doesn't have constructor with custom filename
        // You'll need to add it for testing
        cartManager = new CartManager();
        
        // Setup test data
        testCustomer = new Customer("Test Customer", "password");
        testMovie = new Movie(1, "Test Movie", 2.0, "Director", "2025-01-01");
        testHall = new CinemaHall(1, CinemaHall.HALL_TYPE_STANDARD, 5, 10);
        testShowtime = new Showtime(testMovie, 2025, 1, 15, "10:00 AM", testHall);
        
        // Create test tickets
        testTickets = new ArrayList<>();
        ArrayList<Seat> seats = new ArrayList<>();
        seats.add(new Seat(new SeatId('A', 1), "Single", "Reserved", testHall));
        seats.add(new Seat(new SeatId('A', 2), "Single", "Reserved", testHall));
        testTickets.add(new Ticket(testShowtime, 2, testHall, seats));
        
        // Create test food
        testFood = new ArrayList<>();
        Popcorn popcorn = new Popcorn("Large Popcorn", 12.00);
        popcorn.setQty(1);
        popcorn.setPrice(12.00);
        testFood.add(popcorn);
    }
    
    @After
    public void tearDown() {
        DataFileHandler.saveToJsonFile(java.util.Collections.emptyList(), TEST_CART_FILE);
    }
    
    // ========== SAVE CART TESTS ==========
    
    @Test
    public void testSaveCart_Success() {
        cartManager.saveCart(testCustomer, testTickets, testFood);
        
        // Verify cart was saved by loading it
        CartManager.CartData loaded = cartManager.loadCart(testCustomer);
        
        assertNotNull("Cart should be saved", loaded);
        assertEquals("Customer ID should match", 
                    testCustomer.getId(), loaded.customerId);
        assertEquals("Should have 1 ticket", 1, loaded.tickets.size());
        assertEquals("Should have 1 food item", 1, loaded.food.size());
    }
    
    @Test
    public void testSaveCart_EmptyTickets() {
        ArrayList<Ticket> emptyTickets = new ArrayList<>();
        
        cartManager.saveCart(testCustomer, emptyTickets, testFood);
        
        CartManager.CartData loaded = cartManager.loadCart(testCustomer);
        
        assertNotNull("Cart should be saved", loaded);
        assertTrue("Tickets should be empty", loaded.tickets.isEmpty());
        assertEquals("Should have food", 1, loaded.food.size());
    }
    
    @Test
    public void testSaveCart_EmptyFood() {
        ArrayList<Food> emptyFood = new ArrayList<>();
        
        cartManager.saveCart(testCustomer, testTickets, emptyFood);
        
        CartManager.CartData loaded = cartManager.loadCart(testCustomer);
        
        assertNotNull("Cart should be saved", loaded);
        assertEquals("Should have tickets", 1, loaded.tickets.size());
        assertTrue("Food should be empty", loaded.food.isEmpty());
    }
    
    @Test
    public void testSaveCart_EmptyCart() {
        ArrayList<Ticket> emptyTickets = new ArrayList<>();
        ArrayList<Food> emptyFood = new ArrayList<>();
        
        cartManager.saveCart(testCustomer, emptyTickets, emptyFood);
        
        CartManager.CartData loaded = cartManager.loadCart(testCustomer);
        
        assertNotNull("Cart should be saved", loaded);
        assertTrue("Tickets should be empty", loaded.tickets.isEmpty());
        assertTrue("Food should be empty", loaded.food.isEmpty());
    }
    
    @Test
    public void testSaveCart_Overwrite() {
        // Save first version
        cartManager.saveCart(testCustomer, testTickets, testFood);
        
        // Modify and save again
        Beverage beverage = new Beverage("Coke", 5.00);
        testFood.add(beverage);
        
        cartManager.saveCart(testCustomer, testTickets, testFood);
        
        // Load and verify
        CartManager.CartData loaded = cartManager.loadCart(testCustomer);
        assertEquals("Should have updated food count", 2, loaded.food.size());
    }
    
    @Test
    public void testSaveCart_NullCustomer_NoException() {
        // Should not throw exception, just not save
        cartManager.saveCart(null, testTickets, testFood);
        
        // No exception means pass
        assertTrue(true);
    }
    
    // ========== LOAD CART TESTS ==========
    
    @Test
    public void testLoadCart_ExistingCart() {
        cartManager.saveCart(testCustomer, testTickets, testFood);
        
        CartManager.CartData loaded = cartManager.loadCart(testCustomer);
        
        assertNotNull("Should load saved cart", loaded);
        assertEquals("Customer ID should match", 
                    testCustomer.getId(), loaded.customerId);
    }
    
    @Test
    public void testLoadCart_NonExistentCart() {
        Customer newCustomer = new Customer("New Customer", "password");
        
        CartManager.CartData loaded = cartManager.loadCart(newCustomer);
        
        assertNull("Should return null for non-existent cart", loaded);
    }
    
    @Test
    public void testLoadCart_NullCustomer() {
        CartManager.CartData loaded = cartManager.loadCart(null);
        
        assertNull("Should return null for null customer", loaded);
    }
    
    @Test
    public void testLoadCart_VerifyTicketData() {
        cartManager.saveCart(testCustomer, testTickets, testFood);
        
        CartManager.CartData loaded = cartManager.loadCart(testCustomer);
        
        assertNotNull("Cart should be loaded", loaded);
        assertFalse("Tickets should not be empty", loaded.tickets.isEmpty());
        
        Ticket loadedTicket = loaded.tickets.get(0);
        assertEquals("Movie name should match", 
                    testMovie.getMovieName(), loadedTicket.getMovieName());
        assertEquals("Hall ID should match", 
                    testHall.getHallId(), loadedTicket.getHallId());
        assertEquals("Ticket amount should match", 
                    2, loadedTicket.getTicketAmt());
    }
    
    @Test
    public void testLoadCart_VerifyFoodData() {
        cartManager.saveCart(testCustomer, testTickets, testFood);
        
        CartManager.CartData loaded = cartManager.loadCart(testCustomer);
        
        assertNotNull("Cart should be loaded", loaded);
        assertFalse("Food should not be empty", loaded.food.isEmpty());
        
        Food loadedFood = loaded.food.get(0);
        assertEquals("Food name should match", 
                    "Large Popcorn", loadedFood.getName());
        assertEquals("Quantity should match", 1, loadedFood.getQty());
        assertEquals("Price should match", 12.00, loadedFood.getPrice(), 0.001);
    }
    
    @Test
    public void testLoadCart_VerifySeatData() {
        cartManager.saveCart(testCustomer, testTickets, testFood);
        
        CartManager.CartData loaded = cartManager.loadCart(testCustomer);
        
        Ticket loadedTicket = loaded.tickets.get(0);
        ArrayList<Seat> loadedSeats = loadedTicket.getSeat();
        
        assertEquals("Should have 2 seats", 2, loadedSeats.size());
        assertEquals("First seat should be A1", 
                    new SeatId('A', 1), loadedSeats.get(0).getId());
        assertEquals("Second seat should be A2", 
                    new SeatId('A', 2), loadedSeats.get(1).getId());
    }
    
    // ========== CLEAR CART TESTS ==========
    
    @Test
    public void testClearCart_Success() {
        cartManager.saveCart(testCustomer, testTickets, testFood);
        
        // Verify cart exists
        assertNotNull("Cart should exist", cartManager.loadCart(testCustomer));
        
        // Clear cart
        cartManager.clearCart(testCustomer);
        
        // Verify cart is cleared
        assertNull("Cart should be cleared", cartManager.loadCart(testCustomer));
    }
    
    @Test
    public void testClearCart_NonExistentCart() {
        Customer newCustomer = new Customer("New Customer", "password");
        
        // Should not throw exception
        cartManager.clearCart(newCustomer);
        
        assertTrue(true);
    }
    
    @Test
    public void testClearCart_NullCustomer() {
        // Should not throw exception
        cartManager.clearCart(null);
        
        assertTrue(true);
    }
    
    // ========== GET CART SEAT IDS TESTS ==========
    
    @Test
    public void testGetCartSeatIds_WithSeats() {
        cartManager.saveCart(testCustomer, testTickets, testFood);
        
        List<SeatId> seatIds = cartManager.getCartSeatIds(testCustomer);
        
        assertNotNull("Seat IDs should not be null", seatIds);
        assertEquals("Should have 2 seat IDs", 2, seatIds.size());
        assertTrue("Should contain A1", 
                  seatIds.contains(new SeatId('A', 1)));
        assertTrue("Should contain A2", 
                  seatIds.contains(new SeatId('A', 2)));
    }
    
    @Test
    public void testGetCartSeatIds_EmptyCart() {
        Customer newCustomer = new Customer("New Customer", "password");
        
        List<SeatId> seatIds = cartManager.getCartSeatIds(newCustomer);
        
        assertNotNull("Should return empty list", seatIds);
        assertTrue("Should be empty", seatIds.isEmpty());
    }
    
    @Test
    public void testGetCartSeatIds_MultipleTickets() {
        // Add another ticket
        ArrayList<Seat> moreSeats = new ArrayList<>();
        moreSeats.add(new Seat(new SeatId('B', 1), "Single", "Reserved", testHall));
        moreSeats.add(new Seat(new SeatId('B', 2), "Single", "Reserved", testHall));
        testTickets.add(new Ticket(testShowtime, 2, testHall, moreSeats));
        
        cartManager.saveCart(testCustomer, testTickets, testFood);
        
        List<SeatId> seatIds = cartManager.getCartSeatIds(testCustomer);
        
        assertEquals("Should have 4 seat IDs", 4, seatIds.size());
    }
    
    // ========== CART EXPIRATION TESTS ==========
    
    @Test
    public void testLoadCart_NotExpired() {
        cartManager.saveCart(testCustomer, testTickets, testFood);
        
        // Load immediately (should not be expired)
        CartManager.CartData loaded = cartManager.loadCart(testCustomer);
        
        assertNotNull("Cart should not be expired", loaded);
    }

    
    // ========== PERSISTENCE TESTS ==========
    
    @Test
    public void testPersistence_SaveAndReload() {
        cartManager.saveCart(testCustomer, testTickets, testFood);
        
        // Create new CartManager instance (simulates app restart)
        CartManager newCartManager = new CartManager();
        
        CartManager.CartData loaded = newCartManager.loadCart(testCustomer);
        
        assertNotNull("Cart should persist across instances", loaded);
        assertEquals("Customer ID should match", 
                    testCustomer.getId(), loaded.customerId);
    }
    
    @Test
    public void testPersistence_MultipleCustomers() {
        Customer customer1 = new Customer("Customer 1", "pass1");
        Customer customer2 = new Customer("Customer 2", "pass2");
        
        // Save carts for both customers
        cartManager.saveCart(customer1, testTickets, testFood);
        cartManager.saveCart(customer2, testTickets, testFood);
        
        // Reload
        CartManager newCartManager = new CartManager();
        
        CartManager.CartData cart1 = newCartManager.loadCart(customer1);
        CartManager.CartData cart2 = newCartManager.loadCart(customer2);
        
        assertNotNull("Customer 1 cart should be loaded", cart1);
        assertNotNull("Customer 2 cart should be loaded", cart2);
        assertNotEquals("Carts should have different customer IDs",
                       cart1.customerId, cart2.customerId);
    }
    
    // ========== INTEGRATION TESTS ==========
    
    @Test
    public void testCompleteCartFlow() {
        // 1. Save cart
        cartManager.saveCart(testCustomer, testTickets, testFood);
        
        // 2. Load cart
        CartManager.CartData loaded = cartManager.loadCart(testCustomer);
        assertNotNull("Cart should be loaded", loaded);
        
        // 3. Get seat IDs
        List<SeatId> seatIds = cartManager.getCartSeatIds(testCustomer);
        assertEquals("Should have 2 seats", 2, seatIds.size());
        
        // 4. Clear cart
        cartManager.clearCart(testCustomer);
        
        // 5. Verify cleared
        assertNull("Cart should be cleared", cartManager.loadCart(testCustomer));
    }
    
    @Test
    public void testCartUpdate() {
        // Save initial cart
        cartManager.saveCart(testCustomer, testTickets, testFood);
        
        // Load cart
        CartManager.CartData cart = cartManager.loadCart(testCustomer);
        assertNotNull(cart);
        assertEquals(1, cart.tickets.size());
        
        // Add more tickets
        ArrayList<Seat> newSeats = new ArrayList<>();
        newSeats.add(new Seat(new SeatId('C', 1), "Single", "Reserved", testHall));
        testTickets.add(new Ticket(testShowtime, 1, testHall, newSeats));
        
        // Save updated cart
        cartManager.saveCart(testCustomer, testTickets, testFood);
        
        // Reload and verify
        CartManager.CartData updated = cartManager.loadCart(testCustomer);
        assertEquals("Should have 2 tickets", 2, updated.tickets.size());
    }
    
    // ========== EDGE CASE TESTS ==========
    
    @Test
    public void testSaveCart_MultipleSeatsPerTicket() {
        ArrayList<Seat> manySeats = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            manySeats.add(new Seat(new SeatId('D', i), "Single", "Reserved", testHall));
        }
        
        ArrayList<Ticket> bigTicket = new ArrayList<>();
        bigTicket.add(new Ticket(testShowtime, 10, testHall, manySeats));
        
        cartManager.saveCart(testCustomer, bigTicket, testFood);
        
        CartManager.CartData loaded = cartManager.loadCart(testCustomer);
        
        assertEquals("Should have 1 ticket", 1, loaded.tickets.size());
        assertEquals("Ticket should have 10 seats", 
                    10, loaded.tickets.get(0).getSeat().size());
    }
    
    @Test
    public void testSaveCart_MultipleFood() {
        ArrayList<Food> manyFood = new ArrayList<>();
        
        manyFood.add(new Popcorn("Caramel Popcorn", 15.00));
        manyFood.add(new Beverage("Coke", 5.00));
        manyFood.add(new HotFood("Hot Dog", 10.00));
        
        cartManager.saveCart(testCustomer, testTickets, manyFood);
        
        CartManager.CartData loaded = cartManager.loadCart(testCustomer);
        
        assertEquals("Should have 3 food items", 3, loaded.food.size());
    }
    
    @Test
    public void testSaveCart_SpecialCharactersInNames() {
        Popcorn special = new Popcorn("Large \"Special\" Popcorn", 15.00);
        ArrayList<Food> specialFood = new ArrayList<>();
        specialFood.add(special);
        
        cartManager.saveCart(testCustomer, testTickets, specialFood);
        
        CartManager.CartData loaded = cartManager.loadCart(testCustomer);
        
        assertNotNull("Should handle special characters", loaded);
        assertEquals(1, loaded.food.size());
    }
    
    @Test
    public void testGetCartSeatIds_DifferentRows() {
        ArrayList<Seat> mixedSeats = new ArrayList<>();
        mixedSeats.add(new Seat(new SeatId('A', 1), "Single", "Reserved", testHall));
        mixedSeats.add(new Seat(new SeatId('B', 2), "Single", "Reserved", testHall));
        mixedSeats.add(new Seat(new SeatId('C', 3), "Single", "Reserved", testHall));
        
        ArrayList<Ticket> mixedTicket = new ArrayList<>();
        mixedTicket.add(new Ticket(testShowtime, 3, testHall, mixedSeats));
        
        cartManager.saveCart(testCustomer, mixedTicket, testFood);
        
        List<SeatId> seatIds = cartManager.getCartSeatIds(testCustomer);
        
        assertEquals(3, seatIds.size());
        assertTrue("Should contain A1", seatIds.contains(new SeatId('A', 1)));
        assertTrue("Should contain B2", seatIds.contains(new SeatId('B', 2)));
        assertTrue("Should contain C3", seatIds.contains(new SeatId('C', 3)));
    }
}
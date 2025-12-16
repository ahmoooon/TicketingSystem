package infrastructure.repositories;

import domain.*;
import domain.valueobjects.SeatId;
import infrastructure.repositories.DataFileHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Optional;

public class PaymentRepositoryTest {
    
    private PaymentRepository repository;
    private static final String TEST_FILE = "payment_test.json";
    
    private Customer testCustomer;
    private Movie testMovie;
    private CinemaHall testHall;
    private Showtime testShowtime;
    private ArrayList<Ticket> testTickets;
    private ArrayList<Food> testFood;
    
    @Before
    public void setUp() {
        // Clean up before test
        DataFileHandler.saveToJsonFile(new ArrayList<>(), TEST_FILE);
        
        // Reset Payment ID counter
        Payment.setLastID(0);
        
        // Initialize test data
        testCustomer = new Customer("Test Customer", "password");
        testMovie = new Movie(1, "Test Movie", 2.0, "Test Director", "2025-01-01");
        testHall = new CinemaHall(1, CinemaHall.HALL_TYPE_STANDARD, 5, 10);
        testShowtime = new Showtime(testMovie, 2025, 1, 15, "10:00 AM", testHall);
        
        // Create test tickets
        testTickets = new ArrayList<>();
        ArrayList<Seat> seats = new ArrayList<>();
        seats.add(new Seat(new SeatId('A', 1), "Single", "Booked", testHall));
        seats.add(new Seat(new SeatId('A', 2), "Single", "Booked", testHall));
        testTickets.add(new Ticket(testShowtime, 2, testHall, seats));
        
        // Create test food
        testFood = new ArrayList<>();
        Popcorn popcorn = new Popcorn("Large Popcorn", 12.00);
        popcorn.setQty(1);
        popcorn.setPrice(12.00);
        testFood.add(popcorn);
        
        // Initialize repository
        repository = new PaymentRepository(TEST_FILE);
    }
    
    @After
    public void tearDown() {
        // Clean up after test
        DataFileHandler.saveToJsonFile(new ArrayList<>(), TEST_FILE);
    }
    
    @Test
    public void testConstructor_EmptyFile() {
        assertNotNull(repository);
        assertTrue(repository.getAllPayments().isEmpty());
    }
    
    @Test
    public void testSavePayment_Success() {
        Payment payment = new Payment(
            Optional.of(testCustomer), 
            testTickets, 
            testFood, 
            42.00, 
            true
        );
        
        repository.savePayment(payment);
        
        ArrayList<Payment> payments = repository.getAllPayments();
        assertEquals(1, payments.size());
        assertEquals(payment.getPaymentID(), payments.get(0).getPaymentID());
    }
    
    @Test
    public void testSavePayment_MultiplePayments() {
        Payment p1 = new Payment(Optional.of(testCustomer), testTickets, testFood, 50.00, true);
        Payment p2 = new Payment(Optional.of(testCustomer), testTickets, testFood, 75.00, true);
        
        repository.savePayment(p1);
        repository.savePayment(p2);
        
        ArrayList<Payment> payments = repository.getAllPayments();
        assertEquals(2, payments.size());
    }
    
    @Test
    public void testGetAllPayments_ReturnsDefensiveCopy() {
        Payment payment = new Payment(Optional.of(testCustomer), testTickets, testFood, 50.00, true);
        repository.savePayment(payment);
        
        ArrayList<Payment> payments1 = repository.getAllPayments();
        ArrayList<Payment> payments2 = repository.getAllPayments();
        
        assertNotSame(payments1, payments2);
        assertEquals(payments1.size(), payments2.size());
    }
    
    @Test
    public void testPersistence_SaveAndReload() {
        // Save payment
        Payment payment = new Payment(Optional.of(testCustomer), testTickets, testFood, 50.00, true);
        repository.savePayment(payment);
        
        // Create new repository instance (simulates app restart)
        PaymentRepository newRepo = new PaymentRepository(TEST_FILE);
        
        ArrayList<Payment> loaded = newRepo.getAllPayments();
        assertEquals(1, loaded.size());
        assertEquals(payment.getPaymentID(), loaded.get(0).getPaymentID());
    }
    
    @Test
    public void testSavePayment_WithGuestCustomer() {
        Payment payment = new Payment(Optional.empty(), testTickets, testFood, 50.00, true);
        
        repository.savePayment(payment);
        
        ArrayList<Payment> payments = repository.getAllPayments();
        assertEquals(1, payments.size());
        assertFalse(payments.get(0).getCustomer().isPresent());
    }
    
    @Test
    public void testSavePayment_WithEmptyTickets() {
        Payment payment = new Payment(
            Optional.of(testCustomer), 
            new ArrayList<>(), 
            testFood, 
            12.00, 
            true
        );
        
        repository.savePayment(payment);
        
        ArrayList<Payment> payments = repository.getAllPayments();
        assertEquals(1, payments.size());
        assertTrue(payments.get(0).getTicket().isEmpty());
    }
    
    @Test
    public void testSavePayment_WithEmptyFood() {
        Payment payment = new Payment(
            Optional.of(testCustomer), 
            testTickets, 
            new ArrayList<>(), 
            30.00, 
            true
        );
        
        repository.savePayment(payment);
        
        ArrayList<Payment> payments = repository.getAllPayments();
        assertEquals(1, payments.size());
        assertTrue(payments.get(0).getFood().isEmpty());
    }
    
    @Test
    public void testPaymentIdSync_AfterLoad() {
        // Save payments with specific IDs
        Payment p1 = new Payment(Optional.of(testCustomer), testTickets, testFood, 50.00, true);
        p1.setPaymentID(100);
        repository.savePayment(p1);
        
        // Reload repository
        PaymentRepository newRepo = new PaymentRepository(TEST_FILE);
        
        // Create new payment - should have ID > 100
        Payment p2 = new Payment(Optional.of(testCustomer), testTickets, testFood, 50.00, true);
        newRepo.savePayment(p2);
        
        assertTrue(p2.getPaymentID() > 100);
    }
    
    @Test
    public void testSavePayment_LargeAmount() {
        Payment payment = new Payment(
            Optional.of(testCustomer), 
            testTickets, 
            testFood, 
            9999.99, 
            true
        );
        
        repository.savePayment(payment);
        
        ArrayList<Payment> payments = repository.getAllPayments();
        assertEquals(9999.99, payments.get(0).getTotalPrice(), 0.001);
    }
    
    @Test
    public void testSavePayment_ZeroAmount() {
        Payment payment = new Payment(
            Optional.of(testCustomer), 
            testTickets, 
            testFood, 
            0.0, 
            true
        );
        
        repository.savePayment(payment);
        
        ArrayList<Payment> payments = repository.getAllPayments();
        assertEquals(0.0, payments.get(0).getTotalPrice(), 0.001);
    }
    
    @Test
    public void testSavePayment_PaymentNotMade() {
        Payment payment = new Payment(
            Optional.of(testCustomer), 
            testTickets, 
            testFood, 
            50.00, 
            false // Not paid
        );
        
        repository.savePayment(payment);
        
        ArrayList<Payment> payments = repository.getAllPayments();
        assertEquals(1, payments.size());
    }
    
    @Test
    public void testSavePayment_MultipleSeats() {
        ArrayList<Seat> manySeats = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            manySeats.add(new Seat(new SeatId('A', i), "Single", "Booked", testHall));
        }
        
        ArrayList<Ticket> tickets = new ArrayList<>();
        tickets.add(new Ticket(testShowtime, 10, testHall, manySeats));
        
        Payment payment = new Payment(Optional.of(testCustomer), tickets, testFood, 150.00, true);
        repository.savePayment(payment);
        
        ArrayList<Payment> loaded = repository.getAllPayments();
        assertEquals(10, loaded.get(0).getTicket().get(0).getSeat().size());
    }
    
    @Test
    public void testSavePayment_MultipleFood() {
        ArrayList<Food> foods = new ArrayList<>();
        
        Popcorn p = new Popcorn("Popcorn", 10.00);
        p.setQty(2);
        p.setPrice(20.00);
        foods.add(p);
        
        Beverage b = new Beverage("Coke", 5.00);
        b.setQty(1);
        b.setPrice(5.00);
        foods.add(b);
        
        Payment payment = new Payment(Optional.of(testCustomer), testTickets, foods, 55.00, true);
        repository.savePayment(payment);
        
        ArrayList<Payment> loaded = repository.getAllPayments();
        assertEquals(2, loaded.get(0).getFood().size());
    }
    
    @Test
    public void testSavePayment_SpecialCharactersInName() {
        Customer special = new Customer("John O'Brien-Smith", "password");
        Payment payment = new Payment(Optional.of(special), testTickets, testFood, 50.00, true);
        
        repository.savePayment(payment);
        
        ArrayList<Payment> payments = repository.getAllPayments();
        assertEquals(1, payments.size());
    }
    
    @Test
    public void testGetAllPayments_OrderPreserved() {
        Payment p1 = new Payment(Optional.of(testCustomer), testTickets, testFood, 30.00, true);
        Payment p2 = new Payment(Optional.of(testCustomer), testTickets, testFood, 40.00, true);
        Payment p3 = new Payment(Optional.of(testCustomer), testTickets, testFood, 50.00, true);
        
        repository.savePayment(p1);
        repository.savePayment(p2);
        repository.savePayment(p3);
        
        ArrayList<Payment> payments = repository.getAllPayments();
        assertEquals(3, payments.size());
        assertEquals(30.00, payments.get(0).getTotalPrice(), 0.001);
        assertEquals(40.00, payments.get(1).getTotalPrice(), 0.001);
        assertEquals(50.00, payments.get(2).getTotalPrice(), 0.001);
    }
}
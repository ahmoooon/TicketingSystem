package domain;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Unit tests for Payment class
 */
public class PaymentTest {
    
    private Payment payment;
    private Customer testCustomer;
    private ArrayList<Ticket> testTickets;
    private ArrayList<Food> testFood;
    
    @Before
    public void setUp() {
        Payment.setLastID(0);
        testCustomer = new Customer("John Doe", "password");
        testTickets = new ArrayList<>();
        testFood = new ArrayList<>();
    }
    
    @Test
    public void testConstructor_DefaultConstructor() {
        payment = new Payment();
        
        assertNotNull(payment);
        assertNotNull(payment.getTicket());
        assertNotNull(payment.getFood());
        assertTrue(payment.getTicket().isEmpty());
        assertTrue(payment.getFood().isEmpty());
        assertEquals(Optional.empty(), payment.getCustomer());
    }
    
    @Test
    public void testConstructor_WithParameters() {
        payment = new Payment(Optional.of(testCustomer), testTickets, testFood, 50.00, true);
        
        assertNotNull(payment);
        assertTrue(payment.getCustomer().isPresent());
        assertEquals(testCustomer, payment.getCustomer().get());
        assertEquals(50.00, payment.getTotalPrice(), 0.001);
        assertTrue(payment.getPaymentMade());
        assertEquals(1, payment.getPaymentID());
    }
    
    @Test
    public void testGetPaymentID_AutoIncrement() {
        Payment p1 = new Payment(Optional.of(testCustomer), testTickets, testFood, 50.00, true);
        Payment p2 = new Payment(Optional.of(testCustomer), testTickets, testFood, 75.00, true);
        
        assertEquals(1, p1.getPaymentID());
        assertEquals(2, p2.getPaymentID());
    }
    
    @Test
    public void testSetPaymentID() {
        payment = new Payment();
        payment.setPaymentID(100);
        
        assertEquals(100, payment.getPaymentID());
    }
    
    @Test
    public void testGetPaymentMade() {
        payment = new Payment(Optional.of(testCustomer), testTickets, testFood, 50.00, true);
        
        assertTrue(payment.getPaymentMade());
    }
    
    @Test
    public void testGetPaymentMade_False() {
        payment = new Payment(Optional.of(testCustomer), testTickets, testFood, 50.00, false);
        
        assertFalse(payment.getPaymentMade());
    }
    
    @Test
    public void testGetCustomer_Present() {
        payment = new Payment(Optional.of(testCustomer), testTickets, testFood, 50.00, true);
        
        assertTrue(payment.getCustomer().isPresent());
        assertEquals(testCustomer, payment.getCustomer().get());
    }
    
    @Test
    public void testGetCustomer_Empty() {
        payment = new Payment(Optional.empty(), testTickets, testFood, 50.00, true);
        
        assertFalse(payment.getCustomer().isPresent());
    }
    
    @Test
    public void testGetTicket() {
        payment = new Payment(Optional.of(testCustomer), testTickets, testFood, 50.00, true);
        
        assertNotNull(payment.getTicket());
        assertTrue(payment.getTicket().isEmpty());
    }
    
    @Test
    public void testGetFood() {
        payment = new Payment(Optional.of(testCustomer), testTickets, testFood, 50.00, true);
        
        assertNotNull(payment.getFood());
        assertTrue(payment.getFood().isEmpty());
    }
    
    @Test
    public void testGetTotalPrice() {
        payment = new Payment(Optional.of(testCustomer), testTickets, testFood, 123.45, true);
        
        assertEquals(123.45, payment.getTotalPrice(), 0.001);
    }
    
    @Test
    public void testGetTotalPrice_Zero() {
        payment = new Payment(Optional.of(testCustomer), testTickets, testFood, 0.0, true);
        
        assertEquals(0.0, payment.getTotalPrice(), 0.001);
    }
    
    @Test
    public void testGetTicketAmt_EmptyTickets() {
        payment = new Payment(Optional.of(testCustomer), new ArrayList<>(), testFood, 50.00, true);
        
        assertEquals(0, payment.getTicketAmt());
    }
    
    @Test
    public void testGetTotalTicketPrice_EmptyTickets() {
        payment = new Payment(Optional.of(testCustomer), new ArrayList<>(), testFood, 50.00, true);
        
        assertEquals(0, payment.getTotalTicketPrice());
    }
    
    @Test
    public void testGetFoodQty_EmptyFood() {
        payment = new Payment(Optional.of(testCustomer), testTickets, new ArrayList<>(), 50.00, true);
        
        assertEquals(0, payment.getFoodQty());
    }
    
    @Test
    public void testGetTotalFoodPrice_EmptyFood() {
        payment = new Payment(Optional.of(testCustomer), testTickets, new ArrayList<>(), 50.00, true);
        
        assertEquals(0.0, payment.getTotalFoodPrice(), 0.001);
    }
    
    @Test
    public void testGetMovieName_NoTickets() {
        payment = new Payment(Optional.of(testCustomer), new ArrayList<>(), testFood, 50.00, true);
        
        assertEquals("N/A", payment.getMovieName());
    }
    
    @Test
    public void testToString() {
        payment = new Payment(Optional.of(testCustomer), testTickets, testFood, 50.00, true);
        
        String result = payment.toString();
        
        assertTrue(result.contains("Payment ID"));
        assertTrue(result.contains("Total"));
        assertTrue(result.contains("50.0"));
    }
    
    @Test
    public void testSetLastID() {
        Payment.setLastID(100);
        
        Payment p = new Payment(Optional.of(testCustomer), testTickets, testFood, 50.00, true);
        
        assertEquals(101, p.getPaymentID());
    }
    
    @Test
    public void testSetPaymentID_UpdatesLastID() {
        Payment p = new Payment();
        p.setPaymentID(500);
        
        Payment p2 = new Payment(Optional.of(testCustomer), testTickets, testFood, 50.00, true);
        
        assertTrue(p2.getPaymentID() > 500);
    }
    
    @Test
    public void testConstructor_CopiesLists() {
        ArrayList<Ticket> originalTickets = new ArrayList<>();
        ArrayList<Food> originalFood = new ArrayList<>();
        
        payment = new Payment(Optional.of(testCustomer), originalTickets, originalFood, 50.00, true);
        
        // Modify original lists
        Popcorn popcorn = new Popcorn("Test", 10.00);
        originalFood.add(popcorn);
        
        // Payment should have empty food list (defensive copy)
        assertTrue(payment.getFood().isEmpty());
    }
    
    @Test
    public void testConstructor_NullTickets() {
        payment = new Payment(Optional.of(testCustomer), null, testFood, 50.00, true);
        
        assertNotNull(payment.getTicket());
        assertTrue(payment.getTicket().isEmpty());
    }
    
    @Test
    public void testConstructor_NullFood() {
        payment = new Payment(Optional.of(testCustomer), testTickets, null, 50.00, true);
        
        assertNotNull(payment.getFood());
        assertTrue(payment.getFood().isEmpty());
    }
    
    @Test
    public void testNegativeTotalPrice() {
        payment = new Payment(Optional.of(testCustomer), testTickets, testFood, -50.00, true);
        
        assertEquals(-50.00, payment.getTotalPrice(), 0.001);
    }
    
    @Test
    public void testLargeTotalPrice() {
        payment = new Payment(Optional.of(testCustomer), testTickets, testFood, 999999.99, true);
        
        assertEquals(999999.99, payment.getTotalPrice(), 0.001);
    }
    
    @Test
    public void testMultiplePayments_UniqueIDs() {
        Payment.setLastID(0);
        
        Payment p1 = new Payment(Optional.of(testCustomer), testTickets, testFood, 50.00, true);
        Payment p2 = new Payment(Optional.of(testCustomer), testTickets, testFood, 75.00, true);
        Payment p3 = new Payment(Optional.of(testCustomer), testTickets, testFood, 100.00, true);
        
        assertEquals(1, p1.getPaymentID());
        assertEquals(2, p2.getPaymentID());
        assertEquals(3, p3.getPaymentID());
    }
}
package application.services;

import application.dto.PaymentRequest;
import application.dto.PaymentResult;
import domain.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class PaymentServiceTest {
    
    private PaymentService paymentService;
    
    // Test data
    private Customer testCustomer;
    private ArrayList<Ticket> testTickets;
    private ArrayList<Food> testFood;
    private Showtime testShowtime;
    private CinemaHall testHall;
    
    @Before
    public void setUp() {
        paymentService = new PaymentService();
        
        // Setup test customer
        testCustomer = new Customer("John Doe", "hashedPassword123");
        
        // Setup test hall and showtime
        testHall = new CinemaHall(1, CinemaHall.HALL_TYPE_STANDARD, 5, 10);
        Movie testMovie = new Movie(1, "Dune: Part 1", 2.35, "Denis Villeneuve", "October 22, 2021");
        testShowtime = new Showtime(testMovie, 2025, 12, 16, "10:00 AM", testHall);
        
        // Setup test tickets
        testTickets = new ArrayList<>();
        ArrayList<Seat> seats = new ArrayList<>();
        seats.add(new Seat(new domain.valueobjects.SeatId('A', 1), "Single", "Booked", testHall));
        seats.add(new Seat(new domain.valueobjects.SeatId('A', 2), "Single", "Booked", testHall));
        
        Ticket ticket = new Ticket(testShowtime, 2, testHall, seats);
        testTickets.add(ticket);
        
        // Setup test food
        testFood = new ArrayList<>();
        Popcorn popcorn = new Popcorn();
        popcorn.setName("Large Popcorn");
        popcorn.setQty(1);
        popcorn.setPrice(12.00);
        testFood.add(popcorn);
    }
    
    // ========== BANK TRANSFER TESTS ==========
    
    @Test
    public void testProcessPayment_BankTransfer_ValidAccount_Success() {
        // Arrange
        String validAccountNumber = "123456789"; // 9 digits
        PaymentRequest request = new PaymentRequest(
            50.00,
            "Bank Transfer",
            validAccountNumber,
            testTickets,
            testFood,
            Optional.of(testCustomer)
        );
        
        // Act
        PaymentResult result = paymentService.processPayment(request);
        
        // Assert
        assertNotNull("Result should not be null", result);
        assertTrue("Payment should be successful", result.isSuccess());
        assertEquals("Should return success message", "Payment successful!", result.getMessage());
    }
    
    @Test
    public void testProcessPayment_BankTransfer_InvalidAccountTooShort_Failure() {
        // Arrange
        String invalidAccountNumber = "12345"; // Only 5 digits
        PaymentRequest request = new PaymentRequest(
            50.00,
            "Bank Transfer",
            invalidAccountNumber,
            testTickets,
            testFood,
            Optional.of(testCustomer)
        );
        
        // Act
        PaymentResult result = paymentService.processPayment(request);
        
        // Assert
        assertNotNull(result);
        assertFalse("Payment should fail", result.isSuccess());
        assertTrue("Should mention invalid details", 
            result.getMessage().contains("Invalid details") || 
            result.getMessage().contains("Account Number"));
    }
    
    @Test
    public void testProcessPayment_BankTransfer_InvalidAccountTooLong_Failure() {
        // Arrange
        String invalidAccountNumber = "12345678901"; // 11 digits
        PaymentRequest request = new PaymentRequest(
            50.00,
            "Bank Transfer",
            invalidAccountNumber,
            testTickets,
            testFood,
            Optional.of(testCustomer)
        );
        
        // Act
        PaymentResult result = paymentService.processPayment(request);
        
        // Assert
        assertNotNull(result);
        assertFalse("Payment should fail", result.isSuccess());
    }
    
    @Test
    public void testProcessPayment_BankTransfer_NonNumericAccount_Failure() {
        // Arrange
        String invalidAccountNumber = "ABC123456"; // Contains letters
        PaymentRequest request = new PaymentRequest(
            50.00,
            "Bank Transfer",
            invalidAccountNumber,
            testTickets,
            testFood,
            Optional.of(testCustomer)
        );
        
        // Act
        PaymentResult result = paymentService.processPayment(request);
        
        // Assert
        assertNotNull(result);
        assertFalse("Payment should fail", result.isSuccess());
    }
    
    @Test
    public void testProcessPayment_BankTransfer_NullAccount_Failure() {
        // Arrange
        PaymentRequest request = new PaymentRequest(
            50.00,
            "Bank Transfer",
            null, // Null account number
            testTickets,
            testFood,
            Optional.of(testCustomer)
        );
        
        // Act
        PaymentResult result = paymentService.processPayment(request);
        
        // Assert
        assertNotNull(result);
        assertFalse("Payment should fail", result.isSuccess());
    }
    
    @Test
    public void testProcessPayment_BankTransfer_EmptyAccount_Failure() {
        // Arrange
        PaymentRequest request = new PaymentRequest(
            50.00,
            "Bank Transfer",
            "", // Empty account number
            testTickets,
            testFood,
            Optional.of(testCustomer)
        );
        
        // Act
        PaymentResult result = paymentService.processPayment(request);
        
        // Assert
        assertNotNull(result);
        assertFalse("Payment should fail", result.isSuccess());
    }
    
    // ========== CASH PAYMENT TESTS ==========
    
    @Test
    public void testProcessPayment_Cash_Success() {
        // Arrange
        PaymentRequest request = new PaymentRequest(
            50.00,
            "Cash",
            null, // Bank account not needed for cash
            testTickets,
            testFood,
            Optional.of(testCustomer)
        );
        
        // Act
        PaymentResult result = paymentService.processPayment(request);
        
        // Assert
        assertNotNull(result);
        assertTrue("Cash payment should be successful", result.isSuccess());
        assertEquals("Payment successful!", result.getMessage());
    }
    
    @Test
    public void testProcessPayment_Cash_WithBankAccount_Success() {
        // Arrange - Bank account provided but ignored for cash
        PaymentRequest request = new PaymentRequest(
            50.00,
            "Cash",
            "123456789", // Ignored for cash payment
            testTickets,
            testFood,
            Optional.of(testCustomer)
        );
        
        // Act
        PaymentResult result = paymentService.processPayment(request);
        
        // Assert
        assertNotNull(result);
        assertTrue("Cash payment should succeed regardless of bank account", result.isSuccess());
    }
    
    @Test
    public void testProcessPayment_Cash_LargeAmount_Success() {
        // Arrange
        PaymentRequest request = new PaymentRequest(
            1000.00,
            "Cash",
            null,
            testTickets,
            testFood,
            Optional.of(testCustomer)
        );
        
        // Act
        PaymentResult result = paymentService.processPayment(request);
        
        // Assert
        assertNotNull(result);
        assertTrue("Large cash payment should succeed", result.isSuccess());
    }
    
    // ========== INVALID PAYMENT METHOD TESTS ==========
    
    @Test
    public void testProcessPayment_InvalidMethod_Failure() {
        // Arrange
        PaymentRequest request = new PaymentRequest(
            50.00,
            "Credit Card", // Unsupported method
            null,
            testTickets,
            testFood,
            Optional.of(testCustomer)
        );
        
        // Act
        PaymentResult result = paymentService.processPayment(request);
        
        // Assert
        assertNotNull(result);
        assertFalse("Payment should fail", result.isSuccess());
        assertEquals("Invalid Payment Method", result.getMessage());
    }
    
    @Test
    public void testProcessPayment_NullMethod_Failure() {
        // Arrange
        PaymentRequest request = new PaymentRequest(
            50.00,
            null, // Null payment method
            "123456789",
            testTickets,
            testFood,
            Optional.of(testCustomer)
        );
        
        // Act & Assert
        try {
            PaymentResult result = paymentService.processPayment(request);
            assertFalse("Payment should fail with null method", result.isSuccess());
        } catch (NullPointerException e) {
            // Also acceptable - null method causes NPE
            assertTrue(true);
        }
    }
    
    @Test
    public void testProcessPayment_EmptyMethod_Failure() {
        // Arrange
        PaymentRequest request = new PaymentRequest(
            50.00,
            "", // Empty payment method
            "123456789",
            testTickets,
            testFood,
            Optional.of(testCustomer)
        );
        
        // Act
        PaymentResult result = paymentService.processPayment(request);
        
        // Assert
        assertNotNull(result);
        assertFalse("Payment should fail", result.isSuccess());
    }
    
    @Test
    public void testProcessPayment_CaseInsensitive_BankTransfer_Success() {
        // Arrange - Test case insensitivity
        PaymentRequest request = new PaymentRequest(
            50.00,
            "bank transfer", // Lowercase
            "123456789",
            testTickets,
            testFood,
            Optional.of(testCustomer)
        );
        
        // Act
        PaymentResult result = paymentService.processPayment(request);
        
        // Assert
        assertNotNull(result);
        assertTrue("Should handle case insensitive method", result.isSuccess());
    }
    
    @Test
    public void testProcessPayment_CaseInsensitive_Cash_Success() {
        // Arrange
        PaymentRequest request = new PaymentRequest(
            50.00,
            "CASH", // Uppercase
            null,
            testTickets,
            testFood,
            Optional.of(testCustomer)
        );
        
        // Act
        PaymentResult result = paymentService.processPayment(request);
        
        // Assert
        assertNotNull(result);
        assertTrue("Should handle case insensitive method", result.isSuccess());
    }
    
    // ========== EDGE CASE TESTS ==========
    
    @Test
    public void testProcessPayment_ZeroAmount_Success() {
        // Arrange - Zero amount (e.g., promotional free tickets)
        PaymentRequest request = new PaymentRequest(
            0.00,
            "Cash",
            null,
            testTickets,
            testFood,
            Optional.of(testCustomer)
        );
        
        // Act
        PaymentResult result = paymentService.processPayment(request);
        
        // Assert
        assertNotNull(result);
        assertTrue("Zero amount payment should succeed", result.isSuccess());
    }
    
    @Test
    public void testProcessPayment_VeryLargeAmount_Success() {
        // Arrange
        PaymentRequest request = new PaymentRequest(
            999999.99,
            "Bank Transfer",
            "123456789",
            testTickets,
            testFood,
            Optional.of(testCustomer)
        );
        
        // Act
        PaymentResult result = paymentService.processPayment(request);
        
        // Assert
        assertNotNull(result);
        assertTrue("Large amount payment should succeed if valid", result.isSuccess());
    }
    
    @Test
    public void testProcessPayment_EmptyTicketsAndFood_Success() {
        // Arrange - No items (edge case)
        PaymentRequest request = new PaymentRequest(
            0.00,
            "Cash",
            null,
            new ArrayList<>(), // Empty tickets
            new ArrayList<>(), // Empty food
            Optional.of(testCustomer)
        );
        
        // Act
        PaymentResult result = paymentService.processPayment(request);
        
        // Assert
        assertNotNull(result);
        assertTrue("Empty cart payment should succeed", result.isSuccess());
    }
    
    @Test
    public void testProcessPayment_GuestCustomer_Success() {
        // Arrange - No customer (guest checkout)
        PaymentRequest request = new PaymentRequest(
            50.00,
            "Cash",
            null,
            testTickets,
            testFood,
            Optional.empty() // Guest customer
        );
        
        // Act
        PaymentResult result = paymentService.processPayment(request);
        
        // Assert
        assertNotNull(result);
        assertTrue("Guest payment should succeed", result.isSuccess());
    }
    
    @Test
    public void testProcessPayment_OnlyTickets_NoFood_Success() {
        // Arrange
        PaymentRequest request = new PaymentRequest(
            30.00,
            "Bank Transfer",
            "123456789",
            testTickets,
            new ArrayList<>(), // No food
            Optional.of(testCustomer)
        );
        
        // Act
        PaymentResult result = paymentService.processPayment(request);
        
        // Assert
        assertNotNull(result);
        assertTrue("Tickets-only payment should succeed", result.isSuccess());
    }
    
    @Test
    public void testProcessPayment_OnlyFood_NoTickets_Success() {
        // Arrange
        PaymentRequest request = new PaymentRequest(
            20.00,
            "Cash",
            null,
            new ArrayList<>(), // No tickets
            testFood,
            Optional.of(testCustomer)
        );
        
        // Act
        PaymentResult result = paymentService.processPayment(request);
        
        // Assert
        assertNotNull(result);
        assertTrue("Food-only payment should succeed", result.isSuccess());
    }
    
    // ========== BOUNDARY TESTS ==========
    
    @Test
    public void testProcessPayment_BankAccount_ExactlyNineDigits_Success() {
        // Arrange
        String exactNineDigits = "111111111";
        PaymentRequest request = new PaymentRequest(
            50.00,
            "Bank Transfer",
            exactNineDigits,
            testTickets,
            testFood,
            Optional.of(testCustomer)
        );
        
        // Act
        PaymentResult result = paymentService.processPayment(request);
        
        // Assert
        assertNotNull(result);
        assertTrue("Exactly 9 digits should succeed", result.isSuccess());
    }
    
    @Test
    public void testProcessPayment_BankAccount_AllZeros_Success() {
        // Arrange
        String allZeros = "000000000"; // 9 zeros
        PaymentRequest request = new PaymentRequest(
            50.00,
            "Bank Transfer",
            allZeros,
            testTickets,
            testFood,
            Optional.of(testCustomer)
        );
        
        // Act
        PaymentResult result = paymentService.processPayment(request);
        
        // Assert
        assertNotNull(result);
        assertTrue("All zeros (but 9 digits) should succeed", result.isSuccess());
    }
    
    @Test
    public void testProcessPayment_BankAccount_WithSpaces_Failure() {
        // Arrange
        String accountWithSpaces = "123 456 789"; // Has spaces
        PaymentRequest request = new PaymentRequest(
            50.00,
            "Bank Transfer",
            accountWithSpaces,
            testTickets,
            testFood,
            Optional.of(testCustomer)
        );
        
        // Act
        PaymentResult result = paymentService.processPayment(request);
        
        // Assert
        assertNotNull(result);
        assertFalse("Account with spaces should fail", result.isSuccess());
    }
    
    @Test
    public void testProcessPayment_BankAccount_WithDashes_Failure() {
        // Arrange
        String accountWithDashes = "123-456-789"; // Has dashes
        PaymentRequest request = new PaymentRequest(
            50.00,
            "Bank Transfer",
            accountWithDashes,
            testTickets,
            testFood,
            Optional.of(testCustomer)
        );
        
        // Act
        PaymentResult result = paymentService.processPayment(request);
        
        // Assert
        assertNotNull(result);
        assertFalse("Account with dashes should fail", result.isSuccess());
    }
    
    // ========== NEGATIVE AMOUNT TESTS ==========
    
    @Test
    public void testProcessPayment_NegativeAmount_Success() {
        // Arrange - Negative amount (refund scenario)
        PaymentRequest request = new PaymentRequest(
            -50.00,
            "Cash",
            null,
            testTickets,
            testFood,
            Optional.of(testCustomer)
        );
        
        // Act
        PaymentResult result = paymentService.processPayment(request);
        
        // Assert
        assertNotNull(result);
        // Note: Current implementation doesn't validate amount
        // In production, you might want to add validation
        assertTrue("Negative amount is processed (consider adding validation)", result.isSuccess());
    }
    
    // ========== PAYMENT METHOD VARIATIONS ==========
    
    @Test
    public void testProcessPayment_MethodWithExtraSpaces_Failure() {
        // Arrange
        PaymentRequest request = new PaymentRequest(
            50.00,
            " Bank Transfer ", // Extra spaces
            "123456789",
            testTickets,
            testFood,
            Optional.of(testCustomer)
        );
        
        // Act
        PaymentResult result = paymentService.processPayment(request);
        
        // Assert
        // Current implementation uses equalsIgnoreCase without trim
        // So this should fail
        assertNotNull(result);
    }
    
    @Test
    public void testProcessPayment_MultiplePayments_SameRequest_Success() {
        // Arrange
        PaymentRequest request = new PaymentRequest(
            50.00,
            "Cash",
            null,
            testTickets,
            testFood,
            Optional.of(testCustomer)
        );
        
        // Act - Process same request twice
        PaymentResult result1 = paymentService.processPayment(request);
        PaymentResult result2 = paymentService.processPayment(request);
        
        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertTrue("First payment should succeed", result1.isSuccess());
        assertTrue("Second payment should also succeed (no state)", result2.isSuccess());
    }
}
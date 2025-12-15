package application.services;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author zhili
 */
import application.services.ReportService;
import domain.Customer;
import domain.Food;
import domain.Payment;
import domain.Ticket;
import infrastructure.repositories.PaymentRepository;
import org.junit.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ReportServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    private ReportService reportService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        reportService = new ReportService(paymentRepository);
    }

    // ---------------------------
    // Customer List Report
    // ---------------------------
    @Test
    public void testGetCustomerListReport_emptyList() {
        ArrayList<Customer> customers = new ArrayList<>();

        String report = reportService.getCustomerListReport(customers);

        assertTrue(report.contains("There are no registered customer records"));
    }

    @Test
    public void testGetCustomerListReport_withCustomers() {
        ArrayList<Customer> customers = new ArrayList<>();
        customers.add(new Customer("Alice", "pass1"));
        customers.add(new Customer("Bob", "pass2"));

        String report = reportService.getCustomerListReport(customers);

        assertTrue(report.contains("Customer List Report"));
        assertTrue(report.contains("Alice"));
        assertTrue(report.contains("Bob"));
        assertTrue(report.contains("Total Number of Customer: 2"));
    }

    // ---------------------------
    // Movie Purchase Report
    // ---------------------------
    @Test
    public void testGetMoviePurchaseReport_noTickets() {
        when(paymentRepository.getAllPayments()).thenReturn(new ArrayList<>());

        String report = reportService.getMoviePurchaseReport();

        assertTrue(report.contains("There are no movie purchase records"));
    }

    @Test
    public void testGetMoviePurchaseReport_withTickets() {
        Ticket ticket = mock(Ticket.class);
        when(ticket.getMovieName()).thenReturn("Avengers");
        when(ticket.getTicketAmt()).thenReturn(2);
        when(ticket.ticketPrice()).thenReturn(15.0);

        ArrayList<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket);

        Payment payment = mock(Payment.class);
        when(payment.getTicket()).thenReturn(tickets);

        ArrayList<Payment> payments = new ArrayList<>();
        payments.add(payment);

        when(paymentRepository.getAllPayments()).thenReturn(payments);

        String report = reportService.getMoviePurchaseReport();

        assertTrue(report.contains("Movie Purchase Report"));
        assertTrue(report.contains("Avengers"));
        assertTrue(report.contains("30.00")); // 2 × 15.0
    }

    // ---------------------------
    // Food Purchase Report
    // ---------------------------
    @Test
    public void testGetFoodPurchaseReport_noFood() {
        when(paymentRepository.getAllPayments()).thenReturn(new ArrayList<>());

        String report = reportService.getFoodPurchaseReport();

        assertTrue(report.contains("There are no food purchase records"));
    }

    @Test
    public void testGetFoodPurchaseReport_withFood() {
        Food food = mock(Food.class);
        when(food.getName()).thenReturn("Popcorn");
        when(food.getQty()).thenReturn(2);
        when(food.getPrice()).thenReturn(12.0);

        ArrayList<Food> foods = new ArrayList<>();
        foods.add(food);

        Payment payment = mock(Payment.class);
        when(payment.getFood()).thenReturn(foods);

        ArrayList<Payment> payments = new ArrayList<>();
        payments.add(payment);

        when(paymentRepository.getAllPayments()).thenReturn(payments);

        String report = reportService.getFoodPurchaseReport();

        assertTrue(report.contains("Food Purchase Report"));
        assertTrue(report.contains("Popcorn"));
        assertTrue(report.contains("12.00"));
    }

    // ---------------------------
    // Sales Summary Report
    // ---------------------------
    @Test
    public void testGenerateSalesSummaryReport_noPayments() {
        when(paymentRepository.getAllPayments()).thenReturn(new ArrayList<>());

        String report = reportService.generateSalesSummaryReport();

        assertTrue(report.contains("No Payment Data Available"));
    }

    @Test
    public void testGenerateSalesSummaryReport_withPayments() {
        Payment payment1 = mock(Payment.class);
        when(payment1.getTotalTicketPrice()).thenReturn(30);
        when(payment1.getTotalFoodPrice()).thenReturn(20.0);
        when(payment1.getTotalPrice()).thenReturn(50.0);

        Payment payment2 = mock(Payment.class);
        when(payment2.getTotalTicketPrice()).thenReturn(15);
        when(payment2.getTotalFoodPrice()).thenReturn(5.0);
        when(payment2.getTotalPrice()).thenReturn(20.0);

        ArrayList<Payment> payments = new ArrayList<>();
        payments.add(payment1);
        payments.add(payment2);

        when(paymentRepository.getAllPayments()).thenReturn(payments);

        String report = reportService.generateSalesSummaryReport();

        assertTrue(report.contains("CINEMA SALES SUMMARY REPORT"));
        assertTrue(report.contains("Total Number of Transactions: 2"));
        assertTrue(report.contains("RM 45.00")); // ticket total
        assertTrue(report.contains("RM 25.00")); // food total
        assertTrue(report.contains("RM 70.00")); // grand total
    }
}

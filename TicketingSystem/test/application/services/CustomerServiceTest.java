package application.services;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author zhili
 */
import application.services.CustomerService;
import application.services.AuthService;
import domain.Customer;
import infrastructure.repositories.DataFileHandler;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class CustomerServiceTest {

    private CustomerService service;

    // Test customers
    private Customer customerA;
    private Customer customerB;
    private static final String TEST_FILE = "customer_data_test.json";


    @Before
    public void setUp() {
        // --- Prepare test data ---
        customerA = new Customer("Alice", "password123");
        customerB = new Customer("Bob", "password456");

        List<String> testData = new ArrayList<>();
        testData.add(customerA.toJsonString());
        testData.add(customerB.toJsonString());
        testData.add(new Customer(AuthService.STAFF_ID, "staff").toJsonString());

        // Write test data to file BEFORE service is created
        DataFileHandler.saveToJsonFile(testData, TEST_FILE);

        service = new CustomerService(TEST_FILE);
    }

    @After
    public void tearDown() {
        // Clean up after test
        DataFileHandler.saveToJsonFile(new ArrayList<>(), TEST_FILE);
    }

    // ---------------------------
    // Constructor & loading
    // ---------------------------
    @Test
    public void testConstructor_removesStaffAccount() {
        ArrayList<Customer> customers = service.getCustomerList();

        boolean hasStaff = customers.stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(AuthService.STAFF_ID));

        assertFalse(hasStaff);
    }

    @Test
    public void testGetCustomerList_loadedCorrectly() {
        ArrayList<Customer> customers = service.getCustomerList();

        assertEquals(2, customers.size());
    }

    // ---------------------------
    // Logged-in customer
    // ---------------------------
    @Test
    public void testSetAndGetLoggedInCustomer() {
        service.setLoggedInCustomer(customerA);

        Optional<Customer> result = service.getLoggedInCustomer();

        assertTrue(result.isPresent());
        assertEquals("Alice", result.get().getName());
    }

    @Test
    public void testGetLoggedInCustomer_whenNull() {
        service.setLoggedInCustomer(null);

        Optional<Customer> result = service.getLoggedInCustomer();

        assertFalse(result.isPresent());
    }

    // ---------------------------
    // Add customer
    // ---------------------------
    @Test
    public void testAddCustomer() {
        Customer newCustomer = new Customer("Charlie", "pass789");

        service.addCustomer(newCustomer);

        ArrayList<Customer> customers = service.getCustomerList();

        assertEquals(3, customers.size());
        assertTrue(customers.stream()
                .anyMatch(c -> c.getName().equals("Charlie")));
    }

    // ---------------------------
    // Delete customer
    // ---------------------------
    @Test
    public void testDeleteCustomer_success() {
        boolean result = service.deleteCustomer("Alice");

        assertTrue(result);
        assertEquals(1, service.getCustomerList().size());
    }

    @Test
    public void testDeleteCustomer_notFound() {
        boolean result = service.deleteCustomer("Unknown");

        assertFalse(result);
        assertEquals(2, service.getCustomerList().size());
    }

    @Test
    public void testDeleteCustomer_staffAccountNotAllowed() {
        boolean result = service.deleteCustomer(AuthService.STAFF_ID);

        assertFalse(result);
        assertEquals(2, service.getCustomerList().size());
    }

    @Test
    public void testDeleteCustomer_invalidName() {
        assertFalse(service.deleteCustomer(null));
        assertFalse(service.deleteCustomer(""));
        assertFalse(service.deleteCustomer("   "));
    }
}
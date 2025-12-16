package application.services;

import domain.Customer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StaffServiceTest {
    
    @Mock
    private ReportService reportService;
    
    @Mock
    private CustomerService customerService;
    
    private StaffService staffService;
    private ArrayList<Customer> testCustomers;
    
    @Before
    public void setUp() {
        staffService = new StaffService(reportService, customerService);
        
        // Setup test customers
        testCustomers = new ArrayList<>();
        testCustomers.add(new Customer("Alice", "password1"));
        testCustomers.add(new Customer("Bob", "password2"));
        testCustomers.add(new Customer("Charlie", "password3"));
    }
    
    // ========== TEXT REPORT TESTS ==========
    
    @Test
    public void testGetCustomerListReport_Success() {
        String expectedReport = "Customer List Report Content";
        when(reportService.getCustomerListReport(testCustomers))
            .thenReturn(expectedReport);
        
        String result = staffService.getCustomerListReport(testCustomers);
        
        assertEquals(expectedReport, result);
        verify(reportService, times(1)).getCustomerListReport(testCustomers);
    }
    
    @Test
    public void testGetCustomerListReport_EmptyList() {
        ArrayList<Customer> emptyList = new ArrayList<>();
        String expectedReport = "No customers";
        when(reportService.getCustomerListReport(emptyList))
            .thenReturn(expectedReport);
        
        String result = staffService.getCustomerListReport(emptyList);
        
        assertEquals(expectedReport, result);
        verify(reportService, times(1)).getCustomerListReport(emptyList);
    }
    
    @Test
    public void testGetMoviePurchaseReport_Success() {
        String expectedReport = "Movie Purchase Report Content";
        when(reportService.getMoviePurchaseReport())
            .thenReturn(expectedReport);
        
        String result = staffService.getMoviePurchaseReport();
        
        assertEquals(expectedReport, result);
        verify(reportService, times(1)).getMoviePurchaseReport();
    }
    
    @Test
    public void testGetFoodPurchaseReport_Success() {
        String expectedReport = "Food Purchase Report Content";
        when(reportService.getFoodPurchaseReport())
            .thenReturn(expectedReport);
        
        String result = staffService.getFoodPurchaseReport();
        
        assertEquals(expectedReport, result);
        verify(reportService, times(1)).getFoodPurchaseReport();
    }
    
    @Test
    public void testGetSalesSummaryReport_Success() {
        String expectedReport = "Sales Summary Report Content";
        when(reportService.generateSalesSummaryReport())
            .thenReturn(expectedReport);
        
        String result = staffService.getSalesSummaryReport();
        
        assertEquals(expectedReport, result);
        verify(reportService, times(1)).generateSalesSummaryReport();
    }
    
    // ========== PDF EXPORT TESTS ==========
    
    @Test
    public void testExportCustomerListToPdf_Success() throws IOException {
        File testFile = new File("test_customers.pdf");
        
        doNothing().when(reportService)
            .exportCustomerListToPdf(testCustomers, testFile);
        
        staffService.exportCustomerListToPdf(testCustomers, testFile);
        
        verify(reportService, times(1))
            .exportCustomerListToPdf(testCustomers, testFile);
    }
    
    @Test(expected = IOException.class)
    public void testExportCustomerListToPdf_IOException() throws IOException {
        File testFile = new File("invalid/path/test.pdf");
        
        doThrow(new IOException("Cannot write file"))
            .when(reportService)
            .exportCustomerListToPdf(testCustomers, testFile);
        
        staffService.exportCustomerListToPdf(testCustomers, testFile);
    }
    
    @Test
    public void testExportMoviePurchaseToPdf_Success() throws IOException {
        File testFile = new File("test_movies.pdf");
        
        doNothing().when(reportService)
            .exportMoviePurchaseToPdf(testFile);
        
        staffService.exportMoviePurchaseToPdf(testFile);
        
        verify(reportService, times(1))
            .exportMoviePurchaseToPdf(testFile);
    }
    
    @Test(expected = IOException.class)
    public void testExportMoviePurchaseToPdf_IOException() throws IOException {
        File testFile = new File("invalid/path/test.pdf");
        
        doThrow(new IOException("Cannot write file"))
            .when(reportService)
            .exportMoviePurchaseToPdf(testFile);
        
        staffService.exportMoviePurchaseToPdf(testFile);
    }
    
    @Test
    public void testExportFoodPurchaseToPdf_Success() throws IOException {
        File testFile = new File("test_food.pdf");
        
        doNothing().when(reportService)
            .exportFoodPurchaseToPdf(testFile);
        
        staffService.exportFoodPurchaseToPdf(testFile);
        
        verify(reportService, times(1))
            .exportFoodPurchaseToPdf(testFile);
    }
    
    @Test(expected = IOException.class)
    public void testExportFoodPurchaseToPdf_IOException() throws IOException {
        File testFile = new File("invalid/path/test.pdf");
        
        doThrow(new IOException("Cannot write file"))
            .when(reportService)
            .exportFoodPurchaseToPdf(testFile);
        
        staffService.exportFoodPurchaseToPdf(testFile);
    }
    
    @Test
    public void testExportSalesSummaryToPdf_Success() throws IOException {
        File testFile = new File("test_sales.pdf");
        
        doNothing().when(reportService)
            .exportSalesSummaryToPdf(testFile);
        
        staffService.exportSalesSummaryToPdf(testFile);
        
        verify(reportService, times(1))
            .exportSalesSummaryToPdf(testFile);
    }
    
    @Test(expected = IOException.class)
    public void testExportSalesSummaryToPdf_IOException() throws IOException {
        File testFile = new File("invalid/path/test.pdf");
        
        doThrow(new IOException("Cannot write file"))
            .when(reportService)
            .exportSalesSummaryToPdf(testFile);
        
        staffService.exportSalesSummaryToPdf(testFile);
    }
    
    // ========== CUSTOMER MANAGEMENT TESTS ==========
    
    @Test
    public void testDeleteCustomerAccount_Success() {
        String customerName = "Alice";
        when(customerService.deleteCustomer(customerName))
            .thenReturn(true);
        
        boolean result = staffService.deleteCustomerAccount(customerName);
        
        assertTrue(result);
        verify(customerService, times(1)).deleteCustomer(customerName);
    }
    
    @Test
    public void testDeleteCustomerAccount_CustomerNotFound() {
        String customerName = "NonExistent";
        when(customerService.deleteCustomer(customerName))
            .thenReturn(false);
        
        boolean result = staffService.deleteCustomerAccount(customerName);
        
        assertFalse(result);
        verify(customerService, times(1)).deleteCustomer(customerName);
    }
    
    @Test
    public void testDeleteCustomerAccount_NullName() {
        when(customerService.deleteCustomer(null))
            .thenReturn(false);
        
        boolean result = staffService.deleteCustomerAccount(null);
        
        assertFalse(result);
        verify(customerService, times(1)).deleteCustomer(null);
    }
    
    @Test
    public void testDeleteCustomerAccount_EmptyName() {
        when(customerService.deleteCustomer(""))
            .thenReturn(false);
        
        boolean result = staffService.deleteCustomerAccount("");
        
        assertFalse(result);
        verify(customerService, times(1)).deleteCustomer("");
    }
    
    // ========== MULTIPLE OPERATION TESTS ==========
    
    @Test
    public void testMultipleReportGeneration() {
        when(reportService.getCustomerListReport(any()))
            .thenReturn("Customer Report");
        when(reportService.getMoviePurchaseReport())
            .thenReturn("Movie Report");
        when(reportService.getFoodPurchaseReport())
            .thenReturn("Food Report");
        
        staffService.getCustomerListReport(testCustomers);
        staffService.getMoviePurchaseReport();
        staffService.getFoodPurchaseReport();
        
        verify(reportService, times(1)).getCustomerListReport(any());
        verify(reportService, times(1)).getMoviePurchaseReport();
        verify(reportService, times(1)).getFoodPurchaseReport();
    }
    
    @Test
    public void testMultiplePdfExports() throws IOException {
        File file1 = new File("report1.pdf");
        File file2 = new File("report2.pdf");
        File file3 = new File("report3.pdf");
        
        doNothing().when(reportService).exportCustomerListToPdf(any(), any());
        doNothing().when(reportService).exportMoviePurchaseToPdf(any());
        doNothing().when(reportService).exportFoodPurchaseToPdf(any());
        
        staffService.exportCustomerListToPdf(testCustomers, file1);
        staffService.exportMoviePurchaseToPdf(file2);
        staffService.exportFoodPurchaseToPdf(file3);
        
        verify(reportService, times(1)).exportCustomerListToPdf(any(), any());
        verify(reportService, times(1)).exportMoviePurchaseToPdf(any());
        verify(reportService, times(1)).exportFoodPurchaseToPdf(any());
    }
    
    @Test
    public void testDeleteMultipleCustomers() {
        when(customerService.deleteCustomer("Alice")).thenReturn(true);
        when(customerService.deleteCustomer("Bob")).thenReturn(true);
        when(customerService.deleteCustomer("Charlie")).thenReturn(true);
        
        assertTrue(staffService.deleteCustomerAccount("Alice"));
        assertTrue(staffService.deleteCustomerAccount("Bob"));
        assertTrue(staffService.deleteCustomerAccount("Charlie"));
        
        verify(customerService, times(3)).deleteCustomer(anyString());
    }
    
    // ========== EDGE CASE TESTS ==========
    
    @Test
    public void testExportToPdf_SameFileMultipleTimes() throws IOException {
        File testFile = new File("test.pdf");
        
        doNothing().when(reportService).exportSalesSummaryToPdf(testFile);
        
        staffService.exportSalesSummaryToPdf(testFile);
        staffService.exportSalesSummaryToPdf(testFile);
        
        verify(reportService, times(2)).exportSalesSummaryToPdf(testFile);
    }
    
    @Test
    public void testGetReport_CalledMultipleTimes_SameResult() {
        String expectedReport = "Consistent Report";
        when(reportService.generateSalesSummaryReport())
            .thenReturn(expectedReport);
        
        String result1 = staffService.getSalesSummaryReport();
        String result2 = staffService.getSalesSummaryReport();
        
        assertEquals(result1, result2);
        verify(reportService, times(2)).generateSalesSummaryReport();
    }
    
    @Test
    public void testDeleteCustomer_MultipleAttempts() {
        when(customerService.deleteCustomer("Alice"))
            .thenReturn(true)
            .thenReturn(false); // Already deleted
        
        assertTrue(staffService.deleteCustomerAccount("Alice"));
        assertFalse(staffService.deleteCustomerAccount("Alice"));
    }
    
    @Test
    public void testExportEmptyCustomerList() throws IOException {
        ArrayList<Customer> emptyList = new ArrayList<>();
        File testFile = new File("empty.pdf");
        
        doNothing().when(reportService)
            .exportCustomerListToPdf(emptyList, testFile);
        
        staffService.exportCustomerListToPdf(emptyList, testFile);
        
        verify(reportService, times(1))
            .exportCustomerListToPdf(emptyList, testFile);
    }
}
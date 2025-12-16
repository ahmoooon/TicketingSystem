package application.utilities;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

/**
 * Unit tests for PdfReportGenerator class
 */
public class PdfReportGeneratorTest {
    
    private static final String TEST_PDF_FILE = "test_report.pdf";
    private File testFile;
    
    @Before
    public void setUp() {
        testFile = new File(TEST_PDF_FILE);
        // Clean up any existing test file
        if (testFile.exists()) {
            testFile.delete();
        }
    }
    
    @After
    public void tearDown() {
        // Clean up test file
        if (testFile != null && testFile.exists()) {
            testFile.delete();
        }
    }
    
    // ========== BASIC PDF GENERATION TESTS ==========
    
    @Test
    public void testGeneratePdfReport_CreatesFile() throws IOException {
        String content = "Test Report\n\nThis is a simple test report.";
        
        PdfReportGenerator.generatePdfReport(content, testFile, "Test Report");
        
        assertTrue("PDF file should be created", testFile.exists());
        assertTrue("PDF file should have content", testFile.length() > 0);
    }
    
    @Test
    public void testGeneratePdfReport_WithMultipleLines() throws IOException {
        StringBuilder content = new StringBuilder();
        content.append("=".repeat(50)).append("\n");
        content.append("CINEMA REPORT\n");
        content.append("=".repeat(50)).append("\n");
        content.append("Date: 2025-12-16\n");
        content.append("Total Sales: RM 1500.00\n");
        content.append("=".repeat(50)).append("\n");
        
        PdfReportGenerator.generatePdfReport(
            content.toString(), 
            testFile, 
            "Sales Report"
        );
        
        assertTrue("PDF should be created", testFile.exists());
        assertTrue("PDF should have reasonable size", testFile.length() > 1000);
    }
    
    @Test
    public void testGeneratePdfReport_WithEmptyContent() throws IOException {
        String content = "";
        
        PdfReportGenerator.generatePdfReport(content, testFile, "Empty Report");
        
        assertTrue("Should create PDF even with empty content", testFile.exists());
    }
    
    @Test
    public void testGeneratePdfReport_WithTabCharacters() throws IOException {
        String content = "Column1\t\tColumn2\t\tColumn3\n" +
                        "Value1\t\tValue2\t\tValue3\n";
        
        PdfReportGenerator.generatePdfReport(content, testFile, "Tabbed Report");
        
        assertTrue("Should handle tab characters", testFile.exists());
    }
    
    @Test
    public void testGeneratePdfReport_WithSpecialCharacters() throws IOException {
        String content = "Report with special chars: @#$%^&*()\n" +
                        "Currency: RM 100.00\n" +
                        "Percentage: 85%\n";
        
        PdfReportGenerator.generatePdfReport(content, testFile, "Special Chars");
        
        assertTrue("Should handle special characters", testFile.exists());
    }
    
    @Test
    public void testGeneratePdfReport_WithNumbers() throws IOException {
        String content = "Sales Report\n" +
                        "Total: RM 1234.56\n" +
                        "Items: 789\n" +
                        "Discount: 10%\n";
        
        PdfReportGenerator.generatePdfReport(content, testFile, "Numbers Report");
        
        assertTrue("Should handle numbers correctly", testFile.exists());
    }
    
    // ========== SANITIZATION TESTS ==========
    
    @Test
    public void testGeneratePdfReport_SanitizesControlCharacters() throws IOException {
        // Content with control characters that need sanitization
        String content = "Line 1\u0000\n" +  // NULL character
                        "Line 2\u0007\n" +  // BELL character
                        "Line 3\n";
        
        // Should not throw exception
        PdfReportGenerator.generatePdfReport(content, testFile, "Sanitized Report");
        
        assertTrue("Should handle control characters", testFile.exists());
    }
    
    @Test
    public void testGeneratePdfReport_HandlesNewlines() throws IOException {
        String content = "Line 1\nLine 2\nLine 3\n";
        
        PdfReportGenerator.generatePdfReport(content, testFile, "Newlines Report");
        
        assertTrue("Should handle newlines", testFile.exists());
    }
    
    // ========== ERROR HANDLING TESTS ==========
    
    @Test(expected = IOException.class)
    public void testGeneratePdfReport_InvalidPath_ThrowsException() throws IOException {
        String content = "Test";
        File invalidFile = new File("/invalid/path/that/does/not/exist/report.pdf");
        
        PdfReportGenerator.generatePdfReport(content, invalidFile, "Test");
    }
    
    @Test
    public void testGeneratePdfReport_OverwritesExistingFile() throws IOException {
        String content1 = "First version";
        PdfReportGenerator.generatePdfReport(content1, testFile, "Report");
        long firstSize = testFile.length();
        
        String content2 = "Second version with more content to make it longer";
        PdfReportGenerator.generatePdfReport(content2, testFile, "Report");
        long secondSize = testFile.length();
        
        assertTrue("Second file should exist", testFile.exists());
        assertNotEquals("File size should change", firstSize, secondSize);
    }
    
    // ========== VALIDATION TESTS ==========
    
    @Test
    public void testValidateOutputPath_ValidPath() {
        File validFile = new File("valid_report.pdf");
        
        boolean result = PdfReportGenerator.validateOutputPath(validFile);
        
        assertTrue("Valid path should pass validation", result);
        
        // Cleanup
        validFile.delete();
    }
    
    @Test
    public void testValidateOutputPath_CreatesParentDirectory() {
        File fileWithNewDir = new File("test_dir/report.pdf");
        
        boolean result = PdfReportGenerator.validateOutputPath(fileWithNewDir);
        
        assertTrue("Should create parent directory", result);
        
        // Cleanup
        fileWithNewDir.delete();
        new File("test_dir").delete();
    }
    
    @Test
    public void testValidateOutputPath_NullParent() {
        File fileWithoutParent = new File("report.pdf");
        
        boolean result = PdfReportGenerator.validateOutputPath(fileWithoutParent);
        
        assertTrue("Should handle null parent", result);
        
        // Cleanup
        fileWithoutParent.delete();
    }
    
    // ========== INTEGRATION TESTS ==========
    
    @Test
    public void testGeneratePdfReport_CustomerListReport() throws IOException {
        String content = """
                ========================================
                     Customer List Report
                ========================================
                Date: 2025-12-16
                
                No. Customer Name           Password
                ========================================
                1   Alice                   hash123
                2   Bob                     hash456
                3   Charlie                 hash789
                ========================================
                Total Number of Customer: 3
                ========================================
                """;
        
        PdfReportGenerator.generatePdfReport(content, testFile, "Customer List");
        
        assertTrue("Customer report PDF should be created", testFile.exists());
    }
    
    @Test
    public void testGeneratePdfReport_SalesSummaryReport() throws IOException {
        String content = """
                ==============================================
                       CINEMA SALES SUMMARY REPORT
                ==============================================
                Date: 2025-12-16
                Total Number of Transactions: 5
                ----------------------------------------------
                Total Ticket Sales:   RM 450.00
                Total F&B Sales:      RM 125.00
                ----------------------------------------------
                GRAND TOTAL REVENUE:  RM 575.00
                ==============================================
                """;
        
        PdfReportGenerator.generatePdfReport(content, testFile, "Sales Summary");
        
        assertTrue("Sales report PDF should be created", testFile.exists());
    }
    
    @Test
    public void testGeneratePdfReport_MoviePurchaseReport() throws IOException {
        String content = """
                =============================================================
                Movie Purchase Report
                =============================================================
                Date: 2025-12-16
                
                No. Movie Name                    Unit    Total Price
                =============================================================
                1    Dune: Part 1                2        RM 60.00
                2    Blade Runner 2049           3        RM 90.00
                =============================================================
                Sum of Price: RM 150.00
                =============================================================
                """;
        
        PdfReportGenerator.generatePdfReport(content, testFile, "Movie Purchase");
        
        assertTrue("Movie report PDF should be created", testFile.exists());
    }
    
    // ========== EDGE CASES ==========
    
    @Test
    public void testGeneratePdfReport_VeryLongLines() throws IOException {
        String longLine = "X".repeat(200) + "\n";
        String content = longLine.repeat(5);
        
        PdfReportGenerator.generatePdfReport(content, testFile, "Long Lines");
        
        assertTrue("Should handle very long lines", testFile.exists());
    }
}
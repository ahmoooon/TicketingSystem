/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package application.utilities;

import java.io.ByteArrayInputStream;
import java.util.Scanner;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author MOON
 */
public class UtilityTest {
    
    public UtilityTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of checkError method, of class Utility.
     */
    @Test
    public void testCheckError_ValidInput_WithinRange() {
        // Simulate user entering "5"
        String input = "5\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        
        int result = Utility.checkError(scanner, 1, 10);
        
        assertEquals(5, result);
        scanner.close();
    }
    
    @Test
    public void testCheckError_ValidInput_AtMinimum() {
        String input = "1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        
        int result = Utility.checkError(scanner, 1, 10);
        
        assertEquals(1, result);
        scanner.close();
    }
    
    @Test
    public void testCheckError_ValidInput_AtMaximum() {
        String input = "10\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        
        int result = Utility.checkError(scanner, 1, 10);
        
        assertEquals(10, result);
        scanner.close();
    }
    
    @Test
    public void testCheckError_ValidInput_NegativeRange() {
        String input = "-5\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        
        int result = Utility.checkError(scanner, -10, -1);
        
        assertEquals(-5, result);
        scanner.close();
    }
    
    @Test
    public void testCheckError_ValidInput_ZeroInRange() {
        String input = "0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        
        int result = Utility.checkError(scanner, -5, 5);
        
        assertEquals(0, result);
        scanner.close();
    }
    
    // ========== INVALID INPUT RECOVERY TESTS ==========
    
    @Test
    public void testCheckError_InvalidInput_TooLow_ThenValid() {
        // First input too low, second input valid
        String input = "0\n5\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        
        int result = Utility.checkError(scanner, 1, 10);
        
        assertEquals(5, result);
        scanner.close();
    }
    
    @Test
    public void testCheckError_InvalidInput_TooHigh_ThenValid() {
        // First input too high, second input valid
        String input = "15\n5\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        
        int result = Utility.checkError(scanner, 1, 10);
        
        assertEquals(5, result);
        scanner.close();
    }
    
    @Test
    public void testCheckError_NonNumericInput_ThenValid() {
        // First input is not a number, second input valid
        String input = "abc\n5\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        
        int result = Utility.checkError(scanner, 1, 10);
        
        assertEquals(5, result);
        scanner.close();
    }
    
    @Test
    public void testCheckError_MultipleInvalidInputs_ThenValid() {
        // Multiple invalid inputs before valid one
        String input = "abc\n0\n15\n-1\n5\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        
        int result = Utility.checkError(scanner, 1, 10);
        
        assertEquals(5, result);
        scanner.close();
    }
    
    @Test
    public void testCheckError_DecimalInput_ThenValid() {
        // Decimal number (should be rejected as non-integer)
        String input = "5.5\n5\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        
        int result = Utility.checkError(scanner, 1, 10);
        
        assertEquals(5, result);
        scanner.close();
    }
    
    @Test
    public void testCheckError_EmptyInput_ThenValid() {
        // Empty line followed by valid input
        String input = "\n5\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        
        int result = Utility.checkError(scanner, 1, 10);
        
        assertEquals(5, result);
        scanner.close();
    }
    
    // ========== EDGE CASES ==========
    
    @Test
    public void testCheckError_LargePositiveNumbers() {
        String input = "1000\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        
        int result = Utility.checkError(scanner, 100, 2000);
        
        assertEquals(1000, result);
        scanner.close();
    }
    
    @Test
    public void testCheckError_LargeNegativeNumbers() {
        String input = "-1000\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        
        int result = Utility.checkError(scanner, -2000, -100);
        
        assertEquals(-1000, result);
        scanner.close();
    }
    
    @Test
    public void testCheckError_SingleValueRange() {
        // Range with only one valid value
        String input = "5\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        
        int result = Utility.checkError(scanner, 5, 5);
        
        assertEquals(5, result);
        scanner.close();
    }
    
    @Test
    public void testCheckError_SingleValueRange_Invalid_ThenValid() {
        // Try invalid value first, then correct one
        String input = "4\n5\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        
        int result = Utility.checkError(scanner, 5, 5);
        
        assertEquals(5, result);
        scanner.close();
    }
    
    @Test
    public void testCheckError_SpecialCharacters_ThenValid() {
        String input = "@#$\n5\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        
        int result = Utility.checkError(scanner, 1, 10);
        
        assertEquals(5, result);
        scanner.close();
    }
    
    @Test
    public void testCheckError_Whitespace_ThenValid() {
        String input = "   \n5\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        
        int result = Utility.checkError(scanner, 1, 10);
        
        assertEquals(5, result);
        scanner.close();
    }
    
    // ========== BOUNDARY TESTS ==========
    
    @Test
    public void testCheckError_JustBelowMinimum_ThenValid() {
        String input = "0\n1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        
        int result = Utility.checkError(scanner, 1, 10);
        
        assertEquals(1, result);
        scanner.close();
    }
    
    @Test
    public void testCheckError_JustAboveMaximum_ThenValid() {
        String input = "11\n10\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        
        int result = Utility.checkError(scanner, 1, 10);
        
        assertEquals(10, result);
        scanner.close();
    }
    
    @Test
    public void testCheckError_MaxIntValue_InRange() {
        String input = String.valueOf(Integer.MAX_VALUE) + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        
        int result = Utility.checkError(scanner, 0, Integer.MAX_VALUE);
        
        assertEquals(Integer.MAX_VALUE, result);
        scanner.close();
    }
    
    @Test
    public void testCheckError_MinIntValue_InRange() {
        String input = String.valueOf(Integer.MIN_VALUE) + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        
        int result = Utility.checkError(scanner, Integer.MIN_VALUE, 0);
        
        assertEquals(Integer.MIN_VALUE, result);
        scanner.close();
    }
    
    // ========== MULTIPLE RECOVERY ATTEMPTS ==========
    
    @Test
    public void testCheckError_FiveInvalidAttempts_ThenValid() {
        String input = "0\nabc\n100\n-5\nxyz\n5\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        
        int result = Utility.checkError(scanner, 1, 10);
        
        assertEquals(5, result);
        scanner.close();
    }
    
    @Test
    public void testCheckError_AlternatingInvalidTypes() {
        // Alternate between out-of-range and non-numeric
        String input = "0\nabc\n15\nxyz\n5\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        
        int result = Utility.checkError(scanner, 1, 10);
        
        assertEquals(5, result);
        scanner.close();
    }
    
    // ========== REAL-WORLD SCENARIOS ==========
    
    @Test
    public void testCheckError_MenuSelection_Scenario() {
        // Simulates menu selection (1-4)
        String input = "0\n5\n2\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        
        int result = Utility.checkError(scanner, 1, 4);
        
        assertEquals(2, result);
        scanner.close();
    }
    
    @Test
    public void testCheckError_AgeInput_Scenario() {
        // Simulates age input (18-100)
        String input = "-5\n150\n25\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        
        int result = Utility.checkError(scanner, 18, 100);
        
        assertEquals(25, result);
        scanner.close();
    }
    
    @Test
    public void testCheckError_YearInput_Scenario() {
        // Simulates year input (2020-2030)
        String input = "2015\n2035\n2025\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);
        
        int result = Utility.checkError(scanner, 2020, 2030);
        
        assertEquals(2025, result);
        scanner.close();
    }
    
}

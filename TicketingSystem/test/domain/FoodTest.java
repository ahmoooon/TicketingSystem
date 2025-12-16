package domain;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author zhili
 */

public class FoodTest {
    
    // --- Concrete Implementation for Testing ---
    // This allows us to instantiate the abstract Food class
    public static class FoodImpl extends Food {
        public FoodImpl(String name, double unitPrice) {
            super(name, unitPrice);
        }
        public FoodImpl() {
            super();
        }
        public FoodImpl(int num) {
            super(num);
        }
        
        @Override
        public String getFoodType() {
            return "TestFood";
        }
    }
    // ------------------------------------------
    
    // Test data
    private final String TEST_NAME = "Cheeseburger";
    private final double TEST_UNIT_PRICE = 12.50;
    
    // --- Setup and Teardown ---
    
    @BeforeClass
    public static void setUpClass() {
        // Ensure lastNum starts at a known state for the entire test run
        Food.setLastNum(1); 
    }
    
    @Before
    public void setUp() {
        // Reset lastNum before each test to guarantee consistent foodNum assignment
        Food.setLastNum(1); 
    }

    // --- Constructor Tests ---
    
    @Test
    public void testConstructor_NameAndPrice() {
        System.out.println("Constructor_NameAndPrice");
        
        FoodImpl instance = new FoodImpl(TEST_NAME, TEST_UNIT_PRICE);
        
        assertEquals(1, instance.getFoodNum());
        assertEquals(TEST_NAME, instance.getName());
        assertEquals(TEST_UNIT_PRICE, instance.getPrice(), 0.001); // Price stores unit price
        assertEquals(1, instance.getQty());
        assertEquals(2, Food.getLastNum()); // lastNum should have incremented
    }
    
    @Test
    public void testConstructor_ZeroArg() {
        System.out.println("Constructor_ZeroArg");
        
        FoodImpl instance = new FoodImpl();
        
        // Assertions based on Food() constructor
        assertEquals(0, instance.getQty());
        assertEquals(1, Food.getLastNum()); // lastNum is not incremented
    }
    
    @Test
    public void testConstructor_NumArg() {
        System.out.println("Constructor_NumArg");
        
        FoodImpl instance = new FoodImpl(99);
        
        // Assertions based on Food(int num) constructor
        assertEquals(99, instance.getFoodNum());
        assertEquals(1, Food.getLastNum()); // lastNum is not incremented
    }
    
    // --- Getter/Setter Tests (Single test for group of simple accessors) ---
    
    @Test
    public void testAccessors_NamePriceQtyNum() {
        System.out.println("testAccessors_NamePriceQtyNum");
        
        FoodImpl instance = new FoodImpl("Initial", 1.0); // Initialized with lastNum=1
        
        // Test Setters
        instance.setFoodNum(10);
        instance.setName("New Name");
        instance.setPrice(99.99);
        instance.setQty(5);
        
        // Test Getters
        assertEquals(10, instance.getFoodNum());
        assertEquals("New Name", instance.getName());
        assertEquals(99.99, instance.getPrice(), 0.001);
        assertEquals(5, instance.getQty());
    }
    
    @Test
    public void testAccessors_LastNum() {
        System.out.println("testAccessors_LastNum");
        
        Food.setLastNum(50);
        assertEquals(50, Food.getLastNum());
    }
    
    // --- Logic Method Tests ---

    @Test
    public void testCalPrice() {
        System.out.println("calPrice");
        
        FoodImpl instance = new FoodImpl(TEST_NAME, TEST_UNIT_PRICE); // Price = 12.50
        instance.setQty(4);
        
        instance.calPrice(); // Should calculate total price: 4 * 12.50 = 50.00
        
        assertEquals(50.00, instance.getPrice(), 0.001); 
    }

    @Test
    public void testIncrementQty() {
        System.out.println("incrementQty");
        
        FoodImpl instance = new FoodImpl(TEST_NAME, TEST_UNIT_PRICE); // Qty = 1
        
        instance.incrementQty(3);
        assertEquals(4, instance.getQty());
        
        instance.incrementQty(10);
        assertEquals(14, instance.getQty());
    }
    
    @Test
    public void testIncrementPrice() {
        System.out.println("incrementPrice");
        
        FoodImpl instance = new FoodImpl(TEST_NAME, TEST_UNIT_PRICE); // Price = 12.50
        
        instance.incrementPrice(7.50);
        assertEquals(20.00, instance.getPrice(), 0.001);
        
        instance.incrementPrice(30.00);
        assertEquals(50.00, instance.getPrice(), 0.001);
    }
    
    @Test
    public void testGetFoodType() {
        System.out.println("getFoodType");
        
        FoodImpl instance = new FoodImpl(TEST_NAME, TEST_UNIT_PRICE);
        
        assertEquals("TestFood", instance.getFoodType());
    }

    // --- Formatting Methods Tests ---

    @Test
    public void testToString_MenuFormat() {
        System.out.println("toString_MenuFormat");
        
        FoodImpl instance = new FoodImpl(TEST_NAME, 9.90);
        
        // Expected format: "%3d %-35s RM %6.2f"
        // Expected result: "  1 Cheeseburger                           RM   9.90"
        String expected = String.format("%3d %-35s RM %6.2f", 1, TEST_NAME, 9.90);
        
        assertEquals(expected, instance.toString());
    }

    @Test
    public void testPrintOrder_OrderFormat() {
        System.out.println("printOrder_OrderFormat");
        
        FoodImpl instance = new FoodImpl(TEST_NAME, 12.50);
        instance.setQty(2);
        instance.setPrice(25.00); // Price must be the total price for printOrder
        
        // Expected format: "%-35s %3d RM %6.2f"
        // Expected result: "Cheeseburger                                 2 RM  25.00"
        String expected = String.format("%-35s %3d RM %6.2f", TEST_NAME, 2, 25.00);
        
        assertEquals(expected, instance.printOrder());
    }
    
}
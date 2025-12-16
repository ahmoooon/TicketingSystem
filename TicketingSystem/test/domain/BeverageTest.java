package domain;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for Beverage class
 */
public class BeverageTest {
    
    private Beverage beverage;
    
    @Before
    public void setUp() {
        // Reset Food.lastNum before each test
        Food.setLastNum(1);
    }
    
    @Test
    public void testConstructor_WithNameAndPrice() {
        beverage = new Beverage("Coca Cola", 5.50);
        
        assertNotNull(beverage);
        assertEquals("Coca Cola", beverage.getName());
        assertEquals(5.50, beverage.getPrice(), 0.001);
        assertEquals(1, beverage.getQty());
        assertEquals(1, beverage.getFoodNum());
    }
    
    @Test
    public void testConstructor_DefaultConstructor() {
        beverage = new Beverage();
        
        assertNotNull(beverage);
        assertEquals(0, beverage.getQty());
    }
    
    @Test
    public void testConstructor_WithNumber() {
        beverage = new Beverage(5);
        
        assertNotNull(beverage);
        assertEquals(5, beverage.getFoodNum());
    }
    
    @Test
    public void testGetFoodType() {
        beverage = new Beverage("Sprite", 5.00);
        
        assertEquals("Beverage", beverage.getFoodType());
    }
    
    @Test
    public void testIncrementQty() {
        beverage = new Beverage("Pepsi", 5.00);
        
        beverage.incrementQty(3);
        
        assertEquals(4, beverage.getQty()); // Initial 1 + 3
    }
    
    @Test
    public void testIncrementPrice() {
        beverage = new Beverage("Fanta", 5.00);
        
        beverage.incrementPrice(10.00);
        
        assertEquals(15.00, beverage.getPrice(), 0.001);
    }
    
    @Test
    public void testCalPrice() {
        beverage = new Beverage("Mountain Dew", 6.00);
        beverage.setQty(3);
        
        beverage.calPrice();
        
        assertEquals(18.00, beverage.getPrice(), 0.001);
    }
    
    @Test
    public void testSettersAndGetters() {
        beverage = new Beverage();
        
        beverage.setName("Test Beverage");
        beverage.setPrice(7.50);
        beverage.setQty(2);
        beverage.setFoodNum(10);
        
        assertEquals("Test Beverage", beverage.getName());
        assertEquals(7.50, beverage.getPrice(), 0.001);
        assertEquals(2, beverage.getQty());
        assertEquals(10, beverage.getFoodNum());
    }
    
    @Test
    public void testToString() {
        beverage = new Beverage("Test Drink", 5.00);
        beverage.setFoodNum(1);
        
        String result = beverage.toString();
        
        assertTrue(result.contains("Test Drink"));
        assertTrue(result.contains("5.00"));
    }
    
    @Test
    public void testPrintOrder() {
        beverage = new Beverage("Order Drink", 5.00);
        beverage.setQty(2);
        beverage.setPrice(10.00);
        
        String result = beverage.printOrder();
        
        assertTrue(result.contains("Order Drink"));
        assertTrue(result.contains("2"));
        assertTrue(result.contains("10.00"));
    }
    
    @Test
    public void testLastNumIncrement() {
        Food.setLastNum(1);
        
        Beverage b1 = new Beverage("Drink 1", 5.00);
        Beverage b2 = new Beverage("Drink 2", 6.00);
        
        assertEquals(1, b1.getFoodNum());
        assertEquals(2, b2.getFoodNum());
        assertEquals(3, Food.getLastNum());
    }
    
    @Test
    public void testZeroPrice() {
        beverage = new Beverage("Free Sample", 0.0);
        
        assertEquals(0.0, beverage.getPrice(), 0.001);
    }
    
    @Test
    public void testNegativeQty() {
        beverage = new Beverage("Test", 5.00);
        beverage.setQty(-1);
        
        assertEquals(-1, beverage.getQty());
    }
    
    @Test
    public void testLargeQuantity() {
        beverage = new Beverage("Bulk Order", 5.00);
        beverage.setQty(1000);
        beverage.calPrice();
        
        assertEquals(5000.00, beverage.getPrice(), 0.001);
    }
}
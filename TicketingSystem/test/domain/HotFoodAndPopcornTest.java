package domain;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for HotFood and Popcorn classes
 */
public class HotFoodAndPopcornTest {
    
    @Before
    public void setUp() {
        Food.setLastNum(1);
    }
    
    // ========== HOTFOOD TESTS ==========
    
    @Test
    public void testHotFood_ConstructorWithNameAndPrice() {
        HotFood hotFood = new HotFood("Chicken Nuggets", 10.00);
        
        assertNotNull(hotFood);
        assertEquals("Chicken Nuggets", hotFood.getName());
        assertEquals(10.00, hotFood.getPrice(), 0.001);
        assertEquals(1, hotFood.getQty());
    }
    
    @Test
    public void testHotFood_DefaultConstructor() {
        HotFood hotFood = new HotFood();
        
        assertNotNull(hotFood);
        assertEquals(0, hotFood.getQty());
    }
    
    @Test
    public void testHotFood_ConstructorWithNumber() {
        HotFood hotFood = new HotFood(5);
        
        assertNotNull(hotFood);
        assertEquals(5, hotFood.getFoodNum());
    }
    
    @Test
    public void testHotFood_GetFoodType() {
        HotFood hotFood = new HotFood("French Fries", 8.50);
        
        assertEquals("HotFood", hotFood.getFoodType());
    }
    
    @Test
    public void testHotFood_IncrementQty() {
        HotFood hotFood = new HotFood("Hotdog", 11.00);
        
        hotFood.incrementQty(2);
        
        assertEquals(3, hotFood.getQty());
    }
    
    @Test
    public void testHotFood_IncrementPrice() {
        HotFood hotFood = new HotFood("Nachos", 9.00);
        
        hotFood.incrementPrice(9.00);
        
        assertEquals(18.00, hotFood.getPrice(), 0.001);
    }
    
    @Test
    public void testHotFood_CalPrice() {
        HotFood hotFood = new HotFood("Pizza Slice", 12.00);
        hotFood.setQty(3);
        
        hotFood.calPrice();
        
        assertEquals(36.00, hotFood.getPrice(), 0.001);
    }
    
    @Test
    public void testHotFood_SettersAndGetters() {
        HotFood hotFood = new HotFood();
        
        hotFood.setName("Burger");
        hotFood.setPrice(15.00);
        hotFood.setQty(2);
        
        assertEquals("Burger", hotFood.getName());
        assertEquals(15.00, hotFood.getPrice(), 0.001);
        assertEquals(2, hotFood.getQty());
    }
    
    @Test
    public void testHotFood_ToString() {
        HotFood hotFood = new HotFood("Test Item", 10.00);
        hotFood.setFoodNum(1);
        
        String result = hotFood.toString();
        
        assertTrue(result.contains("Test Item"));
        assertTrue(result.contains("10.00"));
    }
    
    @Test
    public void testHotFood_PrintOrder() {
        HotFood hotFood = new HotFood("Order Item", 10.00);
        hotFood.setQty(2);
        hotFood.setPrice(20.00);
        
        String result = hotFood.printOrder();
        
        assertTrue(result.contains("Order Item"));
        assertTrue(result.contains("2"));
        assertTrue(result.contains("20.00"));
    }
    
    @Test
    public void testHotFood_ZeroPrice() {
        HotFood hotFood = new HotFood("Free Sample", 0.0);
        
        assertEquals(0.0, hotFood.getPrice(), 0.001);
    }
    
    @Test
    public void testHotFood_LargeQuantity() {
        HotFood hotFood = new HotFood("Bulk Order", 5.00);
        hotFood.setQty(100);
        hotFood.calPrice();
        
        assertEquals(500.00, hotFood.getPrice(), 0.001);
    }
    
    // ========== POPCORN TESTS ==========
    
    @Test
    public void testPopcorn_ConstructorWithNameAndPrice() {
        Popcorn popcorn = new Popcorn("Caramel Popcorn", 8.00);
        
        assertNotNull(popcorn);
        assertEquals("Caramel Popcorn", popcorn.getName());
        assertEquals(8.00, popcorn.getPrice(), 0.001);
        assertEquals(1, popcorn.getQty());
    }
    
    @Test
    public void testPopcorn_DefaultConstructor() {
        Popcorn popcorn = new Popcorn();
        
        assertNotNull(popcorn);
        assertEquals(0, popcorn.getQty());
    }
    
    @Test
    public void testPopcorn_ConstructorWithNumber() {
        Popcorn popcorn = new Popcorn(3);
        
        assertNotNull(popcorn);
        assertEquals(3, popcorn.getFoodNum());
    }
    
    @Test
    public void testPopcorn_GetFoodType() {
        Popcorn popcorn = new Popcorn("Salt Popcorn", 10.00);
        
        assertEquals("Popcorn", popcorn.getFoodType());
    }
    
    @Test
    public void testPopcorn_IncrementQty() {
        Popcorn popcorn = new Popcorn("Cheese Popcorn", 12.00);
        
        popcorn.incrementQty(3);
        
        assertEquals(4, popcorn.getQty());
    }
    
    @Test
    public void testPopcorn_IncrementPrice() {
        Popcorn popcorn = new Popcorn("Butter Popcorn", 9.00);
        
        popcorn.incrementPrice(9.00);
        
        assertEquals(18.00, popcorn.getPrice(), 0.001);
    }
    
    @Test
    public void testPopcorn_CalPrice() {
        Popcorn popcorn = new Popcorn("Large Popcorn", 12.00);
        popcorn.setQty(2);
        
        popcorn.calPrice();
        
        assertEquals(24.00, popcorn.getPrice(), 0.001);
    }
    
    @Test
    public void testPopcorn_SettersAndGetters() {
        Popcorn popcorn = new Popcorn();
        
        popcorn.setName("Sweet Popcorn");
        popcorn.setPrice(11.00);
        popcorn.setQty(3);
        
        assertEquals("Sweet Popcorn", popcorn.getName());
        assertEquals(11.00, popcorn.getPrice(), 0.001);
        assertEquals(3, popcorn.getQty());
    }
    
    @Test
    public void testPopcorn_ToString() {
        Popcorn popcorn = new Popcorn("Display Popcorn", 10.00);
        popcorn.setFoodNum(1);
        
        String result = popcorn.toString();
        
        assertTrue(result.contains("Display Popcorn"));
        assertTrue(result.contains("10.00"));
    }
    
    @Test
    public void testPopcorn_PrintOrder() {
        Popcorn popcorn = new Popcorn("Order Popcorn", 10.00);
        popcorn.setQty(2);
        popcorn.setPrice(20.00);
        
        String result = popcorn.printOrder();
        
        assertTrue(result.contains("Order Popcorn"));
        assertTrue(result.contains("2"));
        assertTrue(result.contains("20.00"));
    }
    
    @Test
    public void testPopcorn_ZeroPrice() {
        Popcorn popcorn = new Popcorn("Free Sample", 0.0);
        
        assertEquals(0.0, popcorn.getPrice(), 0.001);
    }
    
    @Test
    public void testPopcorn_LargeQuantity() {
        Popcorn popcorn = new Popcorn("Bulk Popcorn", 8.00);
        popcorn.setQty(50);
        popcorn.calPrice();
        
        assertEquals(400.00, popcorn.getPrice(), 0.001);
    }
    
    // ========== COMPARISON TESTS ==========
    
    @Test
    public void testFoodTypes_AreDifferent() {
        HotFood hotFood = new HotFood("Item", 10.00);
        Popcorn popcorn = new Popcorn("Item", 10.00);
        
        assertNotEquals(hotFood.getFoodType(), popcorn.getFoodType());
    }
    
    @Test
    public void testLastNumIncrement_AcrossTypes() {
        Food.setLastNum(1);
        
        HotFood h1 = new HotFood("Hot1", 10.00);
        Popcorn p1 = new Popcorn("Pop1", 8.00);
        HotFood h2 = new HotFood("Hot2", 12.00);
        
        assertEquals(1, h1.getFoodNum());
        assertEquals(2, p1.getFoodNum());
        assertEquals(3, h2.getFoodNum());
    }
}
package domain;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for CinemaHall class
 */
public class CinemaHallTest {
    
    private CinemaHall standardHall;
    private CinemaHall imaxHall;
    private CinemaHall loungeHall;
    
    @Before
    public void setUp() {
        standardHall = new CinemaHall(1, CinemaHall.HALL_TYPE_STANDARD, 5, 10);
        imaxHall = new CinemaHall(2, CinemaHall.HALL_TYPE_IMAX, 8, 15);
        loungeHall = new CinemaHall(3, CinemaHall.HALL_TYPE_LOUNGE, 5, 5);
    }
    
    @Test
    public void testConstructor_WithParameters() {
        assertNotNull(standardHall);
        assertEquals(1, standardHall.getHallId());
        assertEquals(CinemaHall.HALL_TYPE_STANDARD, standardHall.getHallType());
        assertEquals(5, standardHall.getRowAmt());
        assertEquals(10, standardHall.getColAmt());
        assertEquals(10, standardHall.getMaxSeatCol());
    }
    
    @Test
    public void testConstructor_DefaultConstructor() {
        CinemaHall defaultHall = new CinemaHall();
        
        assertNotNull(defaultHall);
        assertEquals(0, defaultHall.getHallId());
        assertNull(defaultHall.getHallType());
        assertEquals(0, defaultHall.getRowAmt());
        assertEquals(0, defaultHall.getColAmt());
    }
    
    @Test
    public void testGetBasePrice_Standard() {
        double price = standardHall.getBasePrice();
        
        assertEquals(15.00, price, 0.001);
    }
    
    @Test
    public void testGetBasePrice_IMAX() {
        double price = imaxHall.getBasePrice();
        
        assertEquals(30.00, price, 0.001);
    }
    
    @Test
    public void testGetBasePrice_Lounge() {
        double price = loungeHall.getBasePrice();
        
        assertEquals(80.00, price, 0.001);
    }
    
    @Test
    public void testGetBasePrice_NullType() {
        CinemaHall nullHall = new CinemaHall(4, null, 5, 10);
        
        double price = nullHall.getBasePrice();
        
        assertEquals(0.0, price, 0.001);
    }
    
    @Test
    public void testGetBasePrice_UnknownType() {
        CinemaHall unknownHall = new CinemaHall(5, "Premium", 5, 10);
        
        double price = unknownHall.getBasePrice();
        
        assertEquals(15.00, price, 0.001); // Default to Standard
    }
    
    @Test
    public void testGetHallId() {
        assertEquals(1, standardHall.getHallId());
        assertEquals(2, imaxHall.getHallId());
        assertEquals(3, loungeHall.getHallId());
    }
    
    @Test
    public void testGetHallType() {
        assertEquals(CinemaHall.HALL_TYPE_STANDARD, standardHall.getHallType());
        assertEquals(CinemaHall.HALL_TYPE_IMAX, imaxHall.getHallType());
        assertEquals(CinemaHall.HALL_TYPE_LOUNGE, loungeHall.getHallType());
    }
    
    @Test
    public void testGetRowAmt() {
        assertEquals(5, standardHall.getRowAmt());
        assertEquals(8, imaxHall.getRowAmt());
        assertEquals(5, loungeHall.getRowAmt());
    }
    
    @Test
    public void testGetColAmt() {
        assertEquals(10, standardHall.getColAmt());
        assertEquals(15, imaxHall.getColAmt());
        assertEquals(5, loungeHall.getColAmt());
    }
    
    @Test
    public void testGetMaxSeatCol() {
        assertEquals(10, standardHall.getMaxSeatCol());
        assertEquals(15, imaxHall.getMaxSeatCol());
        assertEquals(5, loungeHall.getMaxSeatCol());
    }
    
    @Test
    public void testToString() {
        String result = standardHall.toString();
        
        assertTrue(result.contains("Hall ID"));
        assertTrue(result.contains("1"));
        assertTrue(result.contains(CinemaHall.HALL_TYPE_STANDARD));
        assertTrue(result.contains("10"));
        assertTrue(result.contains("5"));
    }
    
    @Test
    public void testHallTypeConstants() {
        assertEquals("Standard", CinemaHall.HALL_TYPE_STANDARD);
        assertEquals("IMAX", CinemaHall.HALL_TYPE_IMAX);
        assertEquals("Lounge", CinemaHall.HALL_TYPE_LOUNGE);
    }
    
    @Test
    public void testCapacityCalculation() {
        int standardCapacity = standardHall.getRowAmt() * standardHall.getColAmt();
        int imaxCapacity = imaxHall.getRowAmt() * imaxHall.getColAmt();
        int loungeCapacity = loungeHall.getRowAmt() * loungeHall.getColAmt();
        
        assertEquals(50, standardCapacity);
        assertEquals(120, imaxCapacity);
        assertEquals(25, loungeCapacity);
    }
    
    @Test
    public void testZeroRowsAndColumns() {
        CinemaHall emptyHall = new CinemaHall(6, CinemaHall.HALL_TYPE_STANDARD, 0, 0);
        
        assertEquals(0, emptyHall.getRowAmt());
        assertEquals(0, emptyHall.getColAmt());
        assertEquals(15.00, emptyHall.getBasePrice(), 0.001);
    }
    
    @Test
    public void testLargeHall() {
        CinemaHall largeHall = new CinemaHall(7, CinemaHall.HALL_TYPE_IMAX, 20, 30);
        
        assertEquals(20, largeHall.getRowAmt());
        assertEquals(30, largeHall.getColAmt());
        assertEquals(600, largeHall.getRowAmt() * largeHall.getColAmt());
    }
    
    @Test
    public void testNegativeHallId() {
        CinemaHall negativeIdHall = new CinemaHall(-1, CinemaHall.HALL_TYPE_STANDARD, 5, 10);
        
        assertEquals(-1, negativeIdHall.getHallId());
    }
    
    @Test
    public void testImmutability() {
        // Test that hall properties cannot be changed after construction
        int originalId = standardHall.getHallId();
        String originalType = standardHall.getHallType();
        int originalRows = standardHall.getRowAmt();
        int originalCols = standardHall.getColAmt();
        
        // Create another reference
        CinemaHall reference = standardHall;
        
        // Verify same values
        assertEquals(originalId, reference.getHallId());
        assertEquals(originalType, reference.getHallType());
        assertEquals(originalRows, reference.getRowAmt());
        assertEquals(originalCols, reference.getColAmt());
    }
}
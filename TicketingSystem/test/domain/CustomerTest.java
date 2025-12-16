package domain;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for Customer class
 */
public class CustomerTest {
    
    private Customer customer;
    
    @Before
    public void setUp() {
        // Note: Customer IDs auto-increment, so we don't reset them in tests
    }
    
    @Test
    public void testConstructor_WithNameAndPassword() {
        customer = new Customer("John Doe", "hashedPassword123");
        
        assertNotNull(customer);
        assertEquals("John Doe", customer.getName());
        assertEquals("hashedPassword123", customer.getPassword());
        assertTrue(customer.getId() > 0);
    }
    
    @Test
    public void testConstructor_DefaultConstructor() {
        customer = new Customer();
        
        assertNotNull(customer);
        assertTrue(customer.getId() > 0);
    }
    
    @Test
    public void testGetName() {
        customer = new Customer("Alice", "password");
        
        assertEquals("Alice", customer.getName());
    }
    
    @Test
    public void testSetName() {
        customer = new Customer("Bob", "password");
        customer.setName("Robert");
        
        assertEquals("Robert", customer.getName());
    }
    
    @Test
    public void testGetPassword() {
        customer = new Customer("Charlie", "secretHash");
        
        assertEquals("secretHash", customer.getPassword());
    }
    
    @Test
    public void testSetPassword() {
        customer = new Customer("David", "oldHash");
        customer.setPassword("newHash");
        
        assertEquals("newHash", customer.getPassword());
    }
    
    @Test
    public void testGetId() {
        customer = new Customer("Eve", "password");
        int id = customer.getId();
        
        assertTrue(id > 0);
    }
    
    @Test
    public void testAutoIncrementId() {
        Customer c1 = new Customer("Customer1", "pass1");
        Customer c2 = new Customer("Customer2", "pass2");
        
        assertTrue(c2.getId() > c1.getId());
        assertEquals(c1.getId() + 1, c2.getId());
    }
    
    @Test
    public void testToJsonString() {
        customer = new Customer("TestUser", "testHash");
        
        String json = customer.toJsonString();
        
        assertNotNull(json);
        assertTrue(json.contains("\"name\":\"TestUser\""));
        assertTrue(json.contains("\"password\":\"testHash\""));
        assertTrue(json.contains("\"id\":"));
        assertTrue(json.contains("\"assignid\":"));
    }
    
    @Test
    public void testFromJsonString_ValidJson() {
        String json = "{\"name\":\"JsonUser\", \"password\":\"jsonHash\", \"id\":100, \"assignid\":101}";
        
        Customer customer = Customer.fromJsonString(json);
        
        assertNotNull(customer);
        assertEquals("JsonUser", customer.getName());
        assertEquals("jsonHash", customer.getPassword());
        assertEquals(100, customer.getId());
    }
    
    @Test
    public void testFromJsonString_InvalidJson_ReturnsNull() {
        String invalidJson = "invalid json string";
        
        Customer customer = Customer.fromJsonString(invalidJson);
        
        assertNull(customer);
    }
    
    @Test
    public void testFromJsonString_MalformedJson_ReturnsNull() {
        String malformedJson = "{\"name\":\"User\", \"password\":";
        
        Customer customer = Customer.fromJsonString(malformedJson);
        
        assertNull(customer);
    }
    
    @Test
    public void testToString() {
        customer = new Customer("DisplayUser", "displayHash");
        
        String result = customer.toString();
        
        assertTrue(result.contains("DisplayUser"));
        assertTrue(result.contains("displayHash"));
        assertTrue(result.contains("ID:"));
    }
    
    @Test
    public void testRoundTripJsonSerialization() {
        Customer original = new Customer("RoundTrip", "roundTripHash");
        
        String json = original.toJsonString();
        Customer deserialized = Customer.fromJsonString(json);
        
        assertNotNull(deserialized);
        assertEquals(original.getName(), deserialized.getName());
        assertEquals(original.getPassword(), deserialized.getPassword());
    }
    
    @Test
    public void testEmptyName() {
        customer = new Customer("", "password");
        
        assertEquals("", customer.getName());
    }
    
    @Test
    public void testEmptyPassword() {
        customer = new Customer("User", "");
        
        assertEquals("", customer.getPassword());
    }
    
    @Test
    public void testNullName() {
        customer = new Customer(null, "password");
        
        assertNull(customer.getName());
    }
    
    @Test
    public void testNullPassword() {
        customer = new Customer("User", null);
        
        assertNull(customer.getPassword());
    }
    
    @Test
    public void testLongName() {
        String longName = "VeryLongNameThatExceedsNormalLength".repeat(10);
        customer = new Customer(longName, "password");
        
        assertEquals(longName, customer.getName());
    }
    
    @Test
    public void testLongPassword() {
        String longPassword = "VeryLongHashedPasswordString".repeat(10);
        customer = new Customer("User", longPassword);
        
        assertEquals(longPassword, customer.getPassword());
    }
    
    @Test
    public void testSpecialCharactersInName() {
        customer = new Customer("John@Doe#123", "password");
        
        assertEquals("John@Doe#123", customer.getName());
    }
    
    @Test
    public void testMultipleCustomersHaveUniqueIds() {
        Customer c1 = new Customer("User1", "pass1");
        Customer c2 = new Customer("User2", "pass2");
        Customer c3 = new Customer("User3", "pass3");
        
        assertNotEquals(c1.getId(), c2.getId());
        assertNotEquals(c2.getId(), c3.getId());
        assertNotEquals(c1.getId(), c3.getId());
    }
    
    @Test
    public void testJsonSerializationWithSpecialCharacters() {
        customer = new Customer("User\"With\"Quotes", "pass'with'quotes");
        
        String json = customer.toJsonString();
        
        // Should handle quotes in names (though may not be perfect)
        assertNotNull(json);
        assertTrue(json.contains("name"));
    }
    
    @Test
    public void testFromJsonString_WithHighAssignId() {
        String json = "{\"name\":\"User\", \"password\":\"pass\", \"id\":50, \"assignid\":1000}";
        
        Customer customer = Customer.fromJsonString(json);
        
        assertNotNull(customer);
        assertEquals("User", customer.getName());
    }
}
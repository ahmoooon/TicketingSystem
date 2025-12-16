package infrastructure.repositories;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataFileHandlerTest {
    
    private static final String TEST_FILE = "test_data.json";
    private static final String TEST_FOOD_FILE = "test_food.json";
    
    @Before
    public void setUp() {
        // Clean up any existing test files
        deleteTestFile(TEST_FILE);
        deleteTestFile(TEST_FOOD_FILE);
    }
    
    @After
    public void tearDown() {
        // Clean up test files
        deleteTestFile(TEST_FILE);
        deleteTestFile(TEST_FOOD_FILE);
    }
    
    private void deleteTestFile(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
    }
    
    // ========== SAVE TO JSON FILE TESTS ==========
    
    @Test
    public void testSaveToJsonFile_Success() {
        List<String> data = new ArrayList<>();
        data.add("{\"name\":\"Alice\",\"age\":25}");
        data.add("{\"name\":\"Bob\",\"age\":30}");
        
        DataFileHandler.saveToJsonFile(data, TEST_FILE);
        
        File file = new File(TEST_FILE);
        assertTrue("File should be created", file.exists());
        assertTrue("File should not be empty", file.length() > 0);
    }
    
    @Test
    public void testSaveToJsonFile_EmptyList() {
        List<String> emptyList = new ArrayList<>();
        
        DataFileHandler.saveToJsonFile(emptyList, TEST_FILE);
        
        File file = new File(TEST_FILE);
        assertTrue("File should be created even with empty list", file.exists());
    }
    
    @Test
    public void testSaveToJsonFile_SingleItem() {
        List<String> data = new ArrayList<>();
        data.add("{\"name\":\"Charlie\",\"age\":35}");
        
        DataFileHandler.saveToJsonFile(data, TEST_FILE);
        
        File file = new File(TEST_FILE);
        assertTrue("File should be created", file.exists());
    }
    
    @Test
    public void testSaveToJsonFile_LargeDataset() {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            data.add(String.format("{\"id\":%d,\"name\":\"User%d\"}", i, i));
        }
        
        DataFileHandler.saveToJsonFile(data, TEST_FILE);
        
        File file = new File(TEST_FILE);
        assertTrue("Large dataset should be saved", file.exists());
        assertTrue("File should be substantial", file.length() > 10000);
    }
    
    @Test
    public void testSaveToJsonFile_Overwrite() {
        List<String> data1 = new ArrayList<>();
        data1.add("{\"version\":1}");
        
        List<String> data2 = new ArrayList<>();
        data2.add("{\"version\":2}");
        data2.add("{\"additional\":\"data\"}");
        
        DataFileHandler.saveToJsonFile(data1, TEST_FILE);
        long size1 = new File(TEST_FILE).length();
        
        DataFileHandler.saveToJsonFile(data2, TEST_FILE);
        long size2 = new File(TEST_FILE).length();
        
        assertTrue("File should be overwritten", size2 > size1);
    }
    
    @Test
    public void testSaveToJsonFile_SpecialCharacters() {
        List<String> data = new ArrayList<>();
        data.add("{\"text\":\"Line with \\\"quotes\\\"\"}");
        data.add("{\"text\":\"Path: C:\\\\Users\\\\Test\"}");
        
        DataFileHandler.saveToJsonFile(data, TEST_FILE);
        
        assertTrue("Should handle special characters", new File(TEST_FILE).exists());
    }
    
    // ========== LOAD FROM JSON FILE TESTS ==========
    
    @Test
    public void testLoadFromJsonFile_Success() {
        // Save data first
        List<String> originalData = new ArrayList<>();
        originalData.add("{\"name\":\"Alice\"}");
        originalData.add("{\"name\":\"Bob\"}");
        DataFileHandler.saveToJsonFile(originalData, TEST_FILE);
        
        // Load data
        List<String> loadedData = DataFileHandler.loadFromJsonFile(TEST_FILE);
        
        assertNotNull("Loaded data should not be null", loadedData);
        assertEquals("Should load all lines", 2, loadedData.size());
        assertEquals("First line should match", originalData.get(0), loadedData.get(0));
        assertEquals("Second line should match", originalData.get(1), loadedData.get(1));
    }
    
    @Test
    public void testLoadFromJsonFile_NonExistentFile() {
        List<String> data = DataFileHandler.loadFromJsonFile("nonexistent_file.json");
        
        assertNotNull("Should return empty list, not null", data);
        assertTrue("Should return empty list for nonexistent file", data.isEmpty());
    }
    
    @Test
    public void testLoadFromJsonFile_EmptyFile() {
        // Create empty file
        DataFileHandler.saveToJsonFile(new ArrayList<>(), TEST_FILE);
        
        List<String> data = DataFileHandler.loadFromJsonFile(TEST_FILE);
        
        assertNotNull("Should return list", data);
        assertTrue("Should return empty list for empty file", data.isEmpty());
    }
    
    @Test
    public void testLoadFromJsonFile_IgnoresEmptyLines() {
        List<String> data = new ArrayList<>();
        data.add("{\"name\":\"Alice\"}");
        data.add("");  // Empty line
        data.add("   "); // Whitespace only
        data.add("{\"name\":\"Bob\"}");
        DataFileHandler.saveToJsonFile(data, TEST_FILE);
        
        List<String> loaded = DataFileHandler.loadFromJsonFile(TEST_FILE);
        
        // Should only load non-empty lines
        assertEquals("Should skip empty lines", 2, loaded.size());
    }
    
    @Test
    public void testLoadFromJsonFile_LargeFile() {
        // Save large dataset
        List<String> originalData = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            originalData.add(String.format("{\"id\":%d}", i));
        }
        DataFileHandler.saveToJsonFile(originalData, TEST_FILE);
        
        // Load data
        List<String> loaded = DataFileHandler.loadFromJsonFile(TEST_FILE);
        
        assertEquals("Should load all 1000 lines", 1000, loaded.size());
    }
    
    // ========== FOOD INVENTORY TESTS ==========
    
    @Test
    public void testLoadFoodInventoryData_ValidFile() {
        // Create test food file
        String foodJson = "[" +
            "{\"name\":\"Coca Cola\",\"price\":5.0}," +
            "{\"name\":\"Pepsi\",\"price\":5.0}," +
            "{\"name\":\"Sprite\",\"price\":4.5}" +
            "]";
        List<String> data = new ArrayList<>();
        data.add(foodJson);
        DataFileHandler.saveToJsonFile(data, TEST_FOOD_FILE);
        
        // Load food data
        List<Map<String, Object>> foodData = 
            DataFileHandler.loadFoodInventoryData(TEST_FOOD_FILE);
        
        assertNotNull("Food data should not be null", foodData);
        assertEquals("Should load 3 items", 3, foodData.size());
        
        Map<String, Object> firstItem = foodData.get(0);
        assertEquals("Coca Cola", firstItem.get("name"));
        assertEquals(5.0, (Double) firstItem.get("price"), 0.001);
    }
    
    @Test
    public void testLoadFoodInventoryData_EmptyArray() {
        String emptyJson = "[]";
        List<String> data = new ArrayList<>();
        data.add(emptyJson);
        DataFileHandler.saveToJsonFile(data, TEST_FOOD_FILE);
        
        List<Map<String, Object>> foodData = 
            DataFileHandler.loadFoodInventoryData(TEST_FOOD_FILE);
        
        assertNotNull("Should return empty list", foodData);
        assertTrue("Should be empty", foodData.isEmpty());
    }
    
    @Test
    public void testLoadFoodInventoryData_NonExistentFile() {
        List<Map<String, Object>> foodData = 
            DataFileHandler.loadFoodInventoryData("nonexistent_food.json");
        
        assertNotNull("Should return empty list", foodData);
        assertTrue("Should be empty for nonexistent file", foodData.isEmpty());
    }
    
    @Test
    public void testLoadFoodInventoryData_InvalidJson() {
        // Not a valid array
        String invalidJson = "{\"name\":\"Invalid\"}";
        List<String> data = new ArrayList<>();
        data.add(invalidJson);
        DataFileHandler.saveToJsonFile(data, TEST_FOOD_FILE);
        
        List<Map<String, Object>> foodData = 
            DataFileHandler.loadFoodInventoryData(TEST_FOOD_FILE);
        
        assertNotNull("Should handle invalid JSON gracefully", foodData);
        assertTrue("Should return empty list for invalid JSON", foodData.isEmpty());
    }
    
    @Test
    public void testLoadFoodInventoryData_SingleItem() {
        String foodJson = "[{\"name\":\"Popcorn\",\"price\":10.0}]";
        List<String> data = new ArrayList<>();
        data.add(foodJson);
        DataFileHandler.saveToJsonFile(data, TEST_FOOD_FILE);
        
        List<Map<String, Object>> foodData = 
            DataFileHandler.loadFoodInventoryData(TEST_FOOD_FILE);
        
        assertEquals("Should load 1 item", 1, foodData.size());
        assertEquals("Popcorn", foodData.get(0).get("name"));
    }
    
    @Test
    public void testLoadFoodInventoryData_MixedPrices() {
        String foodJson = "[" +
            "{\"name\":\"Cheap Item\",\"price\":2.5}," +
            "{\"name\":\"Expensive Item\",\"price\":99.99}," +
            "{\"name\":\"Free Item\",\"price\":0.0}" +
            "]";
        List<String> data = new ArrayList<>();
        data.add(foodJson);
        DataFileHandler.saveToJsonFile(data, TEST_FOOD_FILE);
        
        List<Map<String, Object>> foodData = 
            DataFileHandler.loadFoodInventoryData(TEST_FOOD_FILE);
        
        assertEquals(3, foodData.size());
        assertEquals(2.5, (Double) foodData.get(0).get("price"), 0.001);
        assertEquals(99.99, (Double) foodData.get(1).get("price"), 0.001);
        assertEquals(0.0, (Double) foodData.get(2).get("price"), 0.001);
    }
    
    @Test
    public void testLoadFoodInventoryData_SpecialCharactersInName() {
        String foodJson = "[" +
            "{\"name\":\"Coca-Cola\",\"price\":5.0}," +
            "{\"name\":\"Dr. Pepper\",\"price\":5.5}," +
            "{\"name\":\"7-Up\",\"price\":4.5}" +
            "]";
        List<String> data = new ArrayList<>();
        data.add(foodJson);
        DataFileHandler.saveToJsonFile(data, TEST_FOOD_FILE);
        
        List<Map<String, Object>> foodData = 
            DataFileHandler.loadFoodInventoryData(TEST_FOOD_FILE);
        
        assertEquals(3, foodData.size());
        assertEquals("Coca-Cola", foodData.get(0).get("name"));
        assertEquals("Dr. Pepper", foodData.get(1).get("name"));
        assertEquals("7-Up", foodData.get(2).get("name"));
    }
    
    // ========== INTEGRATION TESTS ==========
    
    @Test
    public void testSaveAndLoad_RoundTrip() {
        List<String> originalData = new ArrayList<>();
        originalData.add("{\"id\":1,\"name\":\"Test1\",\"value\":100}");
        originalData.add("{\"id\":2,\"name\":\"Test2\",\"value\":200}");
        originalData.add("{\"id\":3,\"name\":\"Test3\",\"value\":300}");
        
        // Save
        DataFileHandler.saveToJsonFile(originalData, TEST_FILE);
        
        // Load
        List<String> loadedData = DataFileHandler.loadFromJsonFile(TEST_FILE);
        
        assertEquals("Data should match after round trip", 
                     originalData.size(), loadedData.size());
        for (int i = 0; i < originalData.size(); i++) {
            assertEquals("Each line should match", 
                        originalData.get(i), loadedData.get(i));
        }
    }
    
    @Test
    public void testMultipleSaveLoad_SameFile() {
        // First save
        List<String> data1 = new ArrayList<>();
        data1.add("{\"version\":1}");
        DataFileHandler.saveToJsonFile(data1, TEST_FILE);
        List<String> loaded1 = DataFileHandler.loadFromJsonFile(TEST_FILE);
        assertEquals(1, loaded1.size());
        
        // Second save (overwrite)
        List<String> data2 = new ArrayList<>();
        data2.add("{\"version\":2}");
        data2.add("{\"version\":3}");
        DataFileHandler.saveToJsonFile(data2, TEST_FILE);
        List<String> loaded2 = DataFileHandler.loadFromJsonFile(TEST_FILE);
        assertEquals(2, loaded2.size());
        
        // Third save (overwrite again)
        List<String> data3 = new ArrayList<>();
        data3.add("{\"version\":4}");
        DataFileHandler.saveToJsonFile(data3, TEST_FILE);
        List<String> loaded3 = DataFileHandler.loadFromJsonFile(TEST_FILE);
        assertEquals(1, loaded3.size());
    }
    
    // ========== EDGE CASE TESTS ==========
    
    @Test
    public void testSaveToJsonFile_VeryLongLine() {
        List<String> data = new ArrayList<>();
        String longLine = "{\"data\":\"" + "x".repeat(10000) + "\"}";
        data.add(longLine);
        
        DataFileHandler.saveToJsonFile(data, TEST_FILE);
        List<String> loaded = DataFileHandler.loadFromJsonFile(TEST_FILE);
        
        assertEquals(1, loaded.size());
        assertEquals(longLine, loaded.get(0));
    }
    
    @Test
    public void testLoadFromJsonFile_FileWithOnlyWhitespace() {
        List<String> data = new ArrayList<>();
        data.add("   ");
        data.add("\t");
        data.add("  \n  ");
        DataFileHandler.saveToJsonFile(data, TEST_FILE);
        
        List<String> loaded = DataFileHandler.loadFromJsonFile(TEST_FILE);
        
        assertTrue("Should skip whitespace-only lines", loaded.isEmpty());
    }
    
    @Test
    public void testSaveToJsonFile_NullFilename() {
        List<String> data = new ArrayList<>();
        data.add("{\"test\":\"data\"}");
        
        try {
            DataFileHandler.saveToJsonFile(data, null);
            // If no exception, test passes (method handles it)
        } catch (NullPointerException e) {
            // Also acceptable - null filename should fail
            assertTrue(true);
        }
    }
    
    @Test
    public void testLoadFromJsonFile_NullFilename() {
        try {
            List<String> data = DataFileHandler.loadFromJsonFile(null);
            // Should return empty list or throw exception
            assertNotNull(data);
        } catch (NullPointerException e) {
            // Also acceptable
            assertTrue(true);
        }
    }
}
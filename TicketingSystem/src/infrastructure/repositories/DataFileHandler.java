/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package infrastructure.repositories;

/**
 *
 * @author zhili
 */
// DataFileHandler.java - Utility/Data Layer
// DataFileHandler.java - Utility/Data Layer
import application.utilities.LoggerSetup;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataFileHandler {
    private static final Logger logger = LoggerSetup.getLogger();

    // --- Customer/Payment Persistence (One JSON Object Per Line) ---
    public static void saveToJsonFile(List<String> jsonList, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String json : jsonList) {
                writer.write(json);
                writer.newLine(); 
            }
            logger.log(Level.INFO, "Data saved successfully to: {0}", filename);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to save data to file {0}: {1}", new Object[]{filename, e.getMessage()});
        }
    }

    public static List<String> loadFromJsonFile(String filename) {
        List<String> jsonList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    jsonList.add(line);
                }
            }
            logger.log(Level.INFO, "Data loaded successfully from: {0}", filename);
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, "File not found: {0}. Returning empty list.", filename);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to read data from file {0}. Error: {1}", new Object[]{filename, e.getMessage()});
        }
        return jsonList;
    }

    // --- Food Inventory Loading (Single JSON Array File) ---

    private static String readEntireFile(String filename) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line.trim());
            }
            return content.toString();
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, "Inventory file not found: {0}.", filename);
            return null;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading inventory file {0}: {1}", new Object[]{filename, e.getMessage()});
            return null;
        }
    }

    public static List<Map<String, Object>> loadFoodInventoryData(String filename) {
        List<Map<String, Object>> inventory = new ArrayList<>();
        String jsonContent = readEntireFile(filename);

        if (jsonContent == null || jsonContent.isEmpty() || !jsonContent.startsWith("[") || !jsonContent.endsWith("]")) {
            return inventory;
        }

        String arrayContent = jsonContent.substring(1, jsonContent.length() - 1);
        String[] itemStrings = arrayContent.split("},\\s*\\{");

        for (String itemStr : itemStrings) {
            Map<String, Object> item = new HashMap<>();

            if (!itemStr.startsWith("{")) itemStr = "{" + itemStr;
            if (!itemStr.endsWith("}")) itemStr = itemStr + "}";

            try {
                // Simple manual parsing assuming the format: {"name":"X", "price":Y}
                int nameStart = itemStr.indexOf("\"name\":\"") + 8;

                // --- FIX APPLIED HERE ---
                // Changed "\",\"price\"" to "\", \"price\"" to match the actual JSON format (note the space)
                int nameEnd = itemStr.indexOf("\", \"price\""); 

                String name = itemStr.substring(nameStart, nameEnd);

                int priceStart = itemStr.indexOf("\"price\":") + 8;
                int priceEnd = itemStr.lastIndexOf("}");
                String priceStr = itemStr.substring(priceStart, priceEnd);
                double price = Double.parseDouble(priceStr);

                item.put("name", name);
                item.put("price", price);
                inventory.add(item);

            } catch (NumberFormatException e) {
                logger.log(Level.SEVERE, "Failed to parse item from file " + filename + ": " + itemStr, e);
            }
        }

        logger.info(() -> "Successfully parsed " + inventory.size() + " items from food inventory file: " + filename);
        return inventory;
    }
}
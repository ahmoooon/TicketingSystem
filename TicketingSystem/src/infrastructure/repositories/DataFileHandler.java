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

            if (!itemStr.startsWith("{")) {
                itemStr = "{" + itemStr;
            }
            if (!itemStr.endsWith("}")) {
                itemStr = itemStr + "}";
            }

            // DataFileHandler.java - loadFoodInventoryData() (Inside the loop)
            // ...
            try {
                // Find the full string value, including the quotes, for "name"
                int nameKeyStart = itemStr.indexOf("\"name\"");
                int nameValueStart = itemStr.indexOf(":", nameKeyStart) + 1; // Find the colon after "name" and move one character right

                // --- Core Fix ---
                // Find the start quote of the name value
                int quote1 = itemStr.indexOf("\"", nameValueStart);
                if (quote1 == -1) {
                    throw new IllegalStateException("Name value quote not found.");
                }

                // Find the end quote of the name value (looking after the start quote)
                int quote2 = itemStr.indexOf("\"", quote1 + 1);
                if (quote2 == -1) {
                    throw new IllegalStateException("Name value end quote not found.");
                }

                // The name is the substring BETWEEN the two quotes.
                String name = itemStr.substring(quote1 + 1, quote2);
                // --- End Core Fix ---

                // Extract price as before (this looks safe)
                int priceStart = itemStr.indexOf("\"price\":") + 8;
                int priceEnd = itemStr.lastIndexOf("}");
                String priceStr = itemStr.substring(priceStart, priceEnd);
                double price = Double.parseDouble(priceStr);

                // We clean the name here to remove any potential hidden characters like the colon
                // that your manual parsing might have accidentally included in the FoodService.
                item.put("name", name.trim());
                item.put("price", price);
                inventory.add(item);

            } catch (NumberFormatException | IllegalStateException e) {
                logger.log(Level.SEVERE, "Failed to parse item from file " + filename + ": " + itemStr, e);
            }
        }

        logger.info(() -> "Successfully parsed " + inventory.size() + " items from food inventory file: " + filename);
        return inventory;
    }
}
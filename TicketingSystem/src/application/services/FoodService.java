/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package application.services;

/**
 *
 * @author zhili
 */
// FoodService.java - Business Logic Layer (Inventory and Order Management)
import application.utilities.LoggerSetup;
import domain.Food;
import domain.factory.FoodFactory;
import infrastructure.repositories.DataFileHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FoodService {
    private static final Logger logger = LoggerSetup.getLogger();
    
    private final Map<String, List<Food>> inventory;

    public FoodService() {
        this.inventory = new HashMap<>();
        loadInventory();
    }

    private void loadInventory() {
        // MATCHING ORIGINAL LOGIC: Reset Food numbering before loading menu
        Food.setLastNum(1); 

        loadCategory("Beverage", "beverage.json");
        loadCategory("Popcorn", "popcorn.json");
        loadCategory("HotFood", "hotfood.json");
        
        logger.log(Level.INFO, "Food inventory loaded. Next item ID will be: {0}", Food.getLastNum());
    }
    
    private void loadCategory(String type, String filename) {
        List<Map<String, Object>> data = DataFileHandler.loadFoodInventoryData(filename);
        List<Food> items = new ArrayList<>();
        
        for (Map<String, Object> item : data) {
            String name = (String) item.get("name");
            Double price = (Double) item.getOrDefault("price", 0.0);
            
            try {
                // Use the factory to create the item, which increments lastNum
                items.add(FoodFactory.createMenuItem(type, name, price));
            } catch (IllegalArgumentException e) {
                logger.log(Level.WARNING, "Skipping item due to unknown type or parsing error: {0}", name);
            }
        }
        inventory.put(type, items);
        logger.log(Level.INFO, "Loaded {0} items for category: {1}", new Object[]{items.size(), type});
    }
    
    public List<Food> getMenuByType(String type) {
        return new ArrayList<>(inventory.getOrDefault(type, List.of()));
    }
    
    /**
     * Finds an item in the current order and either increments its quantity or adds a new item.
     * @param orders
     * @param newItem
     * @param unitPrice
     * @return 
     */
    public ArrayList<Food> addOrMergeOrder(ArrayList<Food> orders, Food newItem, double unitPrice) {
        if (newItem == null) return orders;

        for (Food existingItem : orders) {
            if (existingItem.getName() != null && 
                existingItem.getClass().equals(newItem.getClass()) && 
                existingItem.getName().equals(newItem.getName())) {
                
                // 1. Merge Qty and Price (using your original methods)
                existingItem.incrementQty(newItem.getQty()); 
                existingItem.incrementPrice(newItem.getPrice()); 

                // NOTE: We rely on the caller setting the total price correctly before calling this.
                logger.log(Level.INFO, "Merged order: {0}, new quantity: {1}", new Object[]{existingItem.getName(), existingItem.getQty()});
                return orders; 
            }
        }
        
        // If no duplicate found, add new item (the newItem already has its total price set)
        orders.add(newItem);
        logger.log(Level.INFO, "New item added to order: {0}", newItem.getName());
        return orders;
    }
    
    public double calculateTotal(ArrayList<Food> orders) {
        return orders.stream()
                     .mapToDouble(Food::getPrice) // price field stores total price in order context
                     .sum();
    }
    
    public boolean removeOrderItem(ArrayList<Food> orders, int index) {
        if (index >= 0 && index < orders.size()) {
            String name = orders.get(index).getName();
            orders.remove(index);
            logger.log(Level.INFO, "Removed order item at index {0}: {1}", new Object[]{index, name});
            return true;
        }
        return false;
    }
}

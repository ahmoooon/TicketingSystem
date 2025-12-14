/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package presentation.cli;

/**
 *
 * @author zhili
 */
// FoodOrderController.java - Presentation Layer (I/O & Menu Flow)
// FoodOrderController.java - Presentation Layer (I/O & Menu Flow)
import application.services.FoodService;
import application.utilities.LoggerSetup;
import application.utilities.Utility;
import domain.Beverage;
import domain.Food;
import domain.HotFood;
import domain.Popcorn;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FoodOrderController {
    private static final Logger logger = LoggerSetup.getLogger(); 
    
    private final Scanner sc;
    private final FoodService foodService;
    
    // NOTE: Requires access to a static checkError method (e.g., Utility.checkError)

    public FoodOrderController(Scanner sc, FoodService foodService) {
        this.sc = sc;
        this.foodService = foodService;
        logger.info("FoodOrderController initialized.");
    }

    public ArrayList<Food> startOrdering(ArrayList<Food> oldFood) {
        boolean back = false;
        ArrayList<Food> orders = (oldFood != null) ? oldFood : new ArrayList<>();
        logger.info("Starting new food order process.");

        do {
            System.out.println("\n----------< Food & Beverage Menu >----------");
            System.out.println("\t|| 1 | Order Beverage         ||\n\t|| 2 | Order Popcorn        ||\n\t|| 3 | Order Hot Food        ||\n\t|| 4 | View Orders           ||\n\t|| 5 | Remove Order          ||\n\t|| 6 | Exit to Main Menu     ||\n");
            System.out.print("\nChoose one of the option from menu above ~ ");

            // Assuming Utility.checkError is available
            int choice = Utility.checkError(sc, 1, 6); 
            
            switch (choice) {
                case 1 -> {
                    orders = handleOrderItem(orders, "Beverage");
                    logger.fine("User selected Beverage order.");
                }
                case 2 -> {
                    orders = handleOrderItem(orders, "Popcorn");
                    logger.fine("User selected Popcorn order.");
                }
                case 3 -> { // Hot Food
                    orders = handleOrderItem(orders, "HotFood");
                    logger.fine("User selected Hot Food order.");
                }
                case 4 -> viewOrdersUI(orders);
                case 5 -> orders = removeOrderUI(orders);
                case 6 -> {
                    back = true;
                    logger.info("Exiting food order module.");
                }
                default -> logger.log(Level.WARNING, "Invalid menu choice received: {0}", choice);
            }
        } while (!back);

        return orders;
    }

    private ArrayList<Food> handleOrderItem(ArrayList<Food> orders, String type) {
        logger.log(Level.FINE, "Handling order for food type: {0}", type);
        List<Food> menu = foodService.getMenuByType(type);

        if (menu.isEmpty()) {
            System.out.println("\n <!> " + type + " menu is currently unavailable. <!>");
            logger.log(Level.WARNING, "{0} menu is empty or could not be loaded.", type);
            return orders;
        }
        
        System.out.println("\n----------------< Order " + type + " >-----------------");
        for (Food item : menu) {
            System.out.println(item.toString());
        }
        System.out.println("===================================================");
        System.out.print("Enter Choice (0: Back) ~ ");

        // Determine max valid food number for error checking
        int maxFoodNum = menu.stream().mapToInt(Food::getFoodNum).max().orElse(0);
        int itemChoiceNum = Utility.checkError(sc, 0, maxFoodNum); 
        
        if (itemChoiceNum == 0) return orders;
        
        Food selectedItem = menu.stream()
                                .filter(f -> f.getFoodNum() == itemChoiceNum)
                                .findFirst()
                                .orElse(null);

        if (selectedItem == null) {
            System.out.println("\n <!> Item number " + itemChoiceNum + " not found. <!>");
            logger.log(Level.WARNING, "Selected menu item number {0} not found in {1} menu.", new Object[]{itemChoiceNum, type});
            return orders;
        }
        
        System.out.print("Enter Quantity ~ ");
        int qty = Utility.checkError(sc, 1, 99); 
        
        // 1. Create a NEW instance for the order item (using the appropriate Food constructor)
        Food newItem;
        newItem = switch (type) {
            case "Popcorn" -> new Popcorn(itemChoiceNum);
            case "Beverage" -> new Beverage(itemChoiceNum);
            case "HotFood" -> new HotFood(itemChoiceNum);
            default -> throw new IllegalStateException("Unexpected value: " + (type));
        };
        
        // 2. Set details and calculate price as per your original logic
        newItem.setName(selectedItem.getName());
        newItem.setPrice(selectedItem.getPrice()); // Price field now holds the UNIT PRICE
        newItem.setQty(qty);
        newItem.calPrice(); // Price field now holds the TOTAL PRICE (qty * unitPrice)
        logger.fine(String.format("New item ordered: %s (Qty: %d, Total Price: %.2f)", 
                                  newItem.getName(), newItem.getQty(), newItem.getPrice()));

        // 3. Delegate to the service for merging/adding
        // We pass a dummy unitPrice here as the total price logic is handled in newItem
        return foodService.addOrMergeOrder(orders, newItem, 0.0);
    }
    
    private void viewOrdersUI(ArrayList<Food> orders) {
        if (orders.isEmpty()) {
            System.out.println("\n <!> You Haven't Ordered Anything! <!>");
            return;
        }
        
        System.out.println("\n--------------------< Your Current Food Orders >--------------------");
        System.out.println("\t------------------------------------------------------");
        System.out.printf("\t| %-5s | %-25s | %-4s | %-7s |\n", "No.", "Item Name", "Qty", "Price");
        System.out.println("\t------------------------------------------------------");
        
        double subTotal = 0.0;
        // Use a standard index counter for display number
        for (int i = 0; i < orders.size(); i++) {
            Food item = orders.get(i);
            // Display item number starting from 1 for the user
            System.out.printf("\t| %-5d | %-25s | %-4d | %-7.2f |\n", 
                              i + 1, // Display index for user
                              item.getName(), 
                              item.getQty(), 
                              item.getPrice()); // Assuming price is total price (qty * unitPrice)
            subTotal += item.getPrice();
        }
        
        System.out.println("\t------------------------------------------------------");
        System.out.printf("\t| %-40s | %-7.2f |\n", "TOTAL F&B", subTotal);
        System.out.println("\t======================================================");
        
        // Calculate total using FoodService (redundant but good practice to check)
        double serviceTotal = foodService.calculateTotal(orders);
        if (Math.abs(subTotal - serviceTotal) > 0.01) {
             logger.warning("Discrepancy in calculated total price in Controller vs Service!");
        }
        
        logger.info(String.format("Viewing orders. Total items: %d, Grand Total: %.2f", orders.size(), subTotal));
    }

    // --- Remove Order Implementation ---
    /**
     * Handles the user interaction for removing a food item from the current order.
     * @param orders The current list of food orders.
     * @return The updated list of food orders.
     */
    private ArrayList<Food> removeOrderUI(ArrayList<Food> orders) {
        if (orders.isEmpty()) {
            System.out.println("\n <!> You Haven't Ordered Anything! <!>");
            return orders;
        }
        
        // Display the orders so the user can select an item number (1-based index)
        viewOrdersUI(orders);

        System.out.print("\nEnter Order Number To Remove (0 to cancel): ");
        // Max valid input is the size of the list (orders.size()), which is the last item number (1-based)
        int removeNum = Utility.checkError(sc, 0, orders.size());

        if (removeNum == 0) {
            logger.fine("Order removal cancelled by user.");
            return orders;
        }

        // Convert 1-based user input (removeNum) to 0-based list index
        int index = removeNum - 1;
        
        System.out.print("\nConfirm to delete item with Item No (" + removeNum + ")? (Y: Yes/N: No) > ");
        String confirm = sc.nextLine().toUpperCase();
        
        if (confirm.equals("Y")) {
            // Delegate the removal to the FoodService using the 0-based index
            if (foodService.removeOrderItem(orders, index)) {
                System.out.println("\nSuccessfully removed item with Item No: (" + removeNum + ").");
                logger.log(Level.INFO, "Successfully removed item at list index {0}", index);
            } else {
                // This shouldn't happen if checkError and index calculation is correct
                System.out.println("\nFailed to remove item.");
                logger.log(Level.WARNING, "Failed to remove item at index {0}", index);
            }
        } else {
            System.out.println("\nItem removal cancelled.");
        }
        
        return orders;
    }
}
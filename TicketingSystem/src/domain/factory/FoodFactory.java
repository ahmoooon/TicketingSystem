/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package domain.factory;

/**
 *
 * @author zhili
 */
// FoodFactory.java - REVISED Factory Layer
import application.utilities.LoggerSetup;
import domain.Beverage;
import domain.Food;
import domain.HotFood;
import domain.Popcorn;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FoodFactory {
    private static final Logger logger = LoggerSetup.getLogger();

    /**
     * Creates a concrete Food item for the static inventory menu.
     * Crucially, this is the only place that calls the constructor
     * that increments Food.lastNum, ensuring unique IDs for menu items.
     * @param type The type of food ("Popcorn", "Beverage", "HotFood").
     * @param name The name of the item.
     * @param price The unit price of the item.
     * @return A new concrete Food object.
     */
    public static Food createMenuItem(String type, String name, double price) {
        switch (type.toUpperCase()) {
            case "POPCORN":
                return new Popcorn(name, price);
            case "BEVERAGE":
                return new Beverage(name, price);
            case "HOTFOOD":
                return new HotFood(name, price);
            default:
                logger.log(Level.SEVERE, "Attempted to create unknown food type: {0}", type);
                throw new IllegalArgumentException("Unknown food type: " + type);
        }
    }
}

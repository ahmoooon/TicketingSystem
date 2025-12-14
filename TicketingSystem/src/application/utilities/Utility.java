/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package application.utilities;

/**
 *
 * @author zhili
 */
// Utility.java - Shared Utility Class
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Logger;

public class Utility {
    private static final Logger logger = LoggerSetup.getLogger();

    /**
     * Ensures user input is an integer within a specified valid range.
     * @param sc The Scanner object for input.
     * @param min The minimum valid integer (inclusive).
     * @param max The maximum valid integer (inclusive).
     * @return A valid integer input from the user.
     */
    public static int checkError(Scanner sc, int min, int max) {
        int choice = -1;
        boolean validInput = false;

        do {
            try {
                choice = sc.nextInt();
                sc.nextLine(); // Consume the newline left behind

                if (choice >= min && choice <= max) {
                    validInput = true;
                } else {
                    System.out.printf("\n <!> Please enter a value between %d and %d only. <!>\n", min, max);
                    logger.warning(String.format("Invalid input received: %d. Expected range: [%d, %d]", choice, min, max));
                }
            } catch (InputMismatchException e) {
                System.out.println("\n <!> Invalid input! Please enter a number. <!>");
                sc.nextLine(); // Clear the invalid input from the scanner buffer
                logger.warning("Non-integer input detected.");
            }
        } while (!validInput);

        return choice;
    }
}

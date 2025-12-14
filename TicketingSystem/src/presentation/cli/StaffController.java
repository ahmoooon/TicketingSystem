/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package presentation.cli;

/**
 *
 * @author zhili
 */
// StaffController.java - Presentation Layer (I/O & Flow Control)
import application.services.StaffService;
import application.utilities.Utility;
import domain.Customer;
import domain.Payment;
import java.util.Scanner;
import java.util.ArrayList;
// Assuming checkError utility is available
// Assuming Payment is imported

public class StaffController {
    private final Scanner sc;
    private final StaffService staffService; // Business dependency

    public StaffController(Scanner sc, StaffService staffService) {
        this.sc = sc;
        this.staffService = staffService;
    }

    public void staffMainMenu(ArrayList<Customer> customerList) {
        boolean logout = false;
        
        do {
            System.out.println("\n----------< Staff Main Menu >----------");
            System.out.println("\t|| 1 | Generate Sales Report\t||\n\t|| 2 | Delete Customer Account\t||\n\t|| 3 | Logout\t\t\t||\n");
            System.out.print("Choose option ~ ");
            
            int choice = Utility.checkError(sc, 1, 3);
            
            switch (choice) {
                case 1 -> reportModuleUI(customerList);
                case 2 -> deleteCustomerUI();
                case 3 -> logout = true;
            }
        } while (!logout);
    }
    
    private void deleteCustomerUI() {
        System.out.println("\n----------< Delete Customer Account >----------");
        
        System.out.print("Enter the name of the customer to delete (0 to back): ");
        String nameToDelete = sc.nextLine().trim();

        if (nameToDelete.equals("0")) return;

        System.out.print("Confirm deletion for " + nameToDelete + "? (Y/N): ");
        String confirm = sc.nextLine().trim().toUpperCase();

        if (!confirm.equals("Y")) {
            System.out.println("\nCustomer deletion cancelled by staff.");
            return;
        }
        
        // Delegate business action (which includes persistence) to StaffService
        if (staffService.deleteCustomerAccount(nameToDelete)) {
            System.out.println("\n SUCCESS: Account " + nameToDelete + " has been deleted.");
        } else {
            System.out.println("\n ERROR: Account " + nameToDelete + " was NOT found or cannot be deleted.");
        }
    }
    
    public void reportModuleUI(ArrayList<Customer> customerList) {
        int reportChoice = 0;
        boolean back = false;
        String reportOutput = ""; // Variable to hold the final formatted string

        do {
            System.out.println("\n----------< Reporting Menu >----------");
            System.out.println("\t|| 1 | Customer List                 ||\n\t|| 2 | Movie Purchase Record         ||\n\t|| 3 | Food Purchase Record          ||\n\t|| 4 | Sales Summary Report (NEW)    ||\n\t|| 5 | Back                          ||\n");
            System.out.print("\nWhich report would you like to view? > ");
            
            // Assuming checkError utility is available
            reportChoice = Utility.checkError(sc, 1, 5); 
            
            switch (reportChoice) {
                case 1: // Customer List
                    reportOutput = staffService.getCustomerListReport(customerList);
                    break;
                case 2: // Movie Purchase Record
                    reportOutput = staffService.getMoviePurchaseReport();
                    break;
                case 3: // Food Purchase Record
                    reportOutput = staffService.getFoodPurchaseReport();
                    break;
                case 4: // Sales Summary Report (NEW)
                    reportOutput = staffService.getSalesSummaryReport();
                    break;
                case 5:
                    back = true;
                    reportOutput = "\nReturning to Staff Main Menu...";
                    break;
                default:
                    reportOutput = "\n <!> Please enter within the range of (1 to 5)! <!>";
                    break;
            }
            
            // Output the generated report string (Controller's I/O responsibility)
            System.out.println(reportOutput);
            
            // Pause before looping back if a report was shown
            if (!back) {
                System.out.print("\nPress ENTER to continue...");
                sc.nextLine(); 
            }

        } while (!back);
    }
}

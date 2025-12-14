// src/presentation/cli/MainApplication.java (Final Cleaned Version)
package presentation.cli;

import application.services.AuthService;
import application.services.BookingService;
import application.services.CustomerService;
import application.services.FoodService;
import application.services.OtpService;
import application.services.PasswordService;
import application.services.PaymentService;
import application.services.ReportService;
import application.services.StaffService;
import application.utilities.LoggerSetup;
import application.utilities.Utility;
import domain.Customer;
import domain.Ticket;
import infrastructure.repositories.FileMovieRepository;
import infrastructure.repositories.FileSeatRepository;
import infrastructure.repositories.FileShowtimeRepository;
import domain.repositories.MovieRepository;
import domain.repositories.SeatRepository;
import domain.repositories.ShowtimeRepository;

import java.util.Scanner;
import java.util.ArrayList;
import domain.Food;
import domain.Payment;
import infrastructure.repositories.PaymentRepository;
import java.util.Optional;
import java.util.logging.Logger;
// Removed unnecessary domain imports that are now handled by BookingCliHandler

// We assume the original monolithic classes (Food, Payment, etc.) 
// are still in the original 'ticketingsystem' package for now.

public class MainApplication {
    
    public static void logo(int num) {
        switch (num) {
            case 1:
                System.out.println("___  ___      _        ___  ___                 ");
                System.out.println("|  \\/  |     (_)       |  \\/  |                 ");
                System.out.println("| .  . | __ _ _ _ __   | .  . | ___ _ __  _   _ ");
                System.out.println("| |\\/| |/ _` | | '_ \\  | |\\/| |/ _ \\ '_ \\| | | |");
                System.out.println("| |  | | (_| | | | | | | |  | |  __/ | | | |_| |");
                System.out.println("\\_|  |_/\\__,_|_|_| |_| \\_|  |_/\\___|_| |_|\\__,_|");
                System.out.println("                                                ");
                System.out.println();
                System.out.println("\n----------< Welcome to Main Menu >----------");
                break;
            case 2:
                System.out.println(" ____               _    _             ");
                System.out.println(" |  _ \\            | |  (_)            ");
                System.out.println(" | |_) | ___   ___ | | ___ _ __   __ _ ");
                System.out.println(" |  _ < / _ \\ / _ \\| |/ / | '_ \\ / _` |");
                System.out.println(" | |_) | (_) | (_) |   <| | | | | (_| |");
                System.out.println(" |____/ \\___/ \\___/|_|\\_\\_|_| |_|\\__, |");
                System.out.println("                                  __/ |");
                System.out.println("                                 |___/ ");
                System.out.println();
                System.out.println("\n----------< Welcome to Booking >----------");
                break;
            default:
                break;
        }
    }

    public static void main(String[] args) {
        // --- CORE APPLICATION RESOURCES ---
        Scanner sc = new Scanner(System.in);
        Logger logger = LoggerSetup.getLogger();
        
        logger.info("=== CINEMA TICKETING SYSTEM STARTING ===");
        
        // Infrastructure Layer Setup (Concrete implementations)
        MovieRepository movieRepository = new FileMovieRepository();
        ShowtimeRepository showtimeRepository = new FileShowtimeRepository();
        SeatRepository seatRepository = new FileSeatRepository();
        PaymentRepository paymentRepository = new PaymentRepository();

        // Application Layer Setup (Services injected with Repositories)
        BookingService bookingService = new BookingService(
            movieRepository, 
            showtimeRepository, 
            seatRepository
        );
        CustomerService customerService = new CustomerService(); 
        PasswordService passwordService = new PasswordService();
        OtpService otpService = new OtpService();
        AuthService authService = new AuthService(passwordService, otpService);
        ReportService reportService = new ReportService(paymentRepository); 
        StaffService staffService = new StaffService(reportService, customerService);
        FoodService foodService = new FoodService();
        PaymentService payService = new PaymentService(); 

        // Presentation Layer Setup (CLI Handlers/Controllers injected with Services/Scanner)
        BookingCliHandler bookingHandler = new BookingCliHandler(bookingService, sc);
        StaffController staffController = new StaffController(sc, staffService);
        PaymentCliHandler payHandler = new PaymentCliHandler(payService, paymentRepository, sc);
        AuthController authController = new AuthController(
            sc, authService, customerService, otpService, staffController
        );
        FoodOrderController foodController = new FoodOrderController(sc, foodService);
        
        // --- DATA STORAGE (Lists initialized centrally) ---
        ArrayList<Ticket> currentTicketOrders = new ArrayList<>();
        ArrayList<Food> currentFoodOrders = new ArrayList<>();
        ArrayList<Payment> paymentHistory = new ArrayList<>();
        
        logger.info("Application initialized. Starting Authentication flow.");
        

        // --- START AUTH FLOW ---
        boolean proceedToMain = authController.startAuthFlow(); 
        
        if (!proceedToMain) {
            System.out.println("Application closed.");
            sc.close();
            return;
        }

        int mainMenu = 0;

        // Main menu
        logo(1);

        while(true) {
            Optional<Customer> currentUser = customerService.getLoggedInCustomer();
            if (currentUser.isEmpty()) break;
            System.out.println("\n\t|| 1 | Book Ticket        ||\n\t|| 2 | Order F&B          ||\n\t|| 3 | Payment            ||\n\t|| 4 | Exit Program       ||\n\t|| 5 | Logout             ||\n");
            System.out.print("Choose one of the option from menu above ~ ");
            mainMenu = Utility.checkError(sc, 1, 5);
            switch (mainMenu) {
                case 1://Booking Menu
                    // DELEGATE to the new Handler
                    currentTicketOrders = bookingHandler.handleBookingMenu(currentTicketOrders); 
                    break;
                case 2://F&B Menu (Uses unrefactored static methods)
                    foodController.startOrdering(currentFoodOrders);
                    break;
                case 3: // Payment
                    Payment paid = payHandler.handlePaymentMenu(currentTicketOrders, currentFoodOrders, currentUser, paymentHistory);

                    if (paid != null) {
                        if (paid.getPaymentMade()) {
                            // SUCCESS: Clear cart and add to history
                            paymentHistory.add(paid);
                            currentTicketOrders.clear();
                            currentFoodOrders.clear();
                        } else {
                            System.out.println("\n <$> Payment hasn't been done! <$> ");
                            logger.info("Payment completed. Cart cleared.");
                        }
                    }
                    break;
                case 4://Exit the program
                    System.out.println("\nThank you for using YSCM Cinema Ticketing System. Goodbye!");
                    sc.close();
                    System.exit(0);
                    break;
                case 5://Logout
                    customerService.setLoggedInCustomer(null); 
                    logger.info("User logged out. Returning to Login Menu.");
                    if (!authController.startAuthFlow()) { 
                        System.out.println("\nThank you for using YSCM Cinema Ticketing System. Goodbye!");
                        sc.close();
                        return;
                    }
                    // Clear cart on logout for security
                    currentTicketOrders.clear();
                    currentFoodOrders.clear();
                    logger.info("Cart cleared on logout.");
                    break;
            }
        }
    }
}
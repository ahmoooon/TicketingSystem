package presentation.gui;

import application.services.*;
import domain.Customer;
import domain.Food;
import domain.Ticket;
import infrastructure.repositories.PaymentRepository;
import infrastructure.repositories.CartManager;
import infrastructure.repositories.FileSeatRepository;
import javafx.scene.Scene;
import javafx.stage.Stage;
import presentation.gui.views.*;

import java.util.ArrayList;
import java.util.Optional;

public class ViewManager {
    
    private final Stage primaryStage;
    private final AuthService authService;
    private final CustomerService customerService;
    private final BookingService bookingService;
    private final FoodService foodService;
    private final PaymentService paymentService;
    private final StaffService staffService;
    private final PaymentRepository paymentRepository;
    private final OtpService otpService;
    
    // NEW: Cart management
    private final CartManager cartManager;
    private final FileSeatRepository seatRepository;
    
    private Optional<Customer> currentUser = Optional.empty();
    private ArrayList<Ticket> ticketCart = new ArrayList<>();
    private ArrayList<Food> foodCart = new ArrayList<>();
    
    public ViewManager(Stage primaryStage, AuthService authService, 
                      CustomerService customerService, BookingService bookingService,
                      FoodService foodService, PaymentService paymentService,
                      StaffService staffService, PaymentRepository paymentRepository,
                      OtpService otpService, CartManager cartManager,
                      FileSeatRepository seatRepository) {
        this.primaryStage = primaryStage;
        this.authService = authService;
        this.customerService = customerService;
        this.bookingService = bookingService;
        this.foodService = foodService;
        this.paymentService = paymentService;
        this.staffService = staffService;
        this.paymentRepository = paymentRepository;
        this.otpService = otpService;
        this.cartManager = cartManager;
        this.seatRepository = seatRepository;
    }
    
    public void showLoginView() {
        LoginView loginView = new LoginView(this, authService, customerService, otpService);
        setScene(loginView, "YSCM Cinema - Login");
    }
    
    public void showMainMenu(Customer customer) {
        this.currentUser = Optional.of(customer);
        customerService.setLoggedInCustomer(customer);
        
        // RESTORE CART from persistence
        CartManager.CartData savedCart = cartManager.loadCart(customer);
        if (savedCart != null) {
            this.ticketCart = savedCart.tickets;
            this.foodCart = savedCart.food;
            System.out.println("✓ Restored cart: " + ticketCart.size() + " tickets, " + foodCart.size() + " food items");
        }
        
        MainMenuView mainMenuView = new MainMenuView(this, customer);
        setScene(mainMenuView, "YSCM Cinema - Main Menu");
    }
    
    public void showStaffMenu() {
        StaffMenuView staffMenuView = new StaffMenuView(this, staffService, customerService);
        setScene(staffMenuView, "YSCM Cinema - Staff Portal");
    }
    
    public void showBookingView() {
        BookingView bookingView = new BookingView(this, bookingService, ticketCart);
        setScene(bookingView, "YSCM Cinema - Book Tickets");
    }
    
    public void showFoodOrderView() {
        FoodOrderView foodOrderView = new FoodOrderView(this, foodService, foodCart);
        setScene(foodOrderView, "YSCM Cinema - Order Food & Beverage");
    }
    
    public void showPaymentView() {
        PaymentView paymentView = new PaymentView(
            this, paymentService, paymentRepository, seatRepository,
            ticketCart, foodCart, currentUser
        );
        setScene(paymentView, "YSCM Cinema - Payment");
    }
    
    private void setScene(javafx.scene.Parent root, String title) {
        Scene scene = new Scene(root, 1200, 800);
        try {
            scene.getStylesheets().add(
                getClass().getResource("/styles.css").toExternalForm()
            );
        } catch (Exception e) {
            // CSS file not found, continue without styling
        }
        primaryStage.setScene(scene);
        primaryStage.setTitle(title);
    }
    
    /**
     * UPDATED: Save cart after any booking/food changes
     */
    public void saveCurrentCart() {
        currentUser.ifPresent(customer -> {
            cartManager.saveCart(customer, ticketCart, foodCart);
        });
    }
    
    /**
     * UPDATED: Clear cart and release seat reservations
     */
    public void clearCart() {
        // Release seat reservations
        seatRepository.clearAllCartReservations();
        
        // Clear cart data
        ticketCart.clear();
        foodCart.clear();
        
        // Remove from persistence
        currentUser.ifPresent(cartManager::clearCart);
        
        System.out.println("✓ Cart cleared and seats released");
    }
    
    /**
     * NEW: Cancel specific ticket from cart
     */
    public void cancelTicket(Ticket ticket) {
        // Release seats from cart reservation
        for (domain.Seat seat : ticket.getSeat()) {
            seatRepository.cancelCartReservation(
                ticket.getShowtime(), 
                java.util.List.of(seat.getId())
            );
        }
        
        // Remove from cart
        ticketCart.remove(ticket);
        
        // Save updated cart
        saveCurrentCart();
        
        System.out.println("✓ Ticket cancelled and seats released");
    }
    
    public void logout() {
        // Save cart before logout
        saveCurrentCart();
        
        currentUser = Optional.empty();
        customerService.setLoggedInCustomer(null);
        
        System.out.println("✓ Logged out (cart saved)");
        
        showLoginView();
    }
    
    public ArrayList<Ticket> getTicketCart() {
        return ticketCart;
    }
    
    public ArrayList<Food> getFoodCart() {
        return foodCart;
    }
    
    public Optional<Customer> getCurrentUser() {
        return currentUser;
    }
}
package presentation.gui;

import application.services.*;
import domain.Customer;
import domain.Food;
import domain.Ticket;
import infrastructure.repositories.PaymentRepository;
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
    
    private Optional<Customer> currentUser = Optional.empty();
    private ArrayList<Ticket> ticketCart = new ArrayList<>();
    private ArrayList<Food> foodCart = new ArrayList<>();
    
    public ViewManager(Stage primaryStage, AuthService authService, 
                      CustomerService customerService, BookingService bookingService,
                      FoodService foodService, PaymentService paymentService,
                      StaffService staffService, PaymentRepository paymentRepository,
                      OtpService otpService) {
        this.primaryStage = primaryStage;
        this.authService = authService;
        this.customerService = customerService;
        this.bookingService = bookingService;
        this.foodService = foodService;
        this.paymentService = paymentService;
        this.staffService = staffService;
        this.paymentRepository = paymentRepository;
        this.otpService = otpService;
    }
    
    public void showLoginView() {
        LoginView loginView = new LoginView(this, authService, customerService, otpService);
        setScene(loginView, "YSCM Cinema - Login");
    }
    
    public void showMainMenu(Customer customer) {
        this.currentUser = Optional.of(customer);
        customerService.setLoggedInCustomer(customer);
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
            this, paymentService, paymentRepository, 
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
    
    public void clearCart() {
        ticketCart.clear();
        foodCart.clear();
    }
    
    public void logout() {
        currentUser = Optional.empty();
        customerService.setLoggedInCustomer(null);
        clearCart();
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
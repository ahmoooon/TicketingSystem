package presentation.gui.views;

import domain.Customer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import presentation.gui.ViewManager;

public class MainMenuView extends BorderPane {
    
    private final ViewManager viewManager;
    private final Customer customer;
    
    public MainMenuView(ViewManager viewManager, Customer customer) {
        this.viewManager = viewManager;
        this.customer = customer;
        initializeUI();
    }
    
    private void initializeUI() {
        setTop(createTopBar());
        setCenter(createCenterContent());
        setStyle("-fx-background-color: #ecf0f1;");
    }
    
    private HBox createTopBar() {
        Label welcomeLabel = new Label("Welcome, " + customer.getName() + "!");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        welcomeLabel.setStyle("-fx-text-fill: white;");
        
        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle(
            "-fx-background-color: #e74c3c;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;"
        );
        logoutBtn.setOnAction(e -> viewManager.logout());
        
        HBox topBar = new HBox(20, welcomeLabel, logoutBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(20));
        topBar.setStyle("-fx-background-color: #34495e;");
        HBox.setHgrow(welcomeLabel, Priority.ALWAYS);
        
        return topBar;
    }
    
    private VBox createCenterContent() {
        Label titleLabel = new Label("Main Menu");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");
        
        int ticketCount = viewManager.getTicketCart().size();
        int foodCount = viewManager.getFoodCart().size();
        Label cartLabel = new Label(
            String.format("Cart: %d Tickets | %d Food Items", ticketCount, foodCount)
        );
        cartLabel.setFont(Font.font("Arial", 14));
        cartLabel.setStyle("-fx-text-fill: #7f8c8d;");
        
        Button bookTicketBtn = createMenuButton("🎬 Book Ticket", "#3498db");
        Button orderFoodBtn = createMenuButton("🍿 Order Food & Beverage", "#f39c12");
        Button paymentBtn = createMenuButton("💳 Proceed to Payment", "#2ecc71");
        Button exitBtn = createMenuButton("🚪 Exit", "#95a5a6");
        
        bookTicketBtn.setOnAction(e -> viewManager.showBookingView());
        orderFoodBtn.setOnAction(e -> viewManager.showFoodOrderView());
        paymentBtn.setOnAction(e -> viewManager.showPaymentView());
        exitBtn.setOnAction(e -> System.exit(0));
        
        VBox buttonBox = new VBox(15, bookTicketBtn, orderFoodBtn, paymentBtn, exitBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setMaxWidth(400);
        
        VBox centerContent = new VBox(30, titleLabel, cartLabel, buttonBox);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setPadding(new Insets(50));
        
        return centerContent;
    }
    
    private Button createMenuButton(String text, String color) {
        Button button = new Button(text);
        button.setPrefWidth(350);
        button.setPrefHeight(60);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        button.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;"
        );
        
        button.setOnMouseEntered(e -> 
            button.setStyle(button.getStyle() + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);")
        );
        button.setOnMouseExited(e -> 
            button.setStyle(button.getStyle().replace("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);", ""))
        );
        
        return button;
    }
}
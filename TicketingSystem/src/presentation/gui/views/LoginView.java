package presentation.gui.views;

import application.services.AuthService;
import application.services.CustomerService;
import application.services.OtpService;
import domain.Customer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import presentation.gui.ViewManager;

import java.util.Optional;

public class LoginView extends BorderPane {
    
    private final ViewManager viewManager;
    private final AuthService authService;
    private final CustomerService customerService;
    private final OtpService otpService;
    
    public LoginView(ViewManager viewManager, AuthService authService, 
                    CustomerService customerService, OtpService otpService) {
        this.viewManager = viewManager;
        this.authService = authService;
        this.customerService = customerService;
        this.otpService = otpService;
        
        initializeUI();
    }
    
    private void initializeUI() {
        Label titleLabel = new Label("Welcome to YSCM Cinema!");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");
        
        Label subtitleLabel = new Label("Your Premier Movie Experience");
        subtitleLabel.setFont(Font.font("Arial", 16));
        subtitleLabel.setStyle("-fx-text-fill: #7f8c8d;");
        
        VBox titleBox = new VBox(10, titleLabel, subtitleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(40, 0, 20, 0));
        
        Button customerLoginBtn = createStyledButton("Customer Login", "#3498db");
        Button staffLoginBtn = createStyledButton("Staff Login", "#e74c3c");
        Button registerBtn = createStyledButton("Register New Account", "#2ecc71");
        Button exitBtn = createStyledButton("Exit", "#95a5a6");
        
        customerLoginBtn.setOnAction(e -> showCustomerLogin());
        staffLoginBtn.setOnAction(e -> showStaffLogin());
        registerBtn.setOnAction(e -> showRegistration());
        exitBtn.setOnAction(e -> System.exit(0));
        
        VBox buttonBox = new VBox(15, customerLoginBtn, staffLoginBtn, registerBtn, exitBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20));
        buttonBox.setMaxWidth(400);
        
        VBox mainContainer = new VBox(30, titleBox, buttonBox);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setStyle("-fx-background-color: #ecf0f1;");
        
        setCenter(mainContainer);
    }
    
    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setPrefWidth(300);
        button.setPrefHeight(50);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        button.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        
        button.setOnMouseEntered(e -> 
            button.setStyle(button.getStyle() + "-fx-opacity: 0.8;")
        );
        button.setOnMouseExited(e -> 
            button.setStyle(button.getStyle().replace("-fx-opacity: 0.8;", ""))
        );
        
        return button;
    }
    
    private void showCustomerLogin() {
        Dialog<Customer> dialog = new Dialog<>();
        dialog.setTitle("Customer Login");
        dialog.setHeaderText("Please enter your credentials");
        
        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField nameField = new TextField();
        nameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                String name = nameField.getText().trim();
                String password = passwordField.getText().trim();
                
                Optional<Customer> result = authService.authenticateCustomer(
                    customerService.getCustomerList(), name, password
                );
                
                if (result.isPresent()) {
                    return result.get();
                } else {
                    showError("Login Failed", "Invalid username or password");
                    return null;
                }
            }
            return null;
        });
        
        Optional<Customer> result = dialog.showAndWait();
        result.ifPresent(customer -> viewManager.showMainMenu(customer));
    }
    
    private void showStaffLogin() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Staff Login");
        dialog.setHeaderText("Please enter staff credentials");
        
        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField idField = new TextField();
        idField.setPromptText("Staff ID");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        
        grid.add(new Label("ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                String id = idField.getText().trim();
                String password = passwordField.getText().trim();
                
                Optional<String> result = authService.authenticateStaff(id, password);
                
                if (result.isPresent()) {
                    return true;
                } else {
                    showError("Login Failed", "Invalid staff ID or password");
                    return false;
                }
            }
            return false;
        });
        
        Optional<Boolean> result = dialog.showAndWait();
        if (result.isPresent() && result.get()) {
            viewManager.showStaffMenu();
        }
    }
    
    private void showRegistration() {
        Dialog<Customer> dialog = new Dialog<>();
        dialog.setTitle("Registration");
        dialog.setHeaderText("Create a new account");
        
        ButtonType registerButtonType = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(registerButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField nameField = new TextField();
        nameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password (8-16 chars, 1 letter, 1 number, 1 symbol)");
        TextField otpField = new TextField();
        otpField.setPromptText("Enter OTP");
        otpField.setDisable(true);
        
        Label generatedOtpLabel = new Label();
        generatedOtpLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        
        Button generateOtpBtn = new Button("Generate OTP");
        generateOtpBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String password = passwordField.getText().trim();
            
            if (!AuthService.isPasswordValid(password)) {
                showError("Invalid Password", 
                    "Password must be 8-16 characters with at least 1 letter, 1 number, and 1 symbol");
                return;
            }
            
            try {
                String otp = authService.startRegistration(
                    customerService.getCustomerList(), name, password
                );
                generatedOtpLabel.setText("Your OTP: " + otp);
                otpField.setDisable(false);
            } catch (IllegalArgumentException ex) {
                showError("Registration Error", ex.getMessage());
            }
        });
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(generateOtpBtn, 1, 2);
        grid.add(generatedOtpLabel, 1, 3);
        grid.add(new Label("OTP:"), 0, 4);
        grid.add(otpField, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == registerButtonType) {
                String enteredOtp = otpField.getText().trim();
                String generatedOtp = generatedOtpLabel.getText().replace("Your OTP: ", "");
                
                if (otpService.verifyOtp(enteredOtp, generatedOtp)) {
                    Customer newCustomer = authService.finalizeCustomerRegistration(
                        nameField.getText().trim(), 
                        passwordField.getText().trim()
                    );
                    customerService.addCustomer(newCustomer);
                    showInfo("Success", "Registration successful!");
                    return newCustomer;
                } else {
                    showError("Invalid OTP", "The OTP you entered is incorrect");
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
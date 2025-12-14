package presentation.gui.views;

import application.dto.PaymentRequest;
import application.dto.PaymentResult;
import application.services.PaymentService;
import domain.*;
import infrastructure.repositories.PaymentRepository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import presentation.gui.ViewManager;

import java.util.ArrayList;
import java.util.Optional;

public class PaymentView extends BorderPane {
    
    private final ViewManager viewManager;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final ArrayList<Ticket> tickets;
    private final ArrayList<Food> foods;
    private final Optional<Customer> customer;
    
    public PaymentView(ViewManager viewManager, PaymentService paymentService,
                      PaymentRepository paymentRepository, ArrayList<Ticket> tickets,
                      ArrayList<Food> foods, Optional<Customer> customer) {
        this.viewManager = viewManager;
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
        this.tickets = tickets;
        this.foods = foods;
        this.customer = customer;
        initializeUI();
    }
    
    private void initializeUI() {
        setTop(createTopBar());
        
        VBox receiptView = createReceiptView();
        VBox paymentForm = createPaymentForm();
        
        HBox mainContent = new HBox(20, receiptView, paymentForm);
        mainContent.setPadding(new Insets(20));
        
        setCenter(mainContent);
    }
    
    private HBox createTopBar() {
        Label titleLabel = new Label("Payment");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: white;");
        
        Button backBtn = new Button("← Back to Main Menu");
        backBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
        backBtn.setOnAction(e -> {
            viewManager.showMainMenu(
                viewManager.getCurrentUser().orElse(new Customer("User", ""))
            );
        });
        
        HBox topBar = new HBox(20, titleLabel, backBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(20));
        topBar.setStyle("-fx-background-color: #34495e;");
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        
        return topBar;
    }
    
    private VBox createReceiptView() {
        Label receiptTitle = new Label("Receipt");
        receiptTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        TextArea receiptArea = new TextArea();
        receiptArea.setEditable(false);
        receiptArea.setPrefHeight(400);
        receiptArea.setStyle("-fx-font-family: monospace;");
        
        StringBuilder receipt = new StringBuilder();
        receipt.append("========================================\n");
        receipt.append("         YSCM CINEMA RECEIPT\n");
        receipt.append("========================================\n\n");
        
        double ticketTotal = 0;
        if (!tickets.isEmpty()) {
            receipt.append("TICKETS:\n");
            receipt.append("----------------------------------------\n");
            for (Ticket t : tickets) {
                receipt.append(String.format("%-30s\n", t.getMovieName()));
                receipt.append(String.format("  Date: %s  Time: %s\n", t.getSchedule(), t.time()));
                receipt.append(String.format("  Qty: %d  Hall: %s\n", t.getTicketAmt(), t.getHallType()));
                receipt.append(String.format("  Price: RM%.2f\n\n", t.getTotalPrice()));
                ticketTotal += t.getTotalPrice();
            }
        }
        
        double foodTotal = 0;
        if (!foods.isEmpty()) {
            receipt.append("FOOD & BEVERAGE:\n");
            receipt.append("----------------------------------------\n");
            for (Food f : foods) {
                receipt.append(String.format("%-25s x%d\n", f.getName(), f.getQty()));
                receipt.append(String.format("  RM%.2f\n\n", f.getPrice()));
                foodTotal += f.getPrice();
            }
        }
        
        receipt.append("========================================\n");
        receipt.append(String.format("Tickets Subtotal:       RM%.2f\n", ticketTotal));
        receipt.append(String.format("Food Subtotal:          RM%.2f\n", foodTotal));
        receipt.append("----------------------------------------\n");
        receipt.append(String.format("GRAND TOTAL:            RM%.2f\n", ticketTotal + foodTotal));
        receipt.append("========================================\n");
        
        receiptArea.setText(receipt.toString());
        
        VBox receiptView = new VBox(15, receiptTitle, receiptArea);
        receiptView.setPrefWidth(450);
        
        return receiptView;
    }
    
    private VBox createPaymentForm() {
        Label formTitle = new Label("Payment Method");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        ToggleGroup paymentMethodGroup = new ToggleGroup();
        
        RadioButton bankRadio = new RadioButton("Bank Transfer");
        bankRadio.setToggleGroup(paymentMethodGroup);
        bankRadio.setSelected(true);
        
        RadioButton cashRadio = new RadioButton("Cash");
        cashRadio.setToggleGroup(paymentMethodGroup);
        
        TextField bankAccountField = new TextField();
        bankAccountField.setPromptText("9-digit account number");
        bankAccountField.setPrefWidth(250);
        
        Label bankLabel = new Label("Bank Account:");
        
        bankRadio.selectedProperty().addListener((obs, old, newVal) -> {
            bankLabel.setDisable(!newVal);
            bankAccountField.setDisable(!newVal);
        });
        
        cashRadio.selectedProperty().addListener((obs, old, newVal) -> {
            bankLabel.setDisable(newVal);
            bankAccountField.setDisable(newVal);
        });
        
        Button confirmBtn = new Button("Confirm Payment");
        confirmBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16;");
        confirmBtn.setPrefWidth(250);
        confirmBtn.setPrefHeight(50);
        
        if (tickets.isEmpty() && foods.isEmpty()) {
            confirmBtn.setDisable(true);
            Label emptyLabel = new Label("Cart is empty!");
            emptyLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            VBox form = new VBox(15, formTitle, emptyLabel);
            form.setPadding(new Insets(20));
            return form;
        }
        
        confirmBtn.setOnAction(e -> {
            String paymentMethod = bankRadio.isSelected() ? "Bank Transfer" : "Cash";
            String bankAccount = bankAccountField.getText().trim();
            
            double total = calculateTotal();
            
            PaymentRequest request = new PaymentRequest(
                total, paymentMethod, bankAccount, tickets, foods, customer
            );
            
            PaymentResult result = paymentService.processPayment(request);
            
            if (result.isSuccess()) {
                Payment payment = new Payment(customer, tickets, foods, total, true);
                paymentRepository.savePayment(payment);
                
                showInfo("Payment Successful", 
                    String.format("Payment of RM%.2f completed successfully!\n\nThank you for your purchase!", total));
                
                viewManager.clearCart();
                viewManager.showMainMenu(
                    viewManager.getCurrentUser().orElse(new Customer("User", ""))
                );
            } else {
                showError("Payment Failed", result.getMessage());
            }
        });
        
        VBox form = new VBox(15,
            formTitle,
            new Label("Select Payment Method:"),
            bankRadio,
            cashRadio,
            new Separator(),
            bankLabel,
            bankAccountField,
            new Separator(),
            confirmBtn
        );
        form.setPadding(new Insets(20));
        form.setPrefWidth(350);
        
        return form;
    }
    
    private double calculateTotal() {
        double total = 0;
        for (Ticket t : tickets) total += t.getTotalPrice();
        for (Food f : foods) total += f.getPrice();
        return total;
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
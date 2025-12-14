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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import presentation.gui.ViewManager;

import java.util.ArrayList;
import java.util.Optional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

// ZXing QR Code generation
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.WriterException;

public class PaymentView extends BorderPane {
    
    private final ViewManager viewManager;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final ArrayList<Ticket> tickets;
    private final ArrayList<Food> foods;
    private final Optional<Customer> customer;
    
    private TabPane tabPane;
    
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
        
        // Create tabbed interface
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        // Tab 1: Make Payment
        Tab paymentTab = new Tab("Make Payment");
        paymentTab.setContent(createPaymentPane());
        
        // Tab 2: Payment History
        Tab historyTab = new Tab("Payment History");
        historyTab.setContent(createHistoryPane());
        
        tabPane.getTabs().addAll(paymentTab, historyTab);
        
        setCenter(tabPane);
    }
    
    private HBox createTopBar() {
        Label titleLabel = new Label("Payment & History");
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
    
    // ========== TAB 1: MAKE PAYMENT ==========
    
    private HBox createPaymentPane() {
        VBox receiptView = createReceiptView();
        VBox paymentForm = createPaymentForm();
        
        HBox paymentContent = new HBox(20, receiptView, paymentForm);
        paymentContent.setPadding(new Insets(20));
        
        return paymentContent;
    }
    
    private VBox createReceiptView() {
        Label receiptTitle = new Label("Current Cart Receipt");
        receiptTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        TextArea receiptArea = new TextArea();
        receiptArea.setEditable(false);
        receiptArea.setPrefHeight(400);
        receiptArea.setStyle("-fx-font-family: monospace;");
        
        String receiptText = generateReceiptText(tickets, foods);
        receiptArea.setText(receiptText);
        
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
                
                // Refresh history tab
                Tab historyTab = tabPane.getTabs().get(1);
                historyTab.setContent(createHistoryPane());
                
                // Switch to history tab to show the new payment
                tabPane.getSelectionModel().select(historyTab);
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
    
    // ========== TAB 2: PAYMENT HISTORY ==========
    
    private VBox createHistoryPane() {
        Label historyTitle = new Label("Payment History");
        historyTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        ArrayList<Payment> history = paymentRepository.getAllPayments();
        
        if (history.isEmpty()) {
            Label emptyLabel = new Label("No payment records found.");
            emptyLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #7f8c8d;");
            VBox emptyView = new VBox(30, historyTitle, emptyLabel);
            emptyView.setAlignment(Pos.CENTER);
            emptyView.setPadding(new Insets(50));
            return emptyView;
        }
        
        // Create list view for payment records
        ListView<Payment> paymentListView = new ListView<>();
        paymentListView.getItems().addAll(history);
        paymentListView.setPrefHeight(300);
        
        paymentListView.setCellFactory(lv -> new ListCell<Payment>() {
            @Override
            protected void updateItem(Payment payment, boolean empty) {
                super.updateItem(payment, empty);
                if (empty || payment == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String customerName = payment.getCustomer()
                        .map(Customer::getName).orElse("Guest");
                    
                    setText(String.format(
                        "ID: %d | Customer: %s | Tickets: %d | Food: %d | Total: RM%.2f",
                        payment.getPaymentID(),
                        customerName,
                        payment.getTicketAmt(),
                        payment.getFoodQty(),
                        payment.getTotalPrice()
                    ));
                }
            }
        });
        
        // Details panel
        VBox detailsPanel = new VBox(10);
        detailsPanel.setPadding(new Insets(15));
        detailsPanel.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-background-color: white;");
        detailsPanel.setMinHeight(350);
        
        Label detailsTitle = new Label("Select a payment to view details");
        detailsTitle.setStyle("-fx-font-size: 14; -fx-text-fill: #7f8c8d;");
        detailsPanel.getChildren().add(detailsTitle);
        
        // Handle selection
        paymentListView.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                detailsPanel.getChildren().clear();
                detailsPanel.getChildren().addAll(createPaymentDetails(newVal));
            }
        });
        
        VBox historyView = new VBox(15, 
            historyTitle, 
            new Label("Payment Records:"),
            paymentListView,
            new Label("Details:"),
            detailsPanel
        );
        historyView.setPadding(new Insets(20));
        
        return historyView;
    }
    
    private VBox createPaymentDetails(Payment payment) {
        Label detailsTitle = new Label("Payment Details");
        detailsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        // Basic info
        VBox infoBox = new VBox(5);
        infoBox.getChildren().addAll(
            new Label("Payment ID: " + payment.getPaymentID()),
            new Label("Customer: " + payment.getCustomer().map(Customer::getName).orElse("Guest")),
            new Label("Status: " + (payment.getPaymentMade() ? "✓ Completed" : "✗ Pending"))
        );
        
        // Tickets section
        VBox ticketsBox = new VBox(5);
        Label ticketsLabel = new Label("Tickets:");
        ticketsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        ticketsBox.getChildren().add(ticketsLabel);
        
        if (payment.getTicket().isEmpty()) {
            ticketsBox.getChildren().add(new Label("  Total Tickets: " + payment.getTicketAmt()));
        } else {
            for (Ticket t : payment.getTicket()) {
                ticketsBox.getChildren().add(new Label(String.format(
                    "  • %s (%s) - %d seats - RM%.2f",
                    t.getMovieName(), t.getSchedule(), t.getTicketAmt(), t.getTotalPrice()
                )));
            }
        }
        
        // Food section
        VBox foodBox = new VBox(5);
        Label foodLabel = new Label("Food & Beverage:");
        foodLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        foodBox.getChildren().add(foodLabel);
        
        if (payment.getFood().isEmpty()) {
            foodBox.getChildren().add(new Label("  Total Food Items: " + payment.getFoodQty()));
        } else {
            for (Food f : payment.getFood()) {
                foodBox.getChildren().add(new Label(String.format(
                    "  • %s x%d - RM%.2f",
                    f.getName(), f.getQty(), f.getPrice()
                )));
            }
        }
        
        // Total
        Label totalLabel = new Label(String.format("GRAND TOTAL: RM%.2f", payment.getTotalPrice()));
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        totalLabel.setStyle("-fx-text-fill: #2ecc71;");
        
        Separator sep = new Separator();
        
        // QR Code section (optional - uncomment if ZXing library is added)
        VBox qrBox = createQRCodeSection(payment);
        
        VBox details = new VBox(10,
            detailsTitle,
            infoBox,
            sep,
            ticketsBox,
            foodBox,
            new Separator(),
            totalLabel,
            qrBox
        );
        
        return details;
    }
    
    /**
     * Creates QR code for payment receipt.
     * Now FULLY FUNCTIONAL with ZXing library.
     */
    private VBox createQRCodeSection(Payment payment) {
        VBox qrBox = new VBox(10);
        qrBox.setAlignment(Pos.CENTER);
        
        Label qrLabel = new Label("Digital Receipt QR Code:");
        qrLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        try {
            // Generate QR code data
            String qrData = String.format(
                "YSCM CINEMA RECEIPT\n" +
                "==================\n" +
                "Payment ID: %d\n" +
                "Customer: %s\n" +
                "Tickets: %d\n" +
                "Food Items: %d\n" +
                "Total: RM%.2f\n" +
                "==================\n" +
                "Thank you for your purchase!",
                payment.getPaymentID(),
                payment.getCustomer().map(Customer::getName).orElse("Guest"),
                payment.getTicketAmt(),
                payment.getFoodQty(),
                payment.getTotalPrice()
            );
            
            // Generate QR code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(
                qrData, 
                BarcodeFormat.QR_CODE, 
                200,  // Width
                200   // Height
            );
            
            // Convert to JavaFX Image
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            
            Image qrImage = new Image(new ByteArrayInputStream(outputStream.toByteArray()));
            ImageView qrImageView = new ImageView(qrImage);
            qrImageView.setFitWidth(180);
            qrImageView.setFitHeight(180);
            qrImageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);");
            
            // Add instruction label
            Label scanLabel = new Label("Scan to view receipt details");
            scanLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #7f8c8d;");
            
            qrBox.getChildren().addAll(qrLabel, qrImageView, scanLabel);
            
        } catch (WriterException | java.io.IOException e) {
            // Fallback if QR generation fails
            Label errorLabel = new Label("❌ QR Code generation failed");
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 10;");
            
            Label errorDetails = new Label("Error: " + e.getMessage());
            errorDetails.setStyle("-fx-font-size: 9; -fx-text-fill: #95a5a6;");
            
            qrBox.getChildren().addAll(qrLabel, errorLabel, errorDetails);
        }
        
        return qrBox;
    }
    
    // ========== HELPER METHODS ==========
    
    private String generateReceiptText(ArrayList<Ticket> tickets, ArrayList<Food> foods) {
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
        
        return receipt.toString();
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
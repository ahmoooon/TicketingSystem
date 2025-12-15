package presentation.gui.views;

import application.services.CustomerService;
import application.services.StaffService;
import domain.Customer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import presentation.gui.ViewManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class StaffMenuView extends BorderPane {
    
    private final ViewManager viewManager;
    private final StaffService staffService;
    private final CustomerService customerService;
    private TableView<Customer> customerTable;
    
    // Track current report for PDF export
    private String currentReportContent = "";
    private String currentReportType = "";
    
    public StaffMenuView(ViewManager viewManager, StaffService staffService, 
                        CustomerService customerService) {
        this.viewManager = viewManager;
        this.staffService = staffService;
        this.customerService = customerService;
        initializeUI();
    }
    
    private void initializeUI() {
        setTop(createTopBar());
        
        VBox leftPanel = createLeftPanel();
        VBox rightPanel = createRightPanel();
        
        HBox mainContent = new HBox(20, leftPanel, rightPanel);
        mainContent.setPadding(new Insets(20));
        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        
        setCenter(mainContent);
        setStyle("-fx-background-color: #ecf0f1;");
    }
    
    private HBox createTopBar() {
        Label titleLabel = new Label("Staff Portal");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: white;");
        
        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
        logoutBtn.setOnAction(e -> viewManager.logout());
        
        HBox topBar = new HBox(20, titleLabel, logoutBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(20));
        topBar.setStyle("-fx-background-color: #34495e;");
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        
        return topBar;
    }
    
    private VBox createLeftPanel() {
        Label manageTitle = new Label("Customer Management");
        manageTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        customerTable = new TableView<>();
        customerTable.setPrefHeight(400);
        
        TableColumn<Customer, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);
        
        TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);
        
        TableColumn<Customer, String> passwordCol = new TableColumn<>("Password (Hashed)");
        passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
        passwordCol.setPrefWidth(250);
        
        customerTable.getColumns().addAll(idCol, nameCol, passwordCol);
        refreshCustomerTable();
        
        Button deleteBtn = new Button("Delete Selected Customer");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteBtn.setPrefWidth(200);
        
        deleteBtn.setOnAction(e -> {
            Customer selected = customerTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Confirm Deletion");
                confirm.setHeaderText("Delete Customer");
                confirm.setContentText("Are you sure you want to delete customer: " + selected.getName() + "?");
                
                Optional<ButtonType> result = confirm.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    if (staffService.deleteCustomerAccount(selected.getName())) {
                        showInfo("Success", "Customer account deleted successfully");
                        refreshCustomerTable();
                    } else {
                        showError("Error", "Failed to delete customer account");
                    }
                }
            } else {
                showError("No Selection", "Please select a customer to delete");
            }
        });
        
        VBox leftPanel = new VBox(15, manageTitle, customerTable, deleteBtn);
        leftPanel.setPadding(new Insets(10));
        
        return leftPanel;
    }
    
    private VBox createRightPanel() {
        Label reportTitle = new Label("Reports");
        reportTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        // Report buttons with icons
        Button customerListBtn = createReportButton("📋 Customer List Report");
        Button moviePurchaseBtn = createReportButton("🎬 Movie Purchase Report");
        Button foodPurchaseBtn = createReportButton("🍿 Food Purchase Report");
        Button salesSummaryBtn = createReportButton("💰 Sales Summary Report");
        
        // PDF Export button (disabled until report is generated)
        Button exportPdfBtn = new Button("📄 Export to PDF");
        exportPdfBtn.setPrefWidth(250);
        exportPdfBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
        exportPdfBtn.setDisable(true);
        
        TextArea reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setPrefHeight(400);
        reportArea.setStyle("-fx-font-family: monospace; -fx-font-size: 11;");
        
        // Report generation handlers
        customerListBtn.setOnAction(e -> {
            String report = staffService.getCustomerListReport(customerService.getCustomerList());
            reportArea.setText(report);
            currentReportContent = report;
            currentReportType = "CustomerList";
            exportPdfBtn.setDisable(false);
        });
        
        moviePurchaseBtn.setOnAction(e -> {
            String report = staffService.getMoviePurchaseReport();
            reportArea.setText(report);
            currentReportContent = report;
            currentReportType = "MoviePurchase";
            exportPdfBtn.setDisable(false);
        });
        
        foodPurchaseBtn.setOnAction(e -> {
            String report = staffService.getFoodPurchaseReport();
            reportArea.setText(report);
            currentReportContent = report;
            currentReportType = "FoodPurchase";
            exportPdfBtn.setDisable(false);
        });
        
        salesSummaryBtn.setOnAction(e -> {
            String report = staffService.getSalesSummaryReport();
            reportArea.setText(report);
            currentReportContent = report;
            currentReportType = "SalesSummary";
            exportPdfBtn.setDisable(false);
        });
        
        // PDF Export handler
        exportPdfBtn.setOnAction(e -> handlePdfExport());
        
        // Add helpful instruction label
        Label instructionLabel = new Label("💡 Generate a report first, then click 'Export to PDF'");
        instructionLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #7f8c8d; -fx-font-style: italic;");
        instructionLabel.setWrapText(true);
        
        VBox rightPanel = new VBox(10,
            reportTitle,
            customerListBtn,
            moviePurchaseBtn,
            foodPurchaseBtn,
            salesSummaryBtn,
            new Separator(),
            exportPdfBtn,
            instructionLabel,
            new Separator(),
            new Label("Report Output:"),
            reportArea
        );
        rightPanel.setPadding(new Insets(10));
        rightPanel.setPrefWidth(500);
        
        return rightPanel;
    }
    
    private Button createReportButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(250);
        button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
        return button;
    }
    
    /**
     * Handles PDF export with file chooser dialog.
     */
    private void handlePdfExport() {
        if (currentReportContent.isEmpty() || currentReportType.isEmpty()) {
            showError("No Report", "Please generate a report first before exporting to PDF");
            return;
        }
        
        // File chooser dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF Report");
        fileChooser.setInitialFileName(generateFileName());
        
        // Add PDF extension filter
        FileChooser.ExtensionFilter pdfFilter = 
            new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(pdfFilter);
        
        // Set initial directory to user's documents folder
        String userHome = System.getProperty("user.home");
        File documentsDir = new File(userHome, "Documents");
        if (documentsDir.exists()) {
            fileChooser.setInitialDirectory(documentsDir);
        }
        
        // Show save dialog
        File selectedFile = fileChooser.showSaveDialog(getScene().getWindow());
        
        if (selectedFile != null) {
            // Ensure .pdf extension
            if (!selectedFile.getName().toLowerCase().endsWith(".pdf")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".pdf");
            }
            
            try {
                // Call appropriate export method based on report type
                switch (currentReportType) {
                    case "CustomerList":
                        staffService.exportCustomerListToPdf(
                            customerService.getCustomerList(), 
                            selectedFile
                        );
                        break;
                    case "MoviePurchase":
                        staffService.exportMoviePurchaseToPdf(selectedFile);
                        break;
                    case "FoodPurchase":
                        staffService.exportFoodPurchaseToPdf(selectedFile);
                        break;
                    case "SalesSummary":
                        staffService.exportSalesSummaryToPdf(selectedFile);
                        break;
                }
                
                // Success notification
                showInfo("PDF Exported", 
                    "Report successfully exported to:\n" + selectedFile.getAbsolutePath());
                
            } catch (IOException ex) {
                showError("Export Failed", 
                    "Failed to export PDF: " + ex.getMessage());
            }
        }
    }
    
    /**
     * Generates a default filename based on report type and current date.
     */
    private String generateFileName() {
        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        return String.format("YSCM_Cinema_%s_%s.pdf", currentReportType, timestamp);
    }
    
    private void refreshCustomerTable() {
        ArrayList<Customer> customers = customerService.getCustomerList();
        customerTable.getItems().clear();
        customerTable.getItems().addAll(customers);
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
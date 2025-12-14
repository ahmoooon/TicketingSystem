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
import presentation.gui.ViewManager;

import java.util.ArrayList;
import java.util.Optional;

public class StaffMenuView extends BorderPane {
    
    private final ViewManager viewManager;
    private final StaffService staffService;
    private final CustomerService customerService;
    private TableView<Customer> customerTable;
    
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
        
        Button customerListBtn = createReportButton("Customer List Report");
        Button moviePurchaseBtn = createReportButton("Movie Purchase Report");
        Button foodPurchaseBtn = createReportButton("Food Purchase Report");
        Button salesSummaryBtn = createReportButton("Sales Summary Report");
        
        TextArea reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setPrefHeight(400);
        reportArea.setStyle("-fx-font-family: monospace; -fx-font-size: 11;");
        
        customerListBtn.setOnAction(e -> {
            String report = staffService.getCustomerListReport(customerService.getCustomerList());
            reportArea.setText(report);
        });
        
        moviePurchaseBtn.setOnAction(e -> {
            String report = staffService.getMoviePurchaseReport();
            reportArea.setText(report);
        });
        
        foodPurchaseBtn.setOnAction(e -> {
            String report = staffService.getFoodPurchaseReport();
            reportArea.setText(report);
        });
        
        salesSummaryBtn.setOnAction(e -> {
            String report = staffService.getSalesSummaryReport();
            reportArea.setText(report);
        });
        
        VBox rightPanel = new VBox(10,
            reportTitle,
            customerListBtn,
            moviePurchaseBtn,
            foodPurchaseBtn,
            salesSummaryBtn,
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
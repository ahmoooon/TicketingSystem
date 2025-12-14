package presentation.gui.views;

import application.services.FoodService;
import domain.Food;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import presentation.gui.ViewManager;

import java.util.ArrayList;
import java.util.List;

public class FoodOrderView extends BorderPane {
    
    private final ViewManager viewManager;
    private final FoodService foodService;
    private final ArrayList<Food> foodCart;
    private ListView<Food> cartListView;
    
    public FoodOrderView(ViewManager viewManager, FoodService foodService, 
                        ArrayList<Food> foodCart) {
        this.viewManager = viewManager;
        this.foodService = foodService;
        this.foodCart = foodCart;
        initializeUI();
    }
    
    private void initializeUI() {
        setTop(createTopBar());
        
        VBox orderForm = createOrderForm();
        VBox cartView = createCartView();
        
        SplitPane splitPane = new SplitPane(orderForm, cartView);
        splitPane.setDividerPositions(0.6);
        setCenter(splitPane);
    }
    
    private HBox createTopBar() {
        Label titleLabel = new Label("Order Food & Beverage");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: white;");
        
        Button backBtn = new Button("← Back to Main Menu");
        backBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
        backBtn.setOnAction(e -> {
            viewManager.showMainMenu(
                viewManager.getCurrentUser().orElse(new domain.Customer("User", ""))
            );
        });
        
        HBox topBar = new HBox(20, titleLabel, backBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(20));
        topBar.setStyle("-fx-background-color: #34495e;");
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        
        return topBar;
    }
    
    private VBox createOrderForm() {
        Label formTitle = new Label("Select Items");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.setPromptText("Select Category");
        categoryComboBox.setPrefWidth(300);
        categoryComboBox.getItems().addAll("Beverage", "Popcorn", "HotFood");
        
        ListView<Food> menuListView = new ListView<>();
        menuListView.setPrefHeight(200);
        menuListView.setCellFactory(lv -> new ListCell<Food>() {
            @Override
            protected void updateItem(Food food, boolean empty) {
                super.updateItem(food, empty);
                if (empty || food == null) {
                    setText(null);
                } else {
                    setText(String.format("%s - RM%.2f", 
                        food.getName(), food.getPrice()));
                }
            }
        });
        
        Spinner<Integer> quantitySpinner = new Spinner<>(1, 99, 1);
        quantitySpinner.setPrefWidth(150);
        quantitySpinner.setDisable(true);
        
        Button addToCartBtn = new Button("Add to Cart");
        addToCartBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        addToCartBtn.setDisable(true);
        
        categoryComboBox.setOnAction(e -> {
            String category = categoryComboBox.getValue();
            if (category != null) {
                List<Food> menuItems = foodService.getMenuByType(category);
                menuListView.getItems().clear();
                menuListView.getItems().addAll(menuItems);
            }
        });
        
        menuListView.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            boolean hasSelection = newVal != null;
            quantitySpinner.setDisable(!hasSelection);
            addToCartBtn.setDisable(!hasSelection);
        });
        
        addToCartBtn.setOnAction(e -> {
            Food selected = menuListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                int qty = quantitySpinner.getValue();
                
                // Create new food item for order
                Food orderItem = createFoodInstance(selected.getFoodType());
                orderItem.setName(selected.getName());
                orderItem.setPrice(selected.getPrice()); // Unit price
                orderItem.setQty(qty);
                orderItem.calPrice(); // Calculate total
                
                // Add or merge with existing order
                foodService.addOrMergeOrder(foodCart, orderItem, selected.getPrice());
                
                cartListView.getItems().setAll(foodCart);
                showInfo("Success", "Item added to cart!");
                
                quantitySpinner.getValueFactory().setValue(1);
            }
        });
        
        VBox form = new VBox(15,
            formTitle,
            new Label("Category:"), categoryComboBox,
            new Label("Menu Items:"), menuListView,
            new Label("Quantity:"), quantitySpinner,
            addToCartBtn
        );
        form.setPadding(new Insets(20));
        
        return form;
    }
    
    private Food createFoodInstance(String type) {
        switch (type) {
            case "Popcorn": return new domain.Popcorn();
            case "Beverage": return new domain.Beverage();
            case "HotFood": return new domain.HotFood();
            default: return new domain.Popcorn(); // Default fallback
        }
    }
    
    private VBox createCartView() {
        Label cartTitle = new Label("Your Food Cart");
        cartTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        cartListView = new ListView<>();
        cartListView.getItems().addAll(foodCart);
        cartListView.setCellFactory(lv -> new ListCell<Food>() {
            @Override
            protected void updateItem(Food food, boolean empty) {
                super.updateItem(food, empty);
                if (empty || food == null) {
                    setText(null);
                } else {
                    setText(String.format("%s\nQty: %d - RM%.2f",
                        food.getName(),
                        food.getQty(),
                        food.getPrice()
                    ));
                }
            }
        });
        
        Label totalLabel = new Label();
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        updateTotalLabel(totalLabel);
        
        Button removeBtn = new Button("Remove Selected");
        removeBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        removeBtn.setOnAction(e -> {
            int selectedIdx = cartListView.getSelectionModel().getSelectedIndex();
            if (selectedIdx >= 0) {
                foodService.removeOrderItem(foodCart, selectedIdx);
                cartListView.getItems().setAll(foodCart);
                updateTotalLabel(totalLabel);
            }
        });
        
        VBox cartView = new VBox(15, cartTitle, cartListView, totalLabel, removeBtn);
        cartView.setPadding(new Insets(20));
        
        return cartView;
    }
    
    private void updateTotalLabel(Label label) {
        double total = foodService.calculateTotal(foodCart);
        label.setText(String.format("Total: RM%.2f", total));
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
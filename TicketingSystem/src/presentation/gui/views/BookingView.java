package presentation.gui.views;

import application.dto.BookingRequest;
import application.dto.BookingResult;
import application.services.BookingService;
import domain.*;
import domain.valueobjects.SeatId;
import infrastructure.repositories.SeatUnavailableException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import presentation.gui.ViewManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingView extends BorderPane {
    
    private final ViewManager viewManager;
    private final BookingService bookingService;
    private final ArrayList<Ticket> ticketCart;
    private ListView<Ticket> cartListView;
    
    public BookingView(ViewManager viewManager, BookingService bookingService, 
                      ArrayList<Ticket> ticketCart) {
        this.viewManager = viewManager;
        this.bookingService = bookingService;
        this.ticketCart = ticketCart;
        initializeUI();
    }
    
    private void initializeUI() {
        setTop(createTopBar());
        
        VBox bookingForm = createBookingForm();
        VBox cartView = createCartView();
        
        SplitPane splitPane = new SplitPane(bookingForm, cartView);
        splitPane.setDividerPositions(0.6);
        setCenter(splitPane);
    }
    
    private HBox createTopBar() {
        Label titleLabel = new Label("Book Tickets");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: white;");
        
        Button backBtn = new Button("← Back to Main Menu");
        backBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
        backBtn.setOnAction(e -> {
            Customer c = viewManager.getCurrentUser().orElse(new Customer("User", ""));
            viewManager.showMainMenu(c);
        });
        
        HBox topBar = new HBox(20, titleLabel, backBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(20));
        topBar.setStyle("-fx-background-color: #34495e;");
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        
        return topBar;
    }
    
    private VBox createBookingForm() {
        Label formTitle = new Label("Select Movie & Showtime");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        ComboBox<Movie> movieComboBox = new ComboBox<>();
        movieComboBox.setPromptText("Select Movie");
        movieComboBox.setPrefWidth(300);
        List<Movie> movies = bookingService.getAvailableMovies();
        movieComboBox.getItems().addAll(movies);
        
        ComboBox<LocalDate> dateComboBox = new ComboBox<>();
        dateComboBox.setPromptText("Select Date");
        dateComboBox.setPrefWidth(300);
        dateComboBox.setDisable(true);
        
        ComboBox<String> timeComboBox = new ComboBox<>();
        timeComboBox.setPromptText("Select Time");
        timeComboBox.setPrefWidth(300);
        timeComboBox.setDisable(true);
        
        ComboBox<String> hallComboBox = new ComboBox<>();
        hallComboBox.setPromptText("Select Hall Type");
        hallComboBox.setPrefWidth(300);
        hallComboBox.getItems().addAll("Standard", "IMAX", "Lounge");
        hallComboBox.setDisable(true);
        
        TextField seatRowField = new TextField();
        seatRowField.setPromptText("Row (A-Z)");
        seatRowField.setPrefWidth(145);
        
        TextField seatColField = new TextField();
        seatColField.setPromptText("Col (1-15)");
        seatColField.setPrefWidth(145);
        
        HBox seatBox = new HBox(10, seatRowField, seatColField);
        
        Button addSeatBtn = new Button("Add Seat");
        addSeatBtn.setDisable(true);
        addSeatBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        
        ListView<SeatId> selectedSeatsList = new ListView<>();
        selectedSeatsList.setPrefHeight(100);
        
        Button bookBtn = new Button("Add to Cart");
        bookBtn.setDisable(true);
        bookBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        
        movieComboBox.setOnAction(e -> {
            Movie selected = movieComboBox.getValue();
            if (selected != null) {
                dateComboBox.getItems().clear();
                List<Showtime> dates = bookingService.getAvailableShowtimeDates(selected);
                dates.forEach(st -> dateComboBox.getItems().add(st.getDate()));
                dateComboBox.setDisable(false);
            }
        });
        
        dateComboBox.setOnAction(e -> {
            if (dateComboBox.getValue() != null) {
                timeComboBox.getItems().clear();
                timeComboBox.getItems().addAll(
                    "10:00 AM", "11:40 AM", "01:20 PM", "03:00 PM",
                    "04:40 PM", "06:20 PM", "08:00 PM"
                );
                timeComboBox.setDisable(false);
            }
        });
        
        timeComboBox.setOnAction(e -> {
            if (timeComboBox.getValue() != null) {
                hallComboBox.setDisable(false);
            }
        });
        
        hallComboBox.setOnAction(e -> {
            if (hallComboBox.getValue() != null) {
                addSeatBtn.setDisable(false);
            }
        });
        
        addSeatBtn.setOnAction(e -> {
            try {
                String rowText = seatRowField.getText().toUpperCase();
                if (rowText.isEmpty()) {
                    showError("Invalid Input", "Please enter a seat row");
                    return;
                }
                char row = rowText.charAt(0);
                int col = Integer.parseInt(seatColField.getText());
                SeatId seatId = new SeatId(row, col);
                
                if (!selectedSeatsList.getItems().contains(seatId)) {
                    selectedSeatsList.getItems().add(seatId);
                    seatRowField.clear();
                    seatColField.clear();
                    bookBtn.setDisable(false);
                } else {
                    showError("Duplicate Seat", "This seat is already selected");
                }
            } catch (Exception ex) {
                showError("Invalid Input", "Please enter valid seat row and column");
            }
        });
        
        bookBtn.setOnAction(e -> {
            try {
                Movie movie = movieComboBox.getValue();
                LocalDate date = dateComboBox.getValue();
                String time = timeComboBox.getValue();
                String hallType = hallComboBox.getValue();
                
                int hallId = hallType.equals("IMAX") ? 2 : hallType.equals("Lounge") ? 3 : 1;
                
                BookingRequest request = new BookingRequest(
                    movie.getId(), date, time, hallId,
                    new ArrayList<>(selectedSeatsList.getItems())
                );
                
                BookingResult result = bookingService.bookTickets(request);
                
                Ticket ticket = new Ticket(
                    result.getShowtime(),
                    result.getSeats().size(),
                    result.getShowtime().getCinemaHall(),
                    new ArrayList<>(result.getSeats())
                );
                
                ticketCart.add(ticket);
                cartListView.getItems().setAll(ticketCart);
                
                showInfo("Success", "Tickets added to cart!");
                
                selectedSeatsList.getItems().clear();
                movieComboBox.setValue(null);
                dateComboBox.setValue(null);
                timeComboBox.setValue(null);
                hallComboBox.setValue(null);
                dateComboBox.setDisable(true);
                timeComboBox.setDisable(true);
                hallComboBox.setDisable(true);
                addSeatBtn.setDisable(true);
                bookBtn.setDisable(true);
                
            } catch (SeatUnavailableException ex) {
                showError("Booking Failed", ex.getMessage());
            } catch (Exception ex) {
                showError("Error", ex.getMessage());
            }
        });
        
        VBox form = new VBox(15,
            formTitle,
            new Label("Movie:"), movieComboBox,
            new Label("Date:"), dateComboBox,
            new Label("Time:"), timeComboBox,
            new Label("Hall:"), hallComboBox,
            new Label("Select Seats:"), seatBox,
            addSeatBtn,
            new Label("Selected Seats:"), selectedSeatsList,
            bookBtn
        );
        form.setPadding(new Insets(20));
        
        return form;
    }
    
    private VBox createCartView() {
        Label cartTitle = new Label("Your Cart");
        cartTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        cartListView = new ListView<>();
        cartListView.getItems().addAll(ticketCart);
        cartListView.setCellFactory(lv -> new ListCell<Ticket>() {
            @Override
            protected void updateItem(Ticket ticket, boolean empty) {
                super.updateItem(ticket, empty);
                if (empty || ticket == null) {
                    setText(null);
                } else {
                    setText(String.format("%s\n%s at %s\n%d seats - RM%.2f",
                        ticket.getMovieName(),
                        ticket.getSchedule(),
                        ticket.time(),
                        ticket.getTicketAmt(),
                        ticket.getTotalPrice()
                    ));
                }
            }
        });
        
        Button removeBtn = new Button("Remove Selected");
        removeBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        removeBtn.setOnAction(e -> {
            Ticket selected = cartListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                ticketCart.remove(selected);
                cartListView.getItems().setAll(ticketCart);
            }
        });
        
        VBox cartView = new VBox(15, cartTitle, cartListView, removeBtn);
        cartView.setPadding(new Insets(20));
        
        return cartView;
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
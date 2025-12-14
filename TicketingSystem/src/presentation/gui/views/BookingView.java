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
import javafx.util.StringConverter;
import presentation.gui.ViewManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class BookingView extends BorderPane {
    
    private final ViewManager viewManager;
    private final BookingService bookingService;
    private final ArrayList<Ticket> ticketCart;
    private ListView<Ticket> cartListView;
    
    // Seat selection state
    private GridPane seatGrid;
    private Set<SeatId> selectedSeats;
    private Label selectedCountLabel;
    private Button bookBtn;
    
    // Current selection state
    private Movie selectedMovie;
    private LocalDate selectedDate;
    private String selectedTime;
    private CinemaHall selectedHall;
    
    public BookingView(ViewManager viewManager, BookingService bookingService, 
                      ArrayList<Ticket> ticketCart) {
        this.viewManager = viewManager;
        this.bookingService = bookingService;
        this.ticketCart = ticketCart;
        this.selectedSeats = new HashSet<>();
        initializeUI();
    }
    
    private void initializeUI() {
        setTop(createTopBar());
        
        VBox bookingForm = createBookingForm();
        VBox cartView = createCartView();
        
        SplitPane splitPane = new SplitPane(bookingForm, cartView);
        splitPane.setDividerPositions(0.65);
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
        
        // Movie Selection
        ComboBox<Movie> movieComboBox = new ComboBox<>();
        movieComboBox.setPromptText("Select Movie");
        movieComboBox.setPrefWidth(400);
        List<Movie> movies = bookingService.getAvailableMovies();
        movieComboBox.getItems().addAll(movies);
        
        // Date Selection
        ComboBox<LocalDate> dateComboBox = new ComboBox<>();
        dateComboBox.setPromptText("Select Date");
        dateComboBox.setPrefWidth(400);
        dateComboBox.setDisable(true);
        
        // Time Selection
        ComboBox<String> timeComboBox = new ComboBox<>();
        timeComboBox.setPromptText("Select Time");
        timeComboBox.setPrefWidth(400);
        timeComboBox.setDisable(true);
        
        // Hall Selection - NOW SHOWS SPECIFIC HALLS
        ComboBox<CinemaHall> hallComboBox = new ComboBox<>();
        hallComboBox.setPromptText("Select Hall");
        hallComboBox.setPrefWidth(400);
        hallComboBox.setDisable(true);
        
        // Custom display for halls
        hallComboBox.setConverter(new StringConverter<CinemaHall>() {
            @Override
            public String toString(CinemaHall hall) {
                if (hall == null) return "";
                int capacity = hall.getRowAmt() * hall.getColAmt();
                return String.format("Hall %d - %s (%d seats)", 
                    hall.getHallId(), 
                    hall.getHallType(),
                    capacity);
            }
            
            @Override
            public CinemaHall fromString(String string) {
                return null;
            }
        });
        
        // Load all available halls
        hallComboBox.getItems().addAll(bookingService.getAllHalls());
        
        // Seat Legend
        HBox legend = createSeatLegend();
        
        // Seat Selection Grid Container
        ScrollPane seatScrollPane = new ScrollPane();
        seatScrollPane.setPrefHeight(350);
        seatScrollPane.setFitToWidth(true);
        seatScrollPane.setStyle("-fx-background: white; -fx-border-color: #bdc3c7; -fx-border-radius: 5;");
        
        seatGrid = new GridPane();
        seatGrid.setAlignment(Pos.CENTER);
        seatGrid.setHgap(5);
        seatGrid.setVgap(5);
        seatGrid.setPadding(new Insets(20));
        
        Label seatPlaceholder = new Label("Please select movie, date, time, and hall to view seats");
        seatPlaceholder.setStyle("-fx-font-size: 14; -fx-text-fill: #7f8c8d;");
        seatGrid.add(seatPlaceholder, 0, 0);
        
        seatScrollPane.setContent(seatGrid);
        
        // Selected seats counter
        selectedCountLabel = new Label("Selected: 0 seats");
        selectedCountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        selectedCountLabel.setStyle("-fx-text-fill: #2c3e50;");
        
        // Book button
        bookBtn = new Button("Add to Cart");
        bookBtn.setDisable(true);
        bookBtn.setPrefWidth(200);
        bookBtn.setPrefHeight(40);
        bookBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16;");
        
        // Event Handlers
        movieComboBox.setOnAction(e -> {
            selectedMovie = movieComboBox.getValue();
            if (selectedMovie != null) {
                dateComboBox.getItems().clear();
                List<Showtime> dates = bookingService.getAvailableShowtimeDates(selectedMovie);
                dates.forEach(st -> dateComboBox.getItems().add(st.getDate()));
                dateComboBox.setDisable(false);
                resetSeatSelection();
            }
        });
        
        dateComboBox.setOnAction(e -> {
            selectedDate = dateComboBox.getValue();
            if (selectedDate != null) {
                timeComboBox.getItems().clear();
                timeComboBox.getItems().addAll(
                    "10:00 AM", "11:40 AM", "01:20 PM", "03:00 PM",
                    "04:40 PM", "06:20 PM", "08:00 PM"
                );
                timeComboBox.setDisable(false);
                resetSeatSelection();
            }
        });
        
        timeComboBox.setOnAction(e -> {
            selectedTime = timeComboBox.getValue();
            if (selectedTime != null) {
                hallComboBox.setDisable(false);
                resetSeatSelection();
            }
        });
        
        hallComboBox.setOnAction(e -> {
            selectedHall = hallComboBox.getValue();
            if (selectedHall != null) {
                loadSeatMap();
            }
        });
        
        bookBtn.setOnAction(e -> handleBooking());
        
        VBox form = new VBox(15,
            formTitle,
            new Label("Movie:"), movieComboBox,
            new Label("Date:"), dateComboBox,
            new Label("Time:"), timeComboBox,
            new Label("Hall:"), hallComboBox,
            new Separator(),
            legend,
            new Label("Select Your Seats:"),
            seatScrollPane,
            selectedCountLabel,
            bookBtn
        );
        form.setPadding(new Insets(20));
        
        return form;
    }
    
    private HBox createSeatLegend() {
        // Available seat
        Button availableBtn = new Button("  ");
        availableBtn.setStyle("-fx-background-color: #2ecc71; -fx-min-width: 30; -fx-min-height: 30;");
        availableBtn.setDisable(true);
        Label availableLabel = new Label("Available");
        
        // Selected seat
        Button selectedBtn = new Button("  ");
        selectedBtn.setStyle("-fx-background-color: #3498db; -fx-min-width: 30; -fx-min-height: 30;");
        selectedBtn.setDisable(true);
        Label selectedLabel = new Label("Selected");
        
        // Booked seat
        Button bookedBtn = new Button("  ");
        bookedBtn.setStyle("-fx-background-color: #e74c3c; -fx-min-width: 30; -fx-min-height: 30;");
        bookedBtn.setDisable(true);
        Label bookedLabel = new Label("Booked");
        
        HBox legend = new HBox(15,
            availableBtn, availableLabel,
            selectedBtn, selectedLabel,
            bookedBtn, bookedLabel
        );
        legend.setAlignment(Pos.CENTER);
        legend.setPadding(new Insets(10));
        legend.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 5;");
        
        return legend;
    }
    
    private void loadSeatMap() {
        if (selectedMovie == null || selectedDate == null || 
            selectedTime == null || selectedHall == null) {
            return;
        }
        
        seatGrid.getChildren().clear();
        selectedSeats.clear();
        updateSelectedCount();
        
        int rows = selectedHall.getRowAmt();
        int cols = selectedHall.getColAmt();
        
        // Add screen indicator
        Label screenLabel = new Label("🎬 SCREEN 🎬");
        screenLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        screenLabel.setStyle("-fx-text-fill: #34495e;");
        GridPane.setColumnSpan(screenLabel, cols + 1);
        GridPane.setHalignment(screenLabel, javafx.geometry.HPos.CENTER);
        seatGrid.add(screenLabel, 0, 0);
        
        // Get booked seats for this showtime
        List<Seat> allSeats = bookingService.getSeatsByShowtime(
            selectedMovie, selectedDate, selectedTime, selectedHall.getHallId()
        );
        
        Set<SeatId> bookedSeats = new HashSet<>();
        for (Seat seat : allSeats) {
            if (seat.getSeatStatus().equals("Booked")) {
                bookedSeats.add(seat.getId());
            }
        }
        
        // Create seat buttons
        for (int row = 0; row < rows; row++) {
            char rowLetter = (char) ('A' + row);
            
            // Row label
            Label rowLabel = new Label(String.valueOf(rowLetter));
            rowLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            rowLabel.setStyle("-fx-text-fill: #7f8c8d;");
            rowLabel.setPrefWidth(30);
            rowLabel.setAlignment(Pos.CENTER);
            seatGrid.add(rowLabel, 0, row + 2);
            
            for (int col = 0; col < cols; col++) {
                int colNum = col + 1;
                SeatId seatId = new SeatId(rowLetter, colNum);
                
                Button seatBtn = createSeatButton(seatId, bookedSeats.contains(seatId));
                seatGrid.add(seatBtn, col + 1, row + 2);
            }
        }
        
        // Add column numbers at bottom
        for (int col = 0; col < cols; col++) {
            Label colLabel = new Label(String.valueOf(col + 1));
            colLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            colLabel.setStyle("-fx-text-fill: #7f8c8d;");
            colLabel.setPrefWidth(40);
            colLabel.setAlignment(Pos.CENTER);
            seatGrid.add(colLabel, col + 1, rows + 2);
        }
    }
    
    private Button createSeatButton(SeatId seatId, boolean isBooked) {
        Button seatBtn = new Button(seatId.toDisplayString());
        seatBtn.setMinSize(40, 40);
        seatBtn.setMaxSize(40, 40);
        seatBtn.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        
        if (isBooked) {
            seatBtn.setStyle(
                "-fx-background-color: #e74c3c; " +
                "-fx-text-fill: white; " +
                "-fx-border-color: #c0392b; " +
                "-fx-border-width: 2; " +
                "-fx-background-radius: 5;"
            );
            seatBtn.setDisable(true);
        } else {
            seatBtn.setStyle(
                "-fx-background-color: #2ecc71; " +
                "-fx-text-fill: white; " +
                "-fx-border-color: #27ae60; " +
                "-fx-border-width: 2; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand;"
            );
            
            seatBtn.setOnAction(e -> toggleSeatSelection(seatId, seatBtn));
        }
        
        return seatBtn;
    }
    
    private void toggleSeatSelection(SeatId seatId, Button seatBtn) {
        if (selectedSeats.contains(seatId)) {
            // Deselect
            selectedSeats.remove(seatId);
            seatBtn.setStyle(
                "-fx-background-color: #2ecc71; " +
                "-fx-text-fill: white; " +
                "-fx-border-color: #27ae60; " +
                "-fx-border-width: 2; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand;"
            );
        } else {
            // Select
            selectedSeats.add(seatId);
            seatBtn.setStyle(
                "-fx-background-color: #3498db; " +
                "-fx-text-fill: white; " +
                "-fx-border-color: #2980b9; " +
                "-fx-border-width: 2; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand;"
            );
        }
        
        updateSelectedCount();
    }
    
    private void updateSelectedCount() {
        selectedCountLabel.setText("Selected: " + selectedSeats.size() + " seat(s)");
        bookBtn.setDisable(selectedSeats.isEmpty());
    }
    
    private void resetSeatSelection() {
        selectedSeats.clear();
        seatGrid.getChildren().clear();
        
        Label placeholder = new Label("Please complete all selections to view seats");
        placeholder.setStyle("-fx-font-size: 14; -fx-text-fill: #7f8c8d;");
        seatGrid.add(placeholder, 0, 0);
        
        updateSelectedCount();
    }
    
    private void handleBooking() {
        try {
            BookingRequest request = new BookingRequest(
                selectedMovie.getId(), 
                selectedDate, 
                selectedTime, 
                selectedHall.getHallId(),
                new ArrayList<>(selectedSeats)
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
            
            // SAVE CART to persistence
            viewManager.saveCurrentCart();
            
            String seatList = selectedSeats.stream()
                .map(SeatId::toDisplayString)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
            
            showInfo("Success", 
                String.format("Tickets added to cart!\n\nHall: %d - %s\nSeats: %s", 
                    selectedHall.getHallId(),
                    selectedHall.getHallType(),
                    seatList));
            
            // Refresh seat map
            selectedSeats.clear();
            loadSeatMap();
            
        } catch (SeatUnavailableException ex) {
            showError("Booking Failed", ex.getMessage());
            loadSeatMap(); // Refresh to show newly booked seats
        } catch (Exception ex) {
            showError("Error", ex.getMessage());
        }
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
                    setGraphic(null);
                } else {
                    VBox content = new VBox(5);
                    content.setPadding(new Insets(5));
                    
                    Label movieLabel = new Label("🎬 " + ticket.getMovieName());
                    movieLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                    
                    Label detailsLabel = new Label(
                        String.format("📅 %s | 🕐 %s | 🎭 Hall %d (%s)",
                        ticket.getSchedule(),
                        ticket.time(),
                        ticket.getHallId(),
                        ticket.getHallType())
                    );
                    
                    StringBuilder seatsText = new StringBuilder("💺 Seats: ");
                    for (int i = 0; i < ticket.getSeat().size(); i++) {
                        Seat seat = ticket.getSeat().get(i);
                        seatsText.append(seat.getId().toDisplayString());
                        if (i < ticket.getSeat().size() - 1) {
                            seatsText.append(", ");
                        }
                    }
                    Label seatsLabel = new Label(seatsText.toString());
                    
                    Label priceLabel = new Label(String.format("💰 RM%.2f", ticket.getTotalPrice()));
                    priceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
                    priceLabel.setStyle("-fx-text-fill: #27ae60;");
                    
                    content.getChildren().addAll(movieLabel, detailsLabel, seatsLabel, priceLabel);
                    setGraphic(content);
                }
            }
        });
        
        Button removeBtn = new Button("Remove Selected");
        removeBtn.setPrefWidth(150);
        removeBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        removeBtn.setOnAction(e -> {
            Ticket selected = cartListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Confirm Removal");
                confirm.setHeaderText("Remove Ticket");
                confirm.setContentText("Remove this ticket from cart? This will release the seats.");
                
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        // Cancel ticket (releases seats)
                        viewManager.cancelTicket(selected);
                        cartListView.getItems().setAll(ticketCart);
                        
                        showInfo("Ticket Removed", "Ticket removed and seats released");
                    }
                });
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
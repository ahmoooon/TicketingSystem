package presentation.gui;

import application.services.*;
import domain.repositories.*;
import infrastructure.repositories.*;
import javafx.application.Application;
import javafx.stage.Stage;

public class CinemaApplication extends Application {
    
    private MovieRepository movieRepository;
    private ShowtimeRepository showtimeRepository;
    private SeatRepository seatRepository;
    private PaymentRepository paymentRepository;
    
    private BookingService bookingService;
    private CustomerService customerService;
    private PasswordService passwordService;
    private OtpService otpService;
    private AuthService authService;
    private ReportService reportService;
    private StaffService staffService;
    private FoodService foodService;
    private PaymentService paymentService;
    
    private ViewManager viewManager;
    
    @Override
    public void start(Stage primaryStage) {
        movieRepository = new FileMovieRepository();
        showtimeRepository = new FileShowtimeRepository();
        seatRepository = new FileSeatRepository();
        paymentRepository = new PaymentRepository();
        
        customerService = new CustomerService();
        passwordService = new PasswordService();
        otpService = new OtpService();
        authService = new AuthService(passwordService, otpService);
        bookingService = new BookingService(movieRepository, showtimeRepository, seatRepository);
        reportService = new ReportService(paymentRepository);
        staffService = new StaffService(reportService, customerService);
        foodService = new FoodService();
        paymentService = new PaymentService();
        
        viewManager = new ViewManager(
            primaryStage,
            authService,
            customerService,
            bookingService,
            foodService,
            paymentService,
            staffService,
            paymentRepository,
            otpService
        );
        
        viewManager.showLoginView();
        
        primaryStage.setTitle("YSCM Cinema Ticketing System");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
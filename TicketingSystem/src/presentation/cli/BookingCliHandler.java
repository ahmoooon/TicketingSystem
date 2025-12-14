package presentation.cli;

import application.dto.BookingRequest;
import application.dto.BookingResult;
import application.services.BookingService;
import application.utilities.Utility;
import domain.CinemaHall;
import domain.Movie;
import domain.Showtime;
import domain.Ticket;
import domain.valueobjects.SeatId;
import infrastructure.repositories.SeatUnavailableException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import static presentation.cli.MainApplication.logo;

public class BookingCliHandler {
    
    private final BookingService bookingService;
    private final Scanner scanner;

    public BookingCliHandler(BookingService bookingService, Scanner scanner) {
        this.bookingService = bookingService;
        this.scanner = scanner;
    }
    
    /**
     * Replaces the old TicketingSystem.BookingMenu() (Switch/Loop)
     * @param currentTickets
     */
    public ArrayList<Ticket> handleBookingMenu(ArrayList<Ticket> currentTickets) {
        boolean back = false;
        int menuChoice = 0;

        do {
            logo(2); // Display Booking Logo
            System.out.println("\n\t|| 0 | Back               ||\n\t|| 1 | Booking            ||\n\t|| 2 | View Cart          ||\n\t|| 3 | View Seat Details  ||\n\t|| 4 | Delete Ticket      ||\n\t|| 5 | Exit to Main Menu  ||\n");
            System.out.print("Choose one of the option from menu above ~ ");
            
            menuChoice = Utility.checkError(scanner, 0, 5); 

            switch (menuChoice) {
                case 1:
                    Ticket newTicket = handleBookingFlow();
                    if (newTicket != null) {
                        currentTickets.add(newTicket);
                    }
                    break;
                case 2:
                    displayCart(currentTickets);
                    break;
                case 3:
                    displaySeatDetails(currentTickets);
                    break;
                case 4:
                    // This logic still uses the old, monolithic deletion logic from MainApplication
                    deleteTicket(currentTickets);
                    break;
                case 5:
                    back = true;
                    break;
            }
        } while (!back);
        
        return currentTickets;
    }
    
    /**
     * Replaces the old TicketingSystem.bookingModule() (Transaction Orchestration)
     */
    private Ticket handleBookingFlow() {
        try {
            // 1. I/O: Movie Selection (Uses MovieRepository via Service)
            Movie movie = promptForMovieSelection();
            
            // 2. I/O: Date Selection (Uses ShowtimeRepository via Service)
            LocalDate date = promptForDateSelection(movie);
            
            // 3. I/O: Time Selection 
            String timeString = promptForTimeSelection(movie, date);
            
            // 4. I/O: Hall & Seat Amount Selection
            int hallId = promptForHallSelection();
            int seatAmt = promptForSeatAmount();
            
            // 5. I/O: Seat ID Selection (Uses SeatId Value Object)
            List<SeatId> seatIds = promptForSeatSelection(hallId, seatAmt);
            
            // --- Application Layer Call (The Core Logic) ---
            // NOTE: Assuming Movie has an ID field for this request DTO
            BookingRequest request = new BookingRequest(
                movie.getId(), // Placeholder Movie ID. You need to implement Movie.getId()
                date, 
                timeString, 
                hallId, 
                seatIds
            );
            
            BookingResult result = bookingService.bookTickets(request);
            
            // 6. Output: Success
            System.out.println("\n Booking successful! Details:");
            
            // Re-creating the final Ticket Entity from the result
            return new Ticket(
                result.getShowtime(), 
                result.getSeats().size(), 
                result.getShowtime().getCinemaHall(), 
                new ArrayList<>(result.getSeats())
            );
            
        } catch (IllegalArgumentException | SeatUnavailableException e) {
            // Clean Error Handling (Reporting service exception messages to user)
            System.err.println("\n Booking Failed: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("\n An unexpected error occurred: " + e.getMessage());
            return null;
        }
    }
    
    // --- Helper Methods (I/O Logic) ---
    
    private Movie promptForMovieSelection() {
        List<Movie> movies = bookingService.getAvailableMovies();
        int count = 1;
        System.out.println("\n---< Available Movies >---");
        for (Movie m : movies) {
            System.out.println("(" + count++ + ") " + m.toString() + "\n");
        }
        printSaleTicket(1); 
        int movieInput = Utility.checkError(scanner, 1, movies.size());
        
        // IMPORTANT: Assuming Movie objects are immutable, we return the entity.
        return bookingService.getMovieById(movieInput);
    }
    
    private LocalDate promptForDateSelection(Movie movie) {
        // This should use bookingService.getAvailableShowtimeDates(movie)
        List<Showtime> dates = bookingService.getAvailableShowtimeDates(movie);
        int count = 1;
        System.out.println("\n---< Date Selection >---");
        for (Showtime s : dates) {
            System.out.println("(" + count++ + ") " + s.getDate());
        }
        printSaleTicket(2);
        int dateInput = Utility.checkError(scanner, 1, dates.size());
        
        return dates.get(dateInput - 1).getDate();
    }
    
    private String promptForTimeSelection(Movie movie, LocalDate date) {
        // NOTE: This simulation must eventually use ShowtimeRepository helpers
        System.out.println("\n---< Time Selection >---");
        // We will stick to hardcoded values for simplicity as the generation logic is complex
        System.out.println("(1) 10:00 AM\n(2) 11:40 AM\n(3) 01:20 PM\n(4) 03:00 PM\n(5) 04:40 PM\n(6) 06:20 PM\n(7) 08:00 PM");
        printSaleTicket(3);
        int timeInput = Utility.checkError(scanner, 1, 7);
        
        String[] times = {"10:00 AM", "11:40 AM", "01:20 PM", "03:00 PM", "04:40 PM", "06:20 PM", "08:00 PM"};
        return times[timeInput - 1];
    }
    
    private int promptForHallSelection() {
        // This should list halls using a HallRepository if it existed.
        System.out.println("\n---< Hall Selection >---");
        System.out.println("1: " + CinemaHall.HALL_TYPE_STANDARD);
        System.out.println("2: " + CinemaHall.HALL_TYPE_IMAX);
        System.out.println("3: " + CinemaHall.HALL_TYPE_LOUNGE);
        printSaleTicket(4);
        return Utility.checkError(scanner, 1, 3);
    }
    
    private int promptForSeatAmount() {
        printSaleTicket(5); 
        return Utility.checkError(scanner, 1, 100);
    }
    
    private List<SeatId> promptForSeatSelection(int hallId, int seatAmt) {
        List<SeatId> selectedIds = new ArrayList<>();
        System.out.println("\n---< Seat Selection (Hall ID: " + hallId + ") >---");
        
        // Visual Logic: Determine which seat map to print based on the selected Hall ID
        // 1=Standard (Map 6), 2=IMAX (Map 7), 3=Lounge (Map 8)
        int mapType;
        switch (hallId) {
            case 2:
                mapType = 7; // IMAX
                break;
            case 3:
                mapType = 8; // Lounge
                break;
            default:
                mapType = 6; // Standard
                break;
        }

        for (int i = 0; i < seatAmt; i++) {
            System.out.println("Selecting seat " + (i + 1) + " of " + seatAmt);
            printSaleTicket(mapType); 
            
            System.out.print("Please select seat row (e.g: A ) > ");
            String rowInput = scanner.next();
            char row = rowInput.toUpperCase().charAt(0);
            
            System.out.print("Please select seat column (e.g: 3 ) > ");
            int col = scanner.nextInt();
            
            try {
                selectedIds.add(new SeatId(row, col));
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid seat input: " + e.getMessage() + ". Please re-enter this seat.");
                i--; 
            }
        }
        scanner.nextLine();
        return selectedIds;
    }
    
    private void displayCart(ArrayList<Ticket> tickets) {
        System.out.println("\n---< Your cart >---");
        System.out.println("==========================================================================");
        System.out.println("Movie                Qty    TicketID    Showtime    Date           Hall ID");
        System.out.println("==========================================================================");
        
        if (tickets.isEmpty()) {
            System.out.println("                               (Cart is Empty)                            ");
        } else {
            for (Ticket t : tickets) {
                System.out.printf("%-21s%-5d%3d%19s%14s%6d\n", 
                    t.getMovieName(), 
                    t.getTicketAmt(), 
                    t.getTicketID(), 
                    t.time(), 
                    t.getSchedule(), 
                    t.getHallId()
                );
            }
        }
    }
    
    private void displaySeatDetails(ArrayList<Ticket> tickets) {
        System.out.println("\n---< Your seat information >--- ");
        
        if (tickets.isEmpty()) {
            System.out.println("<!> No tickets booked yet. <!>");
            return;
        }

        System.out.println("=====================");
        for (Ticket t : tickets) {
            System.out.println("Ticket ID  : " + t.getTicketID());
            System.out.println("=====================");
            System.out.println("Hall type  : " + t.getHallType());
            System.out.println("Hall ID    : " + t.getHallId());
            // This relies on Ticket.displaySeatArray() which prints directly to console
            t.displaySeatArray(); 
            System.out.println("=====================");
            System.out.println();
        }
    }
    
    private void deleteTicket(ArrayList<Ticket> tickets) {
        if (tickets.isEmpty()) {
            System.out.println("<!> No record found! <!>");
            return;
        } 
        
        // Reuse displaySeatDetails to show what can be deleted
        displaySeatDetails(tickets);
        
        System.out.print("\nSelect the ticket ID to delete (0 to Back): ");
        
        int selectID = Utility.checkError(scanner, 0, 100);
        
        if (selectID == 0) {
            return;
        }
        
        Ticket ticketToRemove = null;
        for (Ticket t : tickets) {
            if (t.getTicketID() == selectID) {
                ticketToRemove = t;
                break;
            }
        }

        if (ticketToRemove != null) {
            System.out.print("\nConfirm to delete ticket with Ticket ID (Y: Yes/N: No): (" + ticketToRemove.getTicketID() + ")? ");
            char confirm = scanner.next().toUpperCase().charAt(0);
            
            if (confirm == 'Y') {
                tickets.remove(ticketToRemove);
                System.out.println("\nSuccessfully removed ticket with Ticket ID: (" + ticketToRemove.getTicketID() + ").");
            } else {
                System.out.println("\nDeletion cancelled.");
            }
        } else {
            System.out.println("\n <!> Ticket ID not found! Please try again. <!>");
        }
        scanner.nextLine(); 
    }
    private static void printSaleTicket(int choice) {
        switch (choice) {
            case 1:
                System.out.print("\nPlease select the movie (1 to 5):");
                break;
            case 2:
                System.out.print("\nPlease select the Date (1 to 3):");
                break;
            case 3:
                System.out.print("\nPlease select the Time(1 to 7):");
                break;
            case 4:
                System.out.print("\nPlease select the type of hall(1 to 3):");
                break;
            case 5:
                System.out.print("\nPlease enter the amount of seats > ");
                break;
            case 6:
                System.out.println(" ");
                System.out.println("A 1 2 3 4 5 6 7 8 9 10");
                System.out.println("B 1 2 3 4 5 6 7 8 9 10");
                System.out.println("C 1 2 3 4 5 6 7 8 9 10");
                System.out.println("D 1 2 3 4 5 6 7 8 9 10");
                System.out.println("E 1 2 3 4 5 6 7 8 9 10");
                System.out.println("***Please select the Seat ID\n");
                break;
            case 7:
                System.out.println(" ");
                System.out.println("A 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15");
                System.out.println("B 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15");
                System.out.println("C 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15");
                System.out.println("D 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15");
                System.out.println("E 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15");
                System.out.println("F 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15");
                System.out.println("G 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15");
                System.out.println("H 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15");
                System.out.println("***Please select the Seat ID\n");
                break;
            case 8:
                System.out.println(" ");
                System.out.println("A 1 2 3 4 5");
                System.out.println("B 1 2 3 4 5");
                System.out.println("C 1 2 3 4 5");
                System.out.println("D 1 2 3 4 5");
                System.out.println("E 1 2 3 4 5");
                System.out.println("***Please select the Seat ID\n");
            default:
                break;
        }
    }
}
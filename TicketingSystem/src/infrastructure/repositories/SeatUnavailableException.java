package infrastructure.repositories;

/**
 * Custom exception used by SeatRepository when a requested seat is already booked.
 * This exception is caught by the Application/Presentation layers.
 */
public class SeatUnavailableException extends RuntimeException {
    public SeatUnavailableException(String message) {
        super(message);
    }
    
    // You can optionally add a constructor for the underlying cause:
    public SeatUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
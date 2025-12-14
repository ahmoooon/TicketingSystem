// src/application/dto/PaymentResult.java
package application.dto;

// Note: If you fully refactor the Payment entity later, you might return the 
// persisted Payment entity itself here, but a simple result object is cleaner.

public class PaymentResult {
    private final boolean success;
    private final String message;
    
    public PaymentResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Getters only (DTOs should be immutable)
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}
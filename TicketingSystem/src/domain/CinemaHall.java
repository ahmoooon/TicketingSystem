package domain;

public class CinemaHall {
    
    // --- CONSTANTS (No Magic Strings) ---
    public static final String HALL_TYPE_STANDARD = "Standard";
    public static final String HALL_TYPE_IMAX = "IMAX";
    public static final String HALL_TYPE_LOUNGE = "Lounge";
    
    private final int hallNum; // Hall ID (made final for immutability - recommended)
    private final String hallType; // (made final)
    private final int rowAmt; // (made final)
    private final int colAmt; // (made final)
    
    
    public CinemaHall() {
        this(0, null, 0, 0); // Defaulting for compatibility
    }
    
    public CinemaHall(int hallNum, String hallType, int rowAmt, int colAmt) {
        this.hallNum = hallNum;
        this.hallType = hallType;
        this.rowAmt = rowAmt;
        this.colAmt = colAmt;
    }
    
    public double getBasePrice() {
        if (hallType == null) return 0.0;
        
        switch (hallType) {
            case HALL_TYPE_IMAX:
                return 30.00;
            case HALL_TYPE_LOUNGE:
                return 80.00;
            case HALL_TYPE_STANDARD:
            default:
                return 15.00;
        }
    }
    
    // GETTERS
    // -----------------------------------------------------------------------------------------------
    public int getHallId() { 
        return hallNum;
    }
    
    public String getHallType() {
        return hallType;
    }
    
    public int getRowAmt() {
        return rowAmt;
    }
    
    // NEW: Method expected by the SeatRepository
    public int getMaxSeatCol() { 
        return colAmt;
    }

    // Alias for compatibility
    public int getColAmt() {
        return colAmt;
    }
    // -----------------------------------------------------------------------------------------------
    
    // SETTERS REMOVED: Since fields are now 'final' (best practice for Entities/VOs)
    // If setters were mandatory for your assignment, they would need to remain 
    // and the 'final' keyword would be removed.
    
    @Override
    public String toString() {
        return "\nHall ID        : " + hallNum +
               "\nHall Type      : " + hallType +
               "\nHall Column    : " + colAmt +
               "\nHall Row       : " + rowAmt;
    }
}
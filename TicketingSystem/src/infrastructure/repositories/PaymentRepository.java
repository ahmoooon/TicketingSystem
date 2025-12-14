// src/infrastructure/repositories/PaymentRepository.java
package infrastructure.repositories;

import application.utilities.LoggerSetup;
import domain.Customer;
import domain.Food;
import domain.Payment;
import domain.Ticket;
import domain.Movie; 
import domain.Showtime;
import domain.CinemaHall;
import domain.Seat;
import domain.valueobjects.SeatId;
import domain.Popcorn;
import domain.Beverage;
import domain.HotFood;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PaymentRepository {
    private static final String PAYMENT_FILE = "payment_history.json";
    private static final Logger logger = LoggerSetup.getLogger();
    
    private ArrayList<Payment> paymentHistory;
    
    public PaymentRepository() {
        this.paymentHistory = loadPayments();
        syncPaymentIds();
        logger.log(Level.INFO, "PaymentRepository initialized with {0} existing payments.", 
                   this.paymentHistory.size());
    }
    
    private void syncPaymentIds() {
        int maxId = 0;
        for (Payment p : paymentHistory) {
            if (p.getPaymentID() > maxId) maxId = p.getPaymentID();
        }
        Payment.setLastID(maxId);
    }
    
    // --- LOAD LOGIC ---
    private ArrayList<Payment> loadPayments() {
        List<String> jsonLines = DataFileHandler.loadFromJsonFile(PAYMENT_FILE);
        ArrayList<Payment> payments = new ArrayList<>();
        for (String line : jsonLines) {
            if (line.trim().startsWith("{")) {
                Payment p = parsePaymentFromJson(line);
                if (p != null) payments.add(p);
            }
        }
        return payments;
    }
    
    private Payment parsePaymentFromJson(String json) {
        try {
            int paymentId = extractInt(json, "paymentId");
            double totalAmount = extractDouble(json, "totalAmount");
            String custName = extractString(json, "customerName");
            Customer historyCustomer = new Customer(custName, "");
            
            // --- 1. RECONSTRUCT TICKETS WITH REAL DATA ---
            ArrayList<Ticket> tickets = new ArrayList<>();
            String ticketArray = extractArrayString(json, "tickets");
            List<String> ticketObjs = splitObjects(ticketArray);
            
            for (String tJson : ticketObjs) {
                // Extract Saved Data
                String movieName = extractString(tJson, "movieName");
                String dateStr = extractString(tJson, "date");
                String timeStr = extractString(tJson, "time");
                String hallType = extractString(tJson, "hall");
                String seatStr = extractString(tJson, "seats"); // e.g., "A1,A2"
                double price = extractDouble(tJson, "price");
                
                // Reconstruct Movie
                Movie m = new Movie(0, movieName, 0, "", "");
                
                // Reconstruct Hall (Minimal ID, but correct Type)
                CinemaHall hall = new CinemaHall(0, hallType, 0, 0);
                
                // Reconstruct Showtime (Parsing the date string manually is hard, 
                // so we pass the string directly if Showtime allows, or use dummy date parts)
                // Assuming dateStr is "2023-10-25"
                String[] dateParts = dateStr.split("-");
                int year = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]);
                int day = Integer.parseInt(dateParts[2]);
                
                Showtime s = new Showtime(m, year, month, day, timeStr, hall);
                
                // Reconstruct Seats
                ArrayList<Seat> seatList = new ArrayList<>();
                if (!seatStr.isEmpty()) {
                    String[] ids = seatStr.split(",");
                    for (String id : ids) {
                        // id is like "A1"
                        char row = id.charAt(0);
                        int col = Integer.parseInt(id.substring(1));
                        seatList.add(new Seat(new SeatId(row, col), "Single", "Sold", hall));
                    }
                }
                
                // Create Real Ticket
                Ticket t = new Ticket(s, seatList.size(), hall, seatList);
                tickets.add(t);
            }
            
            // --- 2. RECONSTRUCT FOOD ---
            ArrayList<Food> foods = new ArrayList<>();
            String foodArray = extractArrayString(json, "food");
            List<String> foodObjs = splitObjects(foodArray);
            
            for (String fJson : foodObjs) {
                String name = extractString(fJson, "name");
                int qty = extractInt(fJson, "qty");
                double price = extractDouble(fJson, "price");
                
                // Use Popcorn as generic holder
                Food f = new Popcorn();
                f.setName(name);
                f.setQty(qty);
                f.setPrice(price); 
                foods.add(f);
            }

            Payment payment = new Payment(Optional.of(historyCustomer), tickets, foods, totalAmount, true);
            payment.setPaymentID(paymentId);
            return payment;
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to parse payment: {0}", e.getMessage());
            return null;
        }
    }

    // --- SAVE LOGIC ---
    public void savePayment(Payment payment) {
        paymentHistory.add(payment);
        List<String> lines = new ArrayList<>();
        for (Payment p : paymentHistory) {
            lines.add(paymentToJsonString(p));
        }
        DataFileHandler.saveToJsonFile(lines, PAYMENT_FILE);
    }
    
    private String paymentToJsonString(Payment p) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"paymentId\":").append(p.getPaymentID()).append(",");
        sb.append("\"customerName\":\"").append(p.getCustomer().map(Customer::getName).orElse("Guest")).append("\",");
        sb.append("\"totalAmount\":").append(p.getTotalPrice()).append(",");
        
        // SAVE TICKETS (With Date, Time, Seats)
        sb.append("\"tickets\":[");
        ArrayList<Ticket> tickets = p.getTicket();
        for (int i = 0; i < tickets.size(); i++) {
            Ticket t = tickets.get(i);
            Showtime s = t.getShowtime();
            
            sb.append("{");
            sb.append("\"movieName\":\"").append(t.getMovieName()).append("\",");
            sb.append("\"date\":\"").append(s.getDate().toString()).append("\","); // Saves "2023-10-25"
            sb.append("\"time\":\"").append(s.time()).append("\",");             // Saves "10:00 AM"
            sb.append("\"hall\":\"").append(t.getHallType()).append("\",");       // Saves "IMAX"
            
            // Save Seats as "A1,A2"
            sb.append("\"seats\":\"");
            ArrayList<Seat> seats = t.getSeat();
            for(int j=0; j<seats.size(); j++) {
                sb.append(seats.get(j).getSeatRow()).append(seats.get(j).getSeatCol());
                if(j < seats.size()-1) sb.append(",");
            }
            sb.append("\",");
            
            sb.append("\"price\":").append(t.getTotalPrice());
            sb.append("}");
            if (i < tickets.size() - 1) sb.append(",");
        }
        sb.append("],");
        
        // SAVE FOOD (Same as before)
        sb.append("\"food\":[");
        ArrayList<Food> foods = p.getFood();
        for (int i = 0; i < foods.size(); i++) {
            Food f = foods.get(i);
            sb.append("{");
            sb.append("\"name\":\"").append(f.getName()).append("\",");
            sb.append("\"qty\":").append(f.getQty()).append(",");
            sb.append("\"price\":").append(f.getPrice());
            sb.append("}");
            if (i < foods.size() - 1) sb.append(",");
        }
        sb.append("]");
        
        sb.append("}");
        return sb.toString();
    }
    
    public ArrayList<Payment> getAllPayments() {
        return new ArrayList<>(paymentHistory);
    }

    // --- PARSING HELPERS (Standard) ---
    private String extractString(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start == -1) return "";
        start += search.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
    
    private int extractInt(String json, String key) {
        String val = extractValue(json, key);
        return val.isEmpty() ? 0 : Integer.parseInt(val);
    }
    
    private double extractDouble(String json, String key) {
        String val = extractValue(json, key);
        return val.isEmpty() ? 0.0 : Double.parseDouble(val);
    }
    
    private String extractValue(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) return "";
        start += search.length();
        int endComma = json.indexOf(",", start);
        int endBrace = json.indexOf("}", start);
        if (endComma == -1 && endBrace == -1) return ""; 
        int end = (endComma == -1) ? endBrace : (endBrace == -1 ? endComma : Math.min(endComma, endBrace));
        return json.substring(start, end).trim();
    }
    
    private String extractArrayString(String json, String key) {
        String search = "\"" + key + "\":[";
        int start = json.indexOf(search);
        if (start == -1) return "";
        start += search.length();
        int end = json.indexOf("]", start); 
        return json.substring(start, end);
    }
    
    private List<String> splitObjects(String arrayContent) {
        List<String> objs = new ArrayList<>();
        int braceCount = 0;
        StringBuilder current = new StringBuilder();
        
        for (char c : arrayContent.toCharArray()) {
            if (c == '{') braceCount++;
            if (c == '}') braceCount--;
            if (braceCount > 0 || c == '}') current.append(c);
            if (braceCount == 0 && c == '}' && current.length() > 0) {
                objs.add(current.toString());
                current.setLength(0);
            }
        }
        return objs;
    }
}
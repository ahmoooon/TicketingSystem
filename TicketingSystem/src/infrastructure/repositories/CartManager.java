package infrastructure.repositories;

import application.utilities.LoggerSetup;
import domain.Customer;
import domain.Food;
import domain.Ticket;
import domain.Seat;
import domain.CinemaHall;
import domain.Showtime;
import domain.Movie;
import domain.valueobjects.SeatId;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Manages temporary shopping carts per customer.
 * Carts are persisted separately from confirmed bookings.
 */
public class CartManager {
    
    private static final String CART_FILE = "customer_carts.json";
    private static final Logger logger = LoggerSetup.getLogger();
    
    private ConcurrentHashMap<Integer, CartData> customerCarts;
    
    public CartManager() {
        this.customerCarts = loadCarts();
        logger.info("CartManager initialized with " + customerCarts.size() + " saved carts");
    }
    
    /**
     * Inner class to hold cart data for serialization
     */
    public static class CartData {
        public int customerId;
        public ArrayList<Ticket> tickets;
        public ArrayList<Food> food;
        public long timestamp; // For expiration
        
        public CartData(int customerId, ArrayList<Ticket> tickets, ArrayList<Food> food) {
            this.customerId = customerId;
            this.tickets = tickets;
            this.food = food;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    /**
     * Saves current cart for a customer
     */
    public void saveCart(Customer customer, ArrayList<Ticket> tickets, ArrayList<Food> food) {
        if (customer == null) return;
        
        CartData cartData = new CartData(customer.getId(), tickets, food);
        customerCarts.put(customer.getId(), cartData);
        persistCarts();
        
        logger.info("Cart saved for customer: " + customer.getName());
    }
    
    /**
     * Loads cart for a customer on login
     */
    public CartData loadCart(Customer customer) {
        if (customer == null) return null;
        
        CartData cart = customerCarts.get(customer.getId());
        
        if (cart != null) {
            // Check if cart is expired (24 hours)
            long hoursSinceCreation = (System.currentTimeMillis() - cart.timestamp) / (1000 * 60 * 60);
            if (hoursSinceCreation > 24) {
                logger.info("Cart expired for customer: " + customer.getName());
                clearCart(customer);
                return null;
            }
            
            logger.info("Cart loaded for customer: " + customer.getName());
        }
        
        return cart;
    }
    
    /**
     * Clears cart for a customer (after payment or cancellation)
     */
    public void clearCart(Customer customer) {
        if (customer == null) return;
        
        customerCarts.remove(customer.getId());
        persistCarts();
        
        logger.info("Cart cleared for customer: " + customer.getName());
    }
    
    /**
     * Gets all seat IDs from a customer's cart (for checking conflicts)
     */
    public List<SeatId> getCartSeatIds(Customer customer) {
        CartData cart = loadCart(customer);
        if (cart == null) return new ArrayList<>();
        
        List<SeatId> seatIds = new ArrayList<>();
        for (Ticket ticket : cart.tickets) {
            for (Seat seat : ticket.getSeat()) {
                seatIds.add(seat.getId());
            }
        }
        return seatIds;
    }
    
    // === PERSISTENCE LOGIC ===
    
    private void persistCarts() {
        List<String> jsonLines = customerCarts.values().stream()
            .map(this::cartToJsonString)
            .collect(Collectors.toList());
        
        DataFileHandler.saveToJsonFile(jsonLines, CART_FILE);
    }
    
    private ConcurrentHashMap<Integer, CartData> loadCarts() {
        List<String> jsonLines = DataFileHandler.loadFromJsonFile(CART_FILE);
        ConcurrentHashMap<Integer, CartData> carts = new ConcurrentHashMap<>();
        
        for (String line : jsonLines) {
            try {
                CartData cart = parseCartFromJson(line);
                if (cart != null) {
                    carts.put(cart.customerId, cart);
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to parse cart: " + line, e);
            }
        }
        
        return carts;
    }
    
    // === JSON SERIALIZATION ===
    
    private String cartToJsonString(CartData cart) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"customerId\":").append(cart.customerId).append(",");
        sb.append("\"timestamp\":").append(cart.timestamp).append(",");
        
        // Serialize tickets
        sb.append("\"tickets\":[");
        for (int i = 0; i < cart.tickets.size(); i++) {
            Ticket t = cart.tickets.get(i);
            sb.append(ticketToJson(t));
            if (i < cart.tickets.size() - 1) sb.append(",");
        }
        sb.append("],");
        
        // Serialize food
        sb.append("\"food\":[");
        for (int i = 0; i < cart.food.size(); i++) {
            Food f = cart.food.get(i);
            sb.append(foodToJson(f));
            if (i < cart.food.size() - 1) sb.append(",");
        }
        sb.append("]");
        
        sb.append("}");
        return sb.toString();
    }
    
    private String ticketToJson(Ticket ticket) {
        Showtime s = ticket.getShowtime();
        StringBuilder sb = new StringBuilder();
        
        sb.append("{");
        sb.append("\"movieName\":\"").append(ticket.getMovieName()).append("\",");
        sb.append("\"date\":\"").append(s.getDate().toString()).append("\",");
        sb.append("\"time\":\"").append(s.time()).append("\",");
        sb.append("\"hallId\":").append(ticket.getHallId()).append(",");
        sb.append("\"hallType\":\"").append(ticket.getHallType()).append("\",");
        
        // Seats
        sb.append("\"seats\":\"");
        for (int i = 0; i < ticket.getSeat().size(); i++) {
            sb.append(ticket.getSeat().get(i).getId().toDisplayString());
            if (i < ticket.getSeat().size() - 1) sb.append(",");
        }
        sb.append("\",");
        
        sb.append("\"price\":").append(ticket.getTotalPrice());
        sb.append("}");
        
        return sb.toString();
    }
    
    private String foodToJson(Food food) {
        return String.format("{\"name\":\"%s\",\"qty\":%d,\"price\":%.2f}",
            food.getName(), food.getQty(), food.getPrice());
    }
    
    private CartData parseCartFromJson(String json) {
        try {
            int customerId = extractInt(json, "customerId");
            long timestamp = extractLong(json, "timestamp");
            
            // Parse tickets
            ArrayList<Ticket> tickets = new ArrayList<>();
            String ticketsArray = extractArrayString(json, "tickets");
            List<String> ticketObjs = splitObjects(ticketsArray);
            
            for (String tJson : ticketObjs) {
                Ticket t = parseTicketFromJson(tJson);
                if (t != null) tickets.add(t);
            }
            
            // Parse food
            ArrayList<Food> foods = new ArrayList<>();
            String foodArray = extractArrayString(json, "food");
            List<String> foodObjs = splitObjects(foodArray);
            
            for (String fJson : foodObjs) {
                Food f = parseFoodFromJson(fJson);
                if (f != null) foods.add(f);
            }
            
            CartData cart = new CartData(customerId, tickets, foods);
            cart.timestamp = timestamp;
            return cart;
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to parse cart", e);
            return null;
        }
    }
    
    private Ticket parseTicketFromJson(String json) {
        try {
            String movieName = extractString(json, "movieName");
            String dateStr = extractString(json, "date");
            String timeStr = extractString(json, "time");
            int hallId = extractInt(json, "hallId");
            String hallType = extractString(json, "hallType");
            String seatsStr = extractString(json, "seats");
            
            // Reconstruct entities
            Movie movie = new Movie(0, movieName, 0, "", "");
            
            String[] dateParts = dateStr.split("-");
            int year = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]);
            int day = Integer.parseInt(dateParts[2]);
            
            CinemaHall hall = new CinemaHall(hallId, hallType, 0, 0);
            Showtime showtime = new Showtime(movie, year, month, day, timeStr, hall);
            
            // Parse seats
            ArrayList<Seat> seats = new ArrayList<>();
            if (!seatsStr.isEmpty()) {
                String[] seatIds = seatsStr.split(",");
                for (String id : seatIds) {
                    char row = id.charAt(0);
                    int col = Integer.parseInt(id.substring(1));
                    seats.add(new Seat(new SeatId(row, col), "Single", "Reserved", hall));
                }
            }
            
            return new Ticket(showtime, seats.size(), hall, seats);
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to parse ticket", e);
            return null;
        }
    }
    
    private Food parseFoodFromJson(String json) {
        try {
            String name = extractString(json, "name");
            int qty = extractInt(json, "qty");
            double price = extractDouble(json, "price");
            
            domain.Popcorn food = new domain.Popcorn();
            food.setName(name);
            food.setQty(qty);
            food.setPrice(price);
            
            return food;
        } catch (Exception e) {
            return null;
        }
    }
    
    // === PARSING HELPERS ===
    
    private int extractInt(String json, String key) {
        String val = extractValue(json, key);
        return val.isEmpty() ? 0 : Integer.parseInt(val);
    }
    
    private long extractLong(String json, String key) {
        String val = extractValue(json, key);
        return val.isEmpty() ? 0L : Long.parseLong(val);
    }
    
    private double extractDouble(String json, String key) {
        String val = extractValue(json, key);
        return val.isEmpty() ? 0.0 : Double.parseDouble(val);
    }
    
    private String extractString(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start == -1) return "";
        start += search.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
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
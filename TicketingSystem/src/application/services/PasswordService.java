package application.services;

/**
 *
 * @author zhili
 */
import application.utilities.LoggerSetup;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Logger;

public class PasswordService {
    private static final Logger logger = LoggerSetup.getLogger();

    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            // Use Base64 to convert byte array to a readable string
            String hashedPassword = Base64.getEncoder().encodeToString(hash);
            logger.fine("Password hashed successfully.");
            return hashedPassword;
        } catch (NoSuchAlgorithmException e) {
            logger.severe("SHA-256 algorithm not available. Cannot hash password.");
            return password; // Fallback: return plaintext
        }
    }

    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        String enteredHash = hashPassword(plainPassword);
        return enteredHash.equals(hashedPassword);
    }
}

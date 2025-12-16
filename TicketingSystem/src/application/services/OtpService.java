package application.services;

/**
 *
 * @author zhili
 */
import application.utilities.LoggerSetup;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OtpService {
    private static final Logger logger = LoggerSetup.getLogger();
    private static final int OTP_LENGTH = 6;

    /**
     * Generates a random numeric OTP.
     * @return A 6-digit OTP string.
     */
    public String generateOtp() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }
        String otp = sb.toString();
        logger.log(Level.FINE, "OTP generated: {0}", otp);
        return otp;
    }

    /**
     * Validates if the entered OTP matches the generated OTP.
     * @param enteredOtp The OTP provided by the user.
     * @param generatedOtp The expected OTP.
     * @return true if the OTPs match, false otherwise.
     */
    public boolean verifyOtp(String enteredOtp, String generatedOtp) {
        return enteredOtp != null && enteredOtp.equals(generatedOtp);
    }
}

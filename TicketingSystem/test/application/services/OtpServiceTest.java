package application.services;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author zhili
 */

import org.junit.Test;

public class OtpServiceTest {
    
    private final OtpService otpService = new OtpService();
    private static final int EXPECTED_OTP_LENGTH = 6;

    // --- 1. Test generateOtp method ---

    @Test
    public void testGenerateOtp_LengthIsCorrect() {
        System.out.println("generateOtp_LengthIsCorrect");
        
        String result = otpService.generateOtp();
        
        // Assert 1: Check if the generated OTP has the required length (6 digits)
        assertNotNull("Generated OTP should not be null", result);
        assertEquals("Generated OTP should be exactly 6 digits long", 
                     EXPECTED_OTP_LENGTH, 
                     result.length());
    }
    
    @Test
    public void testGenerateOtp_IsNumeric() {
        System.out.println("generateOtp_IsNumeric");
        
        String result = otpService.generateOtp();
        
        // Assert 2: Check if the OTP contains only digits (0-9)
        assertTrue("Generated OTP should contain only digits", 
                   result.matches("\\d+")); // Regex to check for only digits
    }

    @Test
    public void testGenerateOtp_IsRandom() {
        System.out.println("generateOtp_IsRandom");
        
        String otp1 = otpService.generateOtp();
        String otp2 = otpService.generateOtp();
        String otp3 = otpService.generateOtp();

        // Assert 3: Although true randomness is hard to guarantee in unit tests,
        // we assert that multiple calls do NOT return the same value.
        // This confirms the Random object is being used correctly to produce distinct outputs.
        boolean isAllDifferent = !otp1.equals(otp2) || !otp1.equals(otp3) || !otp2.equals(otp3);
        
        assertTrue("Subsequent OTPs should generally be different", isAllDifferent);
    }
    
    // --- 2. Test verifyOtp method ---

    @Test
    public void testVerifyOtp_Success() {
        System.out.println("verifyOtp_Success");
        
        String generatedOtp = "123456";
        String enteredOtp = "123456";
        
        boolean result = otpService.verifyOtp(enteredOtp, generatedOtp);
        
        assertTrue("Verification should succeed when OTPs match", result);
    }

    @Test
    public void testVerifyOtp_MismatchFailure() {
        System.out.println("verifyOtp_MismatchFailure");
        
        String generatedOtp = "123456";
        String enteredOtp = "654321";
        
        boolean result = otpService.verifyOtp(enteredOtp, generatedOtp);
        
        assertFalse("Verification should fail when OTPs do not match", result);
    }

    @Test
    public void testVerifyOtp_NullEnteredOtpFailure() {
        System.out.println("verifyOtp_NullEnteredOtpFailure");
        
        String generatedOtp = "123456";
        String enteredOtp = null;
        
        boolean result = otpService.verifyOtp(enteredOtp, generatedOtp);
        
        assertFalse("Verification should fail if the entered OTP is null", result);
    }
    
    @Test
    public void testVerifyOtp_NullGeneratedOtpFailure() {
        System.out.println("verifyOtp_NullGeneratedOtpFailure");
        
        String generatedOtp = null;
        String enteredOtp = "123456";
        
        // The implementation uses enteredOtp.equals(generatedOtp) which throws a NullPointerException
        // if enteredOtp is not null, and generatedOtp is null.
        // However, your provided code uses enteredOtp.equals(generatedOtp), which is safe 
        // if enteredOtp is not null and throws NullPointerException if enteredOtp is null.
        // Let's test based on your actual code: enteredOtp != null && enteredOtp.equals(generatedOtp)
        
        boolean result = otpService.verifyOtp(enteredOtp, generatedOtp);
        
        assertFalse("Verification should fail if the generated OTP is null", result);
    }
    
    @Test
    public void testVerifyOtp_DifferentLengthFailure() {
        System.out.println("verifyOtp_DifferentLengthFailure");
        
        String generatedOtp = "123456";
        String enteredOtp = "12345";
        
        boolean result = otpService.verifyOtp(enteredOtp, generatedOtp);
        
        assertFalse("Verification should fail if lengths are different", result);
    }
}

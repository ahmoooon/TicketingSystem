/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package application.services;

import domain.Customer;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.Mockito.*;
import application.services.PasswordService;
import application.services.OtpService;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author MOON
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthServiceTest {

    // Mock dependencies
    @Mock
    private PasswordService passwordService;

    @Mock
    private OtpService otpService;

    // Inject Mocks into the AuthService instance
    private AuthService authService;

    private final String TEST_OTP = "123456";
    private final String STAFF_ID = "admin";
    private final String TEST_STAFF_PASSWORD_HASH = "staff";

    @Before
    public void setUp() {
        when(passwordService.hashPassword("staff"))
                .thenReturn(TEST_STAFF_PASSWORD_HASH);

        authService = new AuthService(passwordService, otpService);
    }

    @Test
    public void testAuthenticateStaff_Success() {
        String password = "staff";

        when(passwordService.verifyPassword(password, TEST_STAFF_PASSWORD_HASH))
                .thenReturn(true);

        Optional<String> result
                = authService.authenticateStaff(STAFF_ID, password);

        assertTrue(result.isPresent());
        assertEquals(STAFF_ID, result.get());

        verify(passwordService)
                .verifyPassword(password, TEST_STAFF_PASSWORD_HASH);
    }

    @Test
    public void testAuthenticateStaff_WrongId_Failure() {
        System.out.println("authenticateStaff_WrongId_Failure");
        String wrongId = "wrongadmin";
        String password = "staff";

        Optional<String> result = authService.authenticateStaff(wrongId, password);

        assertFalse(result.isPresent());
        // Verification should not be called since the ID check fails first
        verify(passwordService, never()).verifyPassword(anyString(), anyString());
    }

    @Test
    public void testAuthenticateStaff_WrongPassword_Failure() {
        String password = "wrongpassword";

        when(passwordService.verifyPassword(password, TEST_STAFF_PASSWORD_HASH))
                .thenReturn(false);

        Optional<String> result
                = authService.authenticateStaff(STAFF_ID, password);

        assertFalse(result.isPresent());

        verify(passwordService)
                .verifyPassword(password, TEST_STAFF_PASSWORD_HASH);
    }

    // --- 2. Test authenticateCustomer ---
    @Test
    public void testAuthenticateCustomer_Success() {
        System.out.println("authenticateCustomer_Success");
        String name = "TestCustomer";
        String password = "customerpass";
        String hashedPassword = "hashed_customerpass";
        Customer customer = new Customer(name, hashedPassword);
        ArrayList<Customer> customerList = new ArrayList<>(Arrays.asList(customer));

        // Mock verification
        when(passwordService.verifyPassword(password, hashedPassword)).thenReturn(true);

        Optional<Customer> result = authService.authenticateCustomer(customerList, name, password);

        assertTrue(result.isPresent());
        assertEquals(customer, result.get());
        verify(passwordService).verifyPassword(password, hashedPassword);
    }

    @Test
    public void testAuthenticateCustomer_NotFound_Failure() {
        System.out.println("authenticateCustomer_NotFound_Failure");
        String name = "NonExistent";
        String password = "anypass";
        ArrayList<Customer> customerList = new ArrayList<>();

        Optional<Customer> result = authService.authenticateCustomer(customerList, name, password);

        assertFalse(result.isPresent());
        // Verification should not be called if customer list is empty or match fails
        verify(passwordService, never()).verifyPassword(anyString(), anyString());
    }

    @Test
    public void testAuthenticateCustomer_WrongPassword_Failure() {
        System.out.println("authenticateCustomer_WrongPassword_Failure");
        String name = "TestCustomer";
        String password = "wrongpass";
        String hashedPassword = "hashed_customerpass";
        Customer customer = new Customer(name, hashedPassword);
        ArrayList<Customer> customerList = new ArrayList<>(Arrays.asList(customer));

        // Mock verification
        when(passwordService.verifyPassword(password, hashedPassword)).thenReturn(false);

        Optional<Customer> result = authService.authenticateCustomer(customerList, name, password);

        assertFalse(result.isPresent());
        verify(passwordService).verifyPassword(password, hashedPassword);
    }

    @Test
    public void testAuthenticateCustomer_StaffIdAttempt_Failure() {
        System.out.println("authenticateCustomer_StaffIdAttempt_Failure");
        ArrayList<Customer> customerList = new ArrayList<>();

        Optional<Customer> result = authService.authenticateCustomer(customerList, STAFF_ID, "anypass");

        assertFalse(result.isPresent());
        verify(passwordService, never()).verifyPassword(anyString(), anyString());
    }

    // --- 3. Test startRegistration ---
    @Test
    public void testStartRegistration_Success() {
        System.out.println("startRegistration_Success");
        String newName = "NewUser";
        String password = "ValidPassword1!";
        ArrayList<Customer> userList = new ArrayList<>();

        when(otpService.generateOtp()).thenReturn(TEST_OTP);

        String result = authService.startRegistration(userList, newName, password);

        assertEquals(TEST_OTP, result);
        verify(otpService).generateOtp();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStartRegistration_NameIsStaffId_Failure() {
        System.out.println("startRegistration_NameIsStaffId_Failure");
        ArrayList<Customer> userList = new ArrayList<>();
        // Should throw IllegalArgumentException
        authService.startRegistration(userList, STAFF_ID, "anypass");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStartRegistration_NameAlreadyUsed_Failure() {
        System.out.println("startRegistration_NameAlreadyUsed_Failure");
        String name = "ExistingUser";
        Customer existing = new Customer(name, "hashed");
        ArrayList<Customer> userList = new ArrayList<>(Arrays.asList(existing));

        // Should throw IllegalArgumentException
        authService.startRegistration(userList, name, "anypass");
    }

    // --- 4. Test isPasswordValid (Static) ---
    @Test
    public void testIsPasswordValid_Success() {
        System.out.println("isPasswordValid_Success");
        // 8-16 chars, 1 alphabet, 1 number, 1 symbol
        assertTrue(AuthService.isPasswordValid("Pass@123"));
        assertTrue(AuthService.isPasswordValid("Zxcvbnm1!"));
        assertTrue(AuthService.isPasswordValid("a1b2c3d4e5f6g7h/")); // Max length
    }

    @Test
    public void testIsPasswordValid_TooShort_Failure() {
        System.out.println("isPasswordValid_TooShort_Failure");
        assertFalse(AuthService.isPasswordValid("A1!b7")); // 5 chars
    }

    @Test
    public void testIsPasswordValid_TooLong_Failure() {
        System.out.println("isPasswordValid_TooLong_Failure");
        assertFalse(AuthService.isPasswordValid("ThisIsAVeryLongPasswordWith100Characters!")); // > 16 chars
    }

    @Test
    public void testIsPasswordValid_MissingAlphabet_Failure() {
        System.out.println("isPasswordValid_MissingAlphabet_Failure");
        assertFalse(AuthService.isPasswordValid("12345678!"));
    }

    @Test
    public void testIsPasswordValid_MissingNumber_Failure() {
        System.out.println("isPasswordValid_MissingNumber_Failure");
        assertFalse(AuthService.isPasswordValid("Password!!"));
    }

    @Test
    public void testIsPasswordValid_MissingSymbol_Failure() {
        System.out.println("isPasswordValid_MissingSymbol_Failure");
        assertFalse(AuthService.isPasswordValid("Password123"));
    }

    // --- 5. Test finalizeCustomerRegistration ---
    @Test
    public void testFinalizeCustomerRegistration_Success() {
        System.out.println("finalizeCustomerRegistration_Success");
        String name = "FinalUser";
        String plaintextPassword = "finalpass";
        String expectedHash = "final_hashed_pass";

        // Mock the password hashing
        when(passwordService.hashPassword(plaintextPassword)).thenReturn(expectedHash);

        Customer result = authService.finalizeCustomerRegistration(name, plaintextPassword);

        assertEquals(name, result.getName());
        assertEquals(expectedHash, result.getPassword());

        verify(passwordService).hashPassword(plaintextPassword);
    }
}
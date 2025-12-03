/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package ticketingsystem;

import java.util.Scanner;
import java.time.LocalDateTime;
import java.util.InputMismatchException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

public class TicketingSystem {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean back = false;
        int index = 0;

        //IMPORTANT ARRAYLIST HERE
        ArrayList<Customer> custList = new ArrayList<>();
        ArrayList<Ticket> ticketArr = new ArrayList<>();
        ArrayList<Food> foodArr = new ArrayList<>();
        ArrayList<Payment> paymentArr = new ArrayList<>();

        //User is first prompted to login menu
        custList = (loginMenu(custList, paymentArr));

        int mainMenu = 0;

        //Main menu
        logo(1);

        do {
            logo(1);
            System.out.println("\n\t|| 1 | Book Ticket        ||\n\t|| 2 | Order F&B          ||\n\t|| 3 | Payment            ||\n\t|| 4 | Exit Program       ||\n\t|| 5 | Logout             ||\n");
            System.out.print("Choose one of the option from menu above ~ ");
            mainMenu = checkError(sc, 1, 5);
            switch (mainMenu) {
                case 1://Booking Menu
                    ticketArr = (BookingMenu(ticketArr));
                    break;
                case 2://F&B Menu
                    ArrayList<Food> newFood = OrderFood(foodArr);
                    foodArr = newFood;
                    break;
                case 3: //Payment
                    Payment paid = PaymentModule(ticketArr, foodArr, custList, paymentArr);
                    if (paid != null) {             //If payment is made, the payment will be cleared and stored within the payment array list
                        if (paid.getPaymentMade()) {
                            paymentArr.add(paid);
                            ticketArr.clear();
                            foodArr.clear();
                        } else {
                            System.out.println("\n <$> Payment hasn't been done! <$> ");
                            break;
                        }
                    }
                    break;
                case 4://Exit the program
                    back = true;
                    break;
                case 5://Logout
                    custList = loginMenu(custList, paymentArr);
                    logo(1);
                    break;
                default:
                    System.out.println("\n <!> Please enter within the range! <!>");
                    break;
            }
        } while (!back);

    }

    public static ArrayList<Customer> loginMenu(ArrayList<Customer> oldCust, ArrayList<Payment> payment) {
        ArrayList<Customer> custList = oldCust;
        boolean back = false;
        int loginChoice = 0;
        Scanner sc = new Scanner(System.in);
        int staffOrCust = 0;

        do {
            System.out.println("       =============================");
            System.out.println("      // Welcome to YSCM Cinema! //");
            System.out.println("     =============================");
            System.out.println("\n\t|| 1 | Login        ||\n\t|| 2 | Register     ||\n\t|| 3 | Exit program ||\n");
            System.out.print("Choose one of the option from menu above ~ ");
            loginChoice = checkError(sc, 1, 3);
            switch (loginChoice) {
                case 1://Login
                    System.out.println("\n\tLogin as > \n\t|| 1 | Customer     ||\n\t|| 2 | Staff        ||\n\t|| 3 | Back         ||\n");
                    System.out.print("Choose one of the option from menu above ~ ");
                    staffOrCust = checkError(sc, 1, 3);
                    if (staffOrCust == 1) {
                        if (custLogin(custList)) {; //[To call login function]
                            if (custList.isEmpty()) {
                                break; //If empty will prompt customer to register
                            } else {
                                System.out.println("Proceeding to main menu...");
                                back = true;
                                break;
                            }
                        } else {
                            break;
                        }

                    } else if (staffOrCust == 2) {
                        staff(custList, payment); //[To call staff fucnction where u can call delete cust function and report, i did not do report cuz need ur ticket stuff]
                    } else {
                        break;
                    }
                    break;
                case 2://Register
                    custList.add(custRegistration(custList)); //[To call registration function]
                    break;
                case 3://Exit program
                    System.exit(0);
                    break;
                case 4://To be used for exiting the switch menu
                    back = true;
                    break;
                default:
                    break;
            }
        } while (!back);

        return custList;
    }

    public static void staff(ArrayList<Customer> custList, ArrayList<Payment> payment) {
        int pick;
        String staffID = "S0001"; //Staff ID and password are fixed. Only 1 staff account
        String staffpass = "000000";
        Scanner sc = new Scanner(System.in);
        boolean invalid;

        System.out.print("\nEnter login ID (0.Back): ");
        String id = sc.nextLine();
        if (!id.equals("0")) {
            while (!(id.equals(staffID))) {
                System.out.println("\n <!> Invalid ID! <!>\nPlease enter again > ");
                id = sc.nextLine();
            }

            System.out.print("Enter password: ");
            String password = sc.nextLine();
            while (!(password.equals(staffpass))) {
                System.out.print("\n <!> Wrong password! <!>\nPlease enter again > ");
                password = sc.nextLine();
            }

            boolean back = false;
            do {
                System.out.println("\n----------< Welcome Staff >----------");
                System.out.println("\n\t|| 1 | Delete a customer record     ||\n\t|| 2 | View Report                  ||\n\t|| 3 | Logout                       ||\n");
                System.out.print("Choose one of the option from menu above ~ ");
                pick = checkError(sc, 1, 3);
                switch (pick) {
                    case 1: //Staff section menu
                        custDelete(custList); //Delete customer account
                        break;
                    case 2:
                        reportModule(payment, custList); //Report module
                        break;
                    case 3:
                        back = true;
                        break;
                    default:
                        System.out.println("\n <!> Invalid option. <!>");
                        break;
                }

            } while (!back);
        } else {
            System.out.println("Exiting to login....");
        }
    }

    public static void reportModule(ArrayList<Payment> payment, ArrayList<Customer> customer) {
        int reportChoice = 0;
        Scanner sc = new Scanner(System.in);
        boolean back = true;
        int count = 0;
        double ticketSum = 0;

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String todayDate = dateFormat.format(currentDate);

        do {
            System.out.println("\n\t|| 1 | Customer List                ||\n\t|| 2 | Movie Purchase Record        ||\n\t|| 3 | Food Purchase Record         ||\n\t|| 4 | Back                         ||\n");
            System.out.print("\nWhich report would you like to view? > ");
            reportChoice = checkError(sc, 1, 5);
            switch (reportChoice) {
                case 1: //Customer list that was registered
                    if (!customer.isEmpty()) {
                        System.out.println("\n---< Report >---\n\n");
                        System.out.println("Date    : " + todayDate);
                        System.out.println("< Customer List Registered > \n\n");
                        System.out.println("=========================================================");
                        System.out.println("No. Customer Name                       Customer Password");
                        System.out.println("=========================================================");
                        for (Customer c : customer) {
                            System.out.printf("%d   %s\t\t\t\t%s", ++count, c.getname(), c.getpassword());
                            System.out.println();
                        }
                        System.out.println();
                        System.out.println("=========================================================");
                        System.out.printf("Total Number of Customer: %d\n", count);
                        System.out.println("=========================================================");
                        break;
                    } else {
                        System.out.println("\n <!> There are no records! <!>");
                    }
                    break;
                case 2: //List of movies that will only appear after customer made purchase
                    ArrayList<Ticket> ticketArr = new ArrayList<>();
                    for (Payment p : payment) {
                        ticketArr.addAll(p.getTicket());
                    }
                    if (!ticketArr.isEmpty()) {
                        System.out.println("\n---< Report >--- \n\n");
                        System.out.println("Date    : " + todayDate);
                        System.out.println("< Movie Purchase Record >\n\n");
                        System.out.println("=============================================================");
                        System.out.println("No. Movie Name                Unit                Total Price");
                        System.out.println("=============================================================");
                        for (Ticket t : ticketArr) {
                            if (t.getMovieName() != null) {
                                System.out.printf("%d    %s\t\t%d\t\t\t%.2f", ++count, t.getMovieName(), t.getTicketAmt(), t.ticketPrice() * t.getTicketAmt());
                                System.out.println();
                                ticketSum += t.ticketPrice() * t.getTicketAmt();
                            }
                        }
                        System.out.println();
                        System.out.println("=============================================================");
                        System.out.printf("Sum of Price: %.2f\n", ticketSum);
                        System.out.println("=============================================================");
                        break;
                    } else {
                        System.out.println("\n <!> There are no records! <!>");
                    }
                    break;
                case 3://List of f&b that will only appear after customer made purchase
                    ArrayList<Food> foodArr = new ArrayList<>();
                    for (Payment p : payment) {
                        foodArr.addAll(p.getFood());
                    }
                    if (!foodArr.isEmpty()) {
                        System.out.println("\n---< Report >--- \n\n");
                        System.out.println("Date    : " + todayDate);
                        System.out.println("\n< Food Purchase Record >\n\n");
                        System.out.println("=============================================================");
                        System.out.println("No. Food Name                Unit                Total Price");
                        System.out.println("=============================================================");
                        for (Food f : foodArr) {
                            if (f.getName() != null) {
                                System.out.printf("%d    %s\t\t%d\t\t%.2f", ++count, f.getName(), f.getQty(), f.getPrice());
                                System.out.println();
                                ticketSum += f.getPrice();
                            }
                        }
                        System.out.println();
                        System.out.println("=============================================================");
                        System.out.printf("Sum of Price: %.2f\n", ticketSum);
                        System.out.println("=============================================================");
                        break;
                    } else {
                        System.out.println("\n <!> There are no records! <!>");
                    }
                    break;
                case 4:
                    back = true;
                    break;
                default:
                    System.out.println("\n <!> Please enter within the range of (1 to 4)! <!>");
                    break;
            }
        } while (!back);

    }

    public static ArrayList custDelete(ArrayList<Customer> custList) {
        Customer customerInfo = new Customer();
        Scanner sc = new Scanner(System.in);
        char yesno = 'N';
        char confirm = 'N';
        boolean invalid = false;

        if (custList.isEmpty()) {
            System.out.println("\n <!> There are no records to be deleted. <!>");
        } else {
            System.out.println("\nList of Customers ");
            for (Customer customer : custList) {
                System.out.println(customer.toString());
            }
            System.out.print("\n\nEnter a customer name to be deleted: ");
            String delname = sc.next();
            boolean found = false;
            for (Customer c : custList) {
                if (c != null && c.getname().equals(delname)) { //If customer name matches
                    System.out.print("\nConfirm to customer record with name (Y: Yes/N: No): " + "(" + c.getname() + ")?");
                    found = true;
                    confirm = sc.next().toUpperCase().charAt(0);
                    if (confirm == 'Y') {
                        System.out.println("\nSuccessfully removed customer record with name: " + "(" + c.getname() + ").");
                        custList.remove(c);
                        break;
                    } else if (confirm == 'N') {
                        System.out.println("\nFailed to delete customer record with name: " + "(" + c.getname() + ").");
                        invalid = false;
                        break;
                    } else {
                        invalid = false;
                        break;
                    }
                }
            }

            if (!found) {
                System.out.println("\n<!> Customer record not found! Please try again. <!>");
            }
            return custList;
        }
        return custList;
    }

    public static boolean custLogin(ArrayList<Customer> custList) { //Return true when customer is logged in
        boolean invalid;
        Customer customerInfo = new Customer();
        String password = ".";
        if (custList.isEmpty()) {
            System.out.println("\n <!> There are no records in the list. <!>\nPlease proceed to registeration. \n .....\n ..........\n ...............\n");
        } else {
            System.out.println("  _                 _");
            System.out.println(" | |               (_)");
            System.out.println(" | |     ___   __ _ _ _ __");
            System.out.println(" | |    / _ \\ / _` | | '_ \\");
            System.out.println(" | |___| (_) | (_| | | | | |");
            System.out.println(" |______\\___/ \\__, |_|_| |_|");
            System.out.println("               __/ |");
            System.out.println("              |___/");
            System.out.println("\n----------< Welcome to Login >----------");
            do {
                invalid = true;
                Scanner sc = new Scanner(System.in);
                System.out.print("\nPlease enter your name to login (0 for Back): ");
                String name = sc.nextLine();
                if (name.equals("0")) {
                    return false;
                } else {
                    System.out.print("\nPlease enter your password: ");
                    password = sc.nextLine();
                }
                for (Customer customer : custList) {
                    if (customer.cusLogin(name, password)) {
                        customerInfo = customer;
                        invalid = false;
                        break;
                    } else {
                        System.out.println("\n<!> Wrong username or wrong password, please reenter. <!>");
                    }
                    invalid = true;
                }
            } while (invalid);
        }
        return true;
    }

    public static Customer custRegistration(ArrayList<Customer> custList) { //Return customer whenever a registeration is made
        Customer customerInfo = new Customer();
        Scanner sc = new Scanner(System.in);
        boolean invalid;
        System.out.println("  _____            _     _             _   _ ");
        System.out.println(" |  __ \\          (_)   | |           | | (_)");
        System.out.println(" | |__) |___  __ _ _ ___| |_ _ __ __ _| |_ _  ___  _ __");
        System.out.println(" |  _  // _ \\/ _` | / __| __| '__/ _` | __| |/ _ \\| '_ \\ ");
        System.out.println(" | | \\ \\  __/ (_| | \\__ \\ |_| | | (_| | |_| | (_) | | | |");
        System.out.println(" |_|  \\_\\___|\\__, |_|___/\\__|_|  \\__,_|\\__|_|\\___/|_| |_|");
        System.out.println("              __/ | ");
        System.out.println("             |___/");
        System.out.println("\n----------< Welcome to Registration >----------");
        do {
            invalid = false;
            System.out.print("\nPlease enter your name for registration: ");
            String name = sc.nextLine();
            if (name.isEmpty()) {
                System.out.println(" <!> Name cannot be empty. Please try again. <!>");
                invalid = true;
                continue;
            }

            for (Customer customer : custList) {
                if (customer.cusReg(name)) {
                    System.out.println("\n <!> This name has been used, please try again. <!>");
                    invalid = true;
                    break;
                } else {
                    invalid = false;
                }
            }
            if (!invalid) {
                customerInfo.setname(name);
            }
        } while (invalid);

        System.out.print("\nPlease enter a password of 6 characters for registration: ");
        String password = sc.nextLine();
        while (password.length() != 6) {
            System.out.println(" <!> Password must be 6 characters! <!>");
            System.out.print("\nPlease enter a password of 6 characters for registration: ");
            password = sc.nextLine();
        }
        customerInfo.setpassword(password);
        System.out.println("Registeration has beeen added\n");
        return customerInfo;
    }

    //Payment module
    public static Payment PaymentModule(ArrayList<Ticket> ticketArr, ArrayList<Food> foodArr, ArrayList<Customer> customer, ArrayList<Payment> oldPayment) {
        Payment payment = new Payment();
        Scanner sc = new Scanner(System.in);
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String todayDate = dateFormat.format(currentDate);
        int receiptID = 0;
        int productNo = 0;
        int indexTicket = 0;
        int indexFood = 0;
        ArrayList<Ticket> ticketList = new ArrayList<>(ticketArr);
        ArrayList<Food> foodList = new ArrayList<>(foodArr);
        double[] totalPrice = new double[ticketArr.size()]; //Used for calculating total prices
        double[] foodtotalPrice = new double[foodArr.size()];
        double sumOfPrices = 0;
        for (int i = 0; i < ticketArr.size(); i++) {
            totalPrice[i] = ticketList.get(i).ticketPrice() * ticketList.get(i).getTicketAmt();
            sumOfPrices += totalPrice[i];
        }

        for (int i = 0; i < foodArr.size(); i++) {
            foodtotalPrice[i] = foodList.get(i).getPrice();
            sumOfPrices += foodtotalPrice[i];
        }
        
        for(Payment p: oldPayment){
            if(p != null){
                receiptID = p.getPaymentID();
            }
        }


        System.out.println("\n---< Receipt >--- \n");
        System.out.printf("=============================================================================================\n");
        System.out.printf("Receipt No      : %d\n", ++receiptID);
        System.out.printf("Date            : %s\n", todayDate);
        System.out.printf("---------------------------------------------------------------------------------------------\n");
        System.out.printf("Product No.   Product Name                   Qty                 Unit Price       Sum Price\n");
        for (Ticket t : ticketList) {
            if (t.getMovieName() != null) {
                System.out.printf("%-12d%14s%20d%25.2f%17.2f\n", ++productNo, t.getMovieName(), t.getTicketAmt(), t.ticketPrice(), totalPrice[indexTicket]);
                indexTicket++;
            }
        }

        for (Food f : foodList) {
            if (f.getName() != null) {
                System.out.printf("%-12d%17s%17d%25.2f%17.2f\n", ++productNo, f.getName(), f.getQty(), f.getPrice() / f.getQty(), foodtotalPrice[indexFood]);
                indexFood++;
            }
        }
        System.out.printf("---------------------------------------------------------------------------------------------\n");
        System.out.printf("Total Price: %74.2f\n", sumOfPrices);
        boolean back = false;
        boolean invalid = false;
        int payChoice = 0;
        //If either ticket or food list is not empty it will prompt user to pay, if paid return true, on payment status
        if (!ticketList.isEmpty() || !foodList.isEmpty()) {
            do {
                System.out.println("\n\tWould you like to pay by > ");
                System.out.println("\n\t|| 1 | Bank Transfer      ||\n\t|| 2 | Cash               ||\n\t|| 3 | Back               ||\n");
                System.out.print("Choose one of the option from menu above ~ ");
                payChoice = checkError(sc, 1, 3);
                switch (payChoice) {
                    case 1:
                        System.out.print("\nEnter bank account number: ");
                        String bankNum = sc.next();
                        if (bankNum.length() == 9 && bankNum.matches("\\d+")) {
                            invalid = false;

                        } else {
                            invalid = true;
                        }
                        if (invalid) {
                            System.out.println("\n <!> Invalid account number! Please try again! <!>");
                            bankNum = sc.next();
                        } else {
                            System.out.println("\n\t<$> Payment successful! <$>\n-----< Thank you! Come Again! >-----");
                            payment = new Payment(customer, ticketList, foodList, sumOfPrices, true);
                            back = true;
                        }
                        break;
                    case 2:
                        System.out.printf("\nIs the amount: RM%.2f correct?(Y: Yes/N: No) > ", sumOfPrices);
                        char correct = sc.next().toUpperCase().charAt(0);
                        if (correct == 'Y') {
                            System.out.println("\n\t<$> Payment successful! <$>\n-----< Thank you! Come Again! >-----");
                            payment = new Payment(customer, ticketList, foodList, sumOfPrices, true);
                            back = true;
                        } else {
                            System.out.println("\n <!> Sorry for the inconvenience! <!>");
                            break;
                        }
                        break;
                    case 3:
                        back = true;
                        return null;
                    default:
                        System.out.println("\n <!> Please enter within the range of (1 to 3) only! <!>");
                        break;
                }
            } while (!back);
        } else {
            payment.setPaymentMade(false);
            return null;
        }
        return payment;
    }

    public static void logo(int num) {
        switch (num) {
            case 1:
                System.out.println("___  ___      _        ___  ___                 ");
                System.out.println("|  \\/  |     (_)       |  \\/  |                 ");
                System.out.println("| .  . | __ _ _ _ __   | .  . | ___ _ __  _   _ ");
                System.out.println("| |\\/| |/ _` | | '_ \\  | |\\/| |/ _ \\ '_ \\| | | |");
                System.out.println("| |  | | (_| | | | | | | |  | |  __/ | | | |_| |");
                System.out.println("\\_|  |_/\\__,_|_|_| |_| \\_|  |_/\\___|_| |_|\\__,_|");
                System.out.println("                                                ");
                System.out.println();
                System.out.println("\n----------< Welcome to Main Menu >----------");
                break;
            case 2:
                System.out.println(" ____               _    _             ");
                System.out.println(" |  _ \\            | |  (_)            ");
                System.out.println(" | |_) | ___   ___ | | ___ _ __   __ _ ");
                System.out.println(" |  _ < / _ \\ / _ \\| |/ / | '_ \\ / _` |");
                System.out.println(" | |_) | (_) | (_) |   <| | | | | (_| |");
                System.out.println(" |____/ \\___/ \\___/|_|\\_\\_|_| |_|\\__, |");
                System.out.println("                                  __/ |");
                System.out.println("                                 |___/ ");
                System.out.println();
                System.out.println("\n----------< Welcome to Booking >----------");
                break;
            default:
                break;
        }
    }

    //Booking menu
    public static ArrayList<Ticket> BookingMenu(ArrayList<Ticket> oldTicket) {
        boolean back = false;
        int count = 0;
        ArrayList<Ticket> ticketArr = oldTicket;
        Scanner sc = new Scanner(System.in);
        int menuChoice = 0;

        //BOOKING MOVIE MODULE
        do {
            logo(2);
            System.out.println("\n\t|| 0 | Back               ||\n\t|| 1 | Booking            ||\n\t|| 2 | View Cart          ||\n\t|| 3 | View Seat Details  ||\n\t|| 4 | Delete Ticket      ||\n\t|| 5 | Exit to Main Menu  ||\n");
            System.out.print("Choose one of the option from menu above ~ ");
            menuChoice = checkError(sc, 0, 5);

            do {
                switch (menuChoice) {
                    case 0://Back
                        break;
                    case 1: //Prompt user to enter multiple data choices and adding the result back into array list
                        ticketArr.add(bookingModule(oldTicket));
                        logo(2);
                        System.out.println("\n\t|| 0 | Back               ||\n\t|| 1 | Booking            ||\n\t|| 2 | View Cart          ||\n\t|| 3 | View Seat Details  ||\n\t|| 4 | Delete Ticket      ||\n\t|| 5 | Exit to Main Menu  ||\n");
                        System.out.print("Choose one of the option from menu above ~ ");
                        menuChoice = checkError(sc, 0, 5);
                        break;
                    case 2://Cart
                        System.out.println("\n---< Your cart >---");
                        System.out.println("==========================================================================");
                        System.out.println("Movie                Qty    TicketID    Showtime    Date           Hall ID");
                        System.out.println("==========================================================================");
                        for (Ticket t : ticketArr) {
                            System.out.printf("%-21s%-5d%3d%19s%14s%6d\n", t.getMovieName(), t.getTicketAmt(), t.getTicketID(), t.time(), t.getSchedule(), t.getHallId());
                        }
                        System.out.println("\n\t|| 0 | Back               ||\n\t|| 1 | Booking            ||\n\t|| 2 | View Cart          ||\n\t|| 3 | View Seat Details  ||\n\t|| 4 | Delete Ticket      ||\n\t|| 5 | Exit to Main Menu  ||\n");
                        System.out.print("Choose one of the option from menu above ~ ");
                        menuChoice = checkError(sc, 0, 5);
                        break;
                    case 3://Seat info, seat ID, Hall
                        System.out.println("\n---< Your seat information >--- ");
                        System.out.println("=====================");
                        for (Ticket t : ticketArr) {
                            System.out.println("Ticket ID  : " + t.getTicketID());
                            System.out.println("=====================");
                            System.out.println("Hall type  : " + t.getHallType());
                            System.out.println("Hall ID    : " + t.getHallId());
                            t.displaySeatArray();
                            System.out.println("=====================");
                            System.out.println();
                        }
                        System.out.println("\n\t|| 0 | Back               ||\n\t|| 1 | Booking            ||\n\t|| 2 | View Cart          ||\n\t|| 3 | View Seat Details  ||\n\t|| 4 | Delete Ticket      ||\n\t|| 5 | Exit to Main Menu  ||\n");
                        System.out.print("Choose one of the option from menu above ~ ");
                        menuChoice = checkError(sc, 0, 5);
                        break;
                    case 4://Delete feature, can delete ticket according ticket ID
                        boolean invalid = false;
                        char confirm = 'N';
                        if (ticketArr.isEmpty()) {
                            System.out.println("<!> No record found! <!>");
                            menuChoice = 0;
                        } else {
                            System.out.println("\n---< Your seat information >---");
                            System.out.println("=====================");
                            for (Ticket t : ticketArr) {
                                System.out.println("Ticket ID  : " + t.getTicketID());
                                System.out.println("=====================");
                                System.out.println("Hall type  : " + t.getHallType());
                                System.out.println("Hall ID    : " + t.getHallId());
                                System.out.println("=====================");
                                t.displaySeatArray();
                                System.out.println("=====================");
                                System.out.println();
                            }
                            System.out.print("\nSelect the ticket ID to delete (0 to Back): ");
                            int selectID = checkError(sc, 0, 100);
                            if (selectID == 0) {
                                menuChoice = 0;
                            } else {
                                do {
                                    boolean found = false;
                                    for (Ticket t : ticketArr) {
                                        if (t != null && selectID == t.getTicketID()) { //If ticket ID matches
                                            System.out.print("\nConfirm to delete ticket with Ticket ID (Y: Yes/N: No): " + "(" + t.getTicketID() + ")?");
                                            confirm = sc.next().toUpperCase().charAt(0);
                                            if (confirm == 'Y') {
                                                System.out.println("\nSuccessfully removed ticket with Ticket ID: " + "(" + t.getTicketID() + ").");
                                                ticketArr.remove(t);
                                                found = true;
                                                break;
                                            } else if (confirm == 'N') {
                                                System.out.println("\nFailed to delete ticket with Ticket ID: " + "(" + t.getTicketID() + ").");
                                                invalid = false;
                                                break;
                                            } else {
                                                invalid = false;
                                                break;
                                            }
                                        }
                                    }

                                    if (!found) {
                                        System.out.println("\n <!> Ticket ID not found! Please try again. <!>");
                                        menuChoice = 3;
                                    }
                                } while (invalid);
                            }

                        }
                        break;
                    case 5://Exit the menu
                        menuChoice = 0;
                        back = true;
                        break;

                    default:
                        break;
                }

            } while (menuChoice != 0);
        } while (!back);
        return ticketArr;
    }

    public static ArrayList<Food> OrderFood(ArrayList<Food> oldFood) {
        boolean back = false;
        int choice;
        double totalPrice = 0;
        boolean isDuplicate;
        Scanner sc = new Scanner(System.in);
        ArrayList<Food> orders = oldFood;
        do {
            do {
                System.out.println("\n\t|| 1 | Order Popcorn      ||\n\t|| 2 | Order Beverage     ||\n\t|| 3 | Order Hot Food     ||\n\t|| 4 | View Orders        ||\n\t|| 5 | Remove Order       ||\n\t|| 6 | Exit to Main Menu  ||\n");
                System.out.print("\nChoose one of the option from menu above ~ ");

                choice = sc.nextInt();
                switch (choice) {
                    case 0:
                        break;
                    case 1://ORDER POPCORN
                        Popcorn p = new Popcorn();
                        p = p.getOrder();
                        isDuplicate = false;
                        for (Food f : orders) {
                            if (f.getName() != null) {
                                if (f.getClass().equals(p.getClass()) && f.getName().equals(p.getName())) {
                                    f.incrementQty(p.getQty());
                                    f.incrementPrice(p.getPrice());
                                    isDuplicate = true;
                                    break;
                                }
                            }
                        }

                        if (!isDuplicate) {
                            orders.add(p);
                        }
                        break;
                    case 2://ORDER BEVERAGE
                        Beverage b = new Beverage();
                        b = b.getOrder();
                        isDuplicate = false;
                        for (Food f : orders) {
                            if (f.getName() != null) {
                                if (f.getClass().equals(b.getClass()) && f.getName().equals(b.getName())) {
                                    f.incrementQty(b.getQty());
                                    f.incrementPrice(b.getPrice());
                                    isDuplicate = true;
                                    break;
                                }
                            }

                        }
                        if (!isDuplicate) {
                            orders.add(b);
                        }
                        break;
                    case 3: //ORDER HOTFOOD
                        HotFood h = new HotFood();
                        h = h.getOrder();
                        isDuplicate = false;
                        for (Food f : orders) {
                            if (f.getName() != null) {
                                if (f.getClass().equals(h.getClass()) && f.getName().equals(h.getName())) {
                                    f.incrementQty(h.getQty());
                                    f.incrementPrice(h.getPrice());
                                    isDuplicate = true;
                                    break;
                                }
                            }
                        }
                        if (!isDuplicate) {
                            orders.add(h);
                        }
                        break;
                    case 4: //VIEW ORDERS
                        totalPrice = 0;
                        if (!(orders.isEmpty())) {
                            System.out.println("\nNo  Name                                Qty   Price");
                            System.out.println("=====================================================");
                            for (Food f : orders) {
                                if(f.getName() != null){
                                System.out.printf("%3d ", orders.indexOf(f) + 1);
                                System.out.println(f.printOrder());
                                totalPrice += f.getPrice();
                                }
                            }
                            System.out.println("=====================================================");
                            System.out.printf("                                     Total  RM %6.2f\n", totalPrice);
                        } else {
                            System.out.println("\n <!> You Haven't Ordered Anything! <!>");
                            back = true;
                        }
                        break;
                    case 5: //REMOVE FEATURE
                        if (!(orders.isEmpty())) {
                            System.out.println("\nNo  Name                                Qty   Price");
                            System.out.println("=====================================================");
                            for (Food f : orders) {
                                if(f.getName() != null){
                                System.out.printf("%3d ", orders.indexOf(f) + 1);
                                System.out.println(f.printOrder());
                                totalPrice += f.getPrice();
                                }
                            }
                            System.out.println("=====================================================");
                            System.out.printf("                                     Total  RM %6.2f\n", totalPrice);
                        } else {
                            System.out.println("\n <!> You Haven't Ordered Anything! <!>");
                            back = true;
                        }
                        System.out.print("\nEnter Order To Remove: ");
                        int index;
                        char confirm = 'N';
                        boolean invalid = false;
                        int removeNum = checkError(sc, 0, 100);
                        do {
                            boolean found = false;
                            for (Food f : orders) {
                                if (f != null && removeNum - 1 == orders.indexOf(f)) { //If food No/index matches
                                    index = orders.indexOf(f) + 1;
                                    System.out.print("\nConfirm to delete item with Item No (Y: Yes/N: No): " + "(" + index + ")? > ");
                                    confirm = sc.next().toUpperCase().charAt(0);
                                    if (confirm == 'Y') {
                                        System.out.println("\nSuccessfully removed item with Item No: " + "(" + index + ").");
                                        orders.remove(f);
                                        found = true;
                                        break;
                                    } else if (confirm == 'N') {
                                        System.out.println("\nFailed to delete item with Item No: " + "(" + index + ").");
                                        invalid = false;
                                        break;
                                    } else {
                                        invalid = false;
                                        break;
                                    }
                                }
                            }

                            if (!found) {
                                System.out.println("\n <!> Food order not found! Please try again. <!>");
                                choice = 4;
                            }
                        } while (invalid);
                        break;
                    case 6://Exit to menu
                        back = true;
                        break;
                    default:
                        System.out.println("\n <!> Please Enter (0 to 6) only <!>\n");
                        break;
                }
            } while (choice < 0 && choice > 6);

        } while (!back);

        return orders;
    }

    public static Ticket bookingModule(ArrayList<Ticket> oldTicket) {
        int index = 0;
        Scanner sc = new Scanner(System.in);
        int count = 1;
        Movie[] movieList = new Movie[5];
        movieList[0] = new Movie("Dune: Part 1", 2.35, "Denis Villeneuve", "October 22, 2021");
        movieList[1] = new Movie("Blade Runner 2049", 2.43, "Denis Villeneuve", "October 5, 2017");
        movieList[2] = new Movie("Infinity Pool", 1.58, "Brandon Cronenberg", "January 27, 2023");
        movieList[3] = new Movie("Crimes of the Future", 1.47, "David Cronenberg", "May 25, 2022");
        movieList[4] = new Movie("Asteroid City", 1.45, "Wes Anderson", "June 15, 2023");

        CinemaHall[] hallList = new CinemaHall[3];
        hallList[0] = new CinemaHall(1, "Standard", 5, 10);
        hallList[1] = new CinemaHall(2, "IMAX", 8, 15);
        hallList[2] = new CinemaHall(3, "Lounge", 5, 5);

        Ticket[] ticketArr = new Ticket[100000];

        //DISPLAY MOVIE LIST AND ASK INPUT
        for (Movie m : movieList) {
            System.out.println("(" + count++ + ") " + m.toString() + "\n");
        }
        printSaleTicket(1);
        int movieInput = checkError(sc, 1, 5);

        Movie movie = movieList[movieInput - 1];

        //DISPLAY SHOWTIME LIST AND ASK INPUT
        count = 1;
        Showtime[] available = new Showtime[3];
        available[0] = showtimeAvailable(movie, 1);
        available[1] = showtimeAvailable(movie, 2);
        available[2] = showtimeAvailable(movie, 3);
        for (Showtime show : available) {
            System.out.println("(" + count++ + ") " + show.getDate());
        }
        printSaleTicket(2);
        int dateInput = checkError(sc, 1, 3);

        //DISPLAY TIME AND ASK INPUT
        Showtime showtime = available[dateInput - 1];

        generateShowtime(showtime);
        printSaleTicket(3);
        int timeInput = checkError(sc, 1, 7);
        selectShowtime(showtime, timeInput);

        System.out.println("Showtime result: \n" + showtime.toString());

        //DISPLAY CINEMA HALL LIST
        for (CinemaHall x : hallList) {
            System.out.println(x);
        }
        printSaleTicket(4);
        int hallInput = checkError(sc, 1, 3);

        //ASK FOR AMOUNT OF SEATS
        CinemaHall cinema = hallList[hallInput - 1];
        int seatAmt = 0;

        printSaleTicket(5);
        seatAmt = checkError(sc, 1, 100);

        ArrayList<Seat> StandardSeat = new ArrayList<>();
        ArrayList<Seat> ImaxSeat = new ArrayList<>();
        ArrayList<Seat> LoungeSeat = new ArrayList<>();

        //Check whether which seat belongs to which hall
        if (hallInput == 1) {//STANDARD SECTION
            ArrayList<Seat> initialStandardSeat = new ArrayList<>();
            for (Ticket t : oldTicket) {
                if (t.getHallType().equals("Standard")) {
                    initialStandardSeat.addAll(t.getSeat()); //Previous selected seat data is here
                }
            }

            for (int i = 0; i < seatAmt; i++) {
                Seat initialSeat = selectSeat(hallInput, cinema, seatAmt);
                while (true) {
                    if (isSeatIDUnique(initialSeat, seatAmt, initialStandardSeat)) { //If the seatID is not duplicate, will store
                        StandardSeat.add(initialSeat);
                        break;
                    } else {
                        initialSeat = selectSeat(hallInput, cinema, seatAmt);
                    }
                }

            }

            ticketArr[index] = new Ticket(available[dateInput - 1], seatAmt, cinema, StandardSeat);
            Ticket ticket = ticketArr[index];
            index++;
            return ticket;

        } else if (hallInput == 2) { //IMAX SECTION
            ArrayList<Seat> initialImaxSeat = new ArrayList<>();
            for (Ticket t : oldTicket) {
                if (t.getHallType().equals("IMAX")) {
                    initialImaxSeat.addAll(t.getSeat());
                }
            }

            for (int i = 0; i < seatAmt; i++) {
                Seat initialSeat = selectSeat(hallInput, cinema, seatAmt);
                while (true) {
                    if (isSeatIDUnique(initialSeat, seatAmt, initialImaxSeat)) {
                        ImaxSeat.add(initialSeat);
                        break;
                    } else {
                        initialSeat = selectSeat(hallInput, cinema, seatAmt);
                    }
                }

            }

            ticketArr[index] = new Ticket(available[dateInput - 1], seatAmt, cinema, ImaxSeat);
            Ticket ticket = ticketArr[index];
            index++;
            return ticket;

        } else if (hallInput == 3) {//LOUNGE SECTION
            ArrayList<Seat> initialLoungeSeat = new ArrayList<>();
            for (Ticket t : oldTicket) {
                if (t.getHallType().equals("Lounge")) {
                    initialLoungeSeat.addAll(t.getSeat());
                }
            }

            for (int i = 0; i < seatAmt; i++) {
                Seat initialSeat = selectSeat(hallInput, cinema, seatAmt);
                while (true) {
                    if (isSeatIDUnique(initialSeat, seatAmt, initialLoungeSeat)) {
                        LoungeSeat.add(initialSeat);
                        break;
                    } else {
                        initialSeat = selectSeat(hallInput, cinema, seatAmt);
                    }
                }

            }

            ticketArr[index] = new Ticket(available[dateInput - 1], seatAmt, cinema, LoungeSeat);
            Ticket ticket = ticketArr[index];
            index++;
            return ticket;

        } else {
            return null;
        }
    }

    public static Seat selectSeat(int hallInput, CinemaHall hall, int seatAmt) {
        Seat selectedSeat = null;
        Scanner sc = new Scanner(System.in);
        String seatType = "Single";

        if (hallInput == 1) {
            printSaleTicket(6);
        } else if (hallInput == 2) {
            printSaleTicket(7);
        } else {
            printSaleTicket(8);
        }

        char seatRow = getValidSeatRow(sc, hallInput);
        System.out.println();
        int seatCol = getValidSeatCol(sc, hallInput);
        selectedSeat = new Seat(seatCol, seatRow, seatType, "Booked", hall);

        return selectedSeat;
    }

    public static boolean isSeatIDUnique(Seat initialSeatID, int seatAmt, ArrayList<Seat> existedSeatID) {
        for (Seat s : existedSeatID) {
            String seatID = initialSeatID.getSeatRow() + Integer.toString(initialSeatID.getSeatCol());
            String existingSeatID = s.getSeatRow() + Integer.toString(s.getSeatCol());
            if (seatID.equals(existingSeatID)) {
                System.out.println("\n <!> Seat is already taken! Please choose another one. <!>");
                return false;
            }
        }
        return true;
    }

    //Validate whether seat column fit within the range of respective halls
    public static int getValidSeatCol(Scanner scanner, int typeHall) {
        int initialSeatCol = 0;
        int minSeatCol = 1;
        int maxSeatCol;

        if (typeHall == 1) {
            maxSeatCol = 10;
        } else if (typeHall == 2) {
            maxSeatCol = 15;
        } else if (typeHall == 3) {
            maxSeatCol = 5;
        } else {
            throw new IllegalArgumentException("\n <!>Invalid typeHall<!>");
        }

        while (true) {
            try {
                System.out.print("Please select seat column (e.g: 3 ) > ");
                int input = scanner.nextInt();

                if (input >= minSeatCol && input <= maxSeatCol) {
                    initialSeatCol = input;
                    break;
                } else {
                    throw new InputMismatchException();
                }
            } catch (InputMismatchException e) {
                System.out.println("\n <!>Invalid column input, please try again! <!>");
                scanner.nextLine();
            }
        }
        return initialSeatCol;
    }

    //Validate whether seat row fit within the range of respective halls
    public static char getValidSeatRow(Scanner scanner, int typeHall) {
        char initialSeatRow = 0;

        char minSeatRow = 'A';
        char maxSeatRow;

        if (typeHall == 1 || typeHall == 3) {
            maxSeatRow = 'E';
        } else if (typeHall == 2) {
            maxSeatRow = 'H';
        } else {
            throw new IllegalArgumentException("\n<!> Invalid typeHall <!>");
        }

        while (true) {
            try {
                System.out.print("Please select seat row    (e.g: A ) > ");
                String input = scanner.next();

                if (input.length() == 1 && Character.isLetter(input.charAt(0))) {
                    char inputSeatRow = input.charAt(0);

                    if (inputSeatRow >= minSeatRow && inputSeatRow <= maxSeatRow) {
                        initialSeatRow = inputSeatRow;
                        break;
                    } else {
                        throw new InputMismatchException();
                    }
                } else {
                    throw new InputMismatchException();
                }
            } catch (InputMismatchException e) {
                System.out.println("\n <!>Invalid row input, please try again!<!>");
                scanner.nextLine();
            }
        }
        return initialSeatRow;
    }

    //Used for booking module for printing
    public static void printSaleTicket(int choice) {
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

    //Validate whether the user input is within range
    public static int checkError(Scanner scanner, int range1, int range2) {
        while (true) {
            try {
                int input = scanner.nextInt();

                if (input >= range1 && input <= range2) {
                    return input;
                } else {
                    throw new InputMismatchException();
                }
            } catch (InputMismatchException e) {
                System.out.println("\n <!> Invalid input, please try again! <!>");
                scanner.nextLine();
            }
        }
    }

    //Generate showtime date
    public static Showtime showtimeAvailable(Movie movie, int amt) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        int day = currentDateTime.getDayOfMonth() + amt;
        int month = currentDateTime.getMonthValue();
        int year = currentDateTime.getYear();

        while (day > 30) {
            month += 1;
            day -= 30;
        }

        Showtime showtime = new Showtime(movie, year, month, day, null);
        return showtime;
    }

    //Generate showtime time
    public static void generateShowtime(Showtime showtime) {
        int count;
        LocalDate currentDate = showtime.getDate();
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(22, 0);
        int intervalMinutes = 100; //Can be tweaked 

        List<LocalTime> generatedShowtimes = showtime.generateShowtimes(startTime, endTime, intervalMinutes);
        System.out.println("Showtimes for " + currentDate + ":");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        count = 1;
        for (LocalTime time : generatedShowtimes) {
            System.out.println("(" + count++ + ")" + time.format(timeFormatter));
        }
    }

    public static void selectShowtime(Showtime showtime, int timeInput) {
        LocalDate currentDate = showtime.getDate();
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(22, 0);
        int intervalMinutes = 100;

        List<LocalTime> generatedShowtimes = showtime.generateShowtimes(startTime, endTime, intervalMinutes);
        System.out.println("Showtimes for " + currentDate + ":");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        showtime.setShowtime(generatedShowtimes.get(timeInput - 1).format(timeFormatter));
    }

}

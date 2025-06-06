import java.util.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class CashRegister {

    static ArrayList<String> orderName = new ArrayList<>();
    static ArrayList<Integer> orderQuantity = new ArrayList<>();
    static ArrayList<Double> orderPrice = new ArrayList<>();
    static ArrayList<String> userCredential = new ArrayList<>();
    static ArrayList<String> passCredential = new ArrayList<>();

    static String[] foodMenu = {"WackBurger", "Wacky Fries", "Avocado Ice Cream", "WackNuggets", "WackCafe", "WackMuffin", "Chicken Burger Pastil"};
    static double[] foodPrices = {70.00, 59.00, 39.00, 88.00, 49.00, 68.00, 99.00};

    static Scanner nep = new Scanner(System.in);
    static String userName = "";

    public static void displayMenu() {

        System.out.println("\n----------- Welcome to WACKDonalds! ----------");
        System.out.println("1. Add your order");
        System.out.println("2. Remove your order");
        System.out.println("3. Modify order quantity");
        System.out.println("4. Check your cart");
        System.out.println("5. Checkout / Pay");
        System.out.println("6. Exit");
        System.out.println("Please type the number of your desired option!");

        int desiredOption = 0;
        try {
            desiredOption = nep.nextInt();
            nep.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input! Please enter a number.");
            nep.nextLine();
            displayMenu();
            return;
        }

        menuOrder(desiredOption);

    }

    public static void menuOrder(int choice) {
        switch (choice) {
            case 1:
                System.out.println("\n Here is our WACKDonalds Menu: ");
                for (int i = 0; i < foodMenu.length; i++) {
                    System.out.println((i + 1) + ". " + foodMenu[i] + " - PHP " + foodPrices[i]);
                }

                System.out.println("Please enter the number of the food you wish to order: ");
                int foodNumber = nep.nextInt();
                if (foodNumber > foodMenu.length || foodNumber < 1) {
                    System.out.println("We reject you! Please correct your life choices and input" +
                            " a valid number next time!");
                    return;
                }
                nep.nextLine();

                System.out.println("Enter quantity of your order: ");
                int foodQuantity = nep.nextInt();
                nep.nextLine();

                String foodName = foodMenu[foodNumber - 1];
                double foodPrice = foodPrices[foodNumber - 1];

                orderName.add(foodName);
                orderQuantity.add(foodQuantity);
                orderPrice.add(foodPrice);

                System.out.println(foodQuantity + " " + foodName + " were added to your WACKDonalds cart!");

                break;

            case 2:
                if (orderName.isEmpty()) {
                    System.out.println("You currently do not have any orders in your cart!");
                    return;
                }

                System.out.println("Enter the name of the item you wish to remove: ");
                String foodNameToRemove = nep.nextLine();

                boolean itemFound = false;
                for (int i = 0; i < orderName.size(); i++) {
                    if (orderName.get(i).equalsIgnoreCase(foodNameToRemove)) {
                        orderName.remove(i);
                        orderQuantity.remove(i);
                        orderPrice.remove(i);
                        System.out.println(foodNameToRemove + " was removed from your WACKDonalds cart.");
                        itemFound = true;
                        break;
                    }
                }

                if (!itemFound) {
                    System.out.println("Item not found in your cart!");
                }
                break;

            case 3:
                if (orderName.isEmpty()) {
                    System.out.println("You currently do not have any orders in your cart!");
                    return;
                }
                
                System.out.println("Your current WACKDonalds cart:");
                for (int i = 0; i < orderName.size(); i++) {
                    System.out.println((i + 1) + ". " + orderName.get(i) + " - Quantity: " + orderQuantity.get(i));
                }

                System.out.println("Enter the number of the item you want to modify:");
                try {
                    int itemNumber = nep.nextInt();
                    nep.nextLine();

                    if (itemNumber < 1 || itemNumber > orderName.size()) {
                        System.out.println("Invalid item number!");
                        return;
                    }

                    System.out.println("Current quantity of " + orderName.get(itemNumber - 1) +
                            ": " + orderQuantity.get(itemNumber - 1));
                    System.out.println("Enter new quantity:");

                    int newQuantity = nep.nextInt();
                    nep.nextLine();

                    if (newQuantity < 1) {
                        System.out.println("Quantity must be at least 1!");
                        return;
                    }

                    orderQuantity.set(itemNumber - 1, newQuantity);
                    System.out.println("Quantity updated successfully!");

                } catch (InputMismatchException e) {
                    System.out.println("Invalid input! Please enter numbers only.");
                    nep.nextLine();
                }

            case 4:
                if (orderName.isEmpty()) {
                    System.out.println("You currently do not have any orders in your cart!");
                    return;
                }

                System.out.println("Your WACKDonalds cart: ");
                double total = 0;

                for (int i = 0; i < orderName.size(); i++) {
                    double totalPrice = orderPrice.get(i) * orderQuantity.get(i);
                    System.out.println((i + 1) + ". " + orderName.get(i) + " - PHP " + orderPrice.get(i) + " * " + orderQuantity.get(i));
                    total += totalPrice;

                }

                System.out.println("Total price: " + total + " PHP");
                break;

            case 5:
                if (orderName.isEmpty()) {
                    System.out.println("Your WACKDonalds Cart is empty!");
                    return;
                }

                total = 0;
                for (int i = 0; i < orderName.size(); i++) {
                    total += orderPrice.get(i) * orderQuantity.get(i);
                }

                System.out.println("Total price: " + total + " PHP");
                System.out.println("Enter payment amount: ");
                double payment = nep.nextDouble();
                nep.nextLine();

                if (payment >= total) {
                    double change = payment - total;
                    System.out.println("Sufficient payment, your change is: " + change + " PHP");

                    transactionLog(total, payment, change);

                    orderName.clear();
                    orderQuantity.clear();
                    orderPrice.clear();
                } else {
                    System.out.println("Insufficient payment, more money please!");
                }
                break;

            case 6:
                System.out.println("Thank you for choosing WACKDonalds!");
                System.exit(0);
        }
    }

    public static void transactionLog(double totalAmount, double payment, double change) {
        try {
            FileWriter writer = new FileWriter("transactions.txt", true);
            PrintWriter printWriter = new PrintWriter(writer);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String dateTime = LocalDateTime.now().format(formatter);

            printWriter.println("\n----------- WACKDonalds Transaction -----------");
            printWriter.println("Date & Time: " + dateTime);
            printWriter.println("Your username: " + userName);
            printWriter.println("Items purchased: ");

            for (int i = 0; i < orderName.size(); i++) {
                printWriter.println("- " + orderName.get(i) + " x" + orderQuantity.get(i) + " @ PHP " + orderPrice.get(i));
            }

            printWriter.println("Total Amount: PHP " + totalAmount);
            printWriter.println("Amount Paid: PHP " + payment);
            printWriter.println("Change Given: PHP " + change);
            printWriter.println("----------------------");
            printWriter.close();

            System.out.println("Your transaction has been saved to the file: transactions.txt.");
        } catch (IOException e) {
            System.out.println("Error saving transaction: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        while (true) {
            System.out.println("Welcome to WACKDonalds!");
            System.out.println("1. Sign Up");
            System.out.println("2. Log-in");
            System.out.println("3. Exit");
            System.out.println("Please type the number of your desired option!");
            int choice = nep.nextInt();
            nep.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Creating your WACKDonalds account...");
                    String username;
                    while (true) {
                        System.out.print("Enter your WACKDonalds username (must have 5 to 15 alphanumeric characters): ");
                        username = nep.nextLine();

                        Pattern userPattern = Pattern.compile("^[a-zA-Z0-9]{5,15}$");
                        Matcher userMatcher = userPattern.matcher(username);

                        if (userMatcher.matches()) {
                            System.out.println("Username is valid!");
                            break;
                        } else {
                            System.out.println("Invalid format! Username must contain 5 to 15 alphanumeric characters.");
                        }
                    }

                    String password;
                    while (true) {
                        System.out.println("Enter your WACKDonalds password (must have 8 to 20 characters, and at least" +
                                " one uppercase letter and one number): ");
                        password = nep.nextLine();

                        Pattern passPattern = Pattern.compile("^(?=.*[A-Z])(?=.*[0-9]).{8,20}$");
                        Matcher passMatcher = passPattern.matcher(password);

                        if (passMatcher.matches()) {
                            System.out.println("Password is valid!");
                            break;
                        } else {
                            System.out.println("Invalid format! Password must follow the criteria.");
                        }
                    }

                    userCredential.add(username);
                    passCredential.add(password);

                    System.out.println("Successfully created WACKDOnalds account!");
                    break;

                case 2:
                    System.out.println("Logging-in to your WACKDonalds account...");

                    if (userCredential.isEmpty()) {
                        System.out.println("You have not registered to WACKDonalds yet, please sign-up first.");
                        break;
                    }

                    while (true) {
                        System.out.println("Please enter your username.");
                        username = nep.nextLine();

                        if (userCredential.contains(username)) {
                            break;
                        } else {
                            System.out.println("Username invalid! Try again.");
                        }
                    }

                    while (true) {
                        System.out.println("Please enter your password.");
                        password = nep.nextLine();

                        for (int i = 0; i < userCredential.size(); i++) {
                            if (userCredential.get(i).equals(username) && passCredential.get(i).equals(password)) {
                                System.out.println("Logged-in successfully!");
                                userName = username;
                                while (true) {
                                    displayMenu();
                                }
                            } else {
                                System.out.println("Incorrect password! Try again.");
                            }
                        }
                    }

                case 3:
                    return;
            }
        }
    }
}
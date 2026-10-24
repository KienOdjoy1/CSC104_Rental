import java.util.Scanner;

public class Main {
    private static Scanner input = new Scanner(System.in);
    private static RentalSystem system = new RentalSystem();

    public static void main(String[] args) {
        int choice = -1;
        while (choice != 0) {
            showMenu();
            choice = readInt("Enter choice: ");
            try {
                switch (choice) {
                    case 1: addCustomer(); break;
                    case 2: updateCustomer(); break;
                    case 3: addVan(); break;
                    case 4: deleteVan(); break;
                    case 5: viewVans(); break;
                    case 6: searchVan(); break;
                    case 7: viewCustomers(); break;
                    case 8: rentVan(); break;
                    case 9: returnVan(); break;
                    case 10: viewRentals(); break;
                    case 11: sendExpiry(); break;
                    case 0: System.out.println("Saving data... Goodbye!"); break;
                    default: System.out.println("Invalid choice. Try again.");
                }
            } catch (RentalException e) {
                System.out.println("Error: " + e.getMessage());
            }
            system.save();
        }
    }

    private static void showMenu() {
        System.out.println("\n===== PASSENGER VAN RENTAL SYSTEM =====");
        System.out.println(" 1. Add customer");
        System.out.println(" 2. Update customer contact/email");
        System.out.println(" 3. Add van");
        System.out.println(" 4. Delete van");
        System.out.println(" 5. View all vans");
        System.out.println(" 6. Search van by plate number");
        System.out.println(" 7. View all customers");
        System.out.println(" 8. Rent a van (sends email receipt)");
        System.out.println(" 9. Return a van");
        System.out.println("10. View all rentals");
        System.out.println("11. Send expiration SMS (due today/overdue)");
        System.out.println(" 0. Save and exit");
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(input.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private static String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = input.nextLine().trim();
            if (!text.isEmpty() && !text.contains(",")) return text;
            System.out.println("Input cannot be empty or contain commas.");
        }
    }

    private static String readContact() {
        while (true) {
            String contact = readText("Contact number (e.g. 09123456789): ");
            if (contact.matches("09\\d{9}")) return contact;
            System.out.println("Invalid number. Use 11 digits starting with 09.");
        }
    }

    private static String readEmail() {
        while (true) {
            String email = readText("Email address: ");
            if (email.matches("[\\w.]+@[\\w]+\\.[\\w.]+")) return email;
            System.out.println("Invalid email format.");
        }
    }

    private static void addCustomer() {
        String name = readText("Name: ");
        String contact = readContact();
        String email = readEmail();
        Customer c = system.addCustomer(name, contact, email);
        System.out.println("Customer added. ID: " + c.getCustomerId());
    }

    private static void updateCustomer() throws RentalException {
        String id = readText("Customer ID: ");
        String contact = readContact();
        String email = readEmail();
        system.updateCustomer(id, contact, email);
        System.out.println("Customer updated.");
    }

    private static void addVan() throws RentalException {
        String type = readText("Type (S = Standard, L = Luxury): ").toUpperCase();
        if (!type.equals("S") && !type.equals("L")) throw new RentalException("Type must be S or L.");
        String plate = readText("Plate number: ").toUpperCase();
        String model = readText("Model: ");
        int seats = readInt("Number of seats: ");
        if (seats < 1) throw new RentalException("Seats must be at least 1.");
        system.addVan(type, plate, model, seats);
        System.out.println("Van added.");
    }

    private static void deleteVan() throws RentalException {
        system.deleteVan(readText("Plate number to delete: "));
        System.out.println("Van deleted.");
    }

    private static void viewVans() {
        if (system.getVans().isEmpty()) System.out.println("No vans.");
        for (Van v : system.getVans()) System.out.println(v);
    }

    private static void searchVan() {
        Van v = system.findVan(readText("Plate number: "));
        System.out.println(v == null ? "Van not found." : v.toString());
    }

    private static void viewCustomers() {
        if (system.getCustomers().isEmpty()) System.out.println("No customers.");
        for (Customer c : system.getCustomers()) System.out.println(c);
    }

    private static void rentVan() throws RentalException {
        String id = readText("Customer ID: ");
        String plate = readText("Van plate number: ");
        int days = readInt("Number of days: ");
        Rental r = system.rentVan(id, plate, days);
        System.out.println("\nRental created: " + r.getRentalId());
    }

    private static void returnVan() throws RentalException {
        system.returnVan(readText("Rental ID: "));
        System.out.println("Van returned. Thank you!");
    }

    private static void viewRentals() {
        if (system.getRentals().isEmpty()) System.out.println("No rentals.");
        for (Rental r : system.getRentals()) System.out.println(r);
    }

    private static void sendExpiry() {
        int count = system.sendExpirationMessages();
        System.out.println("\n" + count + " expiration message(s) sent.");
    }
    
}
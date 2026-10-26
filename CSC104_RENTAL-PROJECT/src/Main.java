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
                    case 12: viewOverdue(); break;
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
        System.out.println(" 2. Update customer details (contact, email, address, ID)");
        System.out.println(" 3. Add van");
        System.out.println(" 4. Delete van");
        System.out.println(" 5. View all vans");
        System.out.println(" 6. Search van by plate number");
        System.out.println(" 7. View all customers");
        System.out.println(" 8. Rent a van (sends email receipt)");
        System.out.println(" 9. Return a van");
        System.out.println("10. View all rentals");
        System.out.println("11. Send expiration SMS (due today/overdue)");
        System.out.println("12. Overdue rentals (with customer address and ID)");
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

    private static String readAddress() {
        while (true) {
            String address = readText("Address (use ; instead of commas, e.g. Purok 3; Obrero; Butuan City): ");
            if (address.length() >= 8) return address;
            System.out.println("Please enter the complete address.");
        }
    }

    private static String readIdType() {
        String[] types = {"Driver's License", "Passport", "UMID", "PhilSys National ID", "Postal ID", "Voter's ID"};
        System.out.println("Valid ID type:");
        for (int i = 0; i < types.length; i++) System.out.println("  " + (i + 1) + ". " + types[i]);
        while (true) {
            int pick = readInt("Choose ID type (1-" + types.length + "): ");
            if (pick >= 1 && pick <= types.length) return types[pick - 1].replace("'", "");
            System.out.println("Please choose a number from the list.");
        }
    }

    private static String readIdNumber() {
        while (true) {
            String number = readText("ID number: ").toUpperCase();
            if (number.matches("[A-Z0-9-]{5,20}")) return number;
            System.out.println("Invalid ID number. Use 5 to 20 letters, digits, or dashes.");
        }
    }

    private static void addCustomer() {
        String name = readText("Name: ");
        String contact = readContact();
        String email = readEmail();
        String address = readAddress();
        String idType = readIdType();
        String idNumber = readIdNumber();
        Customer c = system.addCustomer(name, contact, email, address, idType, idNumber);
        System.out.println("Customer added. ID: " + c.getCustomerId());
    }

    private static void updateCustomer() throws RentalException {
        String id = readText("Customer ID: ");
        String contact = readContact();
        String email = readEmail();
        String address = readAddress();
        String idType = readIdType();
        String idNumber = readIdNumber();
        system.updateCustomer(id, contact, email, address, idType, idNumber);
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
        Rental r = system.returnVan(readText("Rental ID: "));
        System.out.println("Van returned. Thank you!");
        if (r.getLateFee() > 0) {
            int late = r.getDaysLate(r.getReturnDate());
            System.out.println("Returned " + late + " day(s) late.");
            System.out.println("Late fee   : P" + Rental.LATE_FEE_PER_DAY + " x " + late + " = P" + r.getLateFee());
            System.out.println("Total to pay: P" + r.getGrandTotal());
        } else {
            System.out.println("Returned on time. No late fee.");
        }
    }

    private static void viewRentals() {
        if (system.getRentals().isEmpty()) System.out.println("No rentals.");
        for (Rental r : system.getRentals()) System.out.println(r);
    }

    private static void viewOverdue() {
        java.util.ArrayList<Rental> overdue = system.getOverdueRentals();
        if (overdue.isEmpty()) {
            System.out.println("No rentals are due today or overdue.");
            return;
        }
        for (Rental r : overdue) {
            Customer c = r.getCustomer();
            System.out.println("\n" + r);
            System.out.println("  Customer : " + c.getName() + " (" + c.getCustomerId() + ")");
            System.out.println("  Contact  : " + c.getContactNumber() + " | " + c.getEmail());
            System.out.println("  Address  : " + c.getAddress());
            System.out.println("  Valid ID : " + c.getIdType() + " " + c.getIdNumber());
            int late = r.getDaysLate(java.time.LocalDate.now());
            if (late > 0) {
                System.out.println("  Late     : " + late + " day(s), late fee so far P" + (late * Rental.LATE_FEE_PER_DAY));
            }
        }
    }

    private static void sendExpiry() {
        int count = system.sendExpirationMessages();
        System.out.println("\n" + count + " expiration message(s) sent.");
    }
    
}
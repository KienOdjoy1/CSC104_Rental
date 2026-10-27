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
                    case 2: deleteCustomer(); break;
                    case 3: updateCustomer(); break;
                    case 4: addVan(); break;
                    case 5: deleteVan(); break;
                    case 6: viewVans(); break;
                    case 7: searchVan(); break;
                    case 8: viewCustomers(); break;
                    case 9: rentVan(); break;
                    case 10: returnVan(); break;
                    case 11: viewRentals(); break;
                    case 12: sendExpiry(); break;
                    case 13: viewOverdue(); break;
                    case 14: startMaintenance(); break;
                    case 15: finishMaintenance(); break;
                    case 16: viewMaintenance(); break;
                    case 0: break;
                    default: System.out.println("Invalid choice. Try again.");
                }
            } catch (RentalException e) {
                System.out.println("Error: " + e.getMessage());
            }
            system.save();
        }
        System.out.println("Saving data... Goodbye!");
    }

    private static Employee askEmployee(String prompt) throws RentalException {
        String id = readText(prompt);
        Employee e = system.findEmployee(id);
        if (e == null) throw new RentalException("Rental staff ID not found. Valid IDs: " + system.getStaffIds() + ".");
        return e;
    }

    private static void showMenu() {
        System.out.println("\n===== PASSENGER VAN RENTAL SYSTEM =====");
        String[] items = {
            "", "Add customer", "Delete customer", "Update customer details (contact, email, address, ID)", "Add van", "Delete van",
            "View all vans (Available / Rented / Under maintenance)", "Search van by plate number (check status)", "View all customers",
            "Rent a van (sends email receipt)", "Return a van", "View all rentals",
            "Send expiration SMS (due today/overdue)", "Overdue rentals (with customer address and ID)",
            "Send van to maintenance", "Finish van maintenance",
            "View maintenance history"
        };
        for (int i = 1; i <= 16; i++) System.out.printf("%2d. %s%n", i, items[i]);
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

    private static void deleteCustomer() throws RentalException {
        String id = readText("Customer ID to delete: ");
        Customer c = system.findCustomer(id);
        if (c == null) throw new RentalException("Customer ID not found.");
        System.out.println("Customer: " + c.getCustomerId() + " - " + c.getName());
        String sure = readText("Delete this customer? (Y/N): ");
        if (!sure.equalsIgnoreCase("Y")) {
            System.out.println("Delete cancelled.");
            return;
        }
        system.deleteCustomer(id);
        System.out.println("Customer deleted.");
    }

    private static void deleteVan() throws RentalException {
        system.deleteVan(readText("Plate number to delete: "));
        System.out.println("Van deleted.");
    }

    private static void viewVans() {
        if (system.getVans().isEmpty()) {
            System.out.println("No vans.");
            return;
        }
        String[] statuses = {"Available", "Rented", "Under maintenance"};
        for (String status : statuses) {
            java.util.ArrayList<Van> list = system.getVansByStatus(status);
            System.out.println("\n--- " + status.toUpperCase() + " (" + list.size() + ") ---");
            if (list.isEmpty()) System.out.println("  none");
            for (Van v : list) System.out.println("  " + v);
        }
    }

    private static void searchVan() {
        Van v = system.findVan(readText("Plate number: "));
        if (v == null) System.out.println("Van not found.");
        else System.out.println(v + "\nStatus: " + v.getStatus() + (v.isRentable() ? " - can be rented." : " - cannot be rented."));
    }

    private static void viewCustomers() {
        if (system.getCustomers().isEmpty()) System.out.println("No customers.");
        for (Customer c : system.getCustomers()) System.out.println(c);
    }

    private static void rentVan() throws RentalException {
        Employee staff = askEmployee("Rental staff ID (staff serving the customer): ");
        String id = readText("Customer ID: ");
        if (system.findCustomer(id) == null) throw new RentalException("Customer ID not found.");

        java.util.ArrayList<Van> free = system.getRentableVans();
        if (free.isEmpty()) {
            throw new RentalException("No van is available right now (all are rented or under maintenance).");
        }
        System.out.println("\nAvailable vans:");
        for (Van v : free) System.out.println("  " + v);
        int unavailable = system.getVans().size() - free.size();
        if (unavailable > 0) System.out.println("  (" + unavailable + " other van(s) are rented or under maintenance - option 6 shows them)");

        String plate = readText("\nVan plate number: ");
        Van chosen = system.checkVanForRent(plate);
        System.out.println("Van " + chosen.getPlateNumber() + " is " + chosen.getStatus() + ".");

        int days = readInt("Number of days: ");
        Rental r = system.rentVan(id, plate, days, staff);
        System.out.println("\nRental created: " + r.getRentalId());
    }

    private static void returnVan() throws RentalException {
        Employee staff = askEmployee("Rental staff ID (staff taking the van back): ");
        Rental r = system.returnVan(readText("Rental ID: "), staff);
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

    private static void startMaintenance() throws RentalException {
        Employee staff = askEmployee("Rental staff ID (staff logging the maintenance): ");
        String plate = readText("Plate number of the van: ");
        String note = readText("What is the problem or service? ");
        MaintenanceRecord m = system.startMaintenance(plate, note, staff);
        System.out.println("Van is now under maintenance. Record: " + m.getRecordId());
    }

    private static void finishMaintenance() throws RentalException {
        MaintenanceRecord m = system.finishMaintenance(readText("Plate number of the van: "));
        System.out.println("Maintenance finished. The van can be rented again. Record: " + m.getRecordId());
    }

    private static void viewMaintenance() {
        if (system.getMaintenanceRecords().isEmpty()) System.out.println("No maintenance records.");
        for (MaintenanceRecord m : system.getMaintenanceRecords()) System.out.println(m);
    }

    private static void sendExpiry() {
        int count = system.sendExpirationMessages();
        System.out.println("\n" + count + " expiration message(s) sent.");
    }
    
}
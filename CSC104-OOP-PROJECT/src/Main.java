import java.util.List;
import java.util.Scanner;

/**
 * Console menu for the Passenger Van Rental System.
 * All errors from the service are caught here and shown as messages.
 */
public class Main {

    private static final Scanner INPUT = new Scanner(System.in);
    private static RentalService service;

    public static void main(String[] args) {
        service = new RentalService(new FileManager("data"));
        System.out.println("=== Passenger Van Rental System (Philippines) ===");

        boolean running = true;
        while (running) {
            showMenu();
            String choice = ask("Choose an option: ");
            try {
                switch (choice) {
                    case "1":  addVan(); break;
                    case "2":  viewVans(service.getAllVans()); break;
                    case "3":  updateVan(); break;
                    case "4":  deleteVan(); break;
                    case "5":  searchVans(); break;
                    case "6":  addCustomer(); break;
                    case "7":  viewCustomers(service.getAllCustomers()); break;
                    case "8":  updateCustomer(); break;
                    case "9":  deleteCustomer(); break;
                    case "10": rentVan(); break;
                    case "11": returnVan(); break;
                    case "12": viewRentals(); break;
                    case "0":
                        service.save();
                        System.out.println("Data saved. Goodbye!");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please enter a number from the menu.");
                }
            } catch (InvalidInputException | VanUnavailableException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void showMenu() {
        System.out.println();
        System.out.println("---------------- MENU ----------------");
        System.out.println(" 1  Add van             7  View customers");
        System.out.println(" 2  View vans           8  Update customer");
        System.out.println(" 3  Update van          9  Delete customer");
        System.out.println(" 4  Delete van         10  Rent a van");
        System.out.println(" 5  Search vans        11  Return a van");
        System.out.println(" 6  Add customer       12  Rental history");
        System.out.println(" 0  Save and exit");
        System.out.println("--------------------------------------");
    }

    // ================= VANS =================

    private static void addVan() throws InvalidInputException {
        System.out.println("Van type: 1 = Standard, 2 = Tourist, 3 = VIP");
        String type = ask("Type: ");
        if (!type.equals("1") && !type.equals("2") && !type.equals("3")) {
            throw new InvalidInputException("Van type must be 1, 2, or 3.");
        }
        String plate = ask("Plate number (example ABC 1234): ");
        String brand = ask("Brand: ");
        String model = ask("Model: ");
        double rate = askDouble("Daily rate in PHP: ");
        int seats = askInt("Seating capacity (8 to 30): ");

        PassengerVan van;
        if (type.equals("1")) {
            van = new StandardVan(plate, brand, model, rate, seats);
        } else if (type.equals("2")) {
            double comfort = askDouble("Comfort fee per day in PHP: ");
            van = new TouristVan(plate, brand, model, rate, seats, comfort);
        } else {
            double premium = askDouble("Premium fee per day in PHP: ");
            double driver = askDouble("Driver fee per day in PHP: ");
            van = new VIPVan(plate, brand, model, rate, seats, premium, driver);
        }
        service.addVan(van);
        System.out.println("Van added: " + van.getPlateNumber());
    }

    private static void viewVans(List<PassengerVan> vans) {
        if (vans.isEmpty()) {
            System.out.println("No vans found.");
            return;
        }
        System.out.printf("%-8s | %-12s | %-10s | %-10s | %10s | %5s | %s%n",
                "Plate", "Type", "Brand", "Model", "Rate (PHP)", "Seats", "Status");
        for (PassengerVan van : vans) {
            // Polymorphism: each van type computes its own 1-day cost
            System.out.println(van + "  (1 day total: " + String.format("%.2f", van.calculateRentalCost(1)) + ")");
        }
    }

    private static void updateVan() throws InvalidInputException {
        PassengerVan van = service.findVan(ask("Plate number of the van to update: "));
        System.out.println("Press Enter to keep the current value.");

        String brand = ask("Brand [" + van.getBrand() + "]: ");
        if (!brand.isEmpty()) {
            van.setBrand(brand);
        }
        String model = ask("Model [" + van.getModel() + "]: ");
        if (!model.isEmpty()) {
            van.setModel(model);
        }
        String rate = ask("Daily rate [" + van.getDailyRate() + "]: ");
        if (!rate.isEmpty()) {
            van.setDailyRate(parseDouble(rate));
        }
        String seats = ask("Seating capacity [" + van.getSeatingCapacity() + "]: ");
        if (!seats.isEmpty()) {
            van.setSeatingCapacity(parseInt(seats));
        }
        service.save();
        System.out.println("Van updated.");
    }

    private static void deleteVan() throws InvalidInputException, VanUnavailableException {
        service.deleteVan(ask("Plate number of the van to delete: "));
        System.out.println("Van deleted.");
    }

    private static void searchVans() {
        viewVans(service.searchVans(ask("Search (plate, brand, model, or type): ")));
    }

    // ================= CUSTOMERS =================

    private static void addCustomer() throws InvalidInputException {
        String name = ask("Full name: ");
        String license = ask("LTO driver's license number (example N01-23-456789): ");
        String contact = ask("Mobile number (example 09171234567): ");
        Customer customer = service.addCustomer(name, license, contact);
        System.out.println("Customer added with ID " + customer.getCustomerId());
    }

    private static void viewCustomers(List<Customer> customers) {
        if (customers.isEmpty()) {
            System.out.println("No customers found.");
            return;
        }
        System.out.printf("%-5s | %-20s | %-15s | %s%n", "ID", "Name", "License", "Contact");
        for (Customer customer : customers) {
            System.out.println(customer);
        }
    }

    private static void updateCustomer() throws InvalidInputException {
        Customer customer = service.findCustomer(ask("Customer ID to update: "));
        System.out.println("Press Enter to keep the current value.");

        String name = ask("Name [" + customer.getName() + "]: ");
        if (!name.isEmpty()) {
            customer.setName(name);
        }
        String license = ask("License [" + customer.getLicenseNumber() + "]: ");
        if (!license.isEmpty()) {
            customer.setLicenseNumber(license);
        }
        String contact = ask("Contact [" + customer.getContactNumber() + "]: ");
        if (!contact.isEmpty()) {
            customer.setContactNumber(contact);
        }
        service.save();
        System.out.println("Customer updated.");
    }

    private static void deleteCustomer() throws InvalidInputException, VanUnavailableException {
        service.deleteCustomer(ask("Customer ID to delete: "));
        System.out.println("Customer deleted.");
    }

    // ================= RENTALS =================

    private static void rentVan() throws InvalidInputException, VanUnavailableException {
        String plate = ask("Plate number of the van (example ABC 1234): ");
        String customerId = ask("Customer ID: ");
        int days = askInt("Number of days: ");

        Rental rental = service.rentVan(plate, customerId, days);
        System.out.println();
        System.out.println("------------- RENTAL RECEIPT -------------");
        System.out.println("Rental ID : " + rental.getRentalId());
        System.out.println("Customer  : " + rental.getCustomer().getName());
        System.out.println("Van       : " + rental.getVan().getPlateNumber()
                + " (" + rental.getVan().getVanType() + ")");
        System.out.println("Start date: " + rental.getStartDate());
        System.out.println("Days      : " + rental.getDays());
        System.out.printf("Total cost: PHP %.2f%n", rental.getTotalCost());
        System.out.println("Note      : Fuel and toll fees are paid by the customer.");
        System.out.println("------------------------------------------");
    }

    private static void returnVan() throws InvalidInputException {
        Rental rental = service.returnVan(ask("Rental ID: "));
        System.out.println("Van " + rental.getVan().getPlateNumber() + " returned. Rental "
                + rental.getRentalId() + " is closed.");
    }

    private static void viewRentals() {
        List<Rental> rentals = service.getAllRentals();
        if (rentals.isEmpty()) {
            System.out.println("No rentals yet.");
            return;
        }
        System.out.printf("%-5s | %-18s | %-8s | %-10s | %3s | %14s | %s%n",
                "ID", "Customer", "Plate", "Start", "Day", "Total (PHP)", "Status");
        for (Rental rental : rentals) {
            System.out.println(rental);
        }
    }

    // ================= INPUT HELPERS =================

    private static String ask(String prompt) {
        System.out.print(prompt);
        if (!INPUT.hasNextLine()) {
            // End of input (for example, when tests pipe input in): save and stop
            service.save();
            System.out.println();
            System.exit(0);
        }
        return INPUT.nextLine().trim();
    }

    private static int askInt(String prompt) throws InvalidInputException {
        return parseInt(ask(prompt));
    }

    private static double askDouble(String prompt) throws InvalidInputException {
        return parseDouble(ask(prompt));
    }

    private static int parseInt(String text) throws InvalidInputException {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("\"" + text + "\" is not a valid whole number.");
        }
    }

    private static double parseDouble(String text) throws InvalidInputException {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("\"" + text + "\" is not a valid number.");
        }
    }
}

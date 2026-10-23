import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Main business logic. Holds the collections (ArrayList) of vans, customers,
 * and rentals, and does CRUD, search, renting, and returning.
 */
public class RentalService {

    private final List<PassengerVan> vans;
    private final List<Customer> customers;
    private final List<Rental> rentals;
    private final FileManager fileManager;

    public RentalService(FileManager fileManager) {
        this.fileManager = fileManager;
        // Load saved data when the program starts
        this.vans = new ArrayList<>(fileManager.loadVans());
        this.customers = new ArrayList<>(fileManager.loadCustomers());
        this.rentals = new ArrayList<>(fileManager.loadRentals(vans, customers));
    }

    // ================= VANS (CRUD) =================

    public void addVan(PassengerVan van) throws InvalidInputException {
        if (findVanOrNull(van.getPlateNumber()) != null) {
            throw new InvalidInputException("A van with plate " + van.getPlateNumber() + " already exists.");
        }
        vans.add(van);
        save();
    }

    public List<PassengerVan> getAllVans() {
        return new ArrayList<>(vans);
    }

    public PassengerVan findVan(String plate) throws InvalidInputException {
        PassengerVan van = findVanOrNull(plate);
        if (van == null) {
            throw new InvalidInputException("No van found with plate " + plate + ".");
        }
        return van;
    }

    public void deleteVan(String plate) throws InvalidInputException, VanUnavailableException {
        PassengerVan van = findVan(plate);
        if (!van.isAvailable()) {
            throw new VanUnavailableException("Cannot delete " + van.getPlateNumber() + ", it is currently rented.");
        }
        vans.remove(van);
        save();
    }

    public List<PassengerVan> searchVans(String keyword) {
        List<PassengerVan> result = new ArrayList<>();
        String key = keyword.trim().toLowerCase();
        for (PassengerVan van : vans) {
            if (van.getPlateNumber().toLowerCase().contains(key)
                    || van.getBrand().toLowerCase().contains(key)
                    || van.getModel().toLowerCase().contains(key)
                    || van.getVanType().toLowerCase().contains(key)) {
                result.add(van);
            }
        }
        return result;
    }

    // ================= CUSTOMERS (CRUD) =================

    public Customer addCustomer(String name, String license, String contact) throws InvalidInputException {
        Customer customer = new Customer(nextId("C", customers.size() == 0 ? 0 : maxCustomerNumber()),
                name, license, contact);
        customers.add(customer);
        save();
        return customer;
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers);
    }

    public Customer findCustomer(String id) throws InvalidInputException {
        Customer customer = findCustomerOrNull(id);
        if (customer == null) {
            throw new InvalidInputException("No customer found with ID " + id + ".");
        }
        return customer;
    }

    public void deleteCustomer(String id) throws InvalidInputException, VanUnavailableException {
        Customer customer = findCustomer(id);
        for (Rental rental : rentals) {
            if (rental.getCustomer() == customer && !rental.isReturned()) {
                throw new VanUnavailableException("Cannot delete " + customer.getName()
                        + ", this customer still has an active rental.");
            }
        }
        customers.remove(customer);
        // Remove this customer's old rental records so saved files stay consistent
        rentals.removeIf(r -> r.getCustomer() == customer);
        save();
    }

    public List<Customer> searchCustomers(String keyword) {
        List<Customer> result = new ArrayList<>();
        String key = keyword.trim().toLowerCase();
        for (Customer c : customers) {
            if (c.getName().toLowerCase().contains(key)
                    || c.getCustomerId().toLowerCase().contains(key)
                    || c.getLicenseNumber().toLowerCase().contains(key)) {
                result.add(c);
            }
        }
        return result;
    }

    // ================= RENTALS =================

    public Rental rentVan(String plate, String customerId, int days)
            throws InvalidInputException, VanUnavailableException {
        if (days < 1 || days > 365) {
            throw new InvalidInputException("Number of days must be from 1 to 365.");
        }
        PassengerVan van = findVan(plate);
        Customer customer = findCustomer(customerId);

        van.rent(); // throws VanUnavailableException if already rented
        double cost = van.calculateRentalCost(days); // polymorphic call

        Rental rental = new Rental(nextId("R", maxRentalNumber()), customer, van,
                LocalDate.now(), days, cost, false);
        rentals.add(rental);
        save();
        return rental;
    }

    public Rental returnVan(String rentalId) throws InvalidInputException {
        Rental rental = null;
        for (Rental r : rentals) {
            if (r.getRentalId().equalsIgnoreCase(rentalId.trim())) {
                rental = r;
            }
        }
        if (rental == null) {
            throw new InvalidInputException("No rental found with ID " + rentalId + ".");
        }
        if (rental.isReturned()) {
            throw new InvalidInputException("Rental " + rental.getRentalId() + " was already returned.");
        }
        rental.markReturned();
        save();
        return rental;
    }

    public List<Rental> getAllRentals() {
        return new ArrayList<>(rentals);
    }

    // ================= SAVING =================

    /** Saves all data to files. Called after every change. */
    public void save() {
        try {
            fileManager.saveVans(vans);
            fileManager.saveCustomers(customers);
            fileManager.saveRentals(rentals);
        } catch (IOException e) {
            System.out.println("Warning: could not save data. " + e.getMessage());
        }
    }

    // ================= HELPERS =================

    private PassengerVan findVanOrNull(String plate) {
        String key;
        try {
            key = PassengerVan.normalizePlate(plate);
        } catch (InvalidInputException e) {
            key = plate.trim().toUpperCase();
        }
        for (PassengerVan van : vans) {
            if (van.getPlateNumber().equals(key)) {
                return van;
            }
        }
        return null;
    }

    private Customer findCustomerOrNull(String id) {
        for (Customer c : customers) {
            if (c.getCustomerId().equalsIgnoreCase(id.trim())) {
                return c;
            }
        }
        return null;
    }

    private int maxCustomerNumber() {
        int max = 0;
        for (Customer c : customers) {
            max = Math.max(max, numberPart(c.getCustomerId()));
        }
        return max;
    }

    private int maxRentalNumber() {
        int max = 0;
        for (Rental r : rentals) {
            max = Math.max(max, numberPart(r.getRentalId()));
        }
        return max;
    }

    private int numberPart(String id) {
        try {
            return Integer.parseInt(id.substring(1));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String nextId(String prefix, int currentMax) {
        return String.format("%s%03d", prefix, currentMax + 1);
    }
}

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles file persistence. Saves and loads vans, customers, and rentals
 * as CSV text files inside the "data" folder.
 */
public class FileManager {

    private final Path folder;
    private final Path vansFile;
    private final Path customersFile;
    private final Path rentalsFile;

    public FileManager(String folderName) {
        this.folder = Paths.get(folderName);
        this.vansFile = folder.resolve("vans.csv");
        this.customersFile = folder.resolve("customers.csv");
        this.rentalsFile = folder.resolve("rentals.csv");
    }

    // ---------- Save ----------

    public void saveVans(List<PassengerVan> vans) throws IOException {
        List<String> lines = new ArrayList<>();
        for (PassengerVan van : vans) {
            lines.add(van.toCsvLine());
        }
        writeLines(vansFile, lines);
    }

    public void saveCustomers(List<Customer> customers) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Customer customer : customers) {
            lines.add(customer.toCsvLine());
        }
        writeLines(customersFile, lines);
    }

    public void saveRentals(List<Rental> rentals) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Rental rental : rentals) {
            lines.add(rental.toCsvLine());
        }
        writeLines(rentalsFile, lines);
    }

    private void writeLines(Path file, List<String> lines) throws IOException {
        Files.createDirectories(folder);
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    // ---------- Load ----------

    public List<PassengerVan> loadVans() {
        List<PassengerVan> vans = new ArrayList<>();
        for (String line : readLines(vansFile)) {
            try {
                vans.add(parseVan(line));
            } catch (InvalidInputException | RuntimeException e) {
                System.out.println("Skipped a bad line in vans.csv: " + line);
            }
        }
        return vans;
    }

    public List<Customer> loadCustomers() {
        List<Customer> customers = new ArrayList<>();
        for (String line : readLines(customersFile)) {
            try {
                String[] p = line.split(",", -1);
                customers.add(new Customer(p[0], p[1], p[2], p[3]));
            } catch (InvalidInputException | RuntimeException e) {
                System.out.println("Skipped a bad line in customers.csv: " + line);
            }
        }
        return customers;
    }

    public List<Rental> loadRentals(List<PassengerVan> vans, List<Customer> customers) {
        List<Rental> rentals = new ArrayList<>();
        for (String line : readLines(rentalsFile)) {
            try {
                String[] p = line.split(",", -1);
                Customer customer = findCustomer(customers, p[1]);
                PassengerVan van = findVan(vans, p[2]);
                if (customer == null || van == null) {
                    throw new InvalidInputException("Missing customer or van.");
                }
                rentals.add(new Rental(p[0], customer, van, LocalDate.parse(p[3]),
                        Integer.parseInt(p[4]), Double.parseDouble(p[5]),
                        Boolean.parseBoolean(p[6])));
            } catch (InvalidInputException | RuntimeException e) {
                System.out.println("Skipped a bad line in rentals.csv: " + line);
            }
        }
        return rentals;
    }

    /** Builds the correct van object from a line (polymorphism in action). */
    private PassengerVan parseVan(String line) throws InvalidInputException {
        String[] p = line.split(",", -1);
        String type = p[0];
        String plate = p[1];
        String brand = p[2];
        String model = p[3];
        double rate = Double.parseDouble(p[4]);
        int seats = Integer.parseInt(p[5]);
        boolean available = Boolean.parseBoolean(p[6]);
        double extra1 = Double.parseDouble(p[7]);
        double extra2 = Double.parseDouble(p[8]);

        PassengerVan van;
        switch (type) {
            case "StandardVan":
                van = new StandardVan(plate, brand, model, rate, seats);
                break;
            case "TouristVan":
                van = new TouristVan(plate, brand, model, rate, seats, extra1);
                break;
            case "VIPVan":
                van = new VIPVan(plate, brand, model, rate, seats, extra1, extra2);
                break;
            default:
                throw new InvalidInputException("Unknown van type: " + type);
        }
        van.setAvailable(available);
        return van;
    }

    private List<String> readLines(Path file) {
        List<String> lines = new ArrayList<>();
        if (!Files.exists(file)) {
            return lines; // first run: no file yet
        }
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read " + file.getFileName() + ": " + e.getMessage());
        }
        return lines;
    }

    private Customer findCustomer(List<Customer> customers, String id) {
        for (Customer c : customers) {
            if (c.getCustomerId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    private PassengerVan findVan(List<PassengerVan> vans, String plate) {
        for (PassengerVan v : vans) {
            if (v.getPlateNumber().equals(plate)) {
                return v;
            }
        }
        return null;
    }
}

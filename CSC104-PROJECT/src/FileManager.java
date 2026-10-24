import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class FileManager {
    private static final String VANS_FILE = "vans.txt";
    private static final String CUSTOMERS_FILE = "customers.txt";
    private static final String RENTALS_FILE = "rentals.txt";

    public void saveAll(ArrayList<Van> vans, ArrayList<Customer> customers, ArrayList<Rental> rentals) {
    	
        try (PrintWriter out = new PrintWriter(new FileWriter(VANS_FILE))) {
            for (Van v : vans) out.println(v.toFileString());
        } catch (IOException e) {
            System.out.println("Error saving vans: " + e.getMessage());
        }
        
        try (PrintWriter out = new PrintWriter(new FileWriter(CUSTOMERS_FILE))) {
            for (Customer c : customers) out.println(c.toFileString());
        } catch (IOException e) {
            System.out.println("Error saving customers: " + e.getMessage());
        }
        
        try (PrintWriter out = new PrintWriter(new FileWriter(RENTALS_FILE))) {
            for (Rental r : rentals) out.println(r.toFileString());
        } catch (IOException e) {
            System.out.println("Error saving rentals: " + e.getMessage());
        }
    }

    public ArrayList<Van> loadVans() {
    	
        ArrayList<Van> vans = new ArrayList<>();
        File file = new File(VANS_FILE);
        
        if (!file.exists()) return vans;
        
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] p = line.split(",");
                Van van;
                if (p[0].equals("Luxury")) {
                    van = new LuxuryVan(p[1], p[2], Integer.parseInt(p[3]));
                } else {
                    van = new StandardVan(p[1], p[2], Integer.parseInt(p[3]));
                }
                van.setAvailable(Boolean.parseBoolean(p[4]));
                vans.add(van);
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading vans: " + e.getMessage());
        }
        
        return vans;
    }

    public ArrayList<Customer> loadCustomers() {
    	
        ArrayList<Customer> customers = new ArrayList<>();
        File file = new File(CUSTOMERS_FILE);
        if (!file.exists()) return customers;
        
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] p = line.split(",");
                customers.add(new Customer(p[0], p[1], p[2], p[3]));
            }
        } catch (IOException e) {
            System.out.println("Error loading customers: " + e.getMessage());
        }
        
        return customers;
    }

    public ArrayList<Rental> loadRentals(ArrayList<Van> vans, ArrayList<Customer> customers) {
    	
        ArrayList<Rental> rentals = new ArrayList<>();
        File file = new File(RENTALS_FILE);
        if (!file.exists()) return rentals;
        
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] p = line.split(",");
                Customer customer = null;
                Van van = null;
                for (Customer c : customers) if (c.getCustomerId().equals(p[1])) customer = c;
                for (Van v : vans) if (v.getPlateNumber().equals(p[2])) van = v;
                if (customer == null || van == null) continue; // skip broken records
                Rental r = new Rental(p[0], customer, van, LocalDate.parse(p[3]), Integer.parseInt(p[4]));
                r.setTotalFee(Double.parseDouble(p[5]));
                r.setReturned(Boolean.parseBoolean(p[6]));
                rentals.add(r);
            }
        } catch (IOException | RuntimeException e) {
            System.out.println("Error loading rentals: " + e.getMessage());
        }
        
        return rentals;
    }
    
}
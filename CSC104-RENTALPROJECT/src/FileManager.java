import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class FileManager {
    private static final String VANS_FILE = "vans.txt";
    private static final String CUSTOMERS_FILE = "customers.txt";
    private static final String RENTALS_FILE = "rentals.txt";
    private static final String EMPLOYEES_FILE = "employees.txt";
    private static final String MAINTENANCE_FILE = "maintenance.txt";

    public void saveAll(ArrayList<Van> vans, ArrayList<Customer> customers, ArrayList<Rental> rentals, ArrayList<Employee> employees, ArrayList<MaintenanceRecord> records) {
    	
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

        try (PrintWriter out = new PrintWriter(new FileWriter(EMPLOYEES_FILE))) {
            for (Employee e : employees) out.println(e.toFileString());
        } catch (IOException e) {
            System.out.println("Error saving employees: " + e.getMessage());
        }

        try (PrintWriter out = new PrintWriter(new FileWriter(MAINTENANCE_FILE))) {
            for (MaintenanceRecord m : records) out.println(m.toFileString());
        } catch (IOException e) {
            System.out.println("Error saving maintenance records: " + e.getMessage());
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
                if (p.length >= 6) van.setUnderMaintenance(Boolean.parseBoolean(p[5]));
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
                if (p.length >= 7) {
                    customers.add(new Customer(p[0], p[1], p[2], p[3], p[4], p[5], p[6]));
                } else {
                    String none = Customer.NOT_PROVIDED;
                    customers.add(new Customer(p[0], p[1], p[2], p[3], none, none, none));
                }
            }
        } catch (IOException | RuntimeException e) {
            System.out.println("Error loading customers: " + e.getMessage());
        }
        
        return customers;
    }

    public ArrayList<Rental> loadRentals(ArrayList<Van> vans, ArrayList<Customer> customers, ArrayList<Employee> employees) {
    	
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
                if (customer == null || van == null) continue;
                Rental r = new Rental(p[0], customer, van, LocalDate.parse(p[3]), Integer.parseInt(p[4]),
                        findEmployee(employees, p.length >= 10 ? p[9] : "none"));
                r.setTotalFee(Double.parseDouble(p[5]));
                r.setReturned(Boolean.parseBoolean(p[6]));
                if (p.length >= 9 && !p[7].equals("none")) {
                    r.restoreReturnInfo(LocalDate.parse(p[7]), Double.parseDouble(p[8]),
                            findEmployee(employees, p.length >= 11 ? p[10] : "none"));
                }
                rentals.add(r);
            }
        } catch (IOException | RuntimeException e) {
            System.out.println("Error loading rentals: " + e.getMessage());
        }
        
        return rentals;
    }

    private static Employee findEmployee(ArrayList<Employee> employees, String id) {
        for (Employee e : employees) if (e.getEmployeeId().equals(id)) return e;
        return null;
    }

    public ArrayList<Employee> loadEmployees() {

        ArrayList<Employee> employees = new ArrayList<>();
        File file = new File(EMPLOYEES_FILE);
        if (!file.exists()) return employees;

        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length < 4) continue;
                employees.add(new Employee(p[0], p[1], p[2], p[3]));
            }
        } catch (IOException e) {
            System.out.println("Error loading employees: " + e.getMessage());
        }

        return employees;
    }

    public ArrayList<MaintenanceRecord> loadMaintenance(ArrayList<Van> vans, ArrayList<Employee> employees) {

        ArrayList<MaintenanceRecord> records = new ArrayList<>();
        File file = new File(MAINTENANCE_FILE);
        if (!file.exists()) return records;

        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length < 6) continue;
                Van van = null;
                for (Van v : vans) if (v.getPlateNumber().equals(p[1])) van = v;
                Employee staff = findEmployee(employees, p[2]);
                if (van == null || staff == null) continue;
                MaintenanceRecord m = new MaintenanceRecord(p[0], van, staff, p[3], LocalDate.parse(p[4]));
                if (!p[5].equals("none")) m.restoreEndDate(LocalDate.parse(p[5]));
                records.add(m);
            }
        } catch (IOException | RuntimeException e) {
            System.out.println("Error loading maintenance records: " + e.getMessage());
        }

        return records;
    }

}

import java.time.LocalDate;
import java.util.ArrayList;

public class RentalSystem {
    private ArrayList<Van> vans;
    private ArrayList<Customer> customers;
    private ArrayList<Rental> rentals;
    private ArrayList<Employee> employees;
    private ArrayList<MaintenanceRecord> maintenanceRecords;
    private FileManager fileManager = new FileManager();
    private Notification email = new EmailNotification(); 
    private Notification sms = new SmsNotification();

    public RentalSystem() {
        vans = fileManager.loadVans();
        customers = fileManager.loadCustomers();
        employees = fileManager.loadEmployees();
        rentals = fileManager.loadRentals(vans, customers, employees);
        maintenanceRecords = fileManager.loadMaintenance(vans, employees);
        syncVanStatus();
        if (employees.isEmpty()) {
            employees.add(new Manager("E1", "Store Manager", "09170000001", "manager@vanrental.ph", "1234"));
            employees.add(new CounterStaff("E2", "Counter Staff", "09170000002", "counter@vanrental.ph", "1111"));
            employees.add(new Mechanic("E3", "Van Mechanic", "09170000003", "mechanic@vanrental.ph", "2222"));
        }
        if (vans.isEmpty()) {
            vans.add(new StandardVan("ABC-123", "Toyota HiAce Commuter", 15));
            vans.add(new StandardVan("DEF-456", "Nissan Urvan", 15));
            vans.add(new LuxuryVan("GHI-789", "Toyota HiAce Grandia", 10));
        }
    }

    private void syncVanStatus() {
        for (Rental rental : rentals) {
            if (!rental.isReturned()) rental.getVan().setAvailable(false);
        }
    }

    public ArrayList<Van> getVansByStatus(String status) {
        ArrayList<Van> list = new ArrayList<>();
        for (Van v : vans) if (v.getStatus().equals(status)) list.add(v);
        return list;
    }

    public ArrayList<Van> getRentableVans() {
        ArrayList<Van> list = new ArrayList<>();
        for (Van v : vans) if (v.isRentable()) list.add(v);
        return list;
    }

    public Van checkVanForRent(String plate) throws RentalException {
        Van v = findVan(plate);
        if (v == null) throw new RentalException("Van not found.");
        if (v.isUnderMaintenance()) throw new RentalException("Van " + v.getPlateNumber() + " is NOT AVAILABLE: it is under maintenance.");
        if (!v.isAvailable()) throw new RentalException("Van " + v.getPlateNumber() + " is NOT AVAILABLE: it is already rented.");
        return v;
    }

    public void save() { 
    	fileManager.saveAll(vans, customers, rentals, employees, maintenanceRecords); 
    }

    public Customer addCustomer(String name, String contact, String emailAddress, String address, String idType, String idNumber) {
        Customer c = new Customer("C" + (customers.size() + 1), name, contact, emailAddress, address, idType, idNumber);
        customers.add(c);
        return c;
    }

    public void addVan(String type, String plate, String model, int seats) throws RentalException {
        if (findVan(plate) != null) throw new RentalException("A van with that plate number already exists.");
        if (type.equals("L")) vans.add(new LuxuryVan(plate, model, seats));
        else vans.add(new StandardVan(plate, model, seats));
    }

    public Van findVan(String plate) {
        for (Van v : vans) if (v.getPlateNumber().equalsIgnoreCase(plate)) return v;
        return null;
    }

    public Customer findCustomer(String id) {
        for (Customer c : customers) if (c.getCustomerId().equalsIgnoreCase(id)) return c;
        return null;
    }

    public Rental findRental(String id) {
        for (Rental r : rentals) if (r.getRentalId().equalsIgnoreCase(id)) return r;
        return null;
    }

    public ArrayList<Van> getVans() { 
    	return vans; 
    }
    
    public ArrayList<Customer> getCustomers() { 
    	return customers; 
    }
    
    public ArrayList<Rental> getRentals() { 
    	return rentals; 
    }

    public void updateCustomer(String id, String newContact, String newEmail, String newAddress, String newIdType, String newIdNumber) throws RentalException {
        Customer c = findCustomer(id);
        if (c == null) throw new RentalException("Customer ID not found.");
        c.setContactNumber(newContact);
        c.setEmail(newEmail);
        c.setAddress(newAddress);
        c.setIdType(newIdType);
        c.setIdNumber(newIdNumber);
    }

    public ArrayList<Rental> getOverdueRentals() {
        ArrayList<Rental> overdue = new ArrayList<>();
        for (Rental r : rentals) {
            if (r.isExpiring()) overdue.add(r);
        }
        return overdue;
    }

    public void deleteVan(String plate) throws RentalException {
        Van v = findVan(plate);
        if (v == null) throw new RentalException("Van not found.");
        if (!v.isAvailable()) throw new RentalException("Cannot delete a van that is currently rented.");
        if (v.isUnderMaintenance()) throw new RentalException("Cannot delete a van that is under maintenance.");
        vans.remove(v);
    }

    public Rental rentVan(String customerId, String plate, int days, Employee by) throws RentalException {
        Customer c = findCustomer(customerId);
        if (c == null) throw new RentalException("Customer ID not found.");
        if (!c.hasProofOfIdentity()) {
            throw new RentalException("This customer has no address or valid ID on file. Update the customer first (option 2).");
        }
        Van v = checkVanForRent(plate);
        if (days < 1) throw new RentalException("Rental days must be at least 1.");

        Rental r = new Rental("R" + (rentals.size() + 1), c, v, LocalDate.now(), days, by);
        v.setAvailable(false);
        rentals.add(r);
        sendReceipt(r);
        return r;
    }

    public Rental returnVan(String rentalId, Employee by) throws RentalException {
        Rental r = findRental(rentalId);
        if (r == null) throw new RentalException("Rental ID not found.");
        if (r.isReturned()) throw new RentalException("This van was already returned.");
        r.markReturned(LocalDate.now(), by);
        r.getVan().setAvailable(true);
        return r;
    }

    private void sendReceipt(Rental r) {
        String message = "Hello " + r.getCustomer().getName() + ",\n"
                + "Thank you for renting with us! Here is your receipt.\n"
                + "Rental ID : " + r.getRentalId() + "\n"
                + "Van       : " + r.getVan().getModel() + " (" + r.getVan().getPlateNumber() + ")\n"
                + "Start     : " + r.getStartDate() + "\n"
                + "Return by : " + r.getEndDate() + "\n"
                + "Days      : " + r.getDays() + "\n"
                + "Total fee : P" + r.getTotalFee() + "\n"
                + "Served by : " + (r.getRentedBy() == null ? "-" : r.getRentedBy().getName() + " (" + r.getRentedBy().getRole() + ")");
        email.send(r.getCustomer().getEmail(), message);
    }

    public int sendExpirationMessages() {
        int count = 0;
        for (Rental r : rentals) {
            if (r.isExpiring()) {
                String message = "Hi " + r.getCustomer().getName() + ", your rental " + r.getRentalId()
                        + " for van " + r.getVan().getPlateNumber() + " expires on "
                        + r.getEndDate() + ". Please return the van.";
                sms.send(r.getCustomer().getContactNumber(), message);
                count++;
            }
        }
        return count;
    }

    public ArrayList<Employee> getEmployees() {
        return employees;
    }

    public ArrayList<MaintenanceRecord> getMaintenanceRecords() {
        return maintenanceRecords;
    }

    public Employee findEmployee(String id) {
        for (Employee e : employees) if (e.getEmployeeId().equalsIgnoreCase(id)) return e;
        return null;
    }

    public Employee login(String id, String pin) throws RentalException {
        Employee e = findEmployee(id);
        if (e == null || !e.checkPin(pin)) throw new RentalException("Wrong employee ID or PIN.");
        return e;
    }

    public Employee addEmployee(String role, String name, String contact, String emailAddress, String pin) {
        String id = "E" + (employees.size() + 1);
        Employee e;
        if (role.equals("M")) e = new Manager(id, name, contact, emailAddress, pin);
        else if (role.equals("X")) e = new Mechanic(id, name, contact, emailAddress, pin);
        else e = new CounterStaff(id, name, contact, emailAddress, pin);
        employees.add(e);
        return e;
    }

    public MaintenanceRecord startMaintenance(String plate, String note, Employee mechanic) throws RentalException {
        Van v = findVan(plate);
        if (v == null) throw new RentalException("Van not found.");
        if (v.isUnderMaintenance()) throw new RentalException("That van is already under maintenance.");
        if (!v.isAvailable()) throw new RentalException("That van is currently rented. Wait until it is returned.");
        MaintenanceRecord m = new MaintenanceRecord("M" + (maintenanceRecords.size() + 1), v, mechanic, note, LocalDate.now());
        v.setUnderMaintenance(true);
        maintenanceRecords.add(m);
        return m;
    }

    public MaintenanceRecord finishMaintenance(String plate) throws RentalException {
        Van v = findVan(plate);
        if (v == null) throw new RentalException("Van not found.");
        for (MaintenanceRecord m : maintenanceRecords) {
            if (m.isOpen() && m.getVan() == v) {
                m.finish(LocalDate.now());
                v.setUnderMaintenance(false);
                return m;
            }
        }
        throw new RentalException("That van is not under maintenance.");
    }

}

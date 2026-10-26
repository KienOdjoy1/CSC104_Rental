import java.time.LocalDate;
import java.util.ArrayList;

public class RentalSystem {
    private ArrayList<Van> vans;
    private ArrayList<Customer> customers;
    private ArrayList<Rental> rentals;
    private FileManager fileManager = new FileManager();
    private Notification email = new EmailNotification(); 
    private Notification sms = new SmsNotification();

    public RentalSystem() {
        vans = fileManager.loadVans();
        customers = fileManager.loadCustomers();
        rentals = fileManager.loadRentals(vans, customers);
        if (vans.isEmpty()) {
            vans.add(new StandardVan("ABC-123", "Toyota HiAce Commuter", 15));
            vans.add(new StandardVan("DEF-456", "Nissan Urvan", 15));
            vans.add(new LuxuryVan("GHI-789", "Toyota HiAce Grandia", 10));
        }
    }

    public void save() { 
    	fileManager.saveAll(vans, customers, rentals); 
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
        vans.remove(v);
    }

    public Rental rentVan(String customerId, String plate, int days) throws RentalException {
        Customer c = findCustomer(customerId);
        if (c == null) throw new RentalException("Customer ID not found.");
        if (!c.hasProofOfIdentity()) {
            throw new RentalException("This customer has no address or valid ID on file. Update the customer first (option 2).");
        }
        Van v = findVan(plate);
        if (v == null) throw new RentalException("Van not found.");
        if (!v.isAvailable()) throw new RentalException("That van is already rented.");
        if (days < 1) throw new RentalException("Rental days must be at least 1.");

        Rental r = new Rental("R" + (rentals.size() + 1), c, v, LocalDate.now(), days);
        v.setAvailable(false);
        rentals.add(r);
        sendReceipt(r);
        return r;
    }

    public Rental returnVan(String rentalId) throws RentalException {
        Rental r = findRental(rentalId);
        if (r == null) throw new RentalException("Rental ID not found.");
        if (r.isReturned()) throw new RentalException("This van was already returned.");
        r.markReturned(LocalDate.now());
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
                + "Total fee : P" + r.getTotalFee();
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
    
}
/**
 * A person who rents a van. All fields are private and validated.
 */
public class Customer {

    private String customerId;
    private String name;
    private String licenseNumber;
    private String contactNumber;

    public Customer(String customerId, String name, String licenseNumber,
                    String contactNumber) throws InvalidInputException {
        this.customerId = customerId;
        setName(name);
        setLicenseNumber(licenseNumber);
        setContactNumber(contactNumber);
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws InvalidInputException {
        this.name = PassengerVan.cleanText(name, "Name");
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) throws InvalidInputException {
        this.licenseNumber = PassengerVan.cleanText(licenseNumber, "License number").toUpperCase();
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) throws InvalidInputException {
        String cleaned = PassengerVan.cleanText(contactNumber, "Contact number");
        if (!cleaned.matches("\\d{10,11}")) {
            throw new InvalidInputException("Contact number must have 10 or 11 digits.");
        }
        this.contactNumber = cleaned;
    }

    /** Line used to save this customer in customers.csv. */
    public String toCsvLine() {
        return customerId + "," + name + "," + licenseNumber + "," + contactNumber;
    }

    @Override
    public String toString() {
        return String.format("%-5s | %-20s | %-15s | %s",
                customerId, name, licenseNumber, contactNumber);
    }
}

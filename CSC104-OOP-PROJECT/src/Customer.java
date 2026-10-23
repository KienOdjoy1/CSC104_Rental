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
        String cleaned = PassengerVan.cleanText(licenseNumber, "License number")
                .toUpperCase().replaceAll("[\\s-]", "");
        if (!cleaned.matches("[A-Z]\\d{2}\\d{2}\\d{6}")) {
            throw new InvalidInputException("LTO license number must look like N01-23-456789.");
        }
        this.licenseNumber = cleaned.substring(0, 3) + "-" + cleaned.substring(3, 5) + "-" + cleaned.substring(5);
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) throws InvalidInputException {
        String cleaned = PassengerVan.cleanText(contactNumber, "Contact number").replaceAll("[\\s-]", "");
        if (cleaned.startsWith("+63")) {
            cleaned = "0" + cleaned.substring(3);
        }
        if (!cleaned.matches("09\\d{9}")) {
            throw new InvalidInputException("Contact number must be a Philippine mobile number like 09171234567.");
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

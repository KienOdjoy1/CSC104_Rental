public class Customer extends Person {
    private String customerId;
    private String address;
    private String idType;
    private String idNumber;

    public static final String NOT_PROVIDED = "Not provided";

    public Customer(String customerId, String name, String contactNumber, String email, String address, String idType, String idNumber) {
        super(name, contactNumber, email);
        this.customerId = customerId;
        this.address = address;
        this.idType = idType;
        this.idNumber = idNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getAddress() {
        return address;
    }

    public String getIdType() {
        return idType;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public boolean hasProofOfIdentity() {
        return !NOT_PROVIDED.equals(address) && !NOT_PROVIDED.equals(idType) && !NOT_PROVIDED.equals(idNumber);
    }

    public String getMaskedIdNumber() {
        if (idNumber.length() <= 4) return idNumber;
        return "****" + idNumber.substring(idNumber.length() - 4);
    }

    @Override
    public String getRole() {
        return "Customer";
    }

    public String toFileString() {
        return customerId + "," + getName() + "," + getContactNumber() + "," + getEmail()
                + "," + address + "," + idType + "," + idNumber;
    }

    @Override
    public String toString() {
        return customerId + " | " + getName() + " | " + getContactNumber() + " | " + getEmail()
                + " | " + address + " | " + idType + " " + getMaskedIdNumber();
    }

}

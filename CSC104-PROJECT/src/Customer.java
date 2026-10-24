public class Customer extends Person {
    private String customerId;

    public Customer(String customerId, String name, String contactNumber, String email) {
        super(name, contactNumber, email);
        this.customerId = customerId;
    }

    public String getCustomerId() { 
    	return customerId; 
    }

    @Override
    public String getRole() { 
    	return "Customer"; 
    }

    public String toFileString() {
        return customerId + "," + getName() + "," + getContactNumber() + "," + getEmail();
    }

    @Override
    public String toString() {
        return customerId + " | " + getName() + " | " + getContactNumber() + " | " + getEmail();
    }
    
}
public abstract class Employee extends Person {
    private String employeeId;
    private String pin;

    public Employee(String employeeId, String name, String contactNumber, String email, String pin) {
        super(name, contactNumber, email);
        this.employeeId = employeeId;
        this.pin = pin;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public boolean checkPin(String enteredPin) {
        return pin.equals(enteredPin);
    }

    public abstract boolean canManageCustomers();
    public abstract boolean canRentAndReturn();
    public abstract boolean canManageVans();
    public abstract boolean canSeeCustomerIds();
    public abstract boolean canManageEmployees();
    public abstract boolean canDoMaintenance();

    public String toFileString() {
        return employeeId + "," + getName() + "," + getContactNumber() + "," + getEmail()
                + "," + getRole() + "," + pin;
    }

    @Override
    public String toString() {
        return employeeId + " | " + getName() + " | " + getRole() + " | " + getContactNumber() + " | " + getEmail();
    }

}

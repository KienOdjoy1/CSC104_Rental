public class Employee extends Person {
    private String employeeId;

    public Employee(String employeeId, String name, String contactNumber, String email) {
        super(name, contactNumber, email);
        this.employeeId = employeeId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    @Override
    public String getRole() {
        return "Rental Staff";
    }

    public String toFileString() {
        return employeeId + "," + getName() + "," + getContactNumber() + "," + getEmail();
    }

    @Override
    public String toString() {
        return employeeId + " | " + getName() + " | " + getRole() + " | " + getContactNumber() + " | " + getEmail();
    }

}

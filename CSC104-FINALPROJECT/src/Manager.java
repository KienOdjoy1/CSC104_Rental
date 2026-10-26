public class Manager extends Employee {

    public Manager(String employeeId, String name, String contactNumber, String email, String pin) {
        super(employeeId, name, contactNumber, email, pin);
    }

    @Override
    public String getRole() {
        return "Manager";
    }

    @Override
    public boolean canManageCustomers() {
        return true;
    }

    @Override
    public boolean canRentAndReturn() {
        return true;
    }

    @Override
    public boolean canManageVans() {
        return true;
    }

    @Override
    public boolean canSeeCustomerIds() {
        return true;
    }

    @Override
    public boolean canManageEmployees() {
        return true;
    }

    @Override
    public boolean canDoMaintenance() {
        return true;
    }

}

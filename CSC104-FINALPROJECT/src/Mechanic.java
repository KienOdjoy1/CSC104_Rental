public class Mechanic extends Employee {

    public Mechanic(String employeeId, String name, String contactNumber, String email, String pin) {
        super(employeeId, name, contactNumber, email, pin);
    }

    @Override
    public String getRole() {
        return "Mechanic";
    }

    @Override
    public boolean canManageCustomers() {
        return false;
    }

    @Override
    public boolean canRentAndReturn() {
        return false;
    }

    @Override
    public boolean canManageVans() {
        return false;
    }

    @Override
    public boolean canSeeCustomerIds() {
        return false;
    }

    @Override
    public boolean canManageEmployees() {
        return false;
    }

    @Override
    public boolean canDoMaintenance() {
        return true;
    }

}

public class CounterStaff extends Employee {

    public CounterStaff(String employeeId, String name, String contactNumber, String email, String pin) {
        super(employeeId, name, contactNumber, email, pin);
    }

    @Override
    public String getRole() {
        return "Counter Staff";
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
        return false;
    }

}

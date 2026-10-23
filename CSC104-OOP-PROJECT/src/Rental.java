import java.time.LocalDate;

/**
 * A rental links one Customer to one PassengerVan (association).
 */
public class Rental {

    private String rentalId;
    private Customer customer;
    private PassengerVan van;
    private LocalDate startDate;
    private int days;
    private double totalCost;
    private boolean returned;

    public Rental(String rentalId, Customer customer, PassengerVan van,
                  LocalDate startDate, int days, double totalCost, boolean returned) {
        this.rentalId = rentalId;
        this.customer = customer;
        this.van = van;
        this.startDate = startDate;
        this.days = days;
        this.totalCost = totalCost;
        this.returned = returned;
    }

    public String getRentalId() {
        return rentalId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public PassengerVan getVan() {
        return van;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public int getDays() {
        return days;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public boolean isReturned() {
        return returned;
    }

    /** Marks the rental as returned and makes the van available again. */
    public void markReturned() {
        returned = true;
        van.returnVan();
    }

    /** Line used to save this rental in rentals.csv. */
    public String toCsvLine() {
        return rentalId + "," + customer.getCustomerId() + "," + van.getPlateNumber() + ","
                + startDate + "," + days + "," + totalCost + "," + returned;
    }

    @Override
    public String toString() {
        return String.format("%-5s | %-18s | %-8s | %s | %3d | %14.2f | %s",
                rentalId, customer.getName(), van.getPlateNumber(), startDate, days,
                totalCost, returned ? "Returned" : "Active");
    }
}

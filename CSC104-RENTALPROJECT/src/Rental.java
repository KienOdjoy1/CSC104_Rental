import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Rental {
    private String rentalId;
    private Customer customer;
    private Van van;
    private LocalDate startDate;
    private int days;
    private double totalFee;
    private boolean returned;
    private LocalDate returnDate;
    private double lateFee;
    private Employee rentedBy;
    private Employee returnedBy;

    public static final double LATE_FEE_PER_DAY = 250;

    public Rental(String rentalId, Customer customer, Van van, LocalDate startDate, int days, Employee rentedBy) {
        this.rentalId = rentalId;
        this.customer = customer;
        this.van = van;
        this.startDate = startDate;
        this.days = days;
        this.totalFee = van.calculateFee(days);
        this.returned = false;
        this.rentedBy = rentedBy;
    }

    public String getRentalId() { 
    	return rentalId; 
    }
    
    public Customer getCustomer() { 
    	return customer; 
    }
    
    public Van getVan() { 
    	return van; 
    }
    
    public LocalDate getStartDate() { 
    	return startDate; 
    }
    
    public int getDays() { 
    	return days; 
    }
    
    public double getTotalFee() { 
    	return totalFee; 
    }
    
    public boolean isReturned() { 
    	return returned; 
    }
    
    public void setReturned(boolean returned) { 
    	this.returned = returned; 
    }
    
    public void setTotalFee(double totalFee) { 
    	this.totalFee = totalFee; 
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public Employee getRentedBy() {
        return rentedBy;
    }

    public Employee getReturnedBy() {
        return returnedBy;
    }

    public double getLateFee() {
        return lateFee;
    }

    public double getGrandTotal() {
        return totalFee + lateFee;
    }

    public int getDaysLate(LocalDate asOf) {
        long late = ChronoUnit.DAYS.between(getEndDate(), asOf);
        return late > 0 ? (int) late : 0;
    }

    public void markReturned(LocalDate date, Employee by) {
        this.returned = true;
        this.returnedBy = by;
        this.returnDate = date;
        this.lateFee = getDaysLate(date) * LATE_FEE_PER_DAY;
    }

    public void restoreReturnInfo(LocalDate date, double lateFee, Employee by) {
        this.returnedBy = by;
        this.returnDate = date;
        this.lateFee = lateFee;
    }

    public LocalDate getEndDate() { 
    	return startDate.plusDays(days); 
    }

    public boolean isExpiring() {
        return !returned && !getEndDate().isAfter(LocalDate.now());
    }

    public String toFileString() {
        return rentalId + "," + customer.getCustomerId() + "," + van.getPlateNumber() + ","
                + startDate + "," + days + "," + totalFee + "," + returned
                + "," + (returnDate == null ? "none" : returnDate.toString()) + "," + lateFee
                + "," + (rentedBy == null ? "none" : rentedBy.getEmployeeId())
                + "," + (returnedBy == null ? "none" : returnedBy.getEmployeeId());
    }

    @Override
    public String toString() {
        String status = returned ? "Returned" : "Active";
        String text = rentalId + " | " + customer.getName() + " | " + van.getPlateNumber()
                + " | " + startDate + " to " + getEndDate() + " | P" + totalFee + " | " + status;
        if (lateFee > 0) text += " | Late fee: P" + lateFee;
        if (rentedBy != null) text += " | Rented by: " + rentedBy.getEmployeeId();
        return text;
    }
    
}
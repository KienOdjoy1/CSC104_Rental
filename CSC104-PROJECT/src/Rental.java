import java.time.LocalDate;

public class Rental {
    private String rentalId;
    private Customer customer;
    private Van van;
    private LocalDate startDate;
    private int days;
    private double totalFee;
    private boolean returned;

    public Rental(String rentalId, Customer customer, Van van, LocalDate startDate, int days) {
        this.rentalId = rentalId;
        this.customer = customer;
        this.van = van;
        this.startDate = startDate;
        this.days = days;
        this.totalFee = van.calculateFee(days);
        this.returned = false;
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

    public LocalDate getEndDate() { 
    	return startDate.plusDays(days); 
    }

    public boolean isExpiring() {
        return !returned && !getEndDate().isAfter(LocalDate.now());
    }

    public String toFileString() {
        return rentalId + "," + customer.getCustomerId() + "," + van.getPlateNumber() + ","
                + startDate + "," + days + "," + totalFee + "," + returned;
    }

    @Override
    public String toString() {
        String status = returned ? "Returned" : "Active";
        return rentalId + " | " + customer.getName() + " | " + van.getPlateNumber()
                + " | " + startDate + " to " + getEndDate() + " | P" + totalFee + " | " + status;
    }
    
}
public abstract class Van {
    private String plateNumber;
    private String model;
    private int seats;
    private boolean available;
    private boolean underMaintenance;

    public Van(String plateNumber, String model, int seats) {
        this.plateNumber = plateNumber;
        this.model = model;
        this.seats = seats;
        this.available = true;
    }

    public String getPlateNumber() { 
    	return plateNumber; 
    }
    
    public String getModel() { 
    	return model; 
    }
    
    public int getSeats() { 
    	return seats; 
    }
    
    public boolean isAvailable() { 
    	return available; 
    }
    
    public boolean isUnderMaintenance() {
        return underMaintenance;
    }

    public void setUnderMaintenance(boolean underMaintenance) {
        this.underMaintenance = underMaintenance;
    }

    public void setAvailable(boolean available) { 
    	this.available = available; 
    }

    public String getStatus() {
        if (underMaintenance) return "Under maintenance";
        if (!available) return "Rented";
        return "Available";
    }

    public boolean isRentable() {
        return available && !underMaintenance;
    }

    public abstract double getDailyRate();
    public abstract double calculateFee(int days);
    public abstract String getVanType();

    public String toFileString() {
        return getVanType() + "," + plateNumber + "," + model + "," + seats + "," + available + "," + underMaintenance;
    }

    @Override
    public String toString() {
        String status = getStatus();
        return plateNumber + " | " + model + " | " + getVanType() + " | " + seats
                + " seats | P" + getDailyRate() + "/day | " + status;
    }
    
}
public abstract class Van {
    private String plateNumber;
    private String model;
    private int seats;
    private boolean available;

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
    
    public void setAvailable(boolean available) { 
    	this.available = available; 
    }

    public abstract double getDailyRate();
    public abstract double calculateFee(int days);
    public abstract String getVanType();

    public String toFileString() {
        return getVanType() + "," + plateNumber + "," + model + "," + seats + "," + available;
    }

    @Override
    public String toString() {
        String status = available ? "Available" : "Rented";
        return plateNumber + " | " + model + " | " + getVanType() + " | " + seats
                + " seats | P" + getDailyRate() + "/day | " + status;
    }
    
}
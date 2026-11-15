public class StandardVan extends Van {
	
    public StandardVan(String plateNumber, String model, int seats) {
        super(plateNumber, model, seats);
    }

    @Override
    public double getDailyRate() { 
    	return 3000; 
    }

    @Override
    public double calculateFee(int days) { 
    	return getDailyRate() * days; 
    }

    @Override
    public String getVanType() { 
    	return "Standard"; 
    }
    
}
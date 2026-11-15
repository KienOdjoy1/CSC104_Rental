public class LuxuryVan extends Van {
    private static final double CLEANING_FEE = 500;

    public LuxuryVan(String plateNumber, String model, int seats) {
        super(plateNumber, model, seats);
    }

    @Override
    public double getDailyRate() { 
    	return 4500; 
    }

    @Override
    public double calculateFee(int days) { 
    	return getDailyRate() * days + CLEANING_FEE; 
    }

    @Override
    public String getVanType() { 
    	return "Luxury"; 
    }
    
}
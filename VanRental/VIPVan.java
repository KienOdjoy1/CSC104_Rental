/**
 * Premium van with a driver included. Adds a premium fee and a driver fee for each day.
 */
public class VIPVan extends PassengerVan {

    private double premiumFee;
    private double driverFee;

    public VIPVan(String plateNumber, String brand, String model,
                  double dailyRate, int seatingCapacity,
                  double premiumFee, double driverFee) throws InvalidInputException {
        super(plateNumber, brand, model, dailyRate, seatingCapacity);
        setPremiumFee(premiumFee);
        setDriverFee(driverFee);
    }

    public double getPremiumFee() {
        return premiumFee;
    }

    public void setPremiumFee(double premiumFee) throws InvalidInputException {
        this.premiumFee = checkFee(premiumFee, "Premium fee");
    }

    public double getDriverFee() {
        return driverFee;
    }

    public void setDriverFee(double driverFee) throws InvalidInputException {
        this.driverFee = checkFee(driverFee, "Driver fee");
    }

    @Override
    public double calculateRentalCost(int days) {
        return (getDailyRate() + premiumFee + driverFee) * days;
    }

    @Override
    public String getVanType() {
        return "VIPVan";
    }

    @Override
    protected String getExtraCsv() {
        return premiumFee + "," + driverFee;
    }
}

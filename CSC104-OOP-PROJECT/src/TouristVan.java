/**
 * Newer and more comfortable van for tours. Adds a comfort fee for each day.
 */
public class TouristVan extends PassengerVan {

    private double comfortFee;

    public TouristVan(String plateNumber, String brand, String model,
                      double dailyRate, int seatingCapacity,
                      double comfortFee) throws InvalidInputException {
        super(plateNumber, brand, model, dailyRate, seatingCapacity);
        setComfortFee(comfortFee);
    }

    public double getComfortFee() {
        return comfortFee;
    }

    public void setComfortFee(double comfortFee) throws InvalidInputException {
        this.comfortFee = checkFee(comfortFee, "Comfort fee");
    }

    @Override
    public double calculateRentalCost(int days) {
        return (getDailyRate() + comfortFee) * days;
    }

    @Override
    public String getVanType() {
        return "TouristVan";
    }

    @Override
    protected String getExtraCsv() {
        return comfortFee + ",0";
    }
}

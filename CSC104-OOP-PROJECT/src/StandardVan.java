/**
 * Regular commuter van. Gets a 10% discount for rentals of 5 days or more.
 */
public class StandardVan extends PassengerVan {

    private static final int DISCOUNT_MIN_DAYS = 5;
    private static final double DISCOUNT_RATE = 0.10;

    public StandardVan(String plateNumber, String brand, String model,
                       double dailyRate, int seatingCapacity) throws InvalidInputException {
        super(plateNumber, brand, model, dailyRate, seatingCapacity);
    }

    @Override
    public double calculateRentalCost(int days) {
        double cost = getDailyRate() * days;
        if (days >= DISCOUNT_MIN_DAYS) {
            cost = cost * (1 - DISCOUNT_RATE);
        }
        return cost;
    }

    @Override
    public String getVanType() {
        return "StandardVan";
    }

    @Override
    protected String getExtraCsv() {
        return "0,0";
    }
}

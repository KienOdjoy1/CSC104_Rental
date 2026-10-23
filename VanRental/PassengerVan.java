/**
 * Abstract parent class of all passenger vans (abstraction + inheritance).
 * It holds the data every van shares. Each child class decides its own
 * rental cost by overriding calculateRentalCost (polymorphism).
 */
public abstract class PassengerVan implements Rentable {

    // Encapsulation: all fields are private, access is only through methods
    private String plateNumber;
    private String brand;
    private String model;
    private double dailyRate;
    private int seatingCapacity;
    private boolean available;

    public PassengerVan(String plateNumber, String brand, String model,
                        double dailyRate, int seatingCapacity) throws InvalidInputException {
        setPlateNumber(plateNumber);
        setBrand(brand);
        setModel(model);
        setDailyRate(dailyRate);
        setSeatingCapacity(seatingCapacity);
        this.available = true;
    }

    // ---------- Abstract methods: every child class must provide these ----------

    /** Computes the total rental cost. Each van type prices differently. */
    public abstract double calculateRentalCost(int days);

    /** Name of the van type, used for display and for saving to file. */
    public abstract String getVanType();

    /** Extra fee data of the child class, used when saving to file. */
    protected abstract String getExtraCsv();

    // ---------- Rentable interface ----------

    @Override
    public void rent() throws VanUnavailableException {
        if (!available) {
            throw new VanUnavailableException("Van " + plateNumber + " is not available.");
        }
        available = false;
    }

    @Override
    public void returnVan() {
        available = true;
    }

    // ---------- Getters and setters with validation ----------

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) throws InvalidInputException {
        this.plateNumber = cleanText(plateNumber, "Plate number").toUpperCase();
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) throws InvalidInputException {
        this.brand = cleanText(brand, "Brand");
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) throws InvalidInputException {
        this.model = cleanText(model, "Model");
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(double dailyRate) throws InvalidInputException {
        if (dailyRate <= 0) {
            throw new InvalidInputException("Daily rate must be greater than 0.");
        }
        this.dailyRate = dailyRate;
    }

    public int getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(int seatingCapacity) throws InvalidInputException {
        if (seatingCapacity < 8 || seatingCapacity > 30) {
            throw new InvalidInputException("Seating capacity must be from 8 to 30 for a passenger van.");
        }
        this.seatingCapacity = seatingCapacity;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    // ---------- Helpers ----------

    /** Line used to save this van in vans.csv. */
    public String toCsvLine() {
        return getVanType() + "," + plateNumber + "," + brand + "," + model + ","
                + dailyRate + "," + seatingCapacity + "," + available + "," + getExtraCsv();
    }

    /** Shared text check: not empty and no commas (commas would break the CSV file). */
    protected static String cleanText(String text, String fieldName) throws InvalidInputException {
        if (text == null || text.trim().isEmpty()) {
            throw new InvalidInputException(fieldName + " must not be empty.");
        }
        if (text.contains(",")) {
            throw new InvalidInputException(fieldName + " must not contain commas.");
        }
        return text.trim();
    }

    /** Shared fee check used by the child classes. */
    protected static double checkFee(double fee, String fieldName) throws InvalidInputException {
        if (fee < 0) {
            throw new InvalidInputException(fieldName + " must not be negative.");
        }
        return fee;
    }

    @Override
    public String toString() {
        return String.format("%-8s | %-12s | %-10s | %-10s | %9.2f | %5d | %s",
                plateNumber, getVanType(), brand, model, dailyRate, seatingCapacity,
                available ? "Available" : "Rented");
    }
}

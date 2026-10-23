/**
 * Custom checked exception thrown when a van cannot be rented or deleted
 * because it is currently rented out.
 */
public class VanUnavailableException extends Exception {
    public VanUnavailableException(String message) {
        super(message);
    }
}

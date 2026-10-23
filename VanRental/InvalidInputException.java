/**
 * Custom checked exception for invalid user input or invalid data.
 * Example: empty name, negative rate, van not found.
 */
public class InvalidInputException extends Exception {
    public InvalidInputException(String message) {
        super(message);
    }
}

/**
 * Interface (abstraction): anything that can be rented and returned.
 * PassengerVan implements this interface.
 */
public interface Rentable {
    void rent() throws VanUnavailableException;

    void returnVan();
}

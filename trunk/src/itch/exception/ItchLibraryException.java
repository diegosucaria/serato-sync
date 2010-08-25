package itch.exception;

public class ItchLibraryException extends Exception {

    public ItchLibraryException(String message) {
        super(message);
    }

    public ItchLibraryException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItchLibraryException(Throwable cause) {
        super(cause);
    }

}

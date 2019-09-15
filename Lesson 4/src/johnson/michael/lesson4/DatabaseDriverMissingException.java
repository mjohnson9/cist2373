package johnson.michael.lesson4;

public class DatabaseDriverMissingException extends ClassNotFoundException {
  public DatabaseDriverMissingException(final String message, final Exception cause) {
    super(message, cause);
  }
}

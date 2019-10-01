package johnson.michael.cist2373.midterm;

/**
 * DatabaseDriverMissingException is thrown when a database driver is not found during first use of
 * a Database.
 */
public class DatabaseDriverMissingException extends ClassNotFoundException {
  /**
   * Instantiates a DatabaseDriverMissingException.
   *
   * @param message The message for the exception.
   * @param cause The exception that caused this exception.
   */
  public DatabaseDriverMissingException(final String message, final Exception cause) {
    super(message, cause);
  }
}

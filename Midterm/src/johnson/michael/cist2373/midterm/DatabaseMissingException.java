package johnson.michael.cist2373.midterm;

import java.io.FileNotFoundException;

/** DatabaseMissingException is thrown whenever a database file cannot be found. */
public class DatabaseMissingException extends FileNotFoundException {
  /**
   * Instantiates DatabaseMissingException.
   *
   * @param message The message to provide with the DatabaseMissingException.
   */
  public DatabaseMissingException(final String message) {
    super(message);
  }
}

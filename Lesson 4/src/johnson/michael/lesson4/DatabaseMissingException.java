package johnson.michael.lesson4;

import java.io.FileNotFoundException;

public class DatabaseMissingException extends FileNotFoundException {
  public DatabaseMissingException(final String message) {
    super(message);
  }
}

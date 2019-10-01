package johnson.michael.cist2373.midterm.database;

import java.sql.SQLException;

/**
 * OrderNotFoundException is thrown whenever an Order cannot be found based on the given specifiers.
 */
public class OrderNotFoundException extends SQLException {
  /**
   * Constructs an OrderNotFoundException with a simple message.
   *
   * @param message The detail message relating to this exception.
   */
  public OrderNotFoundException(final String message) {
    this(message, null);
  }

  /**
   * Constructs an OrderNotFoundException with a message and a cause.
   *
   * @param message The detail message relating to this exception.
   * @param cause The exception that proximately caused this exception.
   */
  public OrderNotFoundException(final String message, final Exception cause) {
    super(message, cause);
  }
}

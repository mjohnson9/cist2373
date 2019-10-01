package johnson.michael.cist2373.midterm.database;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import johnson.michael.cist2373.midterm.DatabaseDriverMissingException;
import johnson.michael.cist2373.midterm.DatabaseMissingException;

/** Database is a convenience wrapper around various calls to the JDBC SQL API. */
public class Database {
  private static final List<String> STANDARD_DATABASE_LOCATIONS =
      Collections.unmodifiableList(Arrays.asList("./Northwind.mdb", "C:\\data\\Northwind.mdb"));

  private static Database sharedConnection = null;

  private Connection connection;

  public static Database getCommonConnection()
      throws SQLException, DatabaseDriverMissingException, DatabaseMissingException {
    if (sharedConnection == null) {
      sharedConnection = new Database();
    }

    return sharedConnection;
  }

  public static void setCommonConnection(final Database database) {
    sharedConnection = database;
  }

  /**
   * Creates a new Database, attempting to load from the standard database locations.
   *
   * @throws DatabaseDriverMissingException when the database driver is not available to be loaded
   * @throws DatabaseMissingException when the database cannot be found at any of the standard
   *     locations
   * @throws SQLException when an unspecified SQL exception occurs
   */
  public Database() throws DatabaseDriverMissingException, DatabaseMissingException, SQLException {
    // Ensure the JDBC driver is loaded
    loadDriver();

    // Attempt to connect to all of the standard locations
    this.attemptStandardLocations();
  }

  /**
   * Creates a new Database, opening from the given filePath.
   *
   * @param filePath the file path to use for the database
   * @throws SQLException when an unspecified SQL exception occurs
   * @throws DatabaseDriverMissingException when the database driver is not available to be loaded
   */
  public Database(final String filePath) throws SQLException, DatabaseDriverMissingException {
    // Ensure the JDBC driver is loaded
    loadDriver();

    // Attempt to connect to this given path
    this.connect(filePath);
  }

  /**
   * Turns a given query into a PreparedStatement.
   *
   * @param query The query to be compiled.
   * @return The PreparedStatement containing the given query.
   * @throws SQLException When an unexpected SQL error occurs.
   */
  public PreparedStatement getStatement(final String query) throws SQLException {
    return this.connection.prepareStatement(query);
  }

  private static void loadDriver() throws DatabaseDriverMissingException {
    try {
      Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
    } catch (final ClassNotFoundException ex) {
      // Rethrow this as a more specific exception
      throw new DatabaseDriverMissingException("The Microsoft Access JDBC driver is missing.", ex);
    }
  }

  private void connect(final String databasePath) throws SQLException {
    this.connection = DriverManager.getConnection("jdbc:ucanaccess://" + databasePath);
  }

  private void attemptStandardLocations() throws DatabaseMissingException, SQLException {
    for (final String location : STANDARD_DATABASE_LOCATIONS) {
      try {
        this.connect(location);

        // Return on first success
        return;
      } catch (final SQLException ex) {
        // Iterate through the exception causes to see if any are eventually a FileNotFoundException
        boolean isFileNotFound = false;
        for (Throwable currentException = ex;
            currentException != null;
            currentException = currentException.getCause()) {
          if (currentException instanceof FileNotFoundException) {
            // The top level exception is because of a FileNotFoundException
            isFileNotFound = true;
            break;
          }
        }

        if (isFileNotFound) {
          // The file wasn't found. This is expected behavior.
          continue;
        }

        // The exception is not based on a FileNotFoundException, rethrow it
        throw ex;
      }
    }

    throw new DatabaseMissingException(
        "The database does not exist in any of the standard locations.");
  }
}

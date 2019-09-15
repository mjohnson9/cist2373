package johnson.michael.lesson4;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Database is a convenience wrapper around JDBC to make accessing the information used in
 * AccessReaderApplication easier.
 */
public class Database {
  private static final List<String> STANDARD_DATABASE_LOCATIONS =
      Collections.unmodifiableList(
          Arrays.asList("./exampleMDB.accdb", "C:\\data\\exampleMDB.accdb"));

  private Connection connection;

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

  private static void loadDriver() throws DatabaseDriverMissingException {
    try {
      Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
    } catch (final ClassNotFoundException ex) {
      // Rethrow this as a more specific exception
      throw new DatabaseDriverMissingException("The Microsoft Access JDBC driver is missing.", ex);
    }
  }

  /**
   * Retrieves all of the table names in this Database.
   *
   * @return an immutable list of table names in this database
   * @throws SQLException when an unspecified SQL exception occurs
   */
  public List<String> getTableNames() throws SQLException {
    final DatabaseMetaData metadata = this.connection.getMetaData();
    final ResultSet tableResults = metadata.getTables(null, null, "%", null);

    final List<String> tableNames = new ArrayList<>();

    while (tableResults.next()) {
      final String tableName = tableResults.getString("TABLE_NAME");
      tableNames.add(tableName);
    }

    return Collections.unmodifiableList(tableNames);
  }

  /**
   * Retrieves a ResultSet of all of the rows in a given table.
   *
   * @param tableName the name of the table to retrieve rows for
   * @return a ResultSet containing all of rows in the given table
   * @throws SQLException when an unspecified SQL exception occurs
   */
  public ResultSet getTableRows(final String tableName) throws SQLException {
    final String query = "SELECT * FROM " + tableName; // We won't worry about SQL injection

    final Statement statement = this.connection.createStatement();

    return statement.executeQuery(query);
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

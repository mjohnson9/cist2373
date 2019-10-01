package johnson.michael.cist2373.midterm.database;

import java.sql.ResultSet;
import java.sql.SQLException;

/** Represents a Product from the database. */
public class Product {
  private final long id;
  private final String name;

  /**
   * Instantiates a Product from the given SQL results.
   *
   * @param result The SQL results.
   * @throws SQLException When an unexpected SQL error occurs.
   */
  public Product(final ResultSet result) throws SQLException {
    this.id = result.getLong("ProductID");
    this.name = result.getString("ProductName");
  }

  public long getProductId() {
    return this.id;
  }

  public String getProductName() {
    return this.name;
  }
}

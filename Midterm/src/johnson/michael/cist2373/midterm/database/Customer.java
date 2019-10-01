package johnson.michael.cist2373.midterm.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Customer represents a customer from the database. */
public class Customer {
  /**
   * This query retrieves all customers who are in a specified state in the USA, as given by
   * parameter 1.
   */
  private static final String CUSTOMERS_BY_STATE_QUERY =
      "SELECT CompanyName, ContactName, City FROM Customers WHERE Country = 'USA' AND Region = ?";

  private final String companyName;
  private final String contactName;
  private final String city;

  /**
   * Retrieves a list of customers within the USA by their state.
   *
   * @param database The database connection to use for retrieving customers.
   * @param state The state in which to search for customers.
   * @return A list of Customers that reside in the given state.
   * @throws SQLException When an unexpected SQL exception occurs.
   */
  public static List<Customer> getCustomersByState(final Database database, final String state)
      throws SQLException {
    final ArrayList<Customer> customers = new ArrayList<>();

    try (final PreparedStatement customerStatement =
        database.getStatement(CUSTOMERS_BY_STATE_QUERY)) {
      customerStatement.setString(1, state);
      try (final ResultSet results = customerStatement.executeQuery()) {
        while (results.next()) {
          final Customer customer = new Customer(results);
          customers.add(customer);
        }

        return customers;
      }
    }
  }

  protected Customer(final ResultSet results) throws SQLException {
    this.companyName = results.getString("CompanyName");
    this.contactName = results.getString("ContactName");
    this.city = results.getString("City");
  }

  public String getCompanyName() {
    return this.companyName;
  }

  public String getContactName() {
    return this.contactName;
  }

  public String getCity() {
    return this.city;
  }
}

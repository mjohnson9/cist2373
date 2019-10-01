package johnson.michael.cist2373.midterm.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Represents an Employee from the Database. */
public class Employee {
  private static final String EMPLOYEES_BY_BIRTH_YEAR_QUERY =
      "SELECT FirstName, LastName FROM [Employees] WHERE Year(BirthDate) = ?";

  private final String firstName;
  private final String lastName;

  /**
   * Retrieves a list of employees by birth year.
   *
   * @param database The database to retrieve employees from.
   * @param year The year to search for in the database.
   * @return A list of employees whose birth year match the given year. This list can be empty if
   *     there are no matches.
   * @throws SQLException When an unexpected SQL error occurs.
   */
  public static List<Employee> getEmployeesByBirthYear(final Database database, final int year)
      throws SQLException {
    final ArrayList<Employee> employees = new ArrayList<>();

    try (final PreparedStatement employeeStatement =
        database.getStatement(EMPLOYEES_BY_BIRTH_YEAR_QUERY)) {
      employeeStatement.setInt(1, year);
      try (final ResultSet results = employeeStatement.executeQuery()) {
        while (results.next()) {
          final Employee employee = new Employee(results);
          employees.add(employee);
        }

        return employees;
      }
    }
  }

  public Employee(final ResultSet results) throws SQLException {
    this.firstName = results.getString("FirstName");
    this.lastName = results.getString("LastName");
  }

  public String getFirstName() {
    return this.firstName;
  }

  public String getLastName() {
    return this.lastName;
  }
}

package johnson.michael.cist2373.midterm.database;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Represents an Order from the Database. */
public class Order {
  private static final String ORDER_INFO_QUERY =
      "SELECT OrderDate, Freight FROM Orders WHERE OrderID = ?";
  private static final String ITEMS_QUERY =
      "SELECT [Order Details].UnitPrice, [Order Details].Quantity, [Order Details].Discount, Products.ProductID, Products.ProductName FROM Orders INNER JOIN [Order Details] ON Orders.OrderID = [Order Details].OrderID INNER JOIN Products ON [Order Details].ProductID = Products.ProductID WHERE Orders.OrderID = ?";
  private static final String ORDER_TOTAL_QUERY =
      "SELECT SUM([Order Details].Quantity*[Order Details].UnitPrice*(1-[Order Details].Discount)) AS OrderTotal FROM [Order Details] WHERE OrderID = ?";

  private final long id;
  private final Date date;
  private final double freightCharge;
  private final List<OrderDetail> items;

  /**
   * Retrieves an order's total based on the order ID.
   *
   * @param database The Database to operate on when retrieving the order total.
   * @param orderId The order ID to retrieve the total for.
   * @return The total cost of the order in US dollars.
   * @throws SQLException When an unexpected SQL error occurs.
   * @throws OrderNotFoundException When the given orderId cannot be found.
   */
  public static double getOrderTotal(final Database database, final long orderId)
      throws SQLException, OrderNotFoundException {
    try (final PreparedStatement orderTotalStatement = database.getStatement(ORDER_TOTAL_QUERY)) {
      orderTotalStatement.setLong(1, orderId);
      try (final ResultSet results = orderTotalStatement.executeQuery()) {
        if (!results.next()) {
          // There was no first row
          throw new OrderNotFoundException("Order #" + orderId + " does not exist");
        }

        if (!results.isLast()) {
          // This ResultSet has more than one result
          throw new OrderNotFoundException("Multiple results were returned for order #" + orderId);
        }

        final double orderTotal = results.getDouble("OrderTotal");

        if (results.wasNull()) {
          throw new OrderNotFoundException("Order #" + orderId + " does not exist");
        }

        return orderTotal;
      }
    }
  }

  /**
   * Instantiates an Order by querying a given Database for an order ID.
   *
   * @param database The database to query for the order.
   * @param orderId The order's ID.
   * @throws SQLException When an unexpected SQL error occurs.
   * @throws OrderNotFoundException When the given order ID cannot be found.
   */
  public Order(final Database database, final long orderId)
      throws SQLException, OrderNotFoundException {
    // We use try-with-resources to make sure that the ResultSets and PreparedStatements get closed

    try (final PreparedStatement orderInfoStatement = database.getStatement(ORDER_INFO_QUERY)) {
      orderInfoStatement.setLong(1, orderId);
      try (final ResultSet results = orderInfoStatement.executeQuery()) {
        if (!results.next()) {
          // There was no first row
          throw new OrderNotFoundException("Order #" + orderId + " does not exist");
        }

        if (!results.isLast()) {
          // This result set has more than one result
          throw new OrderNotFoundException("Multiple results were returned for order #" + orderId);
        }

        this.id = orderId;
        this.date = results.getDate("OrderDate");
        this.freightCharge = results.getDouble("Freight");
      }
    }

    final List<OrderDetail> orderItems = new ArrayList<>();

    try (final PreparedStatement itemsStatement = database.getStatement(ITEMS_QUERY)) {
      itemsStatement.setLong(1, orderId);
      try (final ResultSet results = itemsStatement.executeQuery()) {
        while (results.next()) {
          final OrderDetail orderDetail = new OrderDetail(results);
          orderItems.add(orderDetail);
        }
      }
    }

    this.items = Collections.unmodifiableList(orderItems);
  }

  public long getOrderId() {
    return this.id;
  }

  public Date getDate() {
    return this.date;
  }

  public double getFreightCharge() {
    return this.freightCharge;
  }

  public List<OrderDetail> getItems() {
    return this.items;
  }
}

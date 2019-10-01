package johnson.michael.cist2373.midterm.database;

import java.sql.ResultSet;
import java.sql.SQLException;

/** OrderDetail represents order details from the Database. */
public class OrderDetail {
  private final Product product;
  private final double unitPrice;
  private final long quantity;
  private final double discount;

  /**
   * Instantiates an OrderDetail based on an SQL ResultSet.
   *
   * @param result The ResultSet to build the OrderDetail from.
   * @throws SQLException When an unexpected SQL error occurs.
   */
  public OrderDetail(final ResultSet result) throws SQLException {
    this.product = new Product(result);

    this.unitPrice = result.getDouble("UnitPrice");
    this.quantity = result.getLong("Quantity");
    this.discount = result.getDouble("Discount");
  }

  public Product getProduct() {
    return this.product;
  }

  public double getUnitPrice() {
    return this.unitPrice;
  }

  public long getQuantity() {
    return this.quantity;
  }

  public double getDiscount() {
    return this.discount;
  }
}

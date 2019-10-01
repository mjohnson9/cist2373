package johnson.michael.cist2373.midterm;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Currency;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import johnson.michael.cist2373.midterm.database.Database;
import johnson.michael.cist2373.midterm.database.Order;
import johnson.michael.cist2373.midterm.database.OrderNotFoundException;

/** OrderTotalView provides a view for the user to lookup order totals. */
public class OrderTotalView extends OrderView {
  private static final String PROMPT_TITLE = "Order Total Lookup";

  /**
   * Instantiates a new OrderTotalView with the given parent scene.
   *
   * @param parentScene The parent scene containing this OrderTotalView.
   */
  public OrderTotalView(final NorthwindScene parentScene) {
    super(parentScene);

    // Put a margin between children of 10px
    this.setHgap(10);
    this.setVgap(10);

    this.presentOrderNumberPrompt(PROMPT_TITLE);
  }

  private void showOrderDetails(final long orderId, final double orderTotal) {
    // Clear the pane of its previous contents
    this.clearGrid();

    int currentRow = 0;

    // Go back button

    final Button goBack = new Button("Go back");
    goBack.setOnAction(this::goBackPressed);

    this.add(goBack, 0, currentRow);
    currentRow += 1;

    // Order ID

    final Label orderIdPrefixLabel = new Label("Order ID:");
    this.add(orderIdPrefixLabel, 0, currentRow);
    orderIdPrefixLabel.setAlignment(Pos.CENTER_RIGHT);

    final Label orderIdLabel = new Label(String.valueOf(orderId));
    this.add(orderIdLabel, 1, currentRow);
    orderIdPrefixLabel.setAlignment(Pos.CENTER_LEFT);

    orderIdPrefixLabel.setLabelFor(orderIdLabel);

    currentRow += 1;

    // Order date

    final Label orderTotalPrefixLabel = new Label("Order total:");
    this.add(orderTotalPrefixLabel, 0, currentRow);
    orderTotalPrefixLabel.setAlignment(Pos.CENTER_RIGHT);

    final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
    currencyFormatter.setCurrency(Currency.getInstance("USD"));

    final Label orderTotalLabel = new Label(currencyFormatter.format(orderTotal));
    this.add(orderTotalLabel, 1, currentRow);
    orderTotalLabel.setAlignment(Pos.CENTER_LEFT);

    orderTotalPrefixLabel.setLabelFor(orderTotalLabel);

    currentRow += 1;

    this.getParentScene().resizeToContents();
  }

  @Override
  void lookupFormSubmitted(final ActionEvent event) {
    final long orderNumber;
    try {
      orderNumber = this.readOrderNumber();
    } catch (final NumberFormatException ex) {
      // The given order number is not a readable long
      final Alert alert =
          NorthwindApplication.createAlert(
              AlertType.ERROR,
              "Order number invalid",
              "The provided order number was not a valid number.\nPlease provide a valid order number.");
      alert.showAndWait();
      return;
    }

    final Database database;
    try {
      database = Database.getCommonConnection();
    } catch (final SQLException | DatabaseDriverMissingException | DatabaseMissingException ex) {
      // An unexpected database error occurred
      final ExceptionAlert alert =
          new ExceptionAlert(
              AlertType.ERROR,
              "Database error",
              "An error occurred connecting to the database.",
              ex);
      alert.showAndWait();
      return;
    }

    try {
      final double orderTotal = Order.getOrderTotal(database, orderNumber);
      this.showOrderDetails(orderNumber, orderTotal);
    } catch (final OrderNotFoundException ex) {
      // The order with the given number couldn't be found
      final Alert alert =
          NorthwindApplication.createAlert(
              AlertType.ERROR, "No such order", "There is no order #" + orderNumber + ".");
      alert.showAndWait();
      return;
    } catch (final SQLException ex) {
      // An unexpected SQL error occurred
      final ExceptionAlert alert =
          new ExceptionAlert(
              AlertType.ERROR,
              "Database error",
              "An error occurred while retrieving order #" + orderNumber,
              ex);
      alert.showAndWait();
      return;
    }
  }

  @Override
  void goBackPressed(final ActionEvent event) {
    this.presentOrderNumberPrompt(PROMPT_TITLE);
  }
}

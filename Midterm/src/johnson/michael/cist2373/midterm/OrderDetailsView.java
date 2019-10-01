package johnson.michael.cist2373.midterm;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import johnson.michael.cist2373.midterm.database.Database;
import johnson.michael.cist2373.midterm.database.Order;
import johnson.michael.cist2373.midterm.database.OrderDetail;
import johnson.michael.cist2373.midterm.database.OrderNotFoundException;

/** OrderDetailsView provides an interface for the user to look at order details. */
public class OrderDetailsView extends OrderView {
  private static final String PROMPT_TITLE = "Lookup Order Details";

  private static final double PREF_TABLE_WIDTH = 450d;
  private static final double PREF_TABLE_HEIGHT = 400d;

  /**
   * Instaniates an OrderDetailsView with a specified parent scene.
   *
   * @param parentScene The parent scene of this OrderDetailsView.
   */
  public OrderDetailsView(final NorthwindScene parentScene) {
    super(parentScene);

    // Put a margin between children of 10px
    this.setHgap(10);
    this.setVgap(10);

    this.presentOrderNumberPrompt(PROMPT_TITLE);
  }

  private void showOrderDetails(final Order order) {
    // Clear the pane of its previous contents
    this.getChildren().clear();

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

    final Label orderIdLabel = new Label(String.valueOf(order.getOrderId()));
    this.add(orderIdLabel, 1, currentRow);
    orderIdPrefixLabel.setAlignment(Pos.CENTER_LEFT);

    orderIdPrefixLabel.setLabelFor(orderIdLabel);

    currentRow += 1;

    // Order date

    final Label orderDatePrefixLabel = new Label("Order date:");
    this.add(orderDatePrefixLabel, 0, currentRow);
    orderDatePrefixLabel.setAlignment(Pos.CENTER_RIGHT);

    final Label orderDateLabel =
        new Label(DateFormat.getDateInstance(DateFormat.SHORT).format(order.getDate()));
    this.add(orderDateLabel, 1, currentRow);
    orderDateLabel.setAlignment(Pos.CENTER_LEFT);

    orderDatePrefixLabel.setLabelFor(orderDateLabel);

    currentRow += 1;

    final Label freightPrefixLabel = new Label("Freight charge:");
    this.add(freightPrefixLabel, 0, currentRow);
    freightPrefixLabel.setAlignment(Pos.CENTER_RIGHT);

    final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
    currencyFormatter.setCurrency(
        Currency.getInstance("USD")); // Tell the currency formatter to use USD

    final Label freightLabel = new Label(currencyFormatter.format(order.getFreightCharge()));
    this.add(freightLabel, 1, currentRow);
    freightLabel.setAlignment(Pos.CENTER_LEFT);

    freightPrefixLabel.setLabelFor(freightLabel);

    currentRow += 1;

    final Label itemListLabel = new Label("Items:");
    this.add(itemListLabel, 0, currentRow);
    itemListLabel.setAlignment(Pos.TOP_RIGHT);

    final Node itemList = this.getProductTable(order);
    this.add(itemList, 1, currentRow);

    itemListLabel.setLabelFor(itemList);

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
      final Order order = new Order(database, orderNumber);
      this.showOrderDetails(order);
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

  private Node getProductTable(final Order order) {
    final List<OrderDetail> orders = order.getItems();
    final TableView<OrderDetail> table = new TableView<>(FXCollections.observableList(orders));

    final NumberFormat numberFormatter = NumberFormat.getNumberInstance();
    numberFormatter.setGroupingUsed(
        true); // Use digit grouping to make reading large numbers easier

    final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
    currencyFormatter.setCurrency(Currency.getInstance("USD"));

    final NumberFormat percentFormatter = NumberFormat.getPercentInstance();
    percentFormatter.setMaximumFractionDigits(0);

    final TableColumn<OrderDetail, String> productNameColumn = new TableColumn<>("Product Name");
    productNameColumn.setCellValueFactory(
        orderItem ->
            new ReadOnlyObjectWrapper<>(orderItem.getValue().getProduct().getProductName()));

    final TableColumn<OrderDetail, String> quantityColumn = new TableColumn<>("Qty");
    quantityColumn.setCellValueFactory(
        orderItem ->
            new ReadOnlyObjectWrapper<>(
                numberFormatter.format(orderItem.getValue().getQuantity())));

    final TableColumn<OrderDetail, String> unitPriceColumn = new TableColumn<>("Unit Price");
    unitPriceColumn.setCellValueFactory(
        orderItem ->
            new ReadOnlyObjectWrapper<>(
                currencyFormatter.format(orderItem.getValue().getUnitPrice())));

    final TableColumn<OrderDetail, String> discountColumn = new TableColumn<>("Discount");
    discountColumn.setCellValueFactory(
        orderItem ->
            new ReadOnlyObjectWrapper<>(
                percentFormatter.format(orderItem.getValue().getDiscount())));

    table.getColumns().addAll(productNameColumn, quantityColumn, unitPriceColumn, discountColumn);

    table.setMaxSize(PREF_TABLE_WIDTH, PREF_TABLE_HEIGHT);

    return table;
  }

  @Override
  void goBackPressed(final ActionEvent event) {
    this.presentOrderNumberPrompt(PROMPT_TITLE);
  }
}

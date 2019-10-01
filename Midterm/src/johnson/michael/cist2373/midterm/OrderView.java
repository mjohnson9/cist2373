package johnson.michael.cist2373.midterm;

import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import johnson.michael.cist2373.midterm.database.Database;
import johnson.michael.cist2373.midterm.database.Order;
import johnson.michael.cist2373.midterm.database.OrderNotFoundException;

/** OrderView provides a base class and convenience methods for views that display Orders. */
public abstract class OrderView extends GridPane {
  private final NorthwindScene parentScene;
  private TextField orderNumberField = null;

  protected OrderView(final NorthwindScene parentScene) {
    super();

    this.parentScene = parentScene;
  }

  protected void presentOrderNumberPrompt(final String title) {
    // Clear the pane of its previous contents
    this.getChildren().clear();
    this.orderNumberField = null;

    final Label promptTitle = new Label(title);
    promptTitle.setFont(Font.font(NorthwindApplication.TITLE_FONT_SIZE));
    this.add(promptTitle, 0, 0, 3, 1);
    GridPane.setHalignment(promptTitle, HPos.CENTER);
    GridPane.setValignment(promptTitle, VPos.CENTER);

    final Label orderNumberLabel = new Label("Order number:");

    this.orderNumberField = new TextField();
    this.orderNumberField.setPrefColumnCount(5);
    GridPane.setHgrow(this.orderNumberField, Priority.ALWAYS);

    orderNumberLabel.setLabelFor(this.orderNumberField);

    final Button submitButton = new Button("Look up");
    submitButton.setDefaultButton(true);
    submitButton.setOnAction(this::lookupFormSubmitted);

    this.add(orderNumberLabel, 0, 1);
    this.add(this.orderNumberField, 1, 1);
    this.add(submitButton, 2, 1);

    this.parentScene.resizeToContents();
  }

  protected final Order lookupOrder(final long orderNumber) {
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
      return null;
    }

    try {
      return new Order(database, orderNumber);
    } catch (final OrderNotFoundException ex) {
      // The order with the given number couldn't be found
      final Alert alert =
          NorthwindApplication.createAlert(
              AlertType.ERROR, "No such order", "There is no order #" + orderNumber + ".");
      alert.showAndWait();
      return null;
    } catch (final SQLException ex) {
      // An unexpected SQL error occurred
      final ExceptionAlert alert =
          new ExceptionAlert(
              AlertType.ERROR,
              "Database error",
              "An error occurred while retrieving order #" + orderNumber,
              ex);
      alert.showAndWait();
      return null;
    }
  }

  protected long readOrderNumber() throws NumberFormatException {
    if (this.orderNumberField == null) {
      throw new NullPointerException("orderNumberField is null");
    }

    final String orderNumberText = this.orderNumberField.getText();

    return Long.parseLong(orderNumberText);
  }

  protected void clearGrid() {
    this.getChildren().clear();
    this.orderNumberField = null;
  }

  protected final NorthwindScene getParentScene() {
    return this.parentScene;
  }

  abstract void goBackPressed(final ActionEvent event);

  abstract void lookupFormSubmitted(final ActionEvent event);
}

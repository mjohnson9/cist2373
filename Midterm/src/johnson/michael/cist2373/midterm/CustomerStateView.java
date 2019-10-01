package johnson.michael.cist2373.midterm;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import johnson.michael.cist2373.midterm.database.Customer;
import johnson.michael.cist2373.midterm.database.Database;

/** A View for searching for Customers by state. */
public class CustomerStateView extends GridPane {
  private static final List<String> STATES =
      Collections.unmodifiableList(
          Arrays.asList(
              "AK", "AL", "AR", "AS", "AZ", "CA", "CO", "CT", "DC", "DE", "FL", "GA", "GU", "HI",
              "IA", "ID", "IL", "IN", "KS", "KY", "LA", "MA", "MD", "ME", "MI", "MN", "MO", "MP",
              "MS", "MT", "NC", "ND", "NE", "NH", "NJ", "NM", "NV", "NY", "OH", "OK", "OR", "PA",
              "PR", "RI", "SC", "SD", "TN", "TX", "UM", "UT", "VA", "VI", "VT", "WA", "WI", "WV",
              "WY"));

  private static final double PREF_TABLE_WIDTH = 400d;
  private static final double PREF_TABLE_HEIGHT = 300d;

  private final NorthwindScene parentScene;
  private ComboBox<String> stateField = null;

  /**
   * Instantiates an instance of the customer state view.
   *
   * @param parentScene The parent scene of this state view.
   */
  public CustomerStateView(final NorthwindScene parentScene) {
    super();

    this.parentScene = parentScene;

    // Add a 10px padding between children
    this.setHgap(10);
    this.setVgap(10);

    this.presentStatePrompt();
  }

  protected void presentStatePrompt() {
    this.clearGrid();

    final Label promptTitle = new Label("Lookup customers by state");
    promptTitle.setFont(Font.font(NorthwindApplication.TITLE_FONT_SIZE));
    this.add(promptTitle, 0, 0, 3, 1);
    GridPane.setHalignment(promptTitle, HPos.CENTER);
    GridPane.setValignment(promptTitle, VPos.CENTER);

    final Label stateSelectionLabel = new Label("State:");

    this.stateField = new ComboBox<>(FXCollections.observableList(STATES));
    this.stateField.setValue(STATES.get(0));

    stateSelectionLabel.setLabelFor(this.stateField);

    final Button submitButton = new Button("Look up");
    submitButton.setDefaultButton(true);
    submitButton.setOnAction(this::lookupFormSubmitted);

    this.add(stateSelectionLabel, 0, 1);
    this.add(this.stateField, 1, 1);
    this.add(submitButton, 2, 1);

    this.parentScene.resizeToContents();
  }

  private void lookupFormSubmitted(final ActionEvent event) {
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

    final String state = this.stateField.getValue();
    try {
      final List<Customer> customers = Customer.getCustomersByState(database, state);
      if (customers.isEmpty()) {
        this.showEmptyResults(state);
        return;
      }

      this.showCustomerList(state, customers);
    } catch (final SQLException ex) {
      // An unexpected SQL error occurred
      final ExceptionAlert alert =
          new ExceptionAlert(
              AlertType.ERROR,
              "Database error",
              "An error occurred while retrieving the customers in " + state,
              ex);
      alert.showAndWait();
      return;
    }
  }

  private void showCustomerList(final String state, final List<Customer> customers) {
    this.clearGrid();

    this.addTitle(state);

    // Start at 1 because the title already took 0
    int currentRow = 1;

    // Go back button

    final Button goBack = new Button("Go back");
    goBack.setOnAction(this::goBackPressed);

    this.add(goBack, 0, currentRow);
    currentRow += 1;

    // Customer table

    final Node customerTable = this.createCustomerTable(customers);
    this.add(customerTable, 0, currentRow);
    GridPane.setHgrow(customerTable, Priority.ALWAYS);
    GridPane.setVgrow(customerTable, Priority.ALWAYS);

    currentRow += 1;

    this.parentScene.resizeToContents();
  }

  private void showEmptyResults(final String state) {
    this.clearGrid();

    this.addTitle(state);

    // Start at 1 because the title already took 0
    int currentRow = 1;

    // Go back button

    final Button goBack = new Button("Go back");
    goBack.setOnAction(this::goBackPressed);

    this.add(goBack, 0, currentRow);
    currentRow += 1;

    // Statement that there are no customers

    final Label noCustomerLabel = new Label("There are no customers in " + state + ".");
    this.add(noCustomerLabel, 0, currentRow);
    GridPane.setHalignment(noCustomerLabel, HPos.CENTER);
    GridPane.setValignment(noCustomerLabel, VPos.CENTER);

    currentRow += 1;

    this.parentScene.resizeToContents();
  }

  private void goBackPressed(final ActionEvent event) {
    this.presentStatePrompt();
  }

  private void addTitle(final String state) {
    final Label title = new Label("Viewing customers in " + state);
    title.setFont(Font.font(NorthwindApplication.TITLE_FONT_SIZE));
    this.add(title, 0, 0, 1, 1);
    GridPane.setHalignment(title, HPos.CENTER);
    GridPane.setValignment(title, VPos.CENTER);
  }

  private Node createCustomerTable(final List<Customer> customers) {
    final TableView<Customer> table = new TableView<>(FXCollections.observableList(customers));

    final TableColumn<Customer, String> companyNameColumn = new TableColumn<>("Company Name");
    companyNameColumn.setCellValueFactory(
        customer -> new ReadOnlyObjectWrapper<>(customer.getValue().getCompanyName()));

    final TableColumn<Customer, String> contactNameColumn = new TableColumn<>("Contact Name");
    contactNameColumn.setCellValueFactory(
        customer -> new ReadOnlyObjectWrapper<>(customer.getValue().getContactName()));

    final TableColumn<Customer, String> cityColumn = new TableColumn<>("City");
    cityColumn.setCellValueFactory(
        customer -> new ReadOnlyObjectWrapper<>(customer.getValue().getCity()));

    table.getColumns().addAll(companyNameColumn, contactNameColumn, cityColumn);

    table.setPrefSize(PREF_TABLE_WIDTH, PREF_TABLE_HEIGHT);

    return table;
  }

  private void clearGrid() {
    this.getChildren().clear();
    this.stateField = null;
  }
}

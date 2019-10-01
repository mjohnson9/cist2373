package johnson.michael.cist2373.midterm;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
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
import johnson.michael.cist2373.midterm.database.Database;
import johnson.michael.cist2373.midterm.database.Employee;

/**
 * EmployeeBirthYearView provides an interface for a user to search for employees by their birth
 * year.
 */
public class EmployeesBirthYearView extends GridPane {
  private static final int LAST_YEAR = Calendar.getInstance().get(Calendar.YEAR); // Current year
  private static final int FIRST_YEAR = LAST_YEAR - 100; // First year is 100 years ago
  private static final int DEFAULT_YEAR = LAST_YEAR - 21; // Default year is 21 years ago

  private static final double PREF_TABLE_WIDTH = 300d;
  private static final double PREF_TABLE_HEIGHT = 300d;

  private final NorthwindScene parentScene;
  private ComboBox<Integer> yearField = null;

  /**
   * Instantiates EmployeeBirthYearView.
   *
   * @param parentScene The parent scene of this view.
   */
  public EmployeesBirthYearView(final NorthwindScene parentScene) {
    super();

    this.parentScene = parentScene;

    // Add a 10px padding between children
    this.setHgap(10);
    this.setVgap(10);

    this.presentYearPrompt();
  }

  private void presentYearPrompt() {
    this.clearGrid();

    final Label promptTitle = new Label("Lookup employees by birth year");
    promptTitle.setFont(Font.font(NorthwindApplication.TITLE_FONT_SIZE));
    this.add(promptTitle, 0, 0, 3, 1);
    GridPane.setHalignment(promptTitle, HPos.CENTER);
    GridPane.setValignment(promptTitle, VPos.CENTER);

    final Label yearSelectionLabel = new Label("Birth year:");

    // Create an ArrayList with a capacity of (LAST_YEAR - FIRST_YEAR + 1). The +1 is because we're
    // counting both the last and first years.
    final ArrayList<Integer> years = new ArrayList<>((LAST_YEAR - FIRST_YEAR) + 1);
    for (int i = FIRST_YEAR; i <= LAST_YEAR; i++) {
      years.add(i);
    }

    this.yearField = new ComboBox<>(FXCollections.observableList(years));
    this.yearField.setValue(DEFAULT_YEAR);

    yearSelectionLabel.setLabelFor(this.yearField);

    final Button submitButton = new Button("Look up");
    submitButton.setDefaultButton(true);
    submitButton.setOnAction(this::lookupFormSubmitted);

    this.add(yearSelectionLabel, 0, 1);
    this.add(this.yearField, 1, 1);
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

    final int birthYear = this.yearField.getValue();
    try {
      final List<Employee> employees = Employee.getEmployeesByBirthYear(database, birthYear);
      if (employees.isEmpty()) {
        this.showEmptyResults(birthYear);
        return;
      }

      this.showEmployeeList(birthYear, employees);
    } catch (final SQLException ex) {
      // An unexpected SQL error occurred
      final ExceptionAlert alert =
          new ExceptionAlert(
              AlertType.ERROR,
              "Database error",
              "An error occurred while retrieving the employees born during " + birthYear,
              ex);
      alert.showAndWait();
      return;
    }
  }

  private void showEmployeeList(final int birthYear, final List<Employee> employees) {
    this.clearGrid();

    this.addTitle(birthYear);

    // Start at 1 because the title already took 0
    int currentRow = 1;

    // Go back button

    final Button goBack = new Button("Go back");
    goBack.setOnAction(this::goBackPressed);

    this.add(goBack, 0, currentRow);
    currentRow += 1;

    // Employee table

    final Node employeeTable = this.createEmployeeTable(employees);
    this.add(employeeTable, 0, currentRow);
    GridPane.setHgrow(employeeTable, Priority.ALWAYS);
    GridPane.setVgrow(employeeTable, Priority.ALWAYS);

    currentRow += 1;

    this.parentScene.resizeToContents();
  }

  private void showEmptyResults(final int birthYear) {
    this.clearGrid();

    this.addTitle(birthYear);

    // Start at 1 because the title already took 0
    int currentRow = 1;

    // Go back button

    final Button goBack = new Button("Go back");
    goBack.setOnAction(this::goBackPressed);

    this.add(goBack, 0, currentRow);
    currentRow += 1;

    // Statement that there are no employees

    final Label noEmployeesLabel =
        new Label("There are no employees who were born in " + birthYear + ".");
    this.add(noEmployeesLabel, 0, currentRow);
    GridPane.setHalignment(noEmployeesLabel, HPos.CENTER);
    GridPane.setValignment(noEmployeesLabel, VPos.CENTER);

    currentRow += 1;

    this.parentScene.resizeToContents();
  }

  private void goBackPressed(final ActionEvent event) {
    this.presentYearPrompt();
  }

  private void addTitle(final int birthYear) {
    final Label title = new Label("Viewing employees born in " + birthYear);
    title.setFont(Font.font(NorthwindApplication.TITLE_FONT_SIZE));
    this.add(title, 0, 0, 1, 1);
    GridPane.setHalignment(title, HPos.CENTER);
    GridPane.setValignment(title, VPos.CENTER);
  }

  private Node createEmployeeTable(final List<Employee> employees) {
    employees.sort(Comparator.comparing(Employee::getLastName));
    final TableView<Employee> table = new TableView<>(FXCollections.observableList(employees));

    final TableColumn<Employee, String> lastNameColumn = new TableColumn<>("Last Name");
    lastNameColumn.setCellValueFactory(
        employee -> new ReadOnlyObjectWrapper<>(employee.getValue().getLastName()));

    final TableColumn<Employee, String> firstNameColumn = new TableColumn<>("First Name");
    firstNameColumn.setCellValueFactory(
        employee -> new ReadOnlyObjectWrapper<>(employee.getValue().getFirstName()));

    table.getColumns().addAll(lastNameColumn, firstNameColumn);

    table.setPrefSize(PREF_TABLE_WIDTH, PREF_TABLE_HEIGHT);

    return table;
  }

  private void clearGrid() {
    this.getChildren().clear();
    this.yearField = null;
  }
}

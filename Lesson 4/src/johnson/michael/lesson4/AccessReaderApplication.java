package johnson.michael.lesson4;

import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class AccessReaderApplication extends Application {
  private ComboBox<String> tableSelector;
  private TextArea dataDisplayArea;

  private Database database;

  public static void main(final String[] args) {
    launch(args);
  }

  private void showButtonClicked(final ActionEvent ev) {
    try {
      this.loadTableData();
    } catch (final SQLException ex) {
      // An SQL error occurred loading the data
      final Alert errorAlert =
          new ExceptionAlert(
              AlertType.ERROR,
              "Error",
              "An unexpected database error occurred while loading data.",
              ex);
      errorAlert.show();
      return;
    }
  }

  @Override
  public void start(final Stage primaryStage) {
    try {
      this.database = new Database();
    } catch (final DatabaseDriverMissingException ex) {
      this.showDriverMissingException(ex);

      // Once the alert is closed, end the Application
      Platform.exit();
      return;
    } catch (final DatabaseMissingException ex) {
      // The database wasn't found in the standard locations. Ask the user for a database.
      this.database = this.askUserForDatabase(primaryStage);

      if (this.database == null) {
        // The database wasn't populated for some reason. Don't attempt to build UI.
        return;
      }
    } catch (final SQLException ex) {
      final Alert errorAlert =
          new ExceptionAlert(
              AlertType.ERROR,
              "Error",
              "An unexpected error occurred while loading the database.",
              ex);
      errorAlert.showAndWait();

      Platform.exit();
      return;
    }

    final BorderPane root = new BorderPane();

    final Node topBar;
    final Node center;

    try {
      topBar = this.buildTopBar();
      center = this.buildCenter();

      this.loadDefaultData();
    } catch (final SQLException ex) {
      final Alert errorAlert =
          new ExceptionAlert(
              AlertType.ERROR,
              "Error",
              "An unexpected database error occurred while loading data.",
              ex);
      errorAlert.showAndWait();

      Platform.exit();
      return;
    }

    root.setTop(topBar);
    root.setCenter(center);

    final Scene rootScene = new Scene(root);

    primaryStage.setTitle("Exercise32_06");
    primaryStage.setScene(rootScene);

    primaryStage.show();
  }

  private Database askUserForDatabase(final Stage stage) {
    while (true) { // Loop forever (we break out of the loop inside)
      final FileChooser chooser = new FileChooser();

      // Add access databases as the only extension filter
      final ExtensionFilter accessFileExtension =
          new ExtensionFilter("Access Database (*.accdb)", "*.accdb");
      chooser.getExtensionFilters().add(accessFileExtension);

      // Show the dialog
      final File chosenFile = chooser.showOpenDialog(stage);
      if (chosenFile == null) {
        // The user chose to cancel

        Platform.exit();
        return null;
      }

      try {
        return new Database(chosenFile.getPath());
      } catch (final SQLException ex) {
        final Alert errorAlert =
            new ExceptionAlert(
                AlertType.ERROR,
                "Error",
                "An error occurred loading the chosen database. Please choose another.",
                ex);
        errorAlert.showAndWait();

        continue;
      } catch (final DatabaseDriverMissingException ex) {
        // This error shouldn't come up at this stage because start() has already tried to load the
        // driver, but we'll handle it anyways
        this.showDriverMissingException(ex);

        Platform.exit();
        return null;
      }
    }
  }

  private Node buildCenter() {
    this.dataDisplayArea = new TextArea();

    return this.dataDisplayArea;
  }

  private Node buildTopBar() throws SQLException {
    final HBox topBar = new HBox(10.0);
    topBar.setAlignment(Pos.CENTER);

    this.tableSelector = new ComboBox<>();
    this.fillTableSelector();

    final Label tableNameLabel = new Label("Table Name");
    tableNameLabel.setLabelFor(this.tableSelector);

    final Button showButton = new Button("Show Contents");
    showButton.setOnAction(this::showButtonClicked);

    topBar.getChildren().addAll(tableNameLabel, this.tableSelector, showButton);

    return topBar;
  }

  private void fillTableSelector() throws SQLException {
    final List<String> tableNames = this.database.getTableNames();

    final ObservableList<String> tableSelectorItems = this.tableSelector.getItems();
    tableSelectorItems.setAll(tableNames);
  }

  private void loadTableData() throws SQLException {
    final String table = this.tableSelector.getValue();

    if ((table == null) || table.isEmpty()) {
      // For some reason, the selection is empty. Empty the data display area and return.
      this.dataDisplayArea.setText("");
      return;
    }

    try (final ResultSet results =
        this.database.getTableRows(
            table)) { // Contain the ResultSet in a try-with-resources so that it closes when it
      // goes out of scope
      final ResultSetMetaData resultMetaData = results.getMetaData();
      final int columnCount = resultMetaData.getColumnCount();

      final StringBuilder dataToDisplay = new StringBuilder();

      for (int i = 1; i <= columnCount; i++) {
        final String columnName = resultMetaData.getColumnLabel(i);

        if (i > 1) {
          // After the first column, begin with a tab character
          dataToDisplay.append("    \t");
        }
        dataToDisplay.append(columnName);
      }

      while (results.next()) {
        // If this isn't the first row, begin with a new line to break from the previous line
        dataToDisplay.append('\n');

        for (int i = 1; i <= columnCount; i++) {
          if (i > 1) {
            dataToDisplay.append("    \t");
          }
          final String data = results.getString(i);
          dataToDisplay.append(data);
        }
      }

      this.dataDisplayArea.setText(dataToDisplay.toString());
    }
  }

  private void loadDefaultData() throws SQLException {
    this.tableSelector.setValue(this.tableSelector.getItems().get(0)); // Select the first item

    this.loadTableData();
  }

  private void showDriverMissingException(final DatabaseDriverMissingException ex) {
    // Build the alert, show it, and wait for it to be closed
    final Alert missingAlert =
        new ExceptionAlert(
            AlertType.ERROR,
            "Database driver missing",
            "The program was unable to load the UCanAccess database driver.",
            ex);
    missingAlert.showAndWait();
  }
}

package johnson.michael.cist2373.midterm;

import java.io.File;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import johnson.michael.cist2373.midterm.database.Database;

/**
 * NorthwindApplication provides a JavaFX application for interacting with the Northwind database.
 */
public class NorthwindApplication extends Application {
  public static final double TITLE_FONT_SIZE = 16d;

  public static void main(final String[] args) {
    launch(args);
  }

  public static Alert createAlert(final AlertType type, final String title, final String message) {
    final Alert alert = new Alert(type, message); // Initialize with the correct type and message
    alert.setTitle(title);

    alert.setHeaderText(null); // Clear header

    return alert;
  }

  @Override
  public void start(final Stage primaryStage) {
    // Prime the database
    try {
      Database.getCommonConnection();
    } catch (final DatabaseDriverMissingException ex) {
      this.showDriverMissingException(ex);

      // Once the alert is closed, end the Application
      Platform.exit();
      return;
    } catch (final DatabaseMissingException ex) {
      // The database wasn't found in the standard locations. Ask the user for a database.
      final Database database = this.askUserForDatabase(primaryStage);

      if (database == null) {
        // The database wasn't populated for some reason. Don't attempt to build UI.
        return;
      }

      Database.setCommonConnection(database);
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

    primaryStage.setTitle("Northwind");

    final NorthwindScene primaryScene = new NorthwindScene(primaryStage);

    primaryStage.setScene(primaryScene);

    primaryStage.show();
  }

  private Database askUserForDatabase(final Stage stage) {
    while (true) { // Loop forever (we break out of the loop inside)
      final FileChooser chooser = new FileChooser();

      // Add access databases as the only extension filter
      final ExtensionFilter accessFileExtension1 =
          new ExtensionFilter("Access Database (*.accdb)", "*.accdb");
      final ExtensionFilter accessFileExtension2 =
          new ExtensionFilter("Access Database (*.mdb)", "*.mdb");
      chooser.getExtensionFilters().addAll(accessFileExtension1, accessFileExtension2);

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

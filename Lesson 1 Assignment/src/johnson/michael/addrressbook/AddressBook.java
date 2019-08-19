package johnson.michael.addrressbook;

import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class AddressBook extends Application {
  private AddressFile addressFile;

  private Address displayedAddress;

  private LengthLimitedTextField nameField;
  private LengthLimitedTextField streetField;
  private LengthLimitedTextField cityField;
  private LengthLimitedTextField stateField;
  private LengthLimitedTextField zipField;

  public static void main(final String[] args) {
    // Call the JavaFX launcher
    Application.launch(args);
  }

  @Override
  public void start(final Stage stage) {
    try {
      this.addressFile = new AddressFile("addresses.dat");
    } catch (IOException ex) {
      // Alert the user of the exception and wait for the user to close the error message
      this.showExceptionAndWait("Fatal error", "Failed to open the address database.", ex);

      // Close the stage, ending the program
      stage.close();

      return;
    }

    this.displayedAddress = null;

    final BorderPane root = new BorderPane();

    final VBox controlColumn = new VBox();
    // Set spacing between children so that controls aren't directly abutted against each other
    controlColumn.setSpacing(12d);
    // Add padding so that the control column isn't touching the window borders for visual niceness
    controlColumn.setPadding(new Insets(12, 12, 12, 12));

    // Add the Name field
    this.nameField = this.addSingleInputRow("Name", AddressFile.NAME_LEN, controlColumn);

    // Add the Street field
    this.streetField = this.addSingleInputRow("Street", AddressFile.STREET_LEN, controlColumn);

    // Add the three TextFields on the final row
    this.addFinalTextFieldRow(controlColumn);

    // Add the bottom button row
    this.addButtonRow(controlColumn);

    root.setCenter(controlColumn);

    final Scene scene = new Scene(root);
    stage.setScene(scene);

    try {
      final Address firstAddress = this.addressFile.readAddress();
      this.displayAddress(firstAddress);
    } catch (EOFException ex) {
      // Silently ignore EOFException and clear the text fields; there are no records in this file
      this.clearTextFields();
    } catch (IOException ex) {
      // Alert the user of the exception and wait for the user to close the error message
      this.showExceptionAndWait("Fatal error", "Failed to read the address database.", ex);

      // Close the stage, ending the program
      stage.close();

      return;
    }

    stage.show();
  }

  private void addButtonPressed() {
    if (!this.preRecordChangeCheck()) {
      // The record has changed and the user wouldn't like to continue.
      return;
    }

    try {
      // Seek to the end of the address file plus one. When the user presses update, it will update
      // the last record, because it subtracts one.
      this.addressFile.seekIndex(this.addressFile.getNumAddresses() + 1);

      this.clearTextFields();
    } catch (IOException ex) {
      this.showExceptionAndWait("Error opening new record",
          "An error occurred while opening a new record.\nPlease try again later.", ex);
      return;
    }
  }

  private void firstButtonPressed() {
    if (this.addressFile.getCurrentIndex() == 1) {
      // The currently displayed record is the first record; no op
      return;
    }

    if (!this.preRecordChangeCheck()) {
      // The record has changed and the user wouldn't like to continue.
      return;
    }

    try {
      // Seek to the first record
      this.addressFile.seekIndex(0);

      // Read the first record
      final Address address = this.addressFile.readAddress();
      this.displayAddress(address);
    } catch (EOFException ex) {
      // Ignore the EOF exception and clear the text fields; there are no records in this file

      this.clearTextFields();
    } catch (IOException ex) {
      this.showExceptionAndWait("Error reading first record",
          "An error occurred while reading the first record.\nPlease try again later.", ex);
      return;
    }
  }

  private void previousButtonPressed() {
    if (!this.preRecordChangeCheck()) {
      // The record has changed and the user wouldn't like to continue.
      return;
    }

    try {
      // Seek to the current index minus 2. The currently displayed record is the current index
      // minus one, so the previous one is minus two.
      long seekTo = this.addressFile.getCurrentIndex() - 2;
      if (seekTo < 0) {
        // This would seek past the beginning of the file. Alert the user and don't continue.
        final Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("No more records");
        alert.setHeaderText(null);
        alert.setContentText("Can not seek to the previous record. You are on the first record.");
        alert.showAndWait();

        return;
      }

      this.addressFile.seekIndex(seekTo);

      // Read the first record
      final Address address = this.addressFile.readAddress();
      this.displayAddress(address);
    } catch (EOFException ex) {
      // There is no previous record. This can happen if the user is in a new database. Clear the
      // text fields.

      this.clearTextFields();
    } catch (IOException ex) {
      this.showExceptionAndWait("Error reading previous record",
          "An error occurred while reading the previous record.\nPlease try again later.", ex);
      return;
    }
  }

  private void nextButtonPressed() {
    if (!this.preRecordChangeCheck()) {
      // The record has changed and the user wouldn't like to continue.
      return;
    }

    try {
      // Read the next record
      final Address address = this.addressFile.readAddress();
      this.displayAddress(address);
    } catch (EOFException ex) {
      final Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("No more records");
      alert.setHeaderText(null);
      alert.setContentText("Can not seek to the next record. You are on the last record.");
      alert.showAndWait();

      return;
    } catch (IOException ex) {
      this.showExceptionAndWait("Error reading next record",
          "An error occurred while reading the next record.\nPlease try again later.", ex);
      return;
    }
  }

  private void lastButtonPressed() {
    if (!this.preRecordChangeCheck()) {
      // The record has changed and the user wouldn't like to continue.
      return;
    }

    try {
      // The last record is at numAddresses minus one
      final long seekTo = this.addressFile.getNumAddresses() - 1;
      if (seekTo < 0) {
        // There are 0 addresses in the database. Display an empty record.
        this.clearTextFields();
        return;
      }

      // Read the next record
      final Address address = this.addressFile.readAddress(seekTo);
      this.displayAddress(address);
    } catch (IOException ex) {
      this.showExceptionAndWait("Error reading last record",
          "An error occurred while reading the first record.\nPlease try again later.", ex);
      return;
    }
  }

  private void updateButtonPressed() {
    // The index should be one past the record that we're currently viewing. Thus, we need to write
    // to the index at (currentIndex - 1).
    long writeToIndex = this.addressFile.getCurrentIndex() - 1;
    if (writeToIndex < 0) {
      // This database currently has no records, so we're attempting to write to a negative index.
      // Change that to index 0.
      writeToIndex = 0;
    }

    final Address address = new Address();
    address.setName(this.nameField.getText());
    address.setStreet(this.streetField.getText());
    address.setCity(this.cityField.getText());
    address.setState(this.stateField.getText());
    address.setZip(this.zipField.getText());

    try {
      this.addressFile.writeAddress(writeToIndex, address);
      this.displayedAddress = address;

      final Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("Record saved");
      alert.setHeaderText(null);
      alert.setContentText("Your record has been saved.");
      alert.showAndWait();
    } catch (IOException ex) {
      this.showExceptionAndWait("Error updating record",
          "An error occurred while updating the record.\nPlease try again later.", ex);
    }
  }

  private LinkedList<String> validate() {
    final LinkedList<String> errors = new LinkedList<>();

    if (this.nameField.getCleanedText().length() <= 0) {
      errors.add("You must enter a name.");
    }

    if (this.streetField.getCleanedText().length() <= 0) {
      errors.add("You must enter a street address.");
    }

    if (this.cityField.getCleanedText().length() <= 0) {
      errors.add("You must enter a city.");
    }

    final String cleanedStateField = this.stateField.getCleanedText();
    if (cleanedStateField.length() <= 0) {
      errors.add("You must enter a state.");
    } else if (cleanedStateField.length() != 2) {
      errors.add("The state must be two letters.");
    } else {
      boolean foundNonUppercase = false;
      for (final char c : cleanedStateField.toCharArray()) {
        if (!Character.isLetter(c) || !Character.isUpperCase(c)) {
          foundNonUppercase = true;
          break;
        }
      }

      if (foundNonUppercase) {
        errors.add("The state must be comprised only of uppercase letters.");
      }
    }

    final String cleanedZipCode = this.zipField.getCleanedText();
    if (cleanedZipCode.length() <= 0) {
      errors.add("You must enter a ZIP Code.");
    } else if (cleanedZipCode.length() != 5) {
      errors.add("The ZIP Code must be five digits.");
    } else {
      boolean foundNonDigit = false;
      for (final char c : cleanedZipCode.toCharArray()) {
        if (!Character.isDigit(c)) {
          foundNonDigit = true;
          break;
        }
      }

      if (foundNonDigit) {
        errors.add("The ZIP Code must be comprised only of digits.");
      }
    }

    return errors;
  }

  private boolean preRecordChangeCheck() {
    if (this.hasRecordChanged()) {
      final boolean continueDespiteRecordChange = this.promptUnsavedRecord();
      if (!continueDespiteRecordChange) {
        return false;
      }
    }

    return true;
  }

  private void displayAddress(final Address address) {
    this.nameField.setText(address.getName());
    this.streetField.setText(address.getStreet());
    this.cityField.setText(address.getCity());
    this.stateField.setText(address.getState());
    this.zipField.setText(address.getZip());

    this.displayedAddress = address;
  }

  private void clearTextFields() {
    this.nameField.setText("");
    this.streetField.setText("");
    this.cityField.setText("");
    this.stateField.setText("");
    this.zipField.setText("");

    this.displayedAddress = null;
  }

  private LengthLimitedTextField addSingleInputRow(
      final String labelText, final int maxLength, final Pane parent) {
    final HBox row = new HBox();
    row.setSpacing(8);

    final LengthLimitedTextField textField = this.addLabelAndTextField(labelText, maxLength, row);

    parent.getChildren().add(row);

    return textField;
  }

  private LengthLimitedTextField addLabelAndTextField(
      final String labelText, final int maxLength, final Pane parent) {
    if (parent == null) {
      throw new IllegalArgumentException("parent cannot be null");
    }

    if (maxLength <= 0) {
      throw new IllegalArgumentException(String.format(
          "maxLength must be greater than 0; the given max length was %d", maxLength));
    }

    final Label label = new Label(labelText);
    label.setAlignment(Pos.CENTER_LEFT);

    final LengthLimitedTextField textField = new LengthLimitedTextField(maxLength);
    textField.setAlignment(Pos.CENTER_LEFT);

    // Set the preferred width of the text field to its length
    textField.setPrefWidth(this.getTextFieldWidth(maxLength, textField.getFont()));

    // Make the program more accessible for screen readers by associating the label with the text
    // field
    label.setLabelFor(textField);

    // Add the label and text field to the parent
    parent.getChildren().addAll(label, textField);

    return textField;
  }

  private void addFinalTextFieldRow(final Pane parent) {
    final HBox row = new HBox();
    row.setSpacing(8);

    this.cityField = this.addLabelAndTextField("City", AddressFile.CITY_LEN, row);
    this.stateField = this.addLabelAndTextField("State", AddressFile.STATE_LEN, row);
    this.zipField = this.addLabelAndTextField("Zip", AddressFile.ZIP_LEN, row);

    parent.getChildren().add(row);
  }

  private void addButtonRow(final Pane parent) {
    final HBox row = new HBox();
    row.setSpacing(8);
    row.setAlignment(Pos.CENTER);

    this.addButton("Add", e -> this.addButtonPressed(), row);
    this.addButton("First", e -> this.firstButtonPressed(), row);
    this.addButton("Previous", e -> this.previousButtonPressed(), row);
    this.addButton("Next", e -> this.nextButtonPressed(), row);
    this.addButton("Last", e -> this.lastButtonPressed(), row);
    this.addButton("Update", e -> this.updateButtonPressed(), row);

    parent.getChildren().add(row);
  }

  private void addButton(
      final String buttonText, EventHandler<ActionEvent> eventHandler, final Pane parent) {
    final Button button = new Button(buttonText);

    button.setOnAction(eventHandler);

    parent.getChildren().add(button);
  }

  private double getTextFieldWidth(final int maxLength, final Font font) {
    final FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();
    final FontMetrics fontMetrics = fontLoader.getFontMetrics(font);

    // Create an array of that's (maxLength + 1) long, filled with 'W'. We add 1, otherwise the
    // field will scroll 1 character to the right. 'W' just happens to be a wide character that
    // should give us a sane width.
    final char[] dummyChars = new char[maxLength + 1];
    Arrays.fill(dummyChars, 'W');

    // Compute the width of the characters and return it
    return fontMetrics.computeStringWidth(new String(dummyChars));
  }

  private boolean hasRecordChanged() {
    if (this.displayedAddress == null) {
      final boolean nameChanged = (!"".equals(this.nameField.getCleanedText()));
      final boolean streetChanged = (!"".equals(this.streetField.getCleanedText()));
      final boolean cityChanged = (!"".equals(this.cityField.getCleanedText()));
      final boolean stateChanged = (!"".equals(this.stateField.getCleanedText()));
      final boolean zipChanged = (!"".equals(this.zipField.getCleanedText()));

      return nameChanged || streetChanged || cityChanged || stateChanged || zipChanged;
    }

    final boolean nameChanged =
        (!this.nameField.getCleanedText().equals(this.displayedAddress.getName()));
    final boolean streetChanged =
        (!this.streetField.getCleanedText().equals(this.displayedAddress.getStreet()));
    final boolean cityChanged =
        (!this.cityField.getCleanedText().equals(this.displayedAddress.getCity()));
    final boolean stateChanged =
        (!this.stateField.getCleanedText().equals(this.displayedAddress.getState()));
    final boolean zipChanged =
        (!this.zipField.getCleanedText().equals(this.displayedAddress.getZip()));

    return nameChanged || streetChanged || cityChanged || stateChanged || zipChanged;
  }

  private boolean promptUnsavedRecord() {
    final Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle("Record changed");
    alert.setHeaderText(null);
    alert.setContentText(
        "You have unsaved changes. If you browse to a different record, those changes\nwill be lost. Would you like to continue?");
    Optional<ButtonType> result = alert.showAndWait();

    if (result.isPresent() && result.get() == ButtonType.OK) {
      // The user has consented to continuing and
      return true;
    }

    return false;
  }

  private void showExceptionAndWait(
      final String title, final String errorMessage, final Exception ex) {
    final Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(errorMessage);

    this.addExpandableExceptionTrace(alert, ex);

    alert.showAndWait();
  }

  private void addExpandableExceptionTrace(final Alert alert, final Exception ex) {
    final StringWriter exceptionStringWriter = new StringWriter();
    final PrintWriter exceptionPrintWriter = new PrintWriter(exceptionStringWriter);
    ex.printStackTrace(exceptionPrintWriter);

    final String exceptionMessage = exceptionStringWriter.toString();

    final TextArea stackTraceArea = new TextArea(exceptionMessage);
    stackTraceArea.setWrapText(true);
    stackTraceArea.setEditable(false);

    GridPane.setVgrow(stackTraceArea, Priority.ALWAYS);
    GridPane.setHgrow(stackTraceArea, Priority.ALWAYS);

    final GridPane root = new GridPane();
    root.add(new Label("Stack trace:"), 0, 0);
    root.add(stackTraceArea, 0, 1);

    alert.getDialogPane().setExpandableContent(root);
  }
}

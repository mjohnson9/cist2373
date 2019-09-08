package johnson.michael.bmiclient;

import static javafx.application.Application.launch;

import java.io.EOFException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BMIClientApplication extends Application {

  private BMIClient client;
  private TextArea logArea;

  private TextField heightField;
  private TextField weightField;

  @Override
  public void start(Stage primaryStage) {
    try {
      this.client = new BMIClient("127.0.0.1", 5050);
    } catch (final IOException ex) {
      this.showExceptionAndWait(
          "Error connecting to server", "There was an error connecting to the BMI server.", ex);
      primaryStage.close();
      return;
    }

    final VBox topVbox = new VBox();
    topVbox.setSpacing(4);

    final HBox weightHbox = new HBox();
    weightHbox.setSpacing(12);

    this.weightField = new TextField();
    this.weightField.setPrefWidth(60);

    final Label weightLabel = new Label("Weight:");
    weightLabel.setLabelFor(this.weightField);

    weightHbox.getChildren().addAll(weightLabel, this.weightField);

    topVbox.getChildren().add(weightHbox);

    final HBox heightHbox = new HBox();
    heightHbox.setSpacing(12);

    this.heightField = new TextField();
    this.heightField.setPrefWidth(60);

    final Label heightLabel = new Label("Height:");
    heightLabel.setLabelFor(this.heightField);

    final Button submitButton = new Button("Submit");
    submitButton.setOnAction(
        (ev) -> {
          this.submitButtonPressed();
        });

    heightHbox.getChildren().addAll(heightLabel, this.heightField, submitButton);

    topVbox.getChildren().add(heightHbox);

    this.logArea = new TextArea();
    this.logArea.setWrapText(true);

    BorderPane root = new BorderPane();
    root.setTop(topVbox);
    root.setCenter(this.logArea);

    Scene scene = new Scene(root, 300, 250);

    primaryStage.setTitle("Excercise33_01Client");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private void submitButtonPressed() {
    List<String> errors = new LinkedList<String>();

    // The compiler will assume that these can reach the getBMI call uninitialized if we don't
    // initialize them here. So, we initialize them to 0 to make the compiler happy.
    double height = 0.0;
    double weight = 0.0;

    if (this.heightField.getText().trim().length() <= 0) {
      errors.add("You must enter a height.");
    } else {
      try {
        height = Double.parseDouble(this.heightField.getText().trim());
      } catch (final NumberFormatException ex) {
        errors.add("Height must be a decimal number in inches.");
      }
    }

    if (this.weightField.getText().trim().length() <= 0) {
      errors.add("You must enter a weight.");
    } else {
      try {
        weight = Double.parseDouble(this.weightField.getText().trim());
      } catch (final NumberFormatException ex) {
        errors.add("Weight must be a decimal number in pounds.");
      }
    }

    if (!errors.isEmpty()) {
      this.showValidationErrors(errors);
      return;
    }

    final BMI bmi;
    try {
      bmi = this.client.getBMI(height, weight);
    } catch (final EOFException ex) {
      this.showExceptionAndWait("The server hung up", "The server has closed the connection.", ex);
      Platform.exit();
      return;
    } catch (final IOException | ClassNotFoundException ex) {
      this.showExceptionAndWait(
          "An error occurred", "An error occurred while retrieving the BMI. Please try again.", ex);
      return;
    }

    this.logArea.appendText("Weight: " + weight + "\n");
    this.logArea.appendText("Height: " + height + "\n");
    // Round the BMI to two decimal places
    final double roundedBmi = (Math.round(bmi.getBmi() * 100.0) / 100.0);
    this.logArea.appendText("BMI is  " + roundedBmi + ". " + bmi.getStatus() + "\n");
  }

  private void showValidationErrors(final List<String> errors) {
    final Alert alert = new Alert(AlertType.WARNING);
    alert.setTitle("Input errors");
    alert.setHeaderText(null);

    final StringBuilder messageBuilder = new StringBuilder();

    for (final String error : errors) {
      if (messageBuilder.length() > 0) {
        messageBuilder.append('\n');
      }

      messageBuilder.append(error);
    }

    alert.setContentText(messageBuilder.toString());
    alert.showAndWait();
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

  /** @param args the command line arguments */
  public static void main(String[] args) {
    launch(args);
  }
}

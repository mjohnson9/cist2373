package johnson.michael.bmiserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class BMIServerApplication extends Application {

  private BMIServer server;
  private TextArea logArea;

  @Override
  public void start(Stage primaryStage) {
    try {
      this.server = new BMIServer(this, 5050);
      this.server.start();
    } catch (final IOException ex) {
      this.showExceptionAndWait(
          "Error starting server", "There was an error starting the BMI server.", ex);
      primaryStage.close();
      return;
    }

    this.logArea = new TextArea();
    this.logArea.setWrapText(true);

    StackPane root = new StackPane();
    root.getChildren().add(this.logArea);

    Scene scene = new Scene(root, 300, 250);

    primaryStage.setTitle("Excercise33_01Server");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  @Override
  public void stop() {
    this.server.close();
  }

  public void safeWriteMessage(final String message) {
    Platform.runLater(
        () -> {
          this.logArea.appendText(message);
        });
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

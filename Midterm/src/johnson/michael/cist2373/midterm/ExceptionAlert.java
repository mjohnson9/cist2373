package johnson.michael.cist2373.midterm;

import java.io.PrintWriter;
import java.io.StringWriter;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * ExceptionAlert provides a convenience wrapper around Alert for expedient initialization of Alerts
 * detailing exceptions.
 */
public class ExceptionAlert extends Alert {
  /**
   * Creates a new ExceptionAlert and prepares its contents.
   *
   * @param alertType the type of alert
   * @param title the title of the alert
   * @param friendlyMessage the friendly user message
   * @param ex the exception to be shown
   */
  public ExceptionAlert(
      final AlertType alertType,
      final String title,
      final String friendlyMessage,
      final Exception ex) {
    super(alertType);

    // Set the contents of this alert
    this.setTitle(title);
    this.setHeaderText(null);
    this.setContentText(friendlyMessage);
    this.addExpandableExceptionTrace(ex);
  }

  private void addExpandableExceptionTrace(final Exception ex) {
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

    this.getDialogPane().setExpandableContent(root);
  }
}

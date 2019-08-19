package johnson.michael.addrressbook;

import java.util.function.UnaryOperator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;

public class LengthLimitedTextField extends TextField {
  private final int maxLength;

  /**
   * Creates a new LengthLimitedTextField with a maximum length of maxLength.
   * @param maxLength The maximum length of user entered text.
   */
  public LengthLimitedTextField(final int maxLength) {
    super();

    if (maxLength <= 0) {
      throw new IllegalArgumentException("maxLength must be greater than zero");
    }

    this.maxLength = maxLength;

    final UnaryOperator<Change> changeHandler = change -> {
      // Check if the content actually changed
      if (change.isContentChange()) {
        // Validate the new text
        final String newText = change.getControlNewText();
        if (newText.length() > this.maxLength) {
          // Reject the change because it's longer than the max length (credit:
          // https://stackoverflow.com/a/33217981/476470)
          return null;
        }
      }

      // There is no user specified text formatter
      // Accept the change
      return change;
    };

    // Set the text field's formatter to our changeHandler
    this.setTextFormatter(new TextFormatter(changeHandler));
  }

  /**
   * Gets the text from the LengthLimitedTextField and cleans it up. It trims leading and trailing
   * whitespace and ensures that the text is within the maximum length.
   * @return The cleaned text.
   */
  public String getCleanedText() {
    String str = this.getText();
    str = str.trim(); // Trim leading and trailing whitespace
    if (str.length() > this.maxLength) {
      // The string is longer than the maximum length. This shouldn't be possible because of the
      // text formatter, but handle it properly anyways.
      str = str.substring(0, this.maxLength);
    }

    return str;
  }
}

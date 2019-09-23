package johnson.michael.lesson5;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;

public class DecimalTextField extends TextField {
  protected final DecimalFormat format;

  public DecimalTextField(final DecimalFormat format) {
    if (format == null) {
      throw new NullPointerException("format cannot be null");
    }

    this.format = format;

    this.setTextFormatter(new TextFormatter<String>(this::reformatText));
  }

  public double getValue() {
    final Number number;
    try {
      number = this.format.parse(this.getText());
      if (number == null) {
        return 0.0d;
      }
    } catch (final ParseException ex) {
      return 0.0d;
    }

    return Double.valueOf(String.valueOf(number));
  }

  private Change reformatText(final Change change) {
    if (!change.isContentChange()) {
      // There was no actual change; shortcut
      return change;
    }

    final String newText = change.getControlNewText();
    if (newText == null || newText.isEmpty() || newText.length() <= 0) {
      // The new text is empty
      return change;
    }

    final ParsePosition textPosition = new ParsePosition(0);
    final Number parsedNumber = this.format.parse(newText, textPosition);
    if (parsedNumber == null) {
      // The parser was unable to parse any number
      return null;
    }

    final String parsedText = newText.substring(0, textPosition.getIndex());

    // Make the formatter always include a decimal if the user is attempting to enter a decimal
    final DecimalFormatSymbols decimalSymbols = this.format.getDecimalFormatSymbols();
    final CharSequence decimalSeparator = String.valueOf(decimalSymbols.getDecimalSeparator());
    if (parsedText.contains(decimalSeparator)) {
      this.format.setDecimalSeparatorAlwaysShown(true);

      final char zero = decimalSymbols.getZeroDigit();
      final String zeroString = String.valueOf(zero);
      if (parsedText.endsWith(zeroString)) {
        // Allow for trailing zeroes
        int numTrailingZeroes = 0;
        for (int i = parsedText.length() - 1; i >= 0; i--) {
          if (parsedText.charAt(i) != zero) {
            // We're out of zeroes; break out of the loop
            break;
          }

          numTrailingZeroes += 1;
        }

        this.format.setMinimumFractionDigits(numTrailingZeroes);
      }
    } else {
      this.format.setDecimalSeparatorAlwaysShown(false);
    }

    String formattedNumber = this.format.format(parsedNumber);

    change.setRange(0, change.getControlText().length());
    change.setText(formattedNumber);

    return change;
  }
}

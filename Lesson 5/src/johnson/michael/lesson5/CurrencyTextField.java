package johnson.michael.lesson5;

import java.text.DecimalFormat;
import java.util.Currency;

public class CurrencyTextField extends DecimalTextField {
  public CurrencyTextField(final Currency currency) {
    super(new DecimalFormat());
    this.format.setCurrency(currency);
  }

  public CurrencyTextField(final Currency currency, final DecimalFormat format) {
    super(format);
    this.format.setCurrency(currency);
  }
}

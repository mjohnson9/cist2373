package johnson.michael.lesson5;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Currency;
import java.util.Locale;
import java.util.Locale.Category;

public class CurrencyTextField extends DecimalTextField {
  public CurrencyTextField(final Currency currency) {
    this(currency, new DecimalFormat());
  }

  public CurrencyTextField(final Currency currency, final DecimalFormat format) {
    super(format);
    final Locale userDisplayLocale = Locale.getDefault(Category.DISPLAY);
    final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(userDisplayLocale);
    symbols.setCurrency(currency);
    this.format.setDecimalFormatSymbols(symbols);

    this.format.setMinimumFractionDigits(currency.getDefaultFractionDigits());
    this.format.setMaximumFractionDigits(currency.getDefaultFractionDigits());
    this.format.setDecimalSeparatorAlwaysShown(true);

    this.setText("0");
  }
}

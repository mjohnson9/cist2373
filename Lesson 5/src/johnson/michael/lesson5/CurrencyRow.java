package johnson.michael.lesson5;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class CurrencyRow {
  private final List<Node> children;
  private Currency currency;
  private DecimalFormat exchangeRateFormat;
  private DecimalTextField exchangeRateField;
  private CurrencyTextField currencyField;

  public CurrencyRow(final String currencyCode, final double exchangeRate) {
    this.children = new ArrayList<>();
    // Use the system default FORMAT locale to format the exchange rate field
    this.exchangeRateFormat = new DecimalFormat();
    this.currency = Currency.getInstance(currencyCode);

    this.buildChildren(exchangeRate);
  }

  public List<Node> getRowItems() {
    return Collections.unmodifiableList(this.children);
  }

  public void setNewInputAmount(final double input) {
    final double exchangeRate = this.exchangeRateField.getValue();
    final double newValue = input * exchangeRate;
    this.currencyField.setText(String.valueOf(newValue));
  }

  private void buildChildren(final double exchangeRate) {
    final Label currencyNameLabel = new Label(this.currency.getDisplayName());
    this.children.add(currencyNameLabel);

    this.exchangeRateField = new DecimalTextField(this.exchangeRateFormat);
    this.exchangeRateField.setText(this.exchangeRateFormat.format(exchangeRate));
    this.children.add(this.exchangeRateField);

    this.currencyField = new CurrencyTextField(this.currency);
    this.currencyField.setEditable(false);
    this.currencyField.setText("0");
    this.children.add(this.currencyField);
  }
}

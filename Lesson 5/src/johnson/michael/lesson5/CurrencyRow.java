package johnson.michael.lesson5;

import java.text.DecimalFormat;
import java.util.Currency;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class CurrencyRow extends HBox {
  private Currency currency;
  private DecimalFormat exchangeRateFormat;

  private DecimalTextField exchangeRateField;
  private CurrencyTextField currencyField;

  public CurrencyRow(final String currencyCode, final double exchangeRate) {
    // Use the system default FORMAT locale to format the exchange rate field
    this.exchangeRateFormat = new DecimalFormat();
    this.currency = Currency.getInstance(currencyCode);

    this.buildChildren(exchangeRate);
  }

  public void setNewInputAmount(final double input) {
    final double exchangeRate = this.exchangeRateField.getValue();
    final double newValue = input * exchangeRate;
    this.currencyField.setText(String.valueOf(newValue));
  }

  private void buildChildren(final double exchangeRate) {
    final ObservableList<Node> children = this.getChildren();

    final Label currencyNameLabel = new Label(this.currency.getDisplayName());
    children.add(currencyNameLabel);

    this.exchangeRateField = new DecimalTextField(this.exchangeRateFormat);
    this.exchangeRateField.setText(this.exchangeRateFormat.format(exchangeRate));
    children.add(this.exchangeRateField);

    this.currencyField = new CurrencyTextField(this.currency);
    this.currencyField.setEditable(false);
    this.currencyField.setText("0");
    children.add(this.currencyField);
  }
}

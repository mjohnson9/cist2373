package johnson.michael.lesson5;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class CurrencyRow extends HBox {
  private TextField exchangeRateField;

  public CurrencyRow() {}

  private void buildChildren(final String currencyName, final double exchangeRate) {
    final ObservableList<Node> children = this.getChildren();

    final Label currencyNameLabel = new Label(currencyName);
    children.add(currencyNameLabel);

    this.exchangeRateField = new TextField();
  }

  private void setupDecimalTextFieldFormatter(final TextField field) {
    field.setTextFormatter((change) -> {});
  }
}

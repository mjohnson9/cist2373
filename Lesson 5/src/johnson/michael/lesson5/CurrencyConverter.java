package johnson.michael.lesson5;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class CurrencyConverter extends Application {
  private static final Currency defaultCurrency = Currency.getInstance("USD");

  private CurrencyTextField defaultCurrencyField;

  private List<CurrencyRow> rows = new ArrayList<>();
  private int currentRow = 0;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    final GridPane primaryPane = new GridPane();

    this.addDollarEntryLine(primaryPane);
    this.addTableHeaders(primaryPane);

    final CurrencyRow canadianDollarsRow = this.buildCurrencyLine("CAD", 1.33d);
    this.addRow(primaryPane, canadianDollarsRow);

    final CurrencyRow euroRow = this.buildCurrencyLine("EUR", 0.91d);
    this.addRow(primaryPane, euroRow);

    final CurrencyRow gbpRow = this.buildCurrencyLine("GBP", 0.80d);
    this.addRow(primaryPane, gbpRow);

    final Scene primaryScene = new Scene(primaryPane);

    primaryStage.setTitle("Exercise36_06");
    primaryStage.setScene(primaryScene);
    primaryStage.show();
  }

  private void addRow(final GridPane pane, final CurrencyRow row) {
    int columnIndex = 0;
    for (final Node node : row.getRowItems()) {
      pane.add(node, columnIndex, this.currentRow);
      columnIndex += 1;
    }

    this.rows.add(row);

    this.currentRow += 1;
  }

  private void addDollarEntryLine(final GridPane pane) {
    final Label dollarLabel = new Label(defaultCurrency.getDisplayName());

    this.defaultCurrencyField = new CurrencyTextField(defaultCurrency);

    dollarLabel.setLabelFor(this.defaultCurrencyField);

    final Button convertButton = new Button("Convert");
    convertButton.setOnAction(this::convertPressed);

    pane.addRow(this.currentRow, dollarLabel, this.defaultCurrencyField, convertButton);
    this.currentRow += 1;
  }

  private void addTableHeaders(final GridPane pane) {
    final Label exchangeRateLabel = new Label("Exchange Rate");
    pane.add(exchangeRateLabel, 1, this.currentRow);

    final Label convertedAmountLabel = new Label("Converted Amount");
    pane.add(convertedAmountLabel, 2, this.currentRow);

    this.currentRow += 1;
  }

  private CurrencyRow buildCurrencyLine(final String currency, final double exchangeRate) {
    final CurrencyRow currencyRow = new CurrencyRow(currency, exchangeRate);

    return currencyRow;
  }

  private void convertPressed(ActionEvent event) {
    final double inputAmount = this.defaultCurrencyField.getValue();
    for (final CurrencyRow row : this.rows) {
      row.setNewInputAmount(inputAmount);
    }
  }
}

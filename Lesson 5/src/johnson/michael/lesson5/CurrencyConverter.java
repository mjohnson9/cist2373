package johnson.michael.lesson5;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CurrencyConverter extends Application {
  private static final Currency defaultCurrency = Currency.getInstance("USD");

  private CurrencyTextField defaultCurrencyField;

  private List<CurrencyRow> rows = new ArrayList<>();

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    final VBox mainContent = new VBox();
    final ObservableList<Node> mainChildren = mainContent.getChildren();

    final Node dollarEntryLine = this.buildDollarEntryLine();
    mainChildren.add(dollarEntryLine);

    final CurrencyRow canadianDollarsRow = this.buildCurrencyLine("CAD", 1.33d);
    mainChildren.add(canadianDollarsRow);
    this.rows.add(canadianDollarsRow);

    final CurrencyRow euroRow = this.buildCurrencyLine("EUR", 0.91d);
    mainChildren.add(euroRow);
    this.rows.add(euroRow);

    final CurrencyRow gbpRow = this.buildCurrencyLine("GBP", 0.80d);
    mainChildren.add(gbpRow);
    this.rows.add(gbpRow);

    final BorderPane mainPane = new BorderPane(mainContent);

    final Scene primaryScene = new Scene(mainPane);

    primaryStage.setScene(primaryScene);
    primaryStage.show();
  }

  private Node buildDollarEntryLine() {
    final HBox dollarEntryLine = new HBox();
    final ObservableList<Node> dollarEntryChildren = dollarEntryLine.getChildren();

    final Label dollarLabel = new Label(defaultCurrency.getDisplayName());
    dollarEntryChildren.add(dollarLabel);

    this.defaultCurrencyField = new CurrencyTextField(defaultCurrency);
    dollarEntryChildren.add(this.defaultCurrencyField);

    dollarLabel.setLabelFor(this.defaultCurrencyField);

    final Button convertButton = new Button("Convert");
    convertButton.setOnAction(this::convertPressed);
    dollarEntryChildren.add(convertButton);

    return dollarEntryLine;
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

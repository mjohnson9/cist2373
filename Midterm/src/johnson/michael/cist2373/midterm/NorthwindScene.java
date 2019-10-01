package johnson.michael.cist2373.midterm;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/** NorthwindScene is the scene controller for NorthwindApplication. */
public class NorthwindScene extends Scene {
  protected final Stage primaryStage;
  protected final BorderPane mainLayout;
  protected MenuBar menuBar;

  /**
   * Instantiates a new NorthwindScene.
   *
   * @param primaryStage The primary stage that this Scene is operating on.
   */
  public NorthwindScene(final Stage primaryStage) {
    super(new BorderPane());
    this.primaryStage = primaryStage;
    this.mainLayout = (BorderPane) this.getRoot();

    this.addCommonUI();
  }

  /** Resizes the primary stage to match the preferred size of the tree of child Nodes. */
  public void resizeToContents() {
    // Resize the Stage to match the preferred sizes of the Scene
    this.primaryStage.sizeToScene();
  }

  private void addCommonUI() {
    this.menuBar = new MenuBar();

    final Menu orderMenu = this.createOrderMenu();
    final Menu customersMenu = this.createCustomersMenu();
    final Menu employeesMenu = this.createEmployeesMenu();

    this.menuBar.getMenus().addAll(orderMenu, customersMenu, employeesMenu);

    this.mainLayout.setTop(this.menuBar);

    this.openOrderDetails(null);

    this.resizeToContents();
  }

  private Menu createOrderMenu() {
    final Menu orderMenu = new Menu("Orders");

    final MenuItem orderTotal = new MenuItem("Order Total");
    orderTotal.setOnAction(this::openOrderTotal);

    final MenuItem orderDetails = new MenuItem("Order Details");
    orderDetails.setOnAction(this::openOrderDetails);

    orderMenu.getItems().addAll(orderTotal, orderDetails);

    return orderMenu;
  }

  private Menu createCustomersMenu() {
    final Menu customersMenu = new Menu("Customers");

    final MenuItem byState = new MenuItem("View by state");
    byState.setOnAction(this::openCustomersByState);

    customersMenu.getItems().addAll(byState);

    return customersMenu;
  }

  private Menu createEmployeesMenu() {
    final Menu employeesMenu = new Menu("Employees");

    final MenuItem birthdaysByYear = new MenuItem("View by birth year");
    birthdaysByYear.setOnAction(this::openEmployeeBirthdays);

    employeesMenu.getItems().addAll(birthdaysByYear);

    return employeesMenu;
  }

  private void openOrderTotal(final ActionEvent event) {
    final OrderTotalView orderTotalView = new OrderTotalView(this);

    this.mainLayout.setCenter(orderTotalView);

    BorderPane.setMargin(orderTotalView, new Insets(10));

    this.resizeToContents();
  }

  private void openOrderDetails(final ActionEvent event) {
    final OrderDetailsView orderDetails = new OrderDetailsView(this);

    this.mainLayout.setCenter(orderDetails);

    BorderPane.setMargin(orderDetails, new Insets(10));

    this.resizeToContents();
  }

  private void openCustomersByState(final ActionEvent event) {
    final CustomerStateView customerStateView = new CustomerStateView(this);

    this.mainLayout.setCenter(customerStateView);

    BorderPane.setMargin(customerStateView, new Insets(10));

    this.resizeToContents();
  }

  private void openEmployeeBirthdays(final ActionEvent event) {
    final EmployeesBirthYearView employeeYearView = new EmployeesBirthYearView(this);

    this.mainLayout.setCenter(employeeYearView);

    BorderPane.setMargin(employeeYearView, new Insets(10));

    this.resizeToContents();
  }
}

package johnson.michael.cist2373.midterm;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;

public class NorthwindScene extends Scene {
  protected BorderPane mainLayout;
  protected MenuBar menuBar;

  public NorthwindScene() {
    super(new BorderPane());
    this.mainLayout = (BorderPane) this.getRoot();

    this.addCommonUI();
  }

  private void addCommonUI() {
    this.menuBar = new MenuBar();

    final Menu orderMenu = this.createOrderMenu();
    final Menu customersMenu = this.createCustomersMenu();
    final Menu employeesMenu = this.createEmployeesMenu();

    this.menuBar.getMenus().addAll(orderMenu, customersMenu, employeesMenu);

    this.mainLayout.setTop(this.menuBar);
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
    throw new UnsupportedOperationException("Not yet implemented");
  }

  private void openOrderDetails(final ActionEvent event) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  private void openCustomersByState(final ActionEvent event) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  private void openEmployeeBirthdays(final ActionEvent event) {
    throw new UnsupportedOperationException("Not yet implemented");
  }
}

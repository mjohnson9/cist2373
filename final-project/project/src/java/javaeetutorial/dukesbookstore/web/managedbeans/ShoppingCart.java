/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * <p>You may not modify, use, reproduce, or distribute this software except in compliance with the
 * terms of the License at: http://java.net/projects/javaeetutorial/pages/BerkeleyLicense
 */
package javaeetutorial.dukesbookstore.web.managedbeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaeetutorial.dukesbookstore.entity.Book;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Backing bean used by the <code>/bookcatalog.xhtml</code>, <code>/bookshowcart.xhtml</code>, and
 * <code>/bookcashier.xhtml</code> pages.
 */
@Named("cart")
@SessionScoped
public class ShoppingCart extends AbstractBean implements Serializable {

  private static final Logger logger =
      Logger.getLogger("dukesbookstore.web.managedbeans.ShoppingCart");
  private static final long serialVersionUID = -115105724952554868L;
  HashMap<String, ShoppingCartItem> items = null;
  int numberOfItems = 0;
  @Inject TaxBean taxes;
  @Inject CashierBean cashier;

  public ShoppingCart() {
    items = new HashMap<>();
  }

  public synchronized void add(String bookId, Book book) {
    if (items.containsKey(bookId)) {
      ShoppingCartItem scitem = (ShoppingCartItem) items.get(bookId);
      scitem.incrementQuantity();
    } else {
      ShoppingCartItem newItem = new ShoppingCartItem(book);
      items.put(bookId, newItem);
    }
  }

  public synchronized void remove(String bookId) {
    if (items.containsKey(bookId)) {
      ShoppingCartItem scitem = (ShoppingCartItem) items.get(bookId);
      scitem.decrementQuantity();

      if (scitem.getQuantity() <= 0) {
        items.remove(bookId);
      }

      numberOfItems--;
    }
  }

  public synchronized List<ShoppingCartItem> getItems() {
    List<ShoppingCartItem> results = new ArrayList<>();
    results.addAll(this.items.values());

    return results;
  }

  public synchronized int getNumberOfItems() {
    numberOfItems = 0;
    for (ShoppingCartItem item : getItems()) {
      numberOfItems += item.getQuantity();
    }

    return numberOfItems;
  }

  private double getSubTotalRaw() {
    double amount = 0.0;
    for (ShoppingCartItem item : getItems()) {
      Book bookDetails = (Book) item.getItem();

      amount += (item.getQuantity() * bookDetails.getPrice());
    }

    return roundOff(amount);
  }

  public synchronized double getSubTotal() {
    return this.roundOff(this.getSubTotalRaw());
  }

  public synchronized double getTotal() {
    double total = this.getSubTotalRaw();
    total *= (1d + taxes.getTaxRate());
    total += cashier.getShippingOption().getCost();

    return this.roundOff(total);
  }

  private double roundOff(double x) {
    long val = Math.round(x * 100); // cents

    return val / 100.0;
  }

  /**
   * Buy the items currently in the shopping cart.
   *
   * @return the navigation page
   */
  public String buy() {
    // "Cannot happen" sanity check
    if (getNumberOfItems() < 1) {
      message(null, "CartEmpty");

      return (null);
    } else {
      return ("bookcashier");
    }
  }

  public synchronized void clear() {
    logger.log(Level.INFO, "Clearing cart.");
    items.clear();
    numberOfItems = 0;
    message(null, "CartCleared");
  }
}

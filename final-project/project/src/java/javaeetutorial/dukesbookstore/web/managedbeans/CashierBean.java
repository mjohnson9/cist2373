/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * <p>You may not modify, use, reproduce, or distribute this software except in compliance with the
 * terms of the License at: http://java.net/projects/javaeetutorial/pages/BerkeleyLicense
 */
package javaeetutorial.dukesbookstore.web.managedbeans;

import java.util.Calendar;
import java.util.Date;
import javaeetutorial.dukesbookstore.ejb.BookRequestBean;
import javaeetutorial.dukesbookstore.exception.OrderException;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIOutput;
import javax.faces.component.UISelectBoolean;
import javax.faces.model.SelectItem;
import javax.inject.Named;

/**
 * Backing bean for the <code>/bookcashier.xhtml</code> and <code>bookreceipt.xhtml</code> pages.
 */
@Named
@RequestScoped
public class CashierBean extends AbstractBean {

  private static final long serialVersionUID = -9221440716172304017L;
  @EJB BookRequestBean bookRequestBean;
  private String name = null;
  private String creditCardNumber = null;
  private Date shipDate;
  private ShippingOption shippingOption = ShippingOption.NORMAL_SHIP;
  UIOutput specialOfferText = null;
  UISelectBoolean specialOffer = null;
  UIOutput thankYou = null;
  private String stringProperty = "This is a String property";
  private String[] newsletters;
  private static final SelectItem[] newsletterItems = {
    new SelectItem("Duke's Quarterly"),
    new SelectItem("Innovator's Almanac"),
    new SelectItem("Duke's Diet and Exercise Journal"),
    new SelectItem("Random Ramblings")
  };

  public CashierBean() {
    this.newsletters = new String[0];
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public String getCreditCardNumber() {
    return creditCardNumber;
  }

  public void setCreditCardNumber(String creditCardNumber) {
    this.creditCardNumber = creditCardNumber;
  }

  public void setNewsletters(String[] newsletters) {
    this.newsletters = newsletters;
  }

  public String[] getNewsletters() {
    return this.newsletters;
  }

  public SelectItem[] getNewsletterItems() {
    return newsletterItems;
  }

  public Date getShipDate() {
    return this.shipDate;
  }

  public void setShipDate(Date shipDate) {
    this.shipDate = shipDate;
  }

  public ShippingOption[] getShippingOptions() {
    return ShippingOption.values();
  }

  public void setShippingOption(final ShippingOption shippingOption) {
    this.shippingOption = shippingOption;
  }

  public ShippingOption getShippingOption() {
    return this.shippingOption;
  }

  public UIOutput getSpecialOfferText() {
    return this.specialOfferText;
  }

  public void setSpecialOfferText(UIOutput specialOfferText) {
    this.specialOfferText = specialOfferText;
  }

  public UISelectBoolean getSpecialOffer() {
    return this.specialOffer;
  }

  public void setSpecialOffer(UISelectBoolean specialOffer) {
    this.specialOffer = specialOffer;
  }

  public UIOutput getThankYou() {
    return this.thankYou;
  }

  public void setThankYou(UIOutput thankYou) {
    this.thankYou = thankYou;
  }

  public String getStringProperty() {
    return (this.stringProperty);
  }

  public void setStringProperty(String stringProperty) {
    this.stringProperty = stringProperty;
  }

  public String moveToReview() {
    if ((cart.getSubTotal() > 100.00) && !specialOffer.isRendered()) {
      specialOfferText.setRendered(true);
      specialOffer.setRendered(true);

      return null;
    } else if (specialOffer.isRendered() && !thankYou.isRendered()) {
      thankYou.setRendered(true);

      return null;
    }

    return ("bookconfirmorder");
  }

  public String submit() {
    int days = this.getShippingOption().getNumDays();
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DAY_OF_MONTH, days);
    setShipDate(cal.getTime());

    try {
      bookRequestBean.updateInventory(cart);
    } catch (OrderException ex) {
      return "bookordererror";
    }

    cart.clear();

    return ("bookreceipt");
  }

  public static enum ShippingOption {
    QUICK_SHIP("Quick Shipping", 15.95, 2),
    NORMAL_SHIP("Normal Shipping", 7.95, 5),
    SAVER_SHIP("Saver Shipping", 2.99, 7);

    private final String name;
    private final double cost;
    private final int numDays;

    ShippingOption(final String name, final double cost, final int numDays) {
      this.name = name;
      this.cost = cost;
      this.numDays = numDays;
    }

    public String getId() {
      return this.toString();
    }

    public String getName() {
      return this.name;
    }

    public double getCost() {
      return this.cost;
    }

    public int getNumDays() {
      return this.numDays;
    }
  }
}

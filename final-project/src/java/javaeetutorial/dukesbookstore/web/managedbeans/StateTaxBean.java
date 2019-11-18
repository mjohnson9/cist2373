/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaeetutorial.dukesbookstore.web.managedbeans;

import java.io.Serializable;
import javaeetutorial.dukesbookstore.entity.StateTax;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named("stateTax")
@RequestScoped
public class StateTaxBean extends AbstractBean implements Serializable {
  private String chosenState;

  public StateTaxBean() {}

  public String[] getStates() {
    return new String[] {"AL", "GA"};
  }

  public String getChosenState() {
    return this.chosenState;
  }

  public void setChosenState(final String chosenState) {
    this.chosenState = chosenState;
  }

  public StateTax getStateTax() {}

  public Double getTaxes() {
    return this.cart.getSubTotal() * this.getTaxRate();
  }
}

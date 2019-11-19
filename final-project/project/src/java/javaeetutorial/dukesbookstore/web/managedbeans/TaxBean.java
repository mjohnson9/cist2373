/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaeetutorial.dukesbookstore.web.managedbeans;

import java.io.Serializable;
import java.util.List;
import javaeetutorial.dukesbookstore.ejb.StateTaxBean;
import javaeetutorial.dukesbookstore.entity.StateTax;
import javaeetutorial.dukesbookstore.exception.StateTaxNotFoundException;
import javaeetutorial.dukesbookstore.exception.StateTaxesNotFoundException;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.FacesException;
import javax.inject.Named;

@Named("stateTax")
@RequestScoped
public class TaxBean extends AbstractBean implements Serializable {
  @EJB StateTaxBean stateTaxBean;

  private String chosenState;

  public TaxBean() {}

  public List<StateTax> getStates() {
    try {
      return this.stateTaxBean.getStates();
    } catch (final StateTaxesNotFoundException ex) {
      throw new FacesException("Failed to get the list of states.", ex);
    }
  }

  public String getChosenState() {
    return this.chosenState;
  }

  public void setChosenState(final String chosenState) {
    this.chosenState = chosenState;
  }

  public StateTax getStateTax() throws StateTaxNotFoundException {
    final String chosenState = this.getChosenState();
    if (chosenState == null || chosenState.isEmpty()) {
      return null;
    }

    return this.stateTaxBean.getState(chosenState);
  }

  public double getTaxRate() {
    try {
      final StateTax stateTax = this.getStateTax();
      if (stateTax == null) {
        // Handle no selection
        return 0d;
      }

      return stateTax.getTaxRate();
    } catch (final StateTaxNotFoundException ex) {
      throw new FacesException("Failed to get tax rate.", ex);
    }
  }

  public Double getTaxes() {
    return this.cart.getSubTotal() * this.getTaxRate();
  }
}

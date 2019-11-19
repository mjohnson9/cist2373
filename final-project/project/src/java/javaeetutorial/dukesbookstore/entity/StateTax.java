/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaeetutorial.dukesbookstore.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/** Entity class for bookstore example. */
@Entity
@Table(name = "WEB_BOOKSTORE_STATE_TAXES")
@NamedQuery(name = "findStates", query = "SELECT s FROM StateTax s ORDER BY s.state")
public class StateTax implements Serializable {
  // private static final long serialVersionUID = -4146681491856848089L;
  @Id @NotNull private String state;
  private Double taxRate;

  public StateTax() {}

  public StateTax(final String state) {
    this.state = state;
  }

  public StateTax(final String state, final Double taxRate) {
    this.state = state;
    this.taxRate = taxRate;
  }

  public String getState() {
    return this.state;
  }

  public void setState(final String state) {
    this.state = state;
  }

  public Double getTaxRate() {
    return this.taxRate;
  }

  public void setTaxRate(final double taxRate) {
    this.taxRate = taxRate;
  }
}

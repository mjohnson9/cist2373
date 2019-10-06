/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package johnson.michael.cist2373.lesson6;

import java.text.NumberFormat;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/** @author michael */
@Named(value = "taxCalculator")
@RequestScoped
public class TaxCalculatorBean {
  private final NumberFormat currencyFormatter;

  private Double taxableIncome;
  private Integer filingStatus;

  private Boolean taxCalculated;
  private Double tax;

  /** Creates a new instance of TaxCalculatorBean */
  public TaxCalculatorBean() {
    this.currencyFormatter = NumberFormat.getCurrencyInstance();

    this.taxCalculated = false;
  }

  public void setTaxableIncome(final Double taxableIncome) {
    this.taxableIncome = taxableIncome;
  }

  public Double getTaxableIncome() {
    return this.taxableIncome;
  }

  public void setFilingStatus(final Integer filingStatus) {
    this.filingStatus = filingStatus;
  }

  public Integer getFilingStatus() {
    return this.filingStatus;
  }

  public Boolean getTaxCalculated() {
    return this.taxCalculated;
  }

  public String getFormattedTaxableIncome() {
    return this.currencyFormatter.format(this.getTaxableIncome());
  }

  public String getTax() {
    return this.currencyFormatter.format(this.tax);
  }

  public void calculateTax() {
    this.tax = ComputeTax.computeTax(this.getFilingStatus(), this.getTaxableIncome());
    this.taxCalculated = true;
  }
}

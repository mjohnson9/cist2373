package johnson.michael.cist2373.lesson7;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/** LoanCalculatorBean provides an interface for the loan calculation to occur after a request. */
@Named(value = "loanCalculator")
@RequestScoped
public class LoanCalculatorBean {
  private Double amount;
  private Double annualInterestRate;
  private Integer years;

  private Loan loan;

  private final NumberFormat currencyFormatter;
  private final NumberFormat percentFormatter;

  public LoanCalculatorBean() {
    this.currencyFormatter = NumberFormat.getCurrencyInstance();

    this.percentFormatter = NumberFormat.getPercentInstance();
    final DecimalFormat percentFormatter = (DecimalFormat) this.percentFormatter;
    // Set multiplier to 1 (defaults to 100) because we're accepting the percent values as whole
    // numbers
    percentFormatter.setMultiplier(1);
    percentFormatter.setMinimumFractionDigits(0);
    percentFormatter.setMaximumFractionDigits(3);
  }

  /**
   * Returns a formatted String for amount, based on the default FORMAT Locale.
   *
   * @return The formatted amount String.
   */
  public String getFormattedAmount() {
    return this.currencyFormatter.format(this.getAmount());
  }

  /**
   * Returns a formatted String for annualInterestRate, based on the default FORMAT Locale.
   *
   * @return The formatted annual interest rate string.
   */
  public String getFormattedAnnualInterestRate() {
    return this.percentFormatter.format(this.getAnnualInterestRate());
  }

  /**
   * Returns a formatted String for the loan's monthly payment, based on the default FORMAT Locale.
   *
   * @return The formatted monthly payment string.
   */
  public String getFormattedMonthlyPayment() {
    return this.currencyFormatter.format(this.loan.getMonthlyPayment());
  }

  /**
   * Returns a formatted String for the loan's total payment, based on the default FORMAT Locale.
   *
   * @return The formatted total payment string.
   */
  public String getFormattedTotalPayment() {
    return this.currencyFormatter.format(this.loan.getTotalPayment());
  }

  public Double getAmount() {
    return this.amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public Double getAnnualInterestRate() {
    return this.annualInterestRate;
  }

  public void setAnnualInterestRate(Double annualInterestRate) {
    this.annualInterestRate = annualInterestRate;
  }

  public Integer getYears() {
    return this.years;
  }

  public void setYears(Integer years) {
    this.years = years;
  }

  /**
   * To be called whenever the form is submitted.
   *
   * @return The new page to be loaded.
   */
  public String formSubmitted() {
    this.loan = new Loan(this.getAnnualInterestRate(), this.getYears(), this.getAmount());

    return "results";
  }
}

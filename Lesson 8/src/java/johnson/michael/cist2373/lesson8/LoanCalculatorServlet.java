/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package johnson.michael.cist2373.lesson8;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
    name = "LoanCalculatorServlet",
    urlPatterns = {"/"})
public class LoanCalculatorServlet extends HttpServlet {
  private DecimalFormat currencyFormatter;
  private DecimalFormat percentFormatter;
  private DecimalFormat decimalFormatter;
  private NumberFormat integerFormatter;

  /**
   * Called whenever the servlet is being initialized. We can do our own initialization work here.
   */
  @Override
  public void init() throws ServletException {
    super.init();

    this.decimalFormatter = (DecimalFormat) DecimalFormat.getInstance();
    this.integerFormatter = NumberFormat.getInstance();

    this.currencyFormatter = (DecimalFormat) DecimalFormat.getCurrencyInstance();

    this.percentFormatter = (DecimalFormat) DecimalFormat.getPercentInstance();
    // Set multiplier to 1 so that we handle the user given percents correctly
    this.percentFormatter.setMultiplier(1);
    // Make the percent formatter always have two decimal points
    this.percentFormatter.setMinimumFractionDigits(2);
    this.percentFormatter.setMaximumFractionDigits(2);
  }

  /**
   * Handles the HTTP <code>GET</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
      throws ServletException, IOException {
    this.sendCommonHeaders(request, response);
    try (PrintWriter out = response.getWriter()) {
      this.sendHtmlHead(out);
      this.sendForm(out);
      this.sendHtmlFooter(out);
    }
  }

  /**
   * Handles the HTTP <code>POST</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
      throws ServletException, IOException {
    this.sendCommonHeaders(request, response);
    try (final PrintWriter out = response.getWriter()) {
      this.sendHtmlHead(out);
      try {
        final Loan loan = this.parseLoan(request);
        this.sendForm(
            out,
            this.getResendLoanAmount(request),
            this.getResendInterestRate(request),
            this.getResendYears(request));
        this.sendLoanInformation(out, loan);
      } catch (final ValidationException ex) {
        out.println(
            "<div style=\"color: red\">" + this.escapeHtmlAttribute(ex.getMessage()) + "</div>");
        this.sendForm(
            out,
            this.getResendLoanAmount(request),
            this.getResendInterestRate(request),
            this.getResendYears(request));
      }
      this.sendHtmlFooter(out);
    }
  }

  /**
   * Sets the common request headers.
   *
   * @param request servlet request
   * @param response servlet response
   */
  protected void sendCommonHeaders(
      final HttpServletRequest request, final HttpServletResponse response) {
    response.setContentType("text/html;charset=UTF-8");
  }

  /**
   * Writes out the HTML beginning.
   *
   * @param out servlet writer
   */
  protected void sendHtmlHead(final PrintWriter out) {
    out.println("<!DOCTYPE html>");
    out.println("<html>");
    out.println("<head>");
    out.println("<title>Loan Calculator</title>");
    out.println("</head>");
    out.println("<body>");
    out.println("<h1>Loan Calculator using Servlet</h1>");
  }

  /**
   * Writes out the HTML ending.
   *
   * @param out servlet writer
   */
  protected void sendHtmlFooter(final PrintWriter out) {
    out.println("</body>");
    out.println("</html>");
  }

  /**
   * Writes out a blank loan calculator form.
   *
   * @param out servlet writer
   */
  protected void sendForm(final PrintWriter out) {
    this.sendForm(out, "", "", "");
  }

  /**
   * Writes out a loan calculator form with values pre-filled.
   *
   * @param out servlet writer
   * @param loanAmount loan amount value
   * @param interestRate interest rate value
   * @param numYears number of years value
   */
  protected void sendForm(
      final PrintWriter out,
      final String loanAmount,
      final String interestRate,
      final String numYears) {
    out.println("<form method=\"post\">");
    out.println(
        "<label for=\"loanAmount\"/>Loan Amount</label>&nbsp;<input type=\"text\" name=\"loanAmount\" id=\"loanAmount\" size=\"10\" value=\""
            + this.escapeHtmlAttribute(loanAmount)
            + "\"/><br/>");
    out.println(
        "<label for=\"interestRate\"/>Annual Interest Rate</label>&nbsp;<input type=\"text\" name=\"interestRate\" id=\"interestRate\" size=\"8\" value=\""
            + this.escapeHtmlAttribute(interestRate)
            + "\"/><br/>");
    out.println(
        "<label for=\"numYears\"/>Number of Years</label>&nbsp;<input type=\"text\" name=\"numYears\" id=\"numYears\" size=\"4\" value=\""
            + this.escapeHtmlAttribute(numYears)
            + "\"/><br/>");
    out.println(
        "<input type=\"submit\" value=\"Calculate Loan Payment\"/>&nbsp;<input type=\"reset\" value=\"Reset\"/>");
    out.println("</form>");
  }

  /**
   * Writes out formatted loan information.
   *
   * @param out servlet writer
   * @param loan Loan whose information to write
   */
  protected void sendLoanInformation(final PrintWriter out, final Loan loan) {
    out.println("<p>"); // Open a paragraph
    out.println(
        "<div>Loan Amount: " + this.currencyFormatter.format(loan.getLoanAmount()) + "</div>");
    out.println(
        "<div>Annual Interest Rate: "
            + this.percentFormatter.format(loan.getAnnualInterestRate())
            + "</div>");
    out.println(
        "<div>Number of Years: "
            + this.integerFormatter.format(loan.getNumberOfYears())
            + "</div>");
    out.println(
        "<div style=\"color: red\">Monthly Payment: "
            + this.currencyFormatter.format(loan.getMonthlyPayment())
            + "</div>");
    out.println(
        "<div style=\"color: red\">Total Payment: "
            + this.currencyFormatter.format(loan.getTotalPayment())
            + "</div>");
    out.println("</p>"); // Close paragraph
  }

  /**
   * Parses the loan from the servlet request
   *
   * @param request servlet request
   * @return The parsed Loan.
   * @throws ValidationException when the input from the servlet request is not parseable.
   */
  protected Loan parseLoan(final HttpServletRequest request) throws ValidationException {
    // Parse loan amount
    String loanAmountString = request.getParameter("loanAmount");
    if (loanAmountString == null || loanAmountString.isEmpty()) {
      throw new ValidationException("The Loan Amount field is empty.");
    }

    final double loanAmount;
    try {
      final Number loanNumber = this.parseInputNumber(loanAmountString, this.currencyFormatter);
      loanAmount = loanNumber.doubleValue();

      if (loanAmount <= 0) {
        throw new ValidationException("The Loan Amount must be greater than zero.");
      }
    } catch (final ParseException ex) {
      throw new ValidationException("The given Loan Amount is not a valid currency amount.", ex);
    }

    // Parse interest rate
    final String interestRateString = request.getParameter("interestRate");
    if (interestRateString == null || interestRateString.isEmpty()) {
      throw new ValidationException("The Annual Interest Rate field is empty.");
    }

    final double interestRate;
    try {
      final Number interestRateNumber =
          this.parseInputNumber(interestRateString, this.percentFormatter);
      interestRate = interestRateNumber.doubleValue();

      if (interestRate < 0) {
        throw new ValidationException("The Annual Interest Rate must be greater than zero.");
      }
    } catch (final ParseException ex) {
      throw new ValidationException(
          "The given Annual Interest Rate is not a valid percentage.", ex);
    }

    // Parse number of years
    final String numYearsString = request.getParameter("numYears");
    if (numYearsString == null || numYearsString.isEmpty()) {
      throw new ValidationException("The Number of Years field is empty.");
    }

    final int numYears;
    try {
      final Number numYearsNumber = this.parseInputNumber(numYearsString, this.integerFormatter);
      numYears = numYearsNumber.intValue();

      if (numYears <= 0) {
        throw new ValidationException("The number of years must be greater than 0.");
      }
    } catch (final ParseException ex) {
      throw new ValidationException("The given Number of Years is not a valid integer.", ex);
    }

    // Create the loan
    final Loan newLoan = new Loan(interestRate, numYears, loanAmount);
    return newLoan;
  }

  /**
   * Escapes a string to be safe to use in an HTML attribute
   *
   * @param attribute The desired contents of the HTML attribute
   * @return The escaped HTML attribute
   */
  protected String escapeHtmlAttribute(String attribute) {
    attribute = attribute.replace("&", "&amp;");
    attribute = attribute.replace("'", "&#39;");
    attribute = attribute.replace("\"", "&#34;");

    return attribute;
  }

  /**
   * Parses an input number using a given formatter. If the given formatter doesn't work, it falls
   * back to generic decimal and integer formatters.
   *
   * @param parameterString The string to parse
   * @param formatter The primary formatter to use
   * @return The parsed Number
   * @throws ParseException When the input string can't be parsed
   */
  protected Number parseInputNumber(final String parameterString, final NumberFormat formatter)
      throws ParseException {
    Number parsedNumber;
    try {
      parsedNumber = formatter.parse(parameterString);
    } catch (final ParseException ex) {
      // We failed to parse the number with the given formatter. Attempt to parse it with generic
      // formatters and allow the exception to pass through if it fails.
      parsedNumber = this.parseGenericInputNumber(parameterString);
    }

    return parsedNumber;
  }

  /**
   * Attempt to parse a given input number using the generic decimal and integer formatters.
   *
   * @param parameterString The string to parse
   * @return The parsed Number
   * @throws ParseException When the input string can't be parsed
   */
  protected Number parseGenericInputNumber(final String parameterString) throws ParseException {
    final List<ParseException> suppressedParseExceptions = new ArrayList<>();

    try {
      final Number parsedNumber = this.decimalFormatter.parse(parameterString);
      return parsedNumber; // We parsed it as a decimal. Return the decimal.
    } catch (final ParseException ex) {
      // We failed to parse it as a decimal, try the next fallback
      suppressedParseExceptions.add(ex);
    }

    try {
      final Number parsedNumber = this.integerFormatter.parse(parameterString);
      return parsedNumber;
    } catch (final ParseException ex) {
      // We failed to parse it as an integer, try the next fallback
      suppressedParseExceptions.add(ex);
    }

    final ParseException primaryException =
        suppressedParseExceptions.get(suppressedParseExceptions.size() - 1);

    // Add the other suppressed exceptions to the primary exception
    for (int i = suppressedParseExceptions.size() - 2; i >= 0; i--) {
      final ParseException thisException = suppressedParseExceptions.get(i);
      primaryException.addSuppressed(thisException);
    }

    throw primaryException;
  }

  /**
   * Attempts to build a String to send back to the user for a submitted form field. It attempts to
   * use the provided formatter to parse the input and falls back to the generic decimal and integer
   * formatters if that fails.
   *
   * @param request servlet request
   * @param parameterName The name of the parameter being sent back
   * @param formatter The first formatter to use to parse the parameter.
   * @return A String to send back to the user to fill the form field
   */
  protected String getResendNumber(
      final HttpServletRequest request, final String parameterName, final NumberFormat formatter) {
    final String parameterString = request.getParameter(parameterName);
    if (parameterString == null || parameterString.isEmpty()) {
      return "";
    }

    final Number parsedNumber;
    try {
      parsedNumber = this.parseInputNumber(parameterString, formatter);
    } catch (ParseException ex) {
      // We failed to parse the input with the given formatter or generic formatters. Return the
      // original string.
      return parameterString;
    }

    // We successfully parsed the number. Return it correctly formatted.
    return formatter.format(parsedNumber);
  }

  /**
   * Attempts to build a String to send back for the loan amount form field.
   *
   * @param request servlet request
   * @return A String to send back to fill the loan amount field.
   */
  protected String getResendLoanAmount(final HttpServletRequest request) {
    return this.getResendNumber(request, "loanAmount", this.currencyFormatter);
  }

  /**
   * Attempts to build a String to send back for the interest rate form field.
   *
   * @param request servlet request
   * @return A String to send back to fill the interest rate field.
   */
  protected String getResendInterestRate(final HttpServletRequest request) {
    return this.getResendNumber(request, "interestRate", this.percentFormatter);
  }

  /**
   * Attempts to build a String to send back for the number of years form field.
   *
   * @param request servlet request
   * @return A String to send back to fill the number of years field.
   */
  protected String getResendYears(final HttpServletRequest request) {
    return this.getResendNumber(request, "numYears", this.integerFormatter);
  }

  /**
   * Returns a short description of the servlet.
   *
   * @return a String containing servlet description
   */
  @Override
  public String getServletInfo() {
    return "Loan Calculator";
  }
}

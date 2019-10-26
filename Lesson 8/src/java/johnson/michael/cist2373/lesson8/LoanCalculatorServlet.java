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
  private NumberFormat integerFormatter;

  @Override
  public void init() throws ServletException {
    super.init();

    this.currencyFormatter = (DecimalFormat) DecimalFormat.getCurrencyInstance();

    this.percentFormatter = (DecimalFormat) DecimalFormat.getPercentInstance();
    // Set multiplier to 1 so that we handle the user given percents correctly
    this.percentFormatter.setMultiplier(1);

    this.integerFormatter = NumberFormat.getInstance();
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
        this.sendForm(out);
        this.sendLoanInformation(out, loan);
      } catch (ValidationException ex) {
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

  protected void sendCommonHeaders(
      final HttpServletRequest request, final HttpServletResponse response) {
    response.setContentType("text/html;charset=UTF-8");
  }

  protected void sendHtmlHead(final PrintWriter out) {
    out.println("<!DOCTYPE html>");
    out.println("<html>");
    out.println("<head>");
    out.println("<title>Loan Calculator</title>");
    out.println("</head>");
    out.println("<body>");
    out.println("<h1>Loan Calculator using Servlet</h1>");
  }

  protected void sendHtmlFooter(final PrintWriter out) {
    out.println("</body>");
    out.println("</html>");
  }

  protected void sendForm(final PrintWriter out) {
    this.sendForm(out, "", "", "");
  }

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

  protected void sendLoanInformation(final PrintWriter out, final Loan loan) {}

  protected Loan parseLoan(final HttpServletRequest request) throws ValidationException {
    // Parse loan amount
    String loanAmountString = request.getParameter("loanAmount");
    if (loanAmountString == null || loanAmountString.isEmpty()) {
      throw new ValidationException("The Loan Amount field is empty.");
    }
    final String currencySymbol = this.currencyFormatter.getCurrency().getSymbol();
    if (!loanAmountString.startsWith(currencySymbol)) {
      // The parser wants the string to start with the currency symbol; add that for the user if
      // they haven't put it
      loanAmountString = currencySymbol + loanAmountString;
    }

    final double loanAmount;
    try {
      loanAmount = this.currencyFormatter.parse(loanAmountString).doubleValue();
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
      interestRate = this.percentFormatter.parse(interestRateString).doubleValue();
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
      numYears = this.integerFormatter.parse(numYearsString).intValue();
    } catch (final ParseException ex) {
      throw new ValidationException("The given Number of Years is not a valid integer.", ex);
    }

    // Create the loan
    final Loan newLoan = new Loan(interestRate, numYears, loanAmount);
    return newLoan;
  }

  protected String escapeHtmlAttribute(String attribute) {
    attribute = attribute.replace("<", "&lt;");
    attribute = attribute.replace(">", "&gt;");
    attribute = attribute.replace("&", "&amp;");
    attribute = attribute.replace("'", "&#39;");
    attribute = attribute.replace("\"", "&#34;");

    return attribute;
  }

  protected String getResendNumber(
      final HttpServletRequest request, final String parameterName, final NumberFormat formatter) {
    final String parameterString = request.getParameter(parameterName);
    if (parameterString == null || parameterString.isEmpty()) {
      return "";
    }

    final Number parsedNumber;
    try {
      parsedNumber = formatter.parse(parameterString);
    } catch (final ParseException ex) {
      return parameterString;
    }

    return formatter.format(parsedNumber);
  }

  protected String getResendLoanAmount(final HttpServletRequest request) {
    return this.getResendNumber(request, "loanAmount", this.currencyFormatter);
  }

  protected String getResendInterestRate(final HttpServletRequest request) {
    return this.getResendNumber(request, "interestRate", this.percentFormatter);
  }

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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package johnson.michael.bmiclient;

/** @author michael */
public class BMI {
  private final double bmi;
  private final String status;

  public BMI(final double bmi, final String status) {
    this.bmi = bmi;
    this.status = status;
  }

  public double getBmi() {
    return this.bmi;
  }

  public String getStatus() {
    return this.status;
  }
}

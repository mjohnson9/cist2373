/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaeetutorial.dukesbookstore.exception;

/** @author michael */
public class StateTaxesNotFoundException extends Exception {
  public StateTaxesNotFoundException() {}

  public StateTaxesNotFoundException(final String msg) {
    super(msg);
  }

  public StateTaxesNotFoundException(final String msg, final Throwable cause) {
    super(msg, cause);
  }
}

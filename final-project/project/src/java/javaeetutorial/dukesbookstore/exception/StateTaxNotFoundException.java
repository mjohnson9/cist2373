/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaeetutorial.dukesbookstore.exception;

/** @author michael */
public class StateTaxNotFoundException extends Exception {
  public StateTaxNotFoundException() {}

  public StateTaxNotFoundException(final String msg) {
    super(msg);
  }

  public StateTaxNotFoundException(final String msg, final Throwable cause) {
    super(msg, cause);
  }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaeetutorial.dukesbookstore.ejb;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/** Singleton bean that initializes the book database for the bookstore example. */
@Singleton
@Startup
public class StateTaxConfigBean {

  @EJB private StateTaxBean request;

  @PostConstruct
  public void createData() {
    request.createTaxRate("AL", "Alabama", 0.04);
    request.createTaxRate("AK", "Alaska", 0d);
    request.createTaxRate("AZ", "Arizona", 0.056);
    request.createTaxRate("AR", "Arkansas", 0.065);
    request.createTaxRate("CA", "California", 0.0725);
    request.createTaxRate("CO", "Colorado", 0.029);
    request.createTaxRate("CT", "Connecticut", 0.0635);
    request.createTaxRate("DC", "District of Columbia", 0.06);
    request.createTaxRate("FL", "Florida", 0.06);
    request.createTaxRate("GA", "Georgia", 0.04);
    request.createTaxRate("HI", "Hawaii", 0.04);
    request.createTaxRate("ID", "Idaho", 0.06);
    request.createTaxRate("IL", "Illinois", 0.0625);
    request.createTaxRate("IN", "Indiana", 0.07);
    request.createTaxRate("IA", "Iowa", 0.06);
    request.createTaxRate("KS", "Kansas", 0.065);
    request.createTaxRate("KY", "Kentucky", 0.06);
    request.createTaxRate("LA", "Louisiana", 0.0445);
    request.createTaxRate("ME", "Maine", 0.055);
    request.createTaxRate("MD", "Maryland", 0.055);
    request.createTaxRate("MA", "Massachusetts", 0.0625);
    request.createTaxRate("MI", "Michigan", 0.06);
    request.createTaxRate("MN", "Minnesota", 0.06875);
    request.createTaxRate("MS", "Mississippi", 0.07);
    request.createTaxRate("MO", "Missouri", 0.04225);
    request.createTaxRate("MT", "Montana", 0d);
    request.createTaxRate("NE", "Nebraska", 0.055);
    request.createTaxRate("NV", "Nevada", 0.0685);
    request.createTaxRate("NH", "New Hampshire", 0d);
    request.createTaxRate("NJ", "New Jersey", 0.06625);
    request.createTaxRate("NM", "New Mexico", 0.05125);
    request.createTaxRate("NY", "New York", 0.04);
    request.createTaxRate("NC", "North Carolina", 0.0475);
    request.createTaxRate("ND", "North Dakota", 0.05);
    request.createTaxRate("OH", "Ohio", 0.0575);
    request.createTaxRate("OK", "Oklahoma", 0.045);
    request.createTaxRate("OR", "Oregon", 0d);
    request.createTaxRate("PA", "Pennsylvania", 0.06);
    request.createTaxRate("RI", "Rhode Island", 0.07);
    request.createTaxRate("SC", "South Carolina", 0.06);
    request.createTaxRate("SD", "South Dakota", 0.045);
    request.createTaxRate("TN", "Tennessee", 0.07);
    request.createTaxRate("TX", "Texas", 0.0625);
    request.createTaxRate("UT", "Utah", 0.0485);
    request.createTaxRate("VT", "Vermont", 0.06);
    request.createTaxRate("VA", "Virginia", 0.043);
    request.createTaxRate("WA", "Washington", 0.065);
    request.createTaxRate("WV", "West Virginia", 0.06);
    request.createTaxRate("WI", "Wisconsin", 0.05);
    request.createTaxRate("WY", "Wyoming", 0.04);
  }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaeetutorial.dukesbookstore.ejb;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaeetutorial.dukesbookstore.entity.StateTax;
import javaeetutorial.dukesbookstore.exception.StateTaxNotFoundException;
import javaeetutorial.dukesbookstore.exception.StateTaxesNotFoundException;
import javax.ejb.EJBException;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateful
public class StateTaxBean {
  @PersistenceContext private EntityManager em;
  private static final Logger logger = Logger.getLogger("dukesbookstore.ejb.StateTaxBean");

  public StateTaxBean() throws Exception {}

  public void createTaxRate(final String state, final String name, final Double taxRate) {
    try {
      StateTax stateTax = new StateTax(state, name, taxRate);
      logger.log(Level.INFO, "Created state tax rate for {0}", state);
      em.persist(stateTax);
      logger.log(Level.INFO, "Persisted state rax rate for {0}", state);
    } catch (Exception ex) {
      throw new EJBException(ex.getMessage(), ex);
    }
  }

  public List<StateTax> getStates() throws StateTaxesNotFoundException {
    try {
      return (List<StateTax>) em.createNamedQuery("findStates").getResultList();
    } catch (Exception ex) {
      throw new StateTaxesNotFoundException("Could not get state taxes", ex);
    }
  }

  public StateTax getState(final String state) throws StateTaxNotFoundException {
    final StateTax requestedState = em.find(StateTax.class, state);

    if (requestedState == null) {
      throw new StateTaxNotFoundException("Couldn't find state tax rate" + state);
    }

    return requestedState;
  }
}

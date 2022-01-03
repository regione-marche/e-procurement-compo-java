package it.saga.library.reportGeneratoreModelli;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

public interface RpaBLGLocalHome extends EJBLocalHome {
  RpaBLGLocal create() throws CreateException;
}

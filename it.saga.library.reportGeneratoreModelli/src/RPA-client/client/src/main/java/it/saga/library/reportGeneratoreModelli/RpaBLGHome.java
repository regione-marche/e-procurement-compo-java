package it.saga.library.reportGeneratoreModelli;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;

public interface RpaBLGHome extends EJBHome {
  RpaBLG create() throws RemoteException, CreateException;
}

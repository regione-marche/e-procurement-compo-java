/**
 * RpaWSSRpaSoapImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.saga.library.reportGeneratoreModelli.webservices.impl.RpaWSSRpa;

import it.saga.library.reportGeneratoreModelli.webservices.impl.RpaWSSMetodi;

import java.rmi.RemoteException;

public class RpaWSSRpaSoapImpl implements RpaWSSRpaSoap{
   
	public String rpa_select(String session, String select) throws RemoteException {   
            return RpaWSSMetodi.eseguiSelect(session, select);
	}		

}

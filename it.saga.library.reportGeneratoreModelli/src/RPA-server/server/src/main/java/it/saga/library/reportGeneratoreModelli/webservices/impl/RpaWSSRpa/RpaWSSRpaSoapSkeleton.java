/**
 * RpaWSSRpaSoapSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.saga.library.reportGeneratoreModelli.webservices.impl.RpaWSSRpa;

public class RpaWSSRpaSoapSkeleton implements RpaWSSRpaSoap, org.apache.axis.wsdl.Skeleton {
    private RpaWSSRpaSoap impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)_myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return _myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://tempuri.org/", "session"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://tempuri.org/", "select"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("rpa_select", _params, new javax.xml.namespace.QName("http://tempuri.org/", "rpa_selectResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://tempuri.org/", "rpa_select"));
        _oper.setSoapAction("http://tempuri.org/rpa_select");
        _myOperationsList.add(_oper);
        if (_myOperations.get("rpa_select") == null) {
            _myOperations.put("rpa_select", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("rpa_select")).add(_oper);
    }

    public RpaWSSRpaSoapSkeleton() {
        this.impl = new RpaWSSRpaSoapImpl();
    }

    public RpaWSSRpaSoapSkeleton(RpaWSSRpaSoap impl) {
        this.impl = impl;
    }
    public java.lang.String rpa_select(java.lang.String session, java.lang.String select) throws java.rmi.RemoteException
    {
        String ret = impl.rpa_select(session, select);
        return ret;
    }

}

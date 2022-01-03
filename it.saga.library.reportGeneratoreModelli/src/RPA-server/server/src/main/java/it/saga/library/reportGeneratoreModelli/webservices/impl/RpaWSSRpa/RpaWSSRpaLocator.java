/**
 * RpaWSSRpaLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.saga.library.reportGeneratoreModelli.webservices.impl.RpaWSSRpa;

public class RpaWSSRpaLocator extends org.apache.axis.client.Service implements RpaWSSRpa {

    public RpaWSSRpaLocator() {
    }


    public RpaWSSRpaLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public RpaWSSRpaLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for RpaWSSRpaSoap
    private java.lang.String RpaWSSRpaSoap_address = "http://localhost/aspnet/WebService/WSE_rpa/WSRpa.asmx";

    public java.lang.String getRpaWSSRpaSoapAddress() {
        return RpaWSSRpaSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String RpaWSSRpaSoapWSDDServiceName = "RpaWSSRpaSoap";

    public java.lang.String getRpaWSSRpaSoapWSDDServiceName() {
        return RpaWSSRpaSoapWSDDServiceName;
    }

    public void setRpaWSSRpaSoapWSDDServiceName(java.lang.String name) {
        RpaWSSRpaSoapWSDDServiceName = name;
    }

    public RpaWSSRpaSoap getRpaWSSRpaSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(RpaWSSRpaSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getRpaWSSRpaSoap(endpoint);
    }

    public RpaWSSRpaSoap getRpaWSSRpaSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            RpaWSSRpaSoapStub _stub = new RpaWSSRpaSoapStub(portAddress, this);
            _stub.setPortName(getRpaWSSRpaSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setRpaWSSRpaSoapEndpointAddress(java.lang.String address) {
        RpaWSSRpaSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (RpaWSSRpaSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                RpaWSSRpaSoapStub _stub = new RpaWSSRpaSoapStub(new java.net.URL(RpaWSSRpaSoap_address), this);
                _stub.setPortName(getRpaWSSRpaSoapWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("RpaWSSRpaSoap".equals(inputPortName)) {
            return getRpaWSSRpaSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://tempuri.org/", "RpaWSSRpa");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "RpaWSSRpaSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("RpaWSSRpaSoap".equals(portName)) {
            setRpaWSSRpaSoapEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}

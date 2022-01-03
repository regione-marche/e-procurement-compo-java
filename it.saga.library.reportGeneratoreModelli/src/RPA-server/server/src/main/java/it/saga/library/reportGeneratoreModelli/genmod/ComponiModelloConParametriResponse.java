/**
 * ComponiModelloConParametriResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.saga.library.reportGeneratoreModelli.genmod;

public class ComponiModelloConParametriResponse  implements java.io.Serializable {
    private java.lang.String componiModelloConParametriReturn;

    public ComponiModelloConParametriResponse() {
    }

    public ComponiModelloConParametriResponse(
           java.lang.String componiModelloConParametriReturn) {
           this.componiModelloConParametriReturn = componiModelloConParametriReturn;
    }


    /**
     * Gets the componiModelloConParametriReturn value for this ComponiModelloConParametriResponse.
     * 
     * @return componiModelloConParametriReturn
     */
    public java.lang.String getComponiModelloConParametriReturn() {
        return componiModelloConParametriReturn;
    }


    /**
     * Sets the componiModelloConParametriReturn value for this ComponiModelloConParametriResponse.
     * 
     * @param componiModelloConParametriReturn
     */
    public void setComponiModelloConParametriReturn(java.lang.String componiModelloConParametriReturn) {
        this.componiModelloConParametriReturn = componiModelloConParametriReturn;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ComponiModelloConParametriResponse)) return false;
        ComponiModelloConParametriResponse other = (ComponiModelloConParametriResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.componiModelloConParametriReturn==null && other.getComponiModelloConParametriReturn()==null) || 
             (this.componiModelloConParametriReturn!=null &&
              this.componiModelloConParametriReturn.equals(other.getComponiModelloConParametriReturn())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getComponiModelloConParametriReturn() != null) {
            _hashCode += getComponiModelloConParametriReturn().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ComponiModelloConParametriResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://genmod.bl.gene.eldasoft.it", ">componiModelloConParametriResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("componiModelloConParametriReturn");
        elemField.setXmlName(new javax.xml.namespace.QName("http://genmod.bl.gene.eldasoft.it", "componiModelloConParametriReturn"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}

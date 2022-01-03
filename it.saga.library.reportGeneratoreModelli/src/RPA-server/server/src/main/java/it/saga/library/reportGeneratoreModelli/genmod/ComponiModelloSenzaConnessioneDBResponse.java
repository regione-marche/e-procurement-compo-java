/**
 * ComponiModelloSenzaConnessioneDBResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.saga.library.reportGeneratoreModelli.genmod;

public class ComponiModelloSenzaConnessioneDBResponse  implements java.io.Serializable {
    private java.lang.String componiModelloSenzaConnessioneDBReturn;

    public ComponiModelloSenzaConnessioneDBResponse() {
    }

    public ComponiModelloSenzaConnessioneDBResponse(
           java.lang.String componiModelloSenzaConnessioneDBReturn) {
           this.componiModelloSenzaConnessioneDBReturn = componiModelloSenzaConnessioneDBReturn;
    }


    /**
     * Gets the componiModelloSenzaConnessioneDBReturn value for this ComponiModelloSenzaConnessioneDBResponse.
     * 
     * @return componiModelloSenzaConnessioneDBReturn
     */
    public java.lang.String getComponiModelloSenzaConnessioneDBReturn() {
        return componiModelloSenzaConnessioneDBReturn;
    }


    /**
     * Sets the componiModelloSenzaConnessioneDBReturn value for this ComponiModelloSenzaConnessioneDBResponse.
     * 
     * @param componiModelloSenzaConnessioneDBReturn
     */
    public void setComponiModelloSenzaConnessioneDBReturn(java.lang.String componiModelloSenzaConnessioneDBReturn) {
        this.componiModelloSenzaConnessioneDBReturn = componiModelloSenzaConnessioneDBReturn;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ComponiModelloSenzaConnessioneDBResponse)) return false;
        ComponiModelloSenzaConnessioneDBResponse other = (ComponiModelloSenzaConnessioneDBResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.componiModelloSenzaConnessioneDBReturn==null && other.getComponiModelloSenzaConnessioneDBReturn()==null) || 
             (this.componiModelloSenzaConnessioneDBReturn!=null &&
              this.componiModelloSenzaConnessioneDBReturn.equals(other.getComponiModelloSenzaConnessioneDBReturn())));
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
        if (getComponiModelloSenzaConnessioneDBReturn() != null) {
            _hashCode += getComponiModelloSenzaConnessioneDBReturn().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ComponiModelloSenzaConnessioneDBResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://genmod.bl.gene.eldasoft.it", ">componiModelloSenzaConnessioneDBResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("componiModelloSenzaConnessioneDBReturn");
        elemField.setXmlName(new javax.xml.namespace.QName("http://genmod.bl.gene.eldasoft.it", "componiModelloSenzaConnessioneDBReturn"));
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

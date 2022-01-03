/**
 * ComponiResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.saga.library.reportGeneratoreModelli.genmod;

public class ComponiResponse  implements java.io.Serializable {
    private java.lang.String componiReturn;

    public ComponiResponse() {
    }

    public ComponiResponse(
           java.lang.String componiReturn) {
           this.componiReturn = componiReturn;
    }


    /**
     * Gets the componiReturn value for this ComponiResponse.
     * 
     * @return componiReturn
     */
    public java.lang.String getComponiReturn() {
        return componiReturn;
    }


    /**
     * Sets the componiReturn value for this ComponiResponse.
     * 
     * @param componiReturn
     */
    public void setComponiReturn(java.lang.String componiReturn) {
        this.componiReturn = componiReturn;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ComponiResponse)) return false;
        ComponiResponse other = (ComponiResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.componiReturn==null && other.getComponiReturn()==null) || 
             (this.componiReturn!=null &&
              this.componiReturn.equals(other.getComponiReturn())));
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
        if (getComponiReturn() != null) {
            _hashCode += getComponiReturn().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ComponiResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://genmod.bl.gene.eldasoft.it", ">componiResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("componiReturn");
        elemField.setXmlName(new javax.xml.namespace.QName("http://genmod.bl.gene.eldasoft.it", "componiReturn"));
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

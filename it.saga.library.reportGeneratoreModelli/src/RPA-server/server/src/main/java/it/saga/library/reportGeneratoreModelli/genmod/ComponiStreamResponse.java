/**
 * ComponiStreamResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.saga.library.reportGeneratoreModelli.genmod;

public class ComponiStreamResponse  implements java.io.Serializable {
    private byte[] componiStreamReturn;

    public ComponiStreamResponse() {
    }

    public ComponiStreamResponse(
           byte[] componiStreamReturn) {
           this.componiStreamReturn = componiStreamReturn;
    }


    /**
     * Gets the componiStreamReturn value for this ComponiStreamResponse.
     * 
     * @return componiStreamReturn
     */
    public byte[] getComponiStreamReturn() {
        return componiStreamReturn;
    }


    /**
     * Sets the componiStreamReturn value for this ComponiStreamResponse.
     * 
     * @param componiStreamReturn
     */
    public void setComponiStreamReturn(byte[] componiStreamReturn) {
        this.componiStreamReturn = componiStreamReturn;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ComponiStreamResponse)) return false;
        ComponiStreamResponse other = (ComponiStreamResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.componiStreamReturn==null && other.getComponiStreamReturn()==null) || 
             (this.componiStreamReturn!=null &&
              java.util.Arrays.equals(this.componiStreamReturn, other.getComponiStreamReturn())));
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
        if (getComponiStreamReturn() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getComponiStreamReturn());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getComponiStreamReturn(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ComponiStreamResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://genmod.bl.gene.eldasoft.it", ">componiStreamResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("componiStreamReturn");
        elemField.setXmlName(new javax.xml.namespace.QName("http://genmod.bl.gene.eldasoft.it", "componiStreamReturn"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
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

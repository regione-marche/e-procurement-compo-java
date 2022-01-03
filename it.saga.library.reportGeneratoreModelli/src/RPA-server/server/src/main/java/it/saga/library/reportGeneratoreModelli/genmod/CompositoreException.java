/**
 * CompositoreException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.saga.library.reportGeneratoreModelli.genmod;

public class CompositoreException  extends org.apache.axis.AxisFault  implements java.io.Serializable {
    private java.lang.Object cause1;

    private java.lang.String chiaveResourceBundle;

    private java.lang.String codiceErrore;

    private java.lang.String famiglia;

    private java.lang.String message1;

    private java.lang.String[] parametri;

    public CompositoreException() {
    }

    public CompositoreException(
           java.lang.Object cause1,
           java.lang.String chiaveResourceBundle,
           java.lang.String codiceErrore,
           java.lang.String famiglia,
           java.lang.String message1,
           java.lang.String[] parametri) {
        this.cause1 = cause1;
        this.chiaveResourceBundle = chiaveResourceBundle;
        this.codiceErrore = codiceErrore;
        this.famiglia = famiglia;
        this.message1 = message1;
        this.parametri = parametri;
    }


    /**
     * Gets the cause1 value for this CompositoreException.
     * 
     * @return cause1
     */
    public java.lang.Object getCause1() {
        return cause1;
    }


    /**
     * Sets the cause1 value for this CompositoreException.
     * 
     * @param cause1
     */
    public void setCause1(java.lang.Object cause1) {
        this.cause1 = cause1;
    }


    /**
     * Gets the chiaveResourceBundle value for this CompositoreException.
     * 
     * @return chiaveResourceBundle
     */
    public java.lang.String getChiaveResourceBundle() {
        return chiaveResourceBundle;
    }


    /**
     * Sets the chiaveResourceBundle value for this CompositoreException.
     * 
     * @param chiaveResourceBundle
     */
    public void setChiaveResourceBundle(java.lang.String chiaveResourceBundle) {
        this.chiaveResourceBundle = chiaveResourceBundle;
    }


    /**
     * Gets the codiceErrore value for this CompositoreException.
     * 
     * @return codiceErrore
     */
    public java.lang.String getCodiceErrore() {
        return codiceErrore;
    }


    /**
     * Sets the codiceErrore value for this CompositoreException.
     * 
     * @param codiceErrore
     */
    public void setCodiceErrore(java.lang.String codiceErrore) {
        this.codiceErrore = codiceErrore;
    }


    /**
     * Gets the famiglia value for this CompositoreException.
     * 
     * @return famiglia
     */
    public java.lang.String getFamiglia() {
        return famiglia;
    }


    /**
     * Sets the famiglia value for this CompositoreException.
     * 
     * @param famiglia
     */
    public void setFamiglia(java.lang.String famiglia) {
        this.famiglia = famiglia;
    }


    /**
     * Gets the message1 value for this CompositoreException.
     * 
     * @return message1
     */
    public java.lang.String getMessage1() {
        return message1;
    }


    /**
     * Sets the message1 value for this CompositoreException.
     * 
     * @param message1
     */
    public void setMessage1(java.lang.String message1) {
        this.message1 = message1;
    }


    /**
     * Gets the parametri value for this CompositoreException.
     * 
     * @return parametri
     */
    public java.lang.String[] getParametri() {
        return parametri;
    }


    /**
     * Sets the parametri value for this CompositoreException.
     * 
     * @param parametri
     */
    public void setParametri(java.lang.String[] parametri) {
        this.parametri = parametri;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CompositoreException)) return false;
        CompositoreException other = (CompositoreException) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.cause1==null && other.getCause1()==null) || 
             (this.cause1!=null &&
              this.cause1.equals(other.getCause1()))) &&
            ((this.chiaveResourceBundle==null && other.getChiaveResourceBundle()==null) || 
             (this.chiaveResourceBundle!=null &&
              this.chiaveResourceBundle.equals(other.getChiaveResourceBundle()))) &&
            ((this.codiceErrore==null && other.getCodiceErrore()==null) || 
             (this.codiceErrore!=null &&
              this.codiceErrore.equals(other.getCodiceErrore()))) &&
            ((this.famiglia==null && other.getFamiglia()==null) || 
             (this.famiglia!=null &&
              this.famiglia.equals(other.getFamiglia()))) &&
            ((this.message1==null && other.getMessage1()==null) || 
             (this.message1!=null &&
              this.message1.equals(other.getMessage1()))) &&
            ((this.parametri==null && other.getParametri()==null) || 
             (this.parametri!=null &&
              java.util.Arrays.equals(this.parametri, other.getParametri())));
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
        if (getCause1() != null) {
            _hashCode += getCause1().hashCode();
        }
        if (getChiaveResourceBundle() != null) {
            _hashCode += getChiaveResourceBundle().hashCode();
        }
        if (getCodiceErrore() != null) {
            _hashCode += getCodiceErrore().hashCode();
        }
        if (getFamiglia() != null) {
            _hashCode += getFamiglia().hashCode();
        }
        if (getMessage1() != null) {
            _hashCode += getMessage1().hashCode();
        }
        if (getParametri() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getParametri());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getParametri(), i);
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
        new org.apache.axis.description.TypeDesc(CompositoreException.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://genmod.bl.gene.eldasoft.it", "CompositoreException"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cause1");
        elemField.setXmlName(new javax.xml.namespace.QName("http://genmod.bl.gene.eldasoft.it", "cause"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chiaveResourceBundle");
        elemField.setXmlName(new javax.xml.namespace.QName("http://genmod.bl.gene.eldasoft.it", "chiaveResourceBundle"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("codiceErrore");
        elemField.setXmlName(new javax.xml.namespace.QName("http://genmod.bl.gene.eldasoft.it", "codiceErrore"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("famiglia");
        elemField.setXmlName(new javax.xml.namespace.QName("http://genmod.bl.gene.eldasoft.it", "famiglia"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("message1");
        elemField.setXmlName(new javax.xml.namespace.QName("http://genmod.bl.gene.eldasoft.it", "message"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("parametri");
        elemField.setXmlName(new javax.xml.namespace.QName("http://genmod.bl.gene.eldasoft.it", "parametri"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("http://genmod.bl.gene.eldasoft.it", "item"));
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


    /**
     * Writes the exception data to the faultDetails
     */
    public void writeDetails(javax.xml.namespace.QName qname, org.apache.axis.encoding.SerializationContext context) throws java.io.IOException {
        context.serialize(qname, null, this);
    }
}

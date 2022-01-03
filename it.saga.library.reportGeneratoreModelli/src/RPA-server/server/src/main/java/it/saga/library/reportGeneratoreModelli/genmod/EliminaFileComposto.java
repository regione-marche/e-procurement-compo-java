/**
 * EliminaFileComposto.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.saga.library.reportGeneratoreModelli.genmod;

public class EliminaFileComposto  implements java.io.Serializable {
    private java.lang.String nomeFileComposto;

    private java.lang.String idApplicazione;

    private java.lang.String codiceApplicazione;

    public EliminaFileComposto() {
    }

    public EliminaFileComposto(
           java.lang.String nomeFileComposto,
           java.lang.String idApplicazione,
           java.lang.String codiceApplicazione) {
           this.nomeFileComposto = nomeFileComposto;
           this.idApplicazione = idApplicazione;
           this.codiceApplicazione = codiceApplicazione;
    }


    /**
     * Gets the nomeFileComposto value for this EliminaFileComposto.
     * 
     * @return nomeFileComposto
     */
    public java.lang.String getNomeFileComposto() {
        return nomeFileComposto;
    }


    /**
     * Sets the nomeFileComposto value for this EliminaFileComposto.
     * 
     * @param nomeFileComposto
     */
    public void setNomeFileComposto(java.lang.String nomeFileComposto) {
        this.nomeFileComposto = nomeFileComposto;
    }


    /**
     * Gets the idApplicazione value for this EliminaFileComposto.
     * 
     * @return idApplicazione
     */
    public java.lang.String getIdApplicazione() {
        return idApplicazione;
    }


    /**
     * Sets the idApplicazione value for this EliminaFileComposto.
     * 
     * @param idApplicazione
     */
    public void setIdApplicazione(java.lang.String idApplicazione) {
        this.idApplicazione = idApplicazione;
    }


    /**
     * Gets the codiceApplicazione value for this EliminaFileComposto.
     * 
     * @return codiceApplicazione
     */
    public java.lang.String getCodiceApplicazione() {
        return codiceApplicazione;
    }


    /**
     * Sets the codiceApplicazione value for this EliminaFileComposto.
     * 
     * @param codiceApplicazione
     */
    public void setCodiceApplicazione(java.lang.String codiceApplicazione) {
        this.codiceApplicazione = codiceApplicazione;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EliminaFileComposto)) return false;
        EliminaFileComposto other = (EliminaFileComposto) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.nomeFileComposto==null && other.getNomeFileComposto()==null) || 
             (this.nomeFileComposto!=null &&
              this.nomeFileComposto.equals(other.getNomeFileComposto()))) &&
            ((this.idApplicazione==null && other.getIdApplicazione()==null) || 
             (this.idApplicazione!=null &&
              this.idApplicazione.equals(other.getIdApplicazione()))) &&
            ((this.codiceApplicazione==null && other.getCodiceApplicazione()==null) || 
             (this.codiceApplicazione!=null &&
              this.codiceApplicazione.equals(other.getCodiceApplicazione())));
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
        if (getNomeFileComposto() != null) {
            _hashCode += getNomeFileComposto().hashCode();
        }
        if (getIdApplicazione() != null) {
            _hashCode += getIdApplicazione().hashCode();
        }
        if (getCodiceApplicazione() != null) {
            _hashCode += getCodiceApplicazione().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EliminaFileComposto.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://genmod.bl.gene.eldasoft.it", ">eliminaFileComposto"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nomeFileComposto");
        elemField.setXmlName(new javax.xml.namespace.QName("http://genmod.bl.gene.eldasoft.it", "nomeFileComposto"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("idApplicazione");
        elemField.setXmlName(new javax.xml.namespace.QName("http://genmod.bl.gene.eldasoft.it", "idApplicazione"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("codiceApplicazione");
        elemField.setXmlName(new javax.xml.namespace.QName("http://genmod.bl.gene.eldasoft.it", "codiceApplicazione"));
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

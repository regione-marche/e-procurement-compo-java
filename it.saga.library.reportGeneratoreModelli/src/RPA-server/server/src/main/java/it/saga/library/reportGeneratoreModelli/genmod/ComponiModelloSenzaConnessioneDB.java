/**
 * ComponiModelloSenzaConnessioneDB.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.saga.library.reportGeneratoreModelli.genmod;

public class ComponiModelloSenzaConnessioneDB  implements java.io.Serializable {
    private java.lang.String nomeModello;

    private java.lang.String nomeFileSorgenteDati;

    private java.lang.String idApplicazione;

    private java.lang.String codiceApplicazione;

    private java.lang.String registri;

    public ComponiModelloSenzaConnessioneDB() {
    }

    public ComponiModelloSenzaConnessioneDB(
           java.lang.String nomeModello,
           java.lang.String nomeFileSorgenteDati,
           java.lang.String idApplicazione,
           java.lang.String codiceApplicazione,
           java.lang.String registri) {
           this.nomeModello = nomeModello;
           this.nomeFileSorgenteDati = nomeFileSorgenteDati;
           this.idApplicazione = idApplicazione;
           this.codiceApplicazione = codiceApplicazione;
           this.registri = registri;
    }


    /**
     * Gets the nomeModello value for this ComponiModelloSenzaConnessioneDB.
     * 
     * @return nomeModello
     */
    public java.lang.String getNomeModello() {
        return nomeModello;
    }


    /**
     * Sets the nomeModello value for this ComponiModelloSenzaConnessioneDB.
     * 
     * @param nomeModello
     */
    public void setNomeModello(java.lang.String nomeModello) {
        this.nomeModello = nomeModello;
    }


    /**
     * Gets the nomeFileSorgenteDati value for this ComponiModelloSenzaConnessioneDB.
     * 
     * @return nomeFileSorgenteDati
     */
    public java.lang.String getNomeFileSorgenteDati() {
        return nomeFileSorgenteDati;
    }


    /**
     * Sets the nomeFileSorgenteDati value for this ComponiModelloSenzaConnessioneDB.
     * 
     * @param nomeFileSorgenteDati
     */
    public void setNomeFileSorgenteDati(java.lang.String nomeFileSorgenteDati) {
        this.nomeFileSorgenteDati = nomeFileSorgenteDati;
    }


    /**
     * Gets the idApplicazione value for this ComponiModelloSenzaConnessioneDB.
     * 
     * @return idApplicazione
     */
    public java.lang.String getIdApplicazione() {
        return idApplicazione;
    }


    /**
     * Sets the idApplicazione value for this ComponiModelloSenzaConnessioneDB.
     * 
     * @param idApplicazione
     */
    public void setIdApplicazione(java.lang.String idApplicazione) {
        this.idApplicazione = idApplicazione;
    }


    /**
     * Gets the codiceApplicazione value for this ComponiModelloSenzaConnessioneDB.
     * 
     * @return codiceApplicazione
     */
    public java.lang.String getCodiceApplicazione() {
        return codiceApplicazione;
    }


    /**
     * Sets the codiceApplicazione value for this ComponiModelloSenzaConnessioneDB.
     * 
     * @param codiceApplicazione
     */
    public void setCodiceApplicazione(java.lang.String codiceApplicazione) {
        this.codiceApplicazione = codiceApplicazione;
    }


    /**
     * Gets the registri value for this ComponiModelloSenzaConnessioneDB.
     * 
     * @return registri
     */
    public java.lang.String getRegistri() {
        return registri;
    }


    /**
     * Sets the registri value for this ComponiModelloSenzaConnessioneDB.
     * 
     * @param registri
     */
    public void setRegistri(java.lang.String registri) {
        this.registri = registri;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ComponiModelloSenzaConnessioneDB)) return false;
        ComponiModelloSenzaConnessioneDB other = (ComponiModelloSenzaConnessioneDB) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.nomeModello==null && other.getNomeModello()==null) || 
             (this.nomeModello!=null &&
              this.nomeModello.equals(other.getNomeModello()))) &&
            ((this.nomeFileSorgenteDati==null && other.getNomeFileSorgenteDati()==null) || 
             (this.nomeFileSorgenteDati!=null &&
              this.nomeFileSorgenteDati.equals(other.getNomeFileSorgenteDati()))) &&
            ((this.idApplicazione==null && other.getIdApplicazione()==null) || 
             (this.idApplicazione!=null &&
              this.idApplicazione.equals(other.getIdApplicazione()))) &&
            ((this.codiceApplicazione==null && other.getCodiceApplicazione()==null) || 
             (this.codiceApplicazione!=null &&
              this.codiceApplicazione.equals(other.getCodiceApplicazione()))) &&
            ((this.registri==null && other.getRegistri()==null) || 
             (this.registri!=null &&
              this.registri.equals(other.getRegistri())));
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
        if (getNomeModello() != null) {
            _hashCode += getNomeModello().hashCode();
        }
        if (getNomeFileSorgenteDati() != null) {
            _hashCode += getNomeFileSorgenteDati().hashCode();
        }
        if (getIdApplicazione() != null) {
            _hashCode += getIdApplicazione().hashCode();
        }
        if (getCodiceApplicazione() != null) {
            _hashCode += getCodiceApplicazione().hashCode();
        }
        if (getRegistri() != null) {
            _hashCode += getRegistri().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ComponiModelloSenzaConnessioneDB.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://genmod.bl.gene.eldasoft.it", ">componiModelloSenzaConnessioneDB"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nomeModello");
        elemField.setXmlName(new javax.xml.namespace.QName("http://genmod.bl.gene.eldasoft.it", "nomeModello"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nomeFileSorgenteDati");
        elemField.setXmlName(new javax.xml.namespace.QName("http://genmod.bl.gene.eldasoft.it", "nomeFileSorgenteDati"));
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("registri");
        elemField.setXmlName(new javax.xml.namespace.QName("http://genmod.bl.gene.eldasoft.it", "registri"));
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

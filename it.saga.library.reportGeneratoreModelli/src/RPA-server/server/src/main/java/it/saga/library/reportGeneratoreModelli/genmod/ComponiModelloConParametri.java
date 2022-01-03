/**
 * ComponiModelloConParametri.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.saga.library.reportGeneratoreModelli.genmod;

public class ComponiModelloConParametri  implements java.io.Serializable {
    private java.lang.String nomeModello;

    private java.lang.String entita;

    private java.lang.String elencoChiavi;

    private java.lang.String[] valoriChiavi;

    private java.lang.String idApplicazione;

    private java.lang.String codiceApplicazione;

    private int idSessione;

    public ComponiModelloConParametri() {
    }

    public ComponiModelloConParametri(
           java.lang.String nomeModello,
           java.lang.String entita,
           java.lang.String elencoChiavi,
           java.lang.String[] valoriChiavi,
           java.lang.String idApplicazione,
           java.lang.String codiceApplicazione,
           int idSessione) {
           this.nomeModello = nomeModello;
           this.entita = entita;
           this.elencoChiavi = elencoChiavi;
           this.valoriChiavi = valoriChiavi;
           this.idApplicazione = idApplicazione;
           this.codiceApplicazione = codiceApplicazione;
           this.idSessione = idSessione;
    }


    /**
     * Gets the nomeModello value for this ComponiModelloConParametri.
     * 
     * @return nomeModello
     */
    public java.lang.String getNomeModello() {
        return nomeModello;
    }


    /**
     * Sets the nomeModello value for this ComponiModelloConParametri.
     * 
     * @param nomeModello
     */
    public void setNomeModello(java.lang.String nomeModello) {
        this.nomeModello = nomeModello;
    }


    /**
     * Gets the entita value for this ComponiModelloConParametri.
     * 
     * @return entita
     */
    public java.lang.String getEntita() {
        return entita;
    }


    /**
     * Sets the entita value for this ComponiModelloConParametri.
     * 
     * @param entita
     */
    public void setEntita(java.lang.String entita) {
        this.entita = entita;
    }


    /**
     * Gets the elencoChiavi value for this ComponiModelloConParametri.
     * 
     * @return elencoChiavi
     */
    public java.lang.String getElencoChiavi() {
        return elencoChiavi;
    }


    /**
     * Sets the elencoChiavi value for this ComponiModelloConParametri.
     * 
     * @param elencoChiavi
     */
    public void setElencoChiavi(java.lang.String elencoChiavi) {
        this.elencoChiavi = elencoChiavi;
    }


    /**
     * Gets the valoriChiavi value for this ComponiModelloConParametri.
     * 
     * @return valoriChiavi
     */
    public java.lang.String[] getValoriChiavi() {
        return valoriChiavi;
    }


    /**
     * Sets the valoriChiavi value for this ComponiModelloConParametri.
     * 
     * @param valoriChiavi
     */
    public void setValoriChiavi(java.lang.String[] valoriChiavi) {
        this.valoriChiavi = valoriChiavi;
    }

    public java.lang.String getValoriChiavi(int i) {
        return this.valoriChiavi[i];
    }

    public void setValoriChiavi(int i, java.lang.String _value) {
        this.valoriChiavi[i] = _value;
    }


    /**
     * Gets the idApplicazione value for this ComponiModelloConParametri.
     * 
     * @return idApplicazione
     */
    public java.lang.String getIdApplicazione() {
        return idApplicazione;
    }


    /**
     * Sets the idApplicazione value for this ComponiModelloConParametri.
     * 
     * @param idApplicazione
     */
    public void setIdApplicazione(java.lang.String idApplicazione) {
        this.idApplicazione = idApplicazione;
    }


    /**
     * Gets the codiceApplicazione value for this ComponiModelloConParametri.
     * 
     * @return codiceApplicazione
     */
    public java.lang.String getCodiceApplicazione() {
        return codiceApplicazione;
    }


    /**
     * Sets the codiceApplicazione value for this ComponiModelloConParametri.
     * 
     * @param codiceApplicazione
     */
    public void setCodiceApplicazione(java.lang.String codiceApplicazione) {
        this.codiceApplicazione = codiceApplicazione;
    }


    /**
     * Gets the idSessione value for this ComponiModelloConParametri.
     * 
     * @return idSessione
     */
    public int getIdSessione() {
        return idSessione;
    }


    /**
     * Sets the idSessione value for this ComponiModelloConParametri.
     * 
     * @param idSessione
     */
    public void setIdSessione(int idSessione) {
        this.idSessione = idSessione;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ComponiModelloConParametri)) return false;
        ComponiModelloConParametri other = (ComponiModelloConParametri) obj;
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
            ((this.entita==null && other.getEntita()==null) || 
             (this.entita!=null &&
              this.entita.equals(other.getEntita()))) &&
            ((this.elencoChiavi==null && other.getElencoChiavi()==null) || 
             (this.elencoChiavi!=null &&
              this.elencoChiavi.equals(other.getElencoChiavi()))) &&
            ((this.valoriChiavi==null && other.getValoriChiavi()==null) || 
             (this.valoriChiavi!=null &&
              java.util.Arrays.equals(this.valoriChiavi, other.getValoriChiavi()))) &&
            ((this.idApplicazione==null && other.getIdApplicazione()==null) || 
             (this.idApplicazione!=null &&
              this.idApplicazione.equals(other.getIdApplicazione()))) &&
            ((this.codiceApplicazione==null && other.getCodiceApplicazione()==null) || 
             (this.codiceApplicazione!=null &&
              this.codiceApplicazione.equals(other.getCodiceApplicazione()))) &&
            this.idSessione == other.getIdSessione();
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
        if (getEntita() != null) {
            _hashCode += getEntita().hashCode();
        }
        if (getElencoChiavi() != null) {
            _hashCode += getElencoChiavi().hashCode();
        }
        if (getValoriChiavi() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getValoriChiavi());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getValoriChiavi(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getIdApplicazione() != null) {
            _hashCode += getIdApplicazione().hashCode();
        }
        if (getCodiceApplicazione() != null) {
            _hashCode += getCodiceApplicazione().hashCode();
        }
        _hashCode += getIdSessione();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ComponiModelloConParametri.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://genmod.bl.gene.eldasoft.it", ">componiModelloConParametri"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nomeModello");
        elemField.setXmlName(new javax.xml.namespace.QName("http://genmod.bl.gene.eldasoft.it", "nomeModello"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("entita");
        elemField.setXmlName(new javax.xml.namespace.QName("http://genmod.bl.gene.eldasoft.it", "entita"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("elencoChiavi");
        elemField.setXmlName(new javax.xml.namespace.QName("http://genmod.bl.gene.eldasoft.it", "elencoChiavi"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("valoriChiavi");
        elemField.setXmlName(new javax.xml.namespace.QName("http://genmod.bl.gene.eldasoft.it", "valoriChiavi"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
        elemField.setFieldName("idSessione");
        elemField.setXmlName(new javax.xml.namespace.QName("http://genmod.bl.gene.eldasoft.it", "idSessione"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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

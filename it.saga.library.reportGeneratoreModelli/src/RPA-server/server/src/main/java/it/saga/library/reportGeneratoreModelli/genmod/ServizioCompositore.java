/**
 * ServizioCompositore.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.saga.library.reportGeneratoreModelli.genmod;

public interface ServizioCompositore extends java.rmi.Remote {
    public void compila(java.lang.String nomeModello, java.lang.String idApplicazione, java.lang.String codiceApplicazione) throws java.rmi.RemoteException, it.saga.library.reportGeneratoreModelli.genmod.CompositoreException;
    public void compilaModello(java.lang.String nomeModello, java.lang.String idApplicazione, java.lang.String codiceApplicazione, java.lang.String registri) throws java.rmi.RemoteException, it.saga.library.reportGeneratoreModelli.genmod.CompositoreException;
    public void compilaModelloSenzaConnessioneDB(java.lang.String nomeModello, java.lang.String idApplicazione, java.lang.String codiceApplicazione, java.lang.String registri) throws java.rmi.RemoteException, it.saga.library.reportGeneratoreModelli.genmod.CompositoreException;
    public java.lang.String componiModello(java.lang.String nomeModello, java.lang.String entita, java.lang.String elencoChiavi, java.lang.String[] valoriChiavi, java.lang.String idApplicazione, java.lang.String codiceApplicazione) throws java.rmi.RemoteException, it.saga.library.reportGeneratoreModelli.genmod.CompositoreException;
    public java.lang.String componiModelloConParametri(java.lang.String nomeModello, java.lang.String entita, java.lang.String elencoChiavi, java.lang.String[] valoriChiavi, java.lang.String idApplicazione, java.lang.String codiceApplicazione, int idSessione) throws java.rmi.RemoteException, it.saga.library.reportGeneratoreModelli.genmod.CompositoreException;
    public java.lang.String componi(java.lang.String nomeModello, java.lang.String entita, java.lang.String elencoChiavi, java.lang.String[] valoriChiavi, java.lang.String idApplicazione, java.lang.String codiceApplicazione, java.lang.String registri) throws java.rmi.RemoteException, it.saga.library.reportGeneratoreModelli.genmod.CompositoreException;
    public java.lang.String componiModelloSenzaConnessioneDB(java.lang.String nomeModello, java.lang.String nomeFileSorgenteDati, java.lang.String idApplicazione, java.lang.String codiceApplicazione, java.lang.String registri) throws java.rmi.RemoteException, it.saga.library.reportGeneratoreModelli.genmod.CompositoreException;
    public void eliminaFileComposto(java.lang.String nomeFileComposto, java.lang.String idApplicazione, java.lang.String codiceApplicazione) throws java.rmi.RemoteException, it.saga.library.reportGeneratoreModelli.genmod.CompositoreException;
    public byte[] componiStream(java.lang.String nomeModello, byte[] contenutoModello, java.lang.String entita, java.lang.String elencoChiavi, java.lang.String[] valoriChiavi, java.lang.String idApplicazione, java.lang.String codiceApplicazione, java.lang.String registri) throws java.rmi.RemoteException, it.saga.library.reportGeneratoreModelli.genmod.CompositoreException;
}

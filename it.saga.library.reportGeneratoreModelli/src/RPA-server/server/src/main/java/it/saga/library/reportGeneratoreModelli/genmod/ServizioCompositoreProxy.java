package it.saga.library.reportGeneratoreModelli.genmod;

public class ServizioCompositoreProxy implements it.saga.library.reportGeneratoreModelli.genmod.ServizioCompositore {
  private String _endpoint = null;
  private it.saga.library.reportGeneratoreModelli.genmod.ServizioCompositore servizioCompositore = null;
  
  public ServizioCompositoreProxy() {
    _initServizioCompositoreProxy();
  }
  
  public ServizioCompositoreProxy(String endpoint) {
    _endpoint = endpoint;
    _initServizioCompositoreProxy();
  }
  
  private void _initServizioCompositoreProxy() {
    try {
      servizioCompositore = (new it.saga.library.reportGeneratoreModelli.genmod.ServizioCompositoreServiceLocator()).getServizioCompositore();
      if (servizioCompositore != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)servizioCompositore)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)servizioCompositore)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (servizioCompositore != null)
      ((javax.xml.rpc.Stub)servizioCompositore)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public it.saga.library.reportGeneratoreModelli.genmod.ServizioCompositore getServizioCompositore() {
    if (servizioCompositore == null)
      _initServizioCompositoreProxy();
    return servizioCompositore;
  }
  
  public void compila(java.lang.String nomeModello, java.lang.String idApplicazione, java.lang.String codiceApplicazione) throws java.rmi.RemoteException, it.saga.library.reportGeneratoreModelli.genmod.CompositoreException{
    if (servizioCompositore == null)
      _initServizioCompositoreProxy();
    servizioCompositore.compila(nomeModello, idApplicazione, codiceApplicazione);
  }
  
  public void compilaModello(java.lang.String nomeModello, java.lang.String idApplicazione, java.lang.String codiceApplicazione, java.lang.String registri) throws java.rmi.RemoteException, it.saga.library.reportGeneratoreModelli.genmod.CompositoreException{
    if (servizioCompositore == null)
      _initServizioCompositoreProxy();
    servizioCompositore.compilaModello(nomeModello, idApplicazione, codiceApplicazione, registri);
  }
  
  public void compilaModelloSenzaConnessioneDB(java.lang.String nomeModello, java.lang.String idApplicazione, java.lang.String codiceApplicazione, java.lang.String registri) throws java.rmi.RemoteException, it.saga.library.reportGeneratoreModelli.genmod.CompositoreException{
    if (servizioCompositore == null)
      _initServizioCompositoreProxy();
    servizioCompositore.compilaModelloSenzaConnessioneDB(nomeModello, idApplicazione, codiceApplicazione, registri);
  }
  
  public java.lang.String componiModello(java.lang.String nomeModello, java.lang.String entita, java.lang.String elencoChiavi, java.lang.String[] valoriChiavi, java.lang.String idApplicazione, java.lang.String codiceApplicazione) throws java.rmi.RemoteException, it.saga.library.reportGeneratoreModelli.genmod.CompositoreException{
    if (servizioCompositore == null)
      _initServizioCompositoreProxy();
    return servizioCompositore.componiModello(nomeModello, entita, elencoChiavi, valoriChiavi, idApplicazione, codiceApplicazione);
  }
  
  public java.lang.String componiModelloConParametri(java.lang.String nomeModello, java.lang.String entita, java.lang.String elencoChiavi, java.lang.String[] valoriChiavi, java.lang.String idApplicazione, java.lang.String codiceApplicazione, int idSessione) throws java.rmi.RemoteException, it.saga.library.reportGeneratoreModelli.genmod.CompositoreException{
    if (servizioCompositore == null)
      _initServizioCompositoreProxy();
    return servizioCompositore.componiModelloConParametri(nomeModello, entita, elencoChiavi, valoriChiavi, idApplicazione, codiceApplicazione, idSessione);
  }
  
  public java.lang.String componi(java.lang.String nomeModello, java.lang.String entita, java.lang.String elencoChiavi, java.lang.String[] valoriChiavi, java.lang.String idApplicazione, java.lang.String codiceApplicazione, java.lang.String registri) throws java.rmi.RemoteException, it.saga.library.reportGeneratoreModelli.genmod.CompositoreException{
    if (servizioCompositore == null)
      _initServizioCompositoreProxy();
    return servizioCompositore.componi(nomeModello, entita, elencoChiavi, valoriChiavi, idApplicazione, codiceApplicazione, registri);
  }
  
  public java.lang.String componiModelloSenzaConnessioneDB(java.lang.String nomeModello, java.lang.String nomeFileSorgenteDati, java.lang.String idApplicazione, java.lang.String codiceApplicazione, java.lang.String registri) throws java.rmi.RemoteException, it.saga.library.reportGeneratoreModelli.genmod.CompositoreException{
    if (servizioCompositore == null)
      _initServizioCompositoreProxy();
    return servizioCompositore.componiModelloSenzaConnessioneDB(nomeModello, nomeFileSorgenteDati, idApplicazione, codiceApplicazione, registri);
  }
  
  public void eliminaFileComposto(java.lang.String nomeFileComposto, java.lang.String idApplicazione, java.lang.String codiceApplicazione) throws java.rmi.RemoteException, it.saga.library.reportGeneratoreModelli.genmod.CompositoreException{
    if (servizioCompositore == null)
      _initServizioCompositoreProxy();
    servizioCompositore.eliminaFileComposto(nomeFileComposto, idApplicazione, codiceApplicazione);
  }
  
  public byte[] componiStream(java.lang.String nomeModello, byte[] contenutoModello, java.lang.String entita, java.lang.String elencoChiavi, java.lang.String[] valoriChiavi, java.lang.String idApplicazione, java.lang.String codiceApplicazione, java.lang.String registri) throws java.rmi.RemoteException, it.saga.library.reportGeneratoreModelli.genmod.CompositoreException{
    if (servizioCompositore == null)
      _initServizioCompositoreProxy();
    return servizioCompositore.componiStream(nomeModello, contenutoModello, entita, elencoChiavi, valoriChiavi, idApplicazione, codiceApplicazione, registri);
  }
  
  
}
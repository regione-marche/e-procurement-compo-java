package it.saga.library.reportGeneratoreModelli.compositore.compo;

import it.saga.library.commonDataTypes.CdtHibernateSessionWrapper;
import it.saga.library.reportGeneratoreModelli.compositore.interfaces.RpaImportExternalImageI;

import java.sql.Connection;

public class RpaComposerStartConfiguration {

    // Link: https://regex101.com/r/Nwc4xk/1
    private static final String FILE_PATH_CHECK_REGEX = "^(.+[\\\\\\/].+)\\.(.+)$";

    private CdtHibernateSessionWrapper connection;
    private String dbmsName;
    private String dbDriver;
    private String dbConnectionUrl;
    private String dbUsername;
    private String dbPassword;
    private String documentFilePath;
    private String outputFilePath;
    private String entityDeclaration;
    private String dbType;
    private boolean isDebugMessageActive;
    private boolean isWarningMessageActive;
    private boolean isErrorMessageActive;
    private Long    userId;
    private String  parameters;
    private Character               decimalSeparator;
    private Character               integerSeparator;
    private Long                    limitReadData;
    private RpaImportExternalImageI importExternalImage;
    private Long                    limitMemorySize;
    private boolean                 isForceUseDbParameters;
    //retrocompatibilià
    private String connectionString;


    public RpaComposerStartConfiguration() {
    }

    public RpaComposerStartConfiguration(
        String  dbmsName,
        String  dbDriver,
        String  dbConnectionUrl,
        String  dbUsername,
        String  dbPassword,
        String  documentFilePath,
        String  outputFilePath,
        String  entityDeclaration,
        String  dbType,
        String  connectionString,
        Long    userId,
        String  parameters,
        boolean isDebugMessageActive,
        boolean isWarningMessageActive,
        boolean isErrorMessageActive,
        RpaImportExternalImageI importExternalImage,
        Long    limitMemorySize,
        boolean isForceUseDbParameters
    ) {

        this.dbmsName           = dbmsName;
        this.dbDriver           = dbDriver;
        this.dbConnectionUrl    = dbConnectionUrl;
        this.dbUsername         = dbUsername;
        this.dbPassword         = dbPassword;
        this.documentFilePath   = documentFilePath;
        this.outputFilePath     = outputFilePath;
        this.entityDeclaration  = entityDeclaration;
        this.dbType             = dbType;
        this.connectionString   = connectionString;
        this.userId             = userId;
        this.parameters         = parameters;
        this.isDebugMessageActive   = isDebugMessageActive;
        this.isWarningMessageActive = isWarningMessageActive;
        this.isErrorMessageActive   = isErrorMessageActive;
        this.importExternalImage    = importExternalImage;
        this.limitMemorySize        = limitMemorySize;
        this.isForceUseDbParameters = isForceUseDbParameters;

    }

    public CdtHibernateSessionWrapper getConnection() {
        return connection;
    }

    public void setConnection(CdtHibernateSessionWrapper connection) {
        this.connection = connection;
    }

    public String getDBMSName() {
        return dbmsName;
    }

    public void setDBMSName(String dbmsName) {
        this.dbmsName = dbmsName;
    }

    public String getDBDriver() {
        return dbDriver;
    }

    public void setDBDriver(String dbDriver) {
        this.dbDriver = dbDriver;
    }

    public String getDBConnectionUrl() {
        return dbConnectionUrl;
    }

    public void setDBConnectionUrl(String dbConnectionUrl) {
        this.dbConnectionUrl = dbConnectionUrl;
    }

    public String getDBUsername() {
        return dbUsername;
    }

    public void setDBUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }

    public String getDBPassword() {
        return dbPassword;
    }

    public void setDBPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getDocumentFilePath() {
        return documentFilePath;
    }

    public void setDocumentFilePath(String documentFilePath) {
        this.documentFilePath = documentFilePath;
    }

    public String getEntityDeclaration() {
        return entityDeclaration;
    }

    public void setEntityDeclaration(String entityDeclaration) {
        this.entityDeclaration = entityDeclaration;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public String getOutputFilePath() { return outputFilePath; }

    public void setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    public boolean isDebugMessageActive() { return isDebugMessageActive; }

    public void setIsDebugMessageActive(boolean isDebugMessageActive) { this.isDebugMessageActive = isDebugMessageActive; }

    public boolean isWarningMessageActive() { return isWarningMessageActive; }

    public void setIsWarningMessageActive(boolean isWarningMessageActive) { this.isWarningMessageActive = isWarningMessageActive; }

    public boolean isErrorMessageActive() { return isErrorMessageActive; }

    public void setIsErrorMessageActive(boolean isErrorMessageActive) { this.isErrorMessageActive = isErrorMessageActive; }

    public Long getUserId() { return userId; }

    public void setUserId(Long userId) { this.userId = userId; }

    public String getParameters() { return parameters; }

    public void setParameters(String parameters) { this.parameters = parameters; }

    public Character getDecimalSeparator() { return decimalSeparator; }

    public void setDecimalSeparator(Character decimalSeparator) { this.decimalSeparator = decimalSeparator; }

    public Character getIntegerSeparator() { return integerSeparator; }

    public void setIntegerSeparator(Character integerSeparator) { this.integerSeparator = integerSeparator; }

    public Long getLimitReadData() { return limitReadData; }

    public void setLimitReadData(Long limitReadData) { this.limitReadData = limitReadData; }

    public boolean isForceUseDbParameters() { return isForceUseDbParameters; }

    public void setIsForceUseDbParameters(boolean isForceUseDbParameters) { isForceUseDbParameters = isForceUseDbParameters; }

    public RpaImportExternalImageI getImportExternalImage() { return importExternalImage; }

    public Long getLimitMemorySize() { return limitMemorySize; }

    public void setLimitMemorySize(Long limitMemorySize) { this.limitMemorySize = limitMemorySize; }

}

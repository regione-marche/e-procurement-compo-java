package it.saga.library.reportGeneratoreModelli.setup.version;

import it.saga.library.authentication.AutCFGParamScope;
import it.saga.library.authentication.AutCFGUserSession;
import it.saga.library.ddl.DDLException;
import it.saga.library.messages.SagaException;
import it.saga.library.reportGeneratoreModelli.misc.RpaCostanti;
import it.saga.library.setup.SetupApp;
import it.saga.library.setup.SetupException;
import it.saga.library.setup.SetupStep;
import it.saga.library.setup.SetupStepRemote;
import it.saga.library.setup.SetupVersion;

public class RpaSetupVersion_14 extends SetupVersion {

    public RpaSetupVersion_14(SetupApp setupApp) {
        super(setupApp, 14);
    }

    public SetupStep[] getSetupSteps() throws SetupException, DDLException {
        return new SetupStep[] {
            new ParametroLimitSize(this)
        };
    }

    class ParametroLimitSize extends SetupStepRemote {

        public ParametroLimitSize(SetupVersion setup) {
            super(setup);
        }

        public void executeRemoteStep(AutCFGUserSession session) throws SagaException {

            session.put(
                    AutCFGParamScope.SCOPE_GLOBAL,
                    RpaCostanti.APP_PREFIX,
                    RpaCostanti.PARAMETRO_GLOBALE_LIMIT_SIZE_KB,
                    RpaCostanti.LIMIT_SIZE_DEFAULT_KB.toString()
            );
        }

    }

}
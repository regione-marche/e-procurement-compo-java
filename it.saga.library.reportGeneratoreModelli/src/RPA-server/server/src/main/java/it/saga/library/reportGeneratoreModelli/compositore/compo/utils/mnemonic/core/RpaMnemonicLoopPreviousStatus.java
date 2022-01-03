package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core;

import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScope;
import it.saga.library.reportGeneratoreModelli.compositore.antrl4.scope.RpaScopeMnemonicContext;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.graph.RpaMnemonicGraph;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class RpaMnemonicLoopPreviousStatus {

    private Map<Integer, MnemonicBackupContextStatus> oldBackContextMap;
    private Map<Integer, RpaScopeMnemonicContext> backupContextMap;

    public RpaMnemonicLoopPreviousStatus() {

        oldBackContextMap = new HashMap<Integer, MnemonicBackupContextStatus>();
        backupContextMap = new HashMap<Integer, RpaScopeMnemonicContext>();

    }

    public void backupMnemonicContextStack(Stack<RpaScope> scopeStack) {

        backupContextMap.clear();

        // Clono e salvo ogni "RpaScopeMnemonicContext" presente nello stack
        for (int i = 0; i < scopeStack.size(); i ++) {

            if (scopeStack.get(i) instanceof RpaScopeMnemonicContext) {

                RpaScopeMnemonicContext scopeMnemonicContext = (RpaScopeMnemonicContext) scopeStack.get(i);
                backupContextMap.put(i, scopeMnemonicContext.clone());

            }

        }

    }

    public void recoverMnemonicContextStack(Stack<RpaScope> scopeStack) {

        // Ripristino allo stato precedente ogni "RpaScopeMnemonicContext" presente nello stack
        for (Map.Entry<Integer, RpaScopeMnemonicContext> backupScopeEntry : backupContextMap.entrySet()) {

            Integer                 index       = backupScopeEntry.getKey();
            RpaScopeMnemonicContext backupScope = backupScopeEntry.getValue();

            scopeStack.set(index, backupScope.clone());

        }

    }

    public void oldBackupMnemonicContextStack(Stack<RpaScope> scopeStack) {

        oldBackContextMap.clear();

        for (int i = 0; i < scopeStack.size(); i ++) {

            if (scopeStack.get(i) instanceof RpaScopeMnemonicContext) {

                // Eseguo il backup del contesto
                RpaScopeMnemonicContext scopeMnemonicContext = (RpaScopeMnemonicContext) scopeStack.get(i);

                String lastMnemonicRead         = scopeMnemonicContext.getLastMnemonicRead();
                String lastTableWithDomainRead  = scopeMnemonicContext.getLastTableWithDomainRead();

                // TEST inizio di clone per i mnemonic graph
                RpaMnemonicGraph graphClone = scopeMnemonicContext.getMnemonicGraph().clone();
                // TEST fine di clone per i mnemonic graph

                // TEST inizio di clone per i PathTreeNode
                RpaPathTreeNode pathTreeNodeClone = scopeMnemonicContext.getCachePathTreeNode().clone();
                // TEST fine di clone per i PathTreeNode

                // TEST inizio di clone per gli RpaMnemonicEntityData
                RpaMnemonicEntityData mnemonicEntityDataClone = scopeMnemonicContext.getMnemonicEntityData().clone(scopeMnemonicContext.getMnemonicGraph().clone(), scopeMnemonicContext.getCachePathTreeNode().clone());
                // TEST fine di clone per gli RpaMnemonicEntityData

                // TEST inizio di clone per gli RpaScopeDocument
                scopeMnemonicContext.clone();
                // TEST fine di clone per gli RpaScopeDocument

                // TEST inizio 'toString()' per gli RpaMnemonicEntityData
                scopeMnemonicContext.getMnemonicEntityData().toString();
                // TEST fine 'toString()' per gli RpaMnemonicEntityData

                MnemonicBackupContextStatus mnemonicBackupStatus =
                        new MnemonicBackupContextStatus(lastMnemonicRead, lastTableWithDomainRead);

                // Eseguo il backup delle join implicite
                int directJoinIndex = 0;

                for (Map.Entry<String, RpaDirectJoinEntityContext> joinContextSet : scopeMnemonicContext.getDirectJoinEntityContextMap().entrySet()) {

                    RpaDirectJoinEntityContext joinContext = joinContextSet.getValue();
                    joinContext.setTemporaryId(directJoinIndex);

                    mnemonicBackupStatus.getLastMnemonicReadDirectJoinMap().put(directJoinIndex, joinContext.getLastMnemonicRead());
                    mnemonicBackupStatus.getLastTableWithDomainReadDirectJoinMap().put(directJoinIndex, joinContext.getLastTableNameWithDomainRead());

                    ++ directJoinIndex;

                    // TEST inizio di clone per gli RpaScopeDocument
                    joinContext.clone();
                    // TEST fine di clone per gli RpaScopeDocument

                }

                // Setto l'id temporaneo sul RpaScopeMnemonicContext originale
                oldBackContextMap.put(i, mnemonicBackupStatus);
                scopeMnemonicContext.setTemporaryId(i);

            }

        }

    }

    public void oldRecoverMnemonicContextStack(Stack<RpaScope> scopeStack) {

        for (int i = 0; i < scopeStack.size(); i ++) {

            if (scopeStack.get(i) instanceof RpaScopeMnemonicContext) {

                // Recupero il RpaScopeMnemonicContext di backup tramite l'id precedente
                RpaScopeMnemonicContext scopeMnemonicContext = (RpaScopeMnemonicContext) scopeStack.get(i);
                int scopeId = scopeMnemonicContext.getTemporaryId();
                MnemonicBackupContextStatus mnemonicBackupContextStatus = oldBackContextMap.get(scopeId);

                // Setto il contenuto del backup nel contesto
                scopeMnemonicContext.recover(mnemonicBackupContextStatus);

            }

        }

    }

    public class MnemonicBackupContextStatus {

        public MnemonicBackupContextStatus(String lastMnemonicRead, String lastTableWithDomainRead) {

            this.lastMnemonicRead           = lastMnemonicRead;
            this.lastTableWithDomainRead    = lastTableWithDomainRead;

            this.lastMnemonicReadDirectJoinMap          = new HashMap<Integer, String>();
            this.lastTableWithDomainReadDirectJoinMap   = new HashMap<Integer, String>();

        }

        private String lastMnemonicRead;
        private String lastTableWithDomainRead;
        private Map<Integer, String> lastMnemonicReadDirectJoinMap;
        private Map<Integer, String> lastTableWithDomainReadDirectJoinMap;

        public String getLastMnemonicRead() { return lastMnemonicRead; }

        public String getLastTableWithDomainRead() { return lastTableWithDomainRead; }

        public Map<Integer, String> getLastMnemonicReadDirectJoinMap() { return lastMnemonicReadDirectJoinMap; }

        public Map<Integer, String> getLastTableWithDomainReadDirectJoinMap() { return lastTableWithDomainReadDirectJoinMap; }

    }

}

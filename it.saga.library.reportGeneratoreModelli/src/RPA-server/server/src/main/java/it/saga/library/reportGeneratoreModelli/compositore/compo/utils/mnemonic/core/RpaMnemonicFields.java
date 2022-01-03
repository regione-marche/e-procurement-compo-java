package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core;

import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaMnemonic;

import java.util.HashMap;
import java.util.Map;

public class RpaMnemonicFields {

    private Map<String, RpaMnemonic>    values;
    private Map<String, Integer>        valuesType;

    private RpaMnemonicEntityData               upperMnemonicEntity;
    private Map<String, RpaMnemonicEntityData>  subMnemonicEntityMap;

    public RpaMnemonicFields(RpaMnemonicEntityData upperMnemonicEntity, Map<String, RpaMnemonic> values, Map<String, Integer> valuesType) {

        this.upperMnemonicEntity    = upperMnemonicEntity;
        this.values                 = values;
        this.valuesType             = valuesType;
        this.subMnemonicEntityMap   = new HashMap<String, RpaMnemonicEntityData>();

    }

    public RpaMnemonic getValue(String fieldName) {

        // Elimino l'ultima formattazione del mnemonico
        RpaMnemonic value = values.get(fieldName);
        value.setLastFormattedValue(null);

        return value;

    }

    public Integer getValueType(String fieldName) {

        return valuesType.get(fieldName);

    }

    public void setSubMnemonicEntity(String entityName, RpaMnemonicEntityData subMnemonicEntity) {

        this.subMnemonicEntityMap.put(entityName, subMnemonicEntity);

    }

    public RpaMnemonicEntityData getSubMnemonicEntity(String entityName) {

        return subMnemonicEntityMap.get(entityName);

    }

    public Map<String, RpaMnemonicEntityData> getSubMnemonicEntityMap() {

        return subMnemonicEntityMap;

    }

    public Map<String, RpaMnemonic> getValues() {

        return values;

    }

    public RpaMnemonicFields clone(RpaMnemonicEntityData cloneMnemonicEntityData) {

        Map<String, RpaMnemonic>    cloneValues = new HashMap<String, RpaMnemonic>();
        Map<String, Integer>        cloneTypes  = new HashMap<String, Integer>();

        for (Map.Entry<String, RpaMnemonic> entryValue : values.entrySet()) {

            String      nameValue   = entryValue.getKey();
            RpaMnemonic value       = entryValue.getValue();
            RpaMnemonic cloneValue  = value.clone();

            cloneValues.put(nameValue, cloneValue);

        }

        for (Map.Entry<String, Integer> entryType : valuesType.entrySet()) {

            String  nameValue   = entryType.getKey();
            Integer valueType   = entryType.getValue();
            Integer cloneValue  = new Integer(valueType.intValue());

            cloneTypes.put(nameValue, cloneValue);

        }

        RpaMnemonicFields clone = new RpaMnemonicFields(cloneMnemonicEntityData, cloneValues, cloneTypes);

        for (Map.Entry<String, RpaMnemonicEntityData> mnemonicEntityDataEntry : subMnemonicEntityMap.entrySet()) {

            String                  nameValue                   = mnemonicEntityDataEntry.getKey();
            RpaMnemonicEntityData   mnemonicEntityData          = mnemonicEntityDataEntry.getValue();
            RpaMnemonicEntityData   cloneSubMnemonicEntityData  = mnemonicEntityData.clone(
                    cloneMnemonicEntityData.getMnemonicGraph(),
                    cloneMnemonicEntityData.getPathTreeNode()
            );

            clone.subMnemonicEntityMap.put(nameValue, cloneSubMnemonicEntityData);

        }

        return clone;

    }

}

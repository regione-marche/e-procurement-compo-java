package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core;

import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.HashMap;
import java.util.Map;

public class RpaMnemonicJoinCache {

    private Map<MnemonicJoinKey, RpaAbstractMnemonic> mnemonicJoinMap;

    public RpaMnemonicJoinCache() {

        this.mnemonicJoinMap = new HashMap<MnemonicJoinKey, RpaAbstractMnemonic>();

    }

    public boolean add(
            String leftMnemonicEntity,
            String rightMnemonicName,
            String rightMnemonicValue,
            Integer rightMnemonicType,
            RpaAbstractMnemonic joinValue
    ) {

        MnemonicJoinKey key =
                new MnemonicJoinKey(leftMnemonicEntity, rightMnemonicName, rightMnemonicValue, rightMnemonicType);

        if (!mnemonicJoinMap.containsKey(key)) {

            mnemonicJoinMap.put(key, joinValue);

            return true;

        } else {

            return false;

        }

    }

    public boolean remove(
            String leftMnemonicEntity,
            String rightMnemonicName,
            String rightMnemonicValue,
            Integer rightMnemonicType
    ) {

        MnemonicJoinKey key =
                new MnemonicJoinKey(leftMnemonicEntity, rightMnemonicName, rightMnemonicValue, rightMnemonicType);

        if (mnemonicJoinMap.containsKey(key)) {

            mnemonicJoinMap.remove(key);

            return true;

        } else {

            return false;

        }

    }

    public boolean has(
            String leftMnemonicEntity,
            String rightMnemonicName,
            String rightMnemonicValue,
            Integer rightMnemonicType
    ) {

        MnemonicJoinKey key =
                new MnemonicJoinKey(leftMnemonicEntity, rightMnemonicName, rightMnemonicValue, rightMnemonicType);

        return mnemonicJoinMap.containsKey(key);

    }

    public RpaAbstractMnemonic get(
            String leftMnemonicEntity,
            String rightMnemonicName,
            String rightMnemonicValue,
            Integer rightMnemonicType
    ) {

        MnemonicJoinKey key =
                new MnemonicJoinKey(leftMnemonicEntity, rightMnemonicName, rightMnemonicValue, rightMnemonicType);

        return mnemonicJoinMap.get(key);

    }

    private class MnemonicJoinKey {

        private String  leftMnemonicEntity;
        private String  rightMnemonicName;
        private String  rightMnemonicValue;
        private Integer rightMnemonicType;

        private MnemonicJoinKey(
                String leftMnemonicEntity,
                String rightMnemonicName,
                String rightMnemonicValue,
                Integer rightMnemonicType
        ) {

            this.leftMnemonicEntity = leftMnemonicEntity;
            this.rightMnemonicName  = rightMnemonicName;
            this.rightMnemonicValue = rightMnemonicValue;
            this.rightMnemonicType  = rightMnemonicType;

        }

        @Override
        public String toString() {

            return "(" + leftMnemonicEntity + "," + rightMnemonicName + "," + rightMnemonicValue + "," + rightMnemonicType + ")";

        }

        @Override
        public int hashCode() {

            HashCodeBuilder hashCodeBuilder = new HashCodeBuilder(715, 183);
            return hashCodeBuilder.hashCode();

        }

        @Override
        public boolean equals(Object o) {

            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            MnemonicJoinKey that = (MnemonicJoinKey) o;

            return leftMnemonicEntity.equals(that.leftMnemonicEntity) &&
                    rightMnemonicName.equals(that.rightMnemonicName) &&
                    rightMnemonicValue.equals(that.rightMnemonicValue) &&
                    rightMnemonicType.equals(that.rightMnemonicType);

        }
    }

}

package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core;

import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.core.RpaMnemonicFieldsComparator.ORDER.DESC;

/**
 * Classe che ordina i mnemonici di una entità
 * Utilizzata per ordinare i mnemonici legati ad un loop
 */
public class RpaMnemonicFieldsComparator implements Comparator<RpaMnemonicFields> {

    private static final int PRECISION = 1000;

    private List<String>    fieldsName;
    private List<Integer>   fieldsType;
    private List<ORDER>     orderList;

    public RpaMnemonicFieldsComparator(String fieldName, int fieldType, ORDER order) {

        this.fieldsName = new ArrayList<String>();
        this.fieldsType = new ArrayList<Integer>();
        this.orderList  = new ArrayList<ORDER>();

        this.fieldsName.add(fieldName);
        this.fieldsType.add(fieldType);
        this.orderList.add(order);

    }

    public RpaMnemonicFieldsComparator(List<String> fieldsName, List<Integer> fieldsType, List<ORDER> orderList) {

        this.fieldsName = fieldsName;
        this.fieldsType = fieldsType;
        this.orderList  = orderList;

    }

    @Override
    public int compare(RpaMnemonicFields leftMnemonicFields, RpaMnemonicFields rightMnemonicFields) {

        int lastFieldsDifference = 0;

        int i = 0;

        while (i < fieldsName.size() && lastFieldsDifference == 0) {

            String  fieldName   = fieldsName.get(i);
            int     fieldType   = fieldsType.get(i);
            ORDER   order       = orderList.get(i);

            lastFieldsDifference = compareFields(leftMnemonicFields, rightMnemonicFields, fieldName, fieldType, order);

            ++ i;

        }

        return lastFieldsDifference;

    }

    private int compareFields(RpaMnemonicFields leftMnemonicFields, RpaMnemonicFields rightMnemonicFields, String fieldName, int fieldType, ORDER order) {

        // Controllo se il campo è una stringa
        if (fieldType == Types.VARCHAR) {

            return compareStrings(leftMnemonicFields, rightMnemonicFields, fieldName, order);

        }

        // Controllo se il campo è un intero
        else if (fieldType == Types.INTEGER || fieldType == Types.BIGINT) {

            return compareIntegers(leftMnemonicFields, rightMnemonicFields, fieldName, order);

        }

        // Controllo se il campo è un float
        else if (fieldType == Types.FLOAT) {

            return compareFloats(leftMnemonicFields, rightMnemonicFields, fieldName, order);

        }

        // Controllo se il campo è un double
        else if (fieldType == Types.DOUBLE || fieldType == Types.NUMERIC) {

            return compareDoubles(leftMnemonicFields, rightMnemonicFields, fieldName, order);

        }

        // Controllo se il campo è una data
        else if (fieldType == Types.DATE) {

            return compareDates(leftMnemonicFields, rightMnemonicFields, fieldName, order);

        }

        // Controllo se il campo è un timestamp
        else if (fieldType == Types.TIMESTAMP) {

            return compareTimestamps(leftMnemonicFields, rightMnemonicFields, fieldName, order);

        } else {

            System.err.println("Impossibile eseguire il confronto per il tipo " + fieldType + " di " + fieldName);

            return -1;

        }

    }

    private int compareStrings(RpaMnemonicFields leftMnemonicFields, RpaMnemonicFields rightMnemonicFields, String fieldName, ORDER order) {

        String leftTerm     = leftMnemonicFields.getValue(fieldName).getValue();
        String rightTerm    = rightMnemonicFields.getValue(fieldName).getValue();

        boolean isLeftTermEmpty     = leftTerm == null || leftTerm.isEmpty();
        boolean isRightTermEmpty    = rightTerm == null || rightTerm.isEmpty();

        if (isLeftTermEmpty && isRightTermEmpty) {

            return 0;

        } else if (isLeftTermEmpty) {

            if (order == DESC) {

                return 1;

            } else {

                return -1;

            }

        } else if (isRightTermEmpty) {

            if (order == DESC) {

                return -1;

            } else {

                return 1;

            }

        } else {

            int stringComparisonCode = leftTerm.compareTo(rightTerm);

            if (order == DESC) {

                stringComparisonCode = - stringComparisonCode;

            }

            return stringComparisonCode;

        }

        /*
        if (leftTerm == null) {

            leftTerm = "";

        }

        if (rightTerm == null) {

            rightTerm = "";

        }

        int stringComparisonCode = leftTerm.compareTo(rightTerm);

        if (order == DESC) {

            stringComparisonCode = - stringComparisonCode;

        }

        return stringComparisonCode;
        */

    }

    private int compareIntegers(RpaMnemonicFields leftMnemonicFields, RpaMnemonicFields rightMnemonicFields, String fieldName, ORDER order) {

        String leftTermString   = leftMnemonicFields.getValue(fieldName).getValue();
        String rightTermString  = rightMnemonicFields.getValue(fieldName).getValue();

        Integer leftTerm;
        Integer rightTerm;

        boolean isLeftTermEmpty     = leftTermString == null || leftTermString.isEmpty();
        boolean isRightTermEmpty    = rightTermString == null || rightTermString.isEmpty();

        if (isLeftTermEmpty && isRightTermEmpty) {

            return 0;

        } else if (isLeftTermEmpty) {

            if (order == DESC) {

                return 1;

            } else {

                return -1;

            }

        } else if (isRightTermEmpty) {

            if (order == DESC) {

                return -1;

            } else {

                return 1;

            }

        } else {

            leftTerm    = Integer.valueOf(leftTermString);
            rightTerm   = Integer.valueOf(rightTermString);

            Integer integerDifference = leftTerm - rightTerm;

            if (order == DESC) {

                integerDifference = - integerDifference;

            }

            return integerDifference;

        }

        /*
        if (leftTermString != null && !leftTermString.isEmpty()) {

            leftTerm = Integer.valueOf(leftTermString);

        } else {

            leftTerm = Integer.MIN_VALUE;

        }

        if (rightTermString != null && !rightTermString.isEmpty()) {

            rightTerm = Integer.valueOf(rightTermString);

        } else {

            rightTerm = Integer.MIN_VALUE;

        }

        Integer integerDifference = leftTerm - rightTerm;

        if (order == DESC) {

            integerDifference = - integerDifference;

        }

        return integerDifference;
        */

    }

    private int compareFloats(RpaMnemonicFields leftMnemonicFields, RpaMnemonicFields rightMnemonicFields, String fieldName, ORDER order) {

        String leftTermString   = leftMnemonicFields.getValue(fieldName).getValue();
        String rightTermString  = rightMnemonicFields.getValue(fieldName).getValue();

        Float leftTerm;
        Float rightTerm;

        boolean isLeftTermEmpty     = leftTermString == null || leftTermString.isEmpty();
        boolean isRightTermEmpty    = rightTermString == null || rightTermString.isEmpty();

        if (isLeftTermEmpty && isRightTermEmpty) {

            return 0;

        } else if (isLeftTermEmpty) {

            if (order == DESC) {

                return 1;

            } else {

                return -1;

            }

        } else if (isRightTermEmpty) {

            if (order == DESC) {

                return -1;

            } else {

                return 1;

            }

        } else {

            leftTerm    = Float.valueOf(leftTermString);
            rightTerm   = Float.valueOf(rightTermString);

            Float floatDifference = leftTerm - rightTerm;

            if (order == DESC) {

                floatDifference = - floatDifference;

            }

            if (floatDifference > 0) {

                return 1;

            } else if (floatDifference < 0) {

                return -1;

            } else {

                return 0;

            }

        }

        /*
        if (leftTermString != null && !leftTermString.isEmpty()) {

            leftTerm = Float.valueOf(leftTermString);

        } else {

            leftTerm = Float.MIN_VALUE;

        }

        if (rightTermString != null && !rightTermString.isEmpty()) {

            rightTerm = Float.valueOf(rightTermString);

        } else {

            rightTerm = Float.MIN_VALUE;

        }

        // Float floatDifference = (leftTerm - rightTerm) * PRECISION;
        Float floatDifference = leftTerm - rightTerm;

        if (order == DESC) {

            floatDifference = - floatDifference;

        }

        if (floatDifference > 0) {

            return 1;

        } else if (floatDifference < 0) {

            return -1;

        } else {

            return 0;

        }
        */

        // return floatDifference.intValue();

    }

    private int compareDoubles(RpaMnemonicFields leftMnemonicFields, RpaMnemonicFields rightMnemonicFields, String fieldName, ORDER order) {

        String leftTermString   = leftMnemonicFields.getValue(fieldName).getValue();
        String rightTermString  = rightMnemonicFields.getValue(fieldName).getValue();

        Double leftTerm;
        Double rightTerm;

        boolean isLeftTermEmpty     = leftTermString == null || leftTermString.isEmpty();
        boolean isRightTermEmpty    = rightTermString == null || rightTermString.isEmpty();

        if (isLeftTermEmpty && isRightTermEmpty) {

            return 0;

        } else if (isLeftTermEmpty) {

            if (order == DESC) {

                return 1;

            } else {

                return -1;

            }

        } else if (isRightTermEmpty) {

            if (order == DESC) {

                return -1;

            } else {

                return 1;

            }

        } else {

            leftTerm    = Double.valueOf(leftTermString);
            rightTerm   = Double.valueOf(rightTermString);

            Double doubleDifference = leftTerm - rightTerm;

            if (order == DESC) {

                doubleDifference = - doubleDifference;

            }

            if (doubleDifference > 0) {

                return 1;

            } else if (doubleDifference < 0) {

                return -1;

            } else {

                return 0;

            }

        }

        /*
        if (leftTermString != null && !leftTermString.isEmpty()) {

            leftTerm = Double.valueOf(leftTermString);

        } else {

            leftTerm = Double.MIN_VALUE;

        }

        if (rightTermString != null && !rightTermString.isEmpty()) {

            rightTerm = Double.valueOf(rightTermString);

        } else {

            rightTerm = Double.MIN_VALUE;

        }

        // Double floatDifference = (leftTerm - rightTerm) * PRECISION;
        Double floatDifference = leftTerm - rightTerm;

        if (order == DESC) {

            floatDifference = - floatDifference;

        }

        if (floatDifference > 0) {

            return 1;

        } else if (floatDifference < 0) {

            return -1;

        } else {

            return 0;

        }
        */

        // return floatDifference.intValue();

    }

    private int compareDates(RpaMnemonicFields leftMnemonicFields, RpaMnemonicFields rightMnemonicFields, String fieldName, ORDER order) {

        String leftTermString   = leftMnemonicFields.getValue(fieldName).getValue();
        String rightTermString  = rightMnemonicFields.getValue(fieldName).getValue();

        Long leftTerm;
        Long rightTerm;

        boolean isLeftTermEmpty     = leftTermString == null || leftTermString.isEmpty();
        boolean isRightTermEmpty    = rightTermString == null || rightTermString.isEmpty();

        if (isLeftTermEmpty && isRightTermEmpty) {

            return 0;

        } else if (isLeftTermEmpty) {

            if (order == DESC) {

                return 1;

            } else {

                return -1;

            }

        } else if (isRightTermEmpty) {

            if (order == DESC) {

                return -1;

            } else {

                return 1;

            }

        } else {

            Date leftTermDate   = Date.valueOf(leftTermString);
            leftTerm            = leftTermDate.getTime();

            Date rightTermDate  = Date.valueOf(rightTermString);
            rightTerm           = rightTermDate.getTime();

            Long longDifference = leftTerm - rightTerm;

            if (order == DESC) {

                longDifference = - longDifference;

            }

            if (longDifference > 0) {

                return 1;

            } else if (longDifference < 0) {

                return -1;

            } else {

                return 0;

            }

        }

        /*
        if (leftTermString != null && !leftTermString.isEmpty()) {

            Date leftTermDate   = Date.valueOf(leftTermString);
            leftTerm            = leftTermDate.getTime();

        } else {

            leftTerm = Long.MIN_VALUE;

        }

        if (rightTermString != null && !rightTermString.isEmpty()) {

            Date rightTermDate  = Date.valueOf(rightTermString);
            rightTerm           = rightTermDate.getTime();

        } else {

            rightTerm = Long.MIN_VALUE;

        }

        Long longDifference = leftTerm - rightTerm;

        if (order == DESC) {

            longDifference = - longDifference;

        }

        if (longDifference > 0) {

            return 1;

        } else if (longDifference < 0) {

            return -1;

        } else {

            return 0;

        }
        */

        // return longDifference.intValue();

    }

    private int compareTimestamps(RpaMnemonicFields leftMnemonicFields, RpaMnemonicFields rightMnemonicFields, String fieldName, ORDER order) {

        String leftTermString   = leftMnemonicFields.getValue(fieldName).getValue();
        String rightTermString  = rightMnemonicFields.getValue(fieldName).getValue();

        Long leftTerm;
        Long rightTerm;

        boolean isLeftTermEmpty     = leftTermString == null || leftTermString.isEmpty();
        boolean isRightTermEmpty    = rightTermString == null || rightTermString.isEmpty();

        if (isLeftTermEmpty && isRightTermEmpty) {

            return 0;

        } else if (isLeftTermEmpty) {

            if (order == DESC) {

                return 1;

            } else {

                return -1;

            }

        } else if (isRightTermEmpty) {

            if (order == DESC) {

                return -1;

            } else {

                return 1;

            }

        } else {

            Timestamp leftTimestamp = Timestamp.valueOf(leftTermString);
            leftTerm = leftTimestamp.getTime();

            Timestamp rightTimestamp = Timestamp.valueOf(rightTermString);
            rightTerm = rightTimestamp.getTime();

            Long longDifference = leftTerm - rightTerm;

            if (order == DESC) {

                longDifference = - longDifference;

            }

            if (longDifference > 0) {

                return 1;

            } else if (longDifference < 0) {

                return -1;

            } else {

                return 0;

            }

        }

        /*
        if (leftTermString != null && !leftTermString.isEmpty()) {

            Timestamp leftTimestamp = Timestamp.valueOf(leftTermString);
            leftTerm = leftTimestamp.getTime();

        } else {

            leftTerm = Long.MIN_VALUE;

        }

        if (rightTermString != null && !rightTermString.isEmpty()) {

            Timestamp rightTimestamp = Timestamp.valueOf(rightTermString);
            rightTerm = rightTimestamp.getTime();

        } else {

            rightTerm = Long.MIN_VALUE;

        }

        Long longDifference = leftTerm - rightTerm;

        if (order == DESC) {

            longDifference = - longDifference;

        }

        if (longDifference > 0) {

            return 1;

        } else if (longDifference < 0) {

            return -1;

        } else {

            return 0;

        }
        */

        // return longDifference.intValue();

    }

    public enum ORDER {
        ASC,
        DESC
    }

}

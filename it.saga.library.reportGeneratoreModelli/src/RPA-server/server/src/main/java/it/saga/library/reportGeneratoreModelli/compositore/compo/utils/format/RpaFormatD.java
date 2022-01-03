package it.saga.library.reportGeneratoreModelli.compositore.compo.utils.format;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.formatDate.RpaFormatDate;
import it.saga.library.reportGeneratoreModelli.compositore.compo.utils.mnemonic.type.RpaAbstractMnemonic;
import org.apache.commons.lang.time.DateUtils;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;

public class RpaFormatD extends RpaFormatDate {

    private Long countDays;

    public RpaFormatD(RpaMainCompositore mainCompositore, Integer countDays) throws ParseException {

        super(mainCompositore);

        if (countDays != null) {

            this.countDays = countDays.longValue();
            formatValue();

        }

    }

    public RpaFormatD(RpaMainCompositore mainCompositore, String stringDate) throws ParseException {

        // TODO: Controllare se la conversione da numero a DATE e/o a TIMESTAMP è corretta (Bug conversione aggiunge 1 giorno)

        super(mainCompositore);

        // Controllo che la stringa sia una data valida
        if (stringDate != null && !stringDate.isEmpty()) {

            // Controllo che tipo di data ho ricevuto
            TYPE type = getType(stringDate);

            switch (type) {

                case ELDA:

                    this.value      = convertToDateElda(stringDate);
                    this.countDays  = getDaysDifference();

                    break;

                case DATE:

                    this.value      = convertToDate(stringDate);
                    this.countDays  = getDaysDifference();

                    break;

                case TIMESTAMP:

                    Timestamp timestamp = convertToTimestamp(stringDate);

                    this.value      = new Date(timestamp.getTime());
                    this.countDays  = getDaysDifference();

                    break;

                case INVALID:
                default:

                    throw new ParseException("Impossibile convertire la data '" + stringDate + "'", 0);

            }

        }

    }

    public RpaFormatD(RpaMainCompositore mainCompositore, Double countDaysDouble) throws ParseException {

        super(mainCompositore);

        BigDecimal  bigDecimal  = BigDecimal.valueOf(countDaysDouble);
        long        countDays   = bigDecimal.toBigInteger().longValue();

        this.countDays = countDays;
        formatValue();

    }

    @Override
    public String getPrintedValue() {

        if (value == null) {

            return emptyPrintedValue();

        } else {

            return dateFormat.format(value);

        }

    }

    @Override
    public String getValueAssignment() {

        if (value == null) {

            return null;

        } else {

            return dateFormat.format(value);

        }

    }

    @Override
    public Number getValueNumber() {

        return countDays;

    }

    @Override
    public String getComparisonValue() {

        if (countDays == null) {

            return null;

        } else {

            // return String.valueOf(countDays);
            return dateFormat.format(value);

        }

    }

    @Override
    public String getFormattedValue() {

        if (value == null) {

            return null;

        } else {

            return dateFormat.format(value);

        }

    }

    private void formatValue() {

        // Controllo se il valore è null
        if (countDays == null) {

            return;

        }

        // Aggiungo alla data del 01.01.1000 il numero di giorni
        value = DateUtils.addDays(originDate, countDays.intValue());

    }

    @Override
    public int getMnemonicType() {

        return RpaAbstractMnemonic.TYPE_STRING;

    }

}

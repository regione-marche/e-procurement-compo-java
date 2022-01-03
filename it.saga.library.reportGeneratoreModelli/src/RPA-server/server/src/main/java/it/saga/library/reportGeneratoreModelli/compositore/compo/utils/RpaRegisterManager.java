package it.saga.library.reportGeneratoreModelli.compositore.compo.utils;

import it.saga.library.reportGeneratoreModelli.compositore.compo.RpaMainCompositore;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaComposerException;
import it.saga.library.reportGeneratoreModelli.compositore.compo.exceptions.RpaMalformedOptionsDeclarationException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RpaRegisterManager {

    public static final int TAB1_REGISTER_INDEX     = 87;
    public static final int SESSION_REGISTER_INDEX  = 80;

    // Link: https://regex101.com/r/kpFTmp/1
    private static final String CHECK_CORRECT_REGISTERS_LINE_REGEX = "^[0-9]{1,2}=[^;]+(;[0-9]{1,2}=[^;]+)*$";

    // Link: https://regex101.com/r/5ZOHF5/2
    private static final String EXTRACT_REGISTERS_REGEX = "([0-9]{1,2})=([^;]+)";

    private String                  registersDeclaration;
    private RpaMainCompositore      mainCompositore;
    private Map<Integer, String>    registerMap;

    public RpaRegisterManager(RpaMainCompositore mainCompositore) {

        this.mainCompositore    = mainCompositore;
        this.registerMap        = new HashMap<Integer, String>();

    }

    public void updateRegisters(String registersString) {

        // Controllo che la stringa non sia nulla o vuota
        if (registersString == null || registersString.isEmpty()) {

            return;

        }

        // Controllo che la stringa definisca correttamente dei registri da 1 a 99
        Matcher correctRegistersStringMatcher = Pattern.compile(CHECK_CORRECT_REGISTERS_LINE_REGEX).matcher(registersString);

        if (!correctRegistersStringMatcher.find()) {

            String code     = registersString;
            String message  = "La dichiarazione dei registri è sbagliata";
            int context     = RpaComposerException.PRECOMPILE_MESSAGE;

            throw new RpaMalformedOptionsDeclarationException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

        }

        // Aggiungo tutti i registri trovati
        Matcher registersFoundMatcher = Pattern.compile(EXTRACT_REGISTERS_REGEX).matcher(registersString);

        while (registersFoundMatcher.find()) {

            String registerIndexString  = registersFoundMatcher.group(1);
            String registerValue        = registersFoundMatcher.group(2);

            int registerIndex = Integer.valueOf(registerIndexString);

            if (registerIndex <= 0 || registerIndex > 99) {

                String code     = registersString;
                String message  = "Il registro " + registerIndexString + " deve essere tra 1 e 99";
                int context     = RpaComposerException.PRECOMPILE_MESSAGE;

                throw new RpaMalformedOptionsDeclarationException(mainCompositore.getComposerConfiguration(), mainCompositore.getAntlrErrorListener(), context, code, message);

            }

            registerMap.put(registerIndex, registerValue);

        }

        registersDeclaration = registersString;

    }

    public String getRegister(int index) { return registerMap.get(index); }

    public int count() { return registerMap.size(); }

    public boolean isPresent(int index) { return registerMap.containsKey(index); }

    public String getRegistersDeclaration() { return registersDeclaration; }

}

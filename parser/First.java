package parser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import modeling.GramaticalRules;
import modeling.NoTerminales;
import modeling.TokenType;



public class First {
    public static List<TokenType> TheFirst(NoTerminales Cabecera){
        Object[][] SetRules = GramaticalRules.Rules.get(Cabecera); 
        if (SetRules == null)
            return null;

        List<TokenType> setTerminals = new ArrayList<>();

        for(Object[] production : SetRules) {
            setTerminals.addAll(TheFirst(production));
        }
        return setTerminals; 
    }

    private static List<TokenType> TheFirst(Object[] Production){
        List<TokenType> SetFirstCompilationProduction = new ArrayList<>();
        int longthProduction = Production.length, countEmptyPerLiteral = 0;
        for (Object term : Production) {
            if (term == null) {
                SetFirstCompilationProduction.add(null);
                countEmptyPerLiteral++;
            }else if (term instanceof  TokenType) {
                SetFirstCompilationProduction.add((TokenType)term);
                break;
            }else if (term instanceof NoTerminales) {
                List<TokenType> IdentificationEmpty = TheFirst(((NoTerminales)term));
                if (Arrays.asList(IdentificationEmpty).contains(null)) {
                    countEmptyPerLiteral++;
                    SetFirstCompilationProduction.addAll(IdentificationEmpty);
                }else{
                    SetFirstCompilationProduction.addAll(IdentificationEmpty);
                    break;
                }
            }
        }
        Set<TokenType> set = new HashSet<>(SetFirstCompilationProduction);
        List<TokenType> SetFirstProduction = new ArrayList<>(set);
        if (countEmptyPerLiteral == longthProduction) {
            return SetFirstProduction;
        }else {
            SetFirstProduction.remove(null);
            return SetFirstProduction;
        }
    }
}
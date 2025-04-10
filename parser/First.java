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
    public static TokenType characterPreanalisis;//You can replace whit you global vaiable of preanalisis

    private static List<TokenType> TheFirst(String Empty){
         if(Empty == null){
            List<TokenType> setEmpty = new ArrayList<>();
            setEmpty.add(null);
            return setEmpty;
         }else{
            return null;
         }
    }

    private static List<TokenType> TheFirst(TokenType Terminal){
        if(Terminal == null)
            return null;

        List<TokenType> setTerminal = new ArrayList<>();
        setTerminal.add(Terminal);
        return setTerminal;
    }

    private static List<TokenType> TheFirst(NoTerminales Cabecera){
        Object[][] SetRules = GramaticalRules.Rules.get(Cabecera); 
        if (SetRules == null)
            return null;

        List<TokenType> setTerminals = new ArrayList<>();

        for(Object[] production : SetRules) {
            if (Arrays.asList(production).contains(Cabecera)) {
                    continue;
            }
            setTerminals.addAll(TheFirst(production));
        }
        return setTerminals; 
    }

    private static List<TokenType> TheFirst(Object[] Production){
        List<TokenType> SetFirstCompilationProduction = new ArrayList<>();
        int longthProduction = Production.length, countEmptyPerLiteral = 0;

        for (Object term : Production) {
          System.out.println(term);
            if (term instanceof String) {
                SetFirstCompilationProduction.addAll(TheFirst((String)term));
                countEmptyPerLiteral++;
            }else if (term instanceof  TokenType) {
                SetFirstCompilationProduction.addAll((TheFirst((TokenType)term)));
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

    //EXAMPLE OF IMPLA
    public static Object[] first(NoTerminales Cabecera){
      Object[][] SetRules = GramaticalRules.Rules.get(Cabecera);
      System.out.println(SetRules);
        if (SetRules == null)
            return null;

        List<TokenType> OficialList = new ArrayList<>();
        for (Object[] production : SetRules) {
          if (Arrays.asList(production).contains(Cabecera)) {
            continue;
          }
          System.out.println(production);
          OficialList = TheFirst(production);
          System.out.println(OficialList);
          if (Arrays.asList(OficialList).contains(characterPreanalisis)) {
            return production;
          }
        }
        
        return null; 
    }
}
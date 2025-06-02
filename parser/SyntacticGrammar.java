package parser;

import java.util.ArrayList;
import modeling.GramaticalRules;
import modeling.NonTerminal;
import modeling.TokenType;

public class SyntacticGrammar {
  public static ArrayList<Object> first(Object alpha) {
    ArrayList<Object> set = new ArrayList<>();

    if (alpha instanceof TokenType) {
      set.add(alpha);
      return set;
    }

    Object[][] productions = GramaticalRules.rules.get((NonTerminal) alpha);

    for (Object[] p : productions) {
      if (p == null) {
        if (!set.contains(p))
          set.add(p);

        continue;
      }

      boolean containsEpsilon = false;

      for (Object pe : p) {
        if (pe.equals(alpha))
          continue;

        for (Object o : first(pe)) {
          if (o == null) {
            containsEpsilon = true;
            continue;
          }

          if (!set.contains(o))
            set.add(o);
        }

        if (!containsEpsilon)
          break;
      }
    }
    
    if (!set.contains(null) && productions[productions.length - 1] == null)
      set.add(null);

    return set;
  }
}

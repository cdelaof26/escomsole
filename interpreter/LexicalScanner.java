package interpreter;

import java.util.ArrayList;
import modeling.Token;
import modeling.TokenType;

/**
 *
 * @author cristopher
 */
public class LexicalScanner {
    private static final String [] RESERVED_WORDS = {
        "and", "else", "false", "fun", "for", "if", "null", "or",
        "print", "return", "true", "var", "while"
    };
    
    public static ArrayList<Token> tokens = new ArrayList<>();
    
    private static TokenType getTokenType(String data) {
        for (String word : RESERVED_WORDS)
            if (data.equals(word)) 
                return TokenType.valueOf("ESC_" + word.toUpperCase());
        
        return TokenType.NONE;
    }
    
    /**
     * This function implements a lexical scanner
     * @param code the line to analyze
     * @param fileLine the specific line of text in the file
     * @param previousState is the state previously returned by this function
     * @return the automaton status code
     */
    public static int scan(String code, int fileLine, int previousState) {
        char c;
        int state = previousState < 0 ? 0 : previousState;
        String lexeme = "";

        // System.out.println("line " + code); // Debug
        // System.out.println(String.format("LINE #%d ; len %d", fileLine, code.length())); // Debug

        for (int i = 0; i < code.length(); i++) {
            c = code.charAt(i);

            /**
             * Required automata:
             *  - Comment analyser : DONE
             *  - Identifiers and reserved words analyser : DONE
             *  - Operators analyser
             *  - Punctuation analyser
             *  - Number analyser: this may require an evaluation function ; almost done
             *  - String analyser
             *  - Handle error
             *  - Extra automata
             *      - ++, --
             *      - Ternary operation
             *      - Array bracket notation
             */

            // System.out.println(c + " : state " + state); // Debug

            switch (state) {
                case 0:
                    if (Character.isLetter(c)) { // IDENTIFIERS and reserved words
                        state = 13;
                        lexeme += c;
                        continue;
                    }

                    if (c == '/') { // Comments
                        state = 24;
                        lexeme += c;
                        continue;
                    }
                
                // TODO: Uncomment in completion
                // return Status.SYNTAX_ERROR;
                break;
                
                case 13:
                    if (Character.isLetterOrDigit(c)) { // IDENTIFIERS and reserved words
                        lexeme += c;
                    } else { // end of identifier or reserved word
                        Token t;
                        TokenType type = getTokenType(lexeme);
                        if (type == TokenType.NONE)
                            t = new Token(TokenType.ESC_IDENTIFIER, lexeme, fileLine);
                        else
                            t = new Token(type, fileLine);

                        lexeme = "";
                        tokens.add(t);
                        state = 0;
                        i--;
                    }
                break;

                case 24:
                    if (c == '/') {
                        state = 28;
                        lexeme = "";
                    } else if (c == '*') {
                        state = 25;
                        lexeme = "";
                    } else { // state 30
                        tokens.add(new Token(TokenType.ESC_SLASH, fileLine));
                        lexeme = "";
                        state = 0;
                        i--;
                    }
                break;

                case 25:
                    if (c == '*')
                        state = 26;
                break;

                case 26:
                    if (c != '*' && c != '/')
                        state = 25;

                    if (c == '/') // state 27
                        state = 0;
                break;

                case 28:
                    if (c == '\n')
                        state = 0;
                break;

                default:
                return Status.SYNTAX_ERROR;
            }
        }

        return state;
    }
}

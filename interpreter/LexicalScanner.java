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
     * @param codeSnippet the code to analyze
     * @param lineNumber the line number in the file
     * @param previousState is the state previously returned by this function
     * @return the automaton status code
     */
    public static int scan(String codeSnippet, int lineNumber, int previousState) {
        char c;
        int state = previousState < 0 ? 0 : previousState;
        String lexeme = "";
        
        // System.out.println("line " + code); // Debug
        // System.out.println(String.format("LINE #%d ; len %d", fileLine, code.length())); // Debug

        for (int i = 0; i < codeSnippet.length(); i++) {
            c = codeSnippet.charAt(i);

            /**
             * Required automata:
             *  - Comment analyser : DONE
             *  - Identifiers and reserved words analyser : DONE
             *  - Operators analyser
             *  - Punctuation analyser
             *  - Number analyser: this may require an evaluation function ; DONE
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
                    } else if (Character.isDigit(c)) { // Numbers
                        state = 15;
                        lexeme += c;
                    } else if (c == '/') { // Comments
                        state = 24;
                        lexeme += c;
                    } else if (Character.isWhitespace(c)) {
                        // handle delimiters
                    } else {
                        System.out.println("Unknown char c = '" + c + "' state 0");
                        // TODO: Uncomment in completion
                        // return Status.SYNTAX_ERROR;
                    }
                break;
                
                case 13:
                    if (Character.isLetterOrDigit(c)) { // IDENTIFIERS and reserved words
                        lexeme += c;
                    } else { // end of identifier or reserved word
                        Token t;
                        TokenType type = getTokenType(lexeme);
                        if (type == TokenType.NONE)
                            t = new Token(TokenType.ESC_IDENTIFIER, lexeme, lineNumber);
                        else
                            t = new Token(type, lineNumber);

                        lexeme = "";
                        tokens.add(t);
                        state = 0;
                        i--;
                    }
                break;
                
                case 15:
                    if (c == '.') {
                        state = 16;
                        lexeme += c;
                    } else if (Character.isDigit(c)) { // state 15
                        lexeme += c;
                    } else if (c == 'E' || c == 'e') {
                        state = 18;
                        lexeme += c;
                    } else {
                        tokens.add(new Token(TokenType.ESC_NUMBER, lexeme, Integer.valueOf(lexeme), lineNumber));
                        lexeme = "";
                        state = 0;
                        i--;
                    }
                break;
                
                case 16:
                    if (Character.isDigit(c)) {
                        state = 17;
                        lexeme += c;
                    } else
                        return Status.MALFORMED_NUMBER;
                break;
                
                case 17:
                    if (Character.isDigit(c)) { // state 17
                        lexeme += c;
                    } else if (c == 'E' || c == 'e') {
                        state = 18;
                        lexeme += c;
                    } else {
                        tokens.add(new Token(TokenType.ESC_NUMBER, lexeme, Float.valueOf(lexeme), lineNumber));
                        lexeme = "";
                        state = 0;
                        i--;
                    }
                break;
                
                case 18:
                    if (Character.isDigit(c)) {
                        state = 20;
                        lexeme += c;
                    } else if (c == '+' || c == '-') {
                        state = 19;
                        lexeme += c;
                    } else
                        return Status.MALFORMED_NUMBER;
                break;
                
                case 19:
                    if (Character.isDigit(c)) {
                        state = 20;
                        lexeme += c;
                    } else
                        return Status.MALFORMED_NUMBER;
                break;
                
                case 20:
                    if (Character.isDigit(c)) { // state 20
                        lexeme += c;
                    } else {
                        tokens.add(new Token(TokenType.ESC_NUMBER, lexeme, Float.valueOf(lexeme), lineNumber));
                        lexeme = "";
                        state = 0;
                        i--;
                    }
                break;

                case 24:
                    if (c == '*') {
                        state = 25;
                        lexeme = "";
                    } else if (c == '/') {
                        state = 28;
                        lexeme = "";
                    } else { // state 30
                        tokens.add(new Token(TokenType.ESC_SLASH, lineNumber));
                        lexeme = "";
                        state = 0;
                        i--;
                    }
                break;

                case 25:
                    if (c == '*')
                        state = 26;
                    else
                        state = 25;
                break;

                case 26:
                    if (c == '*') 
                        state = 26;
                    else if (c == '/') // state 27
                        state = 0;
                break;

                case 28:
                    if (c != '\n')
                        state = 28;
                    else
                        state = 0;
                break;

                default:
                return Status.SYNTAX_ERROR;
            }
        }

        return state;
    }
}

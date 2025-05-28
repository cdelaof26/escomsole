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

    private static final ArrayList<Character> SYMBOLS = new ArrayList<>();
    private static final ArrayList<TokenType> SYMBOLS_TOKEN_TYPES = new ArrayList();

    static {
        SYMBOLS.add('(');
        SYMBOLS_TOKEN_TYPES.add(TokenType.ESC_LEFT_PAREN);
        SYMBOLS.add(')');
        SYMBOLS_TOKEN_TYPES.add(TokenType.ESC_RIGHT_PAREN);
        
        SYMBOLS.add('{');
        SYMBOLS_TOKEN_TYPES.add(TokenType.ESC_LEFT_BRACE);
        SYMBOLS.add('}');
        SYMBOLS_TOKEN_TYPES.add(TokenType.ESC_RIGHT_BRACE);
        
        SYMBOLS.add('[');
        SYMBOLS_TOKEN_TYPES.add(TokenType.ESC_LEFT_BRACKET);
        SYMBOLS.add(']');
        SYMBOLS_TOKEN_TYPES.add(TokenType.ESC_RIGHT_BRACKET);
        
        
//        SYMBOLS.add('.');
//        SYMBOLS_TOKEN_TYPES.add(TokenType.ESC_DOT);
        SYMBOLS.add(',');
        SYMBOLS_TOKEN_TYPES.add(TokenType.ESC_COMMA);
        SYMBOLS.add(':');
        SYMBOLS_TOKEN_TYPES.add(TokenType.ESC_COLON);
        SYMBOLS.add(';');
        SYMBOLS_TOKEN_TYPES.add(TokenType.ESC_SEMICOLON);
        
        SYMBOLS.add('?');
        SYMBOLS_TOKEN_TYPES.add(TokenType.ESC_QUESTION_MARK);
    }

    public static ArrayList<Token> tokens = new ArrayList<>();
    
    public static int scanIndex = 0;
    
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
        String lexeme = "", myString = "";
        
        // System.out.println("line " + code); // Debug
        // System.out.println(String.format("LINE #%d ; len %d", fileLine, code.length())); // Debug

        for (scanIndex = 0; scanIndex < codeSnippet.length(); scanIndex++) {
            c = codeSnippet.charAt(scanIndex);

            /**
             * Required automata:
             *  - Comment analyser : DONE
             *  - Identifiers and reserved words analyser : DONE
             *  - Operators analyser : DONE
             *  - Punctuation analyser : DONE
             *  - Number analyser: this may require an evaluation function ; DONE
             *  - String analyser : DONE
             *  - Handle error
             *  - Extra automata
             *      - ++, --
             *      - Ternary operation : DONE ('?' and ':')
             *      - Array bracket notation : DONE ('[' and ']')
             */

            // System.out.println(c + " : state " + state); // Debug

            switch (state) {
                case 0:
                    if (Character.isLetter(c) || c == '_') { // IDENTIFIERS and reserved words
                        state = 13;
                        lexeme += c;
                    } else if (Character.isDigit(c)) { // Numbers
                        state = 15;
                        lexeme += c;
                    } else if (c == '/') { // Comments
                        state = 24;
                        lexeme += c;
                    } else if (SYMBOLS.contains(c)) { // Punctuation marks
                        TokenType to = SYMBOLS_TOKEN_TYPES.get(SYMBOLS.indexOf(c));
                        tokens.add(new Token(to, lineNumber, scanIndex));
                    } else if (c == '+') {
                        state = 1;
                        lexeme += c;
                    } else if (c == '-') {
                        state = 6;
                        lexeme += c;
                    } else if (c == '*') {
                        state = 5;
                        lexeme += c;
                    } else if (c == '"') {
                        state = 43;
                        lexeme += c;
                    } else if (c == '<') {
                        state = 32;
                        lexeme += c;
                    } else if (c == '>') {
                        state = 36;
                        lexeme += c;
                    } else if (c == '=') {
                        state = 41;
                        lexeme += c;
                    } else if (c == '!') {
                        state = 38;
                        lexeme += c;
                    } else {
                        if (!Character.isWhitespace(c))
//                            System.out.println("Unknown char c = '" + c + "' state 0"); // Debug
                             return LexicalScannerStatus.INVALID_CHAR;
                    }
                break;
                
                case 1:
                    lexeme = "";
                    state = 0;
                    
                    if (c == '=') {
                        tokens.add(new Token(TokenType.ESC_PLUS_EQUAL, lineNumber, scanIndex - 1));
                    } else if (c == '+') {
                        tokens.add(new Token(TokenType.ESC_PLUS_PLUS, lineNumber, scanIndex - 1));
                    } else {
                        tokens.add(new Token(TokenType.ESC_PLUS, lineNumber, scanIndex));
                        scanIndex--;
                    }
                break;
                
                case 5:
                    lexeme = "";
                    state = 0;
                    if (c == '=') {
                        tokens.add(new Token(TokenType.ESC_STAR_EQUAL, lineNumber, scanIndex - 1));
                    } else {
                        tokens.add(new Token(TokenType.ESC_STAR, lineNumber, scanIndex));
                        scanIndex--;
                    }
                break;
                
                case 6:
                    lexeme = "";
                    state = 0;
                    if (c == '-') {
                        tokens.add(new Token(TokenType.ESC_MINUS_MINUS, lineNumber, scanIndex - 1));
                    } else if (c == '=') {
                        tokens.add(new Token(TokenType.ESC_MINUS_EQUAL, lineNumber, scanIndex - 1));
                    } else {
                        tokens.add(new Token(TokenType.ESC_MINUS, lineNumber, scanIndex));
                        scanIndex--;
                    }
                break;
                
                case 13:
                    if (Character.isLetterOrDigit(c) || c == '_') { // IDENTIFIERS and reserved words
                        lexeme += c;
                    } else { // end of identifier or reserved word
                        Token t;
                        TokenType type = getTokenType(lexeme);
                        if (type == TokenType.NONE)
                            t = new Token(TokenType.ESC_IDENTIFIER, lexeme, lexeme, lineNumber, scanIndex - lexeme.length());
                        else
                            t = new Token(type, lineNumber, scanIndex - lexeme.length());

                        lexeme = "";
                        tokens.add(t);
                        state = 0;
                        scanIndex--;
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
                        tokens.add(new Token(
                                TokenType.ESC_NUMBER, lexeme, Integer.valueOf(lexeme), 
                                lineNumber, scanIndex - lexeme.length()
                        ));
                        lexeme = "";
                        state = 0;
                        scanIndex--;
                    }
                break;
                
                case 16:
                    if (Character.isDigit(c)) {
                        state = 17;
                        lexeme += c;
                    } else
                        return LexicalScannerStatus.MALFORMED_NUMBER;
                break;
                
                case 17:
                    if (Character.isDigit(c)) { // state 17
                        lexeme += c;
                    } else if (c == 'E' || c == 'e') {
                        state = 18;
                        lexeme += c;
                    } else {
                        tokens.add(new Token(
                                TokenType.ESC_FLOATING_NUMBER, lexeme, Float.valueOf(lexeme), 
                                lineNumber, scanIndex - lexeme.length()
                        ));
                        lexeme = "";
                        state = 0;
                        scanIndex--;
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
                        return LexicalScannerStatus.MALFORMED_NUMBER;
                break;
                
                case 19:
                    if (Character.isDigit(c)) {
                        state = 20;
                        lexeme += c;
                    } else
                        return LexicalScannerStatus.MALFORMED_NUMBER;
                break;
                
                case 20:
                    if (Character.isDigit(c)) { // state 20
                        lexeme += c;
                    } else {
                        tokens.add(new Token(
                                TokenType.ESC_DOUBLE_NUMBER, lexeme, Double.valueOf(lexeme), 
                                lineNumber, scanIndex - lexeme.length()
                        ));
                        lexeme = "";
                        state = 0;
                        scanIndex--;
                    }
                break;

                case 24:
                    lexeme = "";
                    if (c == '*') {
                        state = 25;
                    } else if (c == '/') {
                        state = 28;
                    } else if (c == '=') {
                        tokens.add(new Token(TokenType.ESC_SLASH_EQUAL, lineNumber, scanIndex - 1));
                        state = 0;
                    } else { // state 30
                        tokens.add(new Token(TokenType.ESC_SLASH, lineNumber, scanIndex));
                        state = 0;
                        scanIndex--;
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
                    else
                        state = 25;
                break;

                case 28:
                    if (c != '\n')
                        state = 28;
                    else
                        state = 0;
                break;
                
                case 32:
                    lexeme = "";
                    state = 0;
                    if (c == '=') {
                        tokens.add(new Token(TokenType.ESC_LESS_EQUAL, lineNumber, scanIndex - 1));
                    } else {
                        tokens.add(new Token(TokenType.ESC_LESS, lineNumber, scanIndex));
                        scanIndex--; 
                    }
                break;
                
                case 36:
                    lexeme = "";
                    state = 0; 
                    if (c == '=') {
                        tokens.add(new Token(TokenType.ESC_GREATER_EQUAL, lineNumber, scanIndex - 1));
                    } else {
                        tokens.add(new Token(TokenType.ESC_GREATER, lineNumber, scanIndex));
                        scanIndex--; 
                    }
                break;
                
                case 38:
                    lexeme = "";
                    state = 0; 
                    if (c == '=') {
                        tokens.add(new Token(TokenType.ESC_NOT_EQUAL, lineNumber, scanIndex - 1));
                    } else {
                        tokens.add(new Token(TokenType.ESC_NOT, lineNumber, scanIndex));
                        scanIndex--; 
                    }
                break;
                
                case 41:
                    lexeme = "";
                    state = 0; 
                    if (c == '=') {
                        tokens.add(new Token(TokenType.ESC_EQUAL_EQUAL, lineNumber, scanIndex - 1));
                    } else {
                        tokens.add(new Token(TokenType.ESC_EQUAL, lineNumber, scanIndex));
                        scanIndex--; 
                    }
                break;
                
                case 43:
                    if (c == '\n') {
                        return LexicalScannerStatus.INVALID_STRING;
                    } else if (c == '\\') {
                        state = 59;
                        lexeme += c;
                    } else if(c == '"') {
                        lexeme += c; 
                        tokens.add(new Token (TokenType.ESC_STRING, lexeme, myString, lineNumber, scanIndex - lexeme.length()));
                        state = 0;
                        lexeme = "";
                        myString = "";
                    } else {
                        myString += c;
                        lexeme += c;
                    }
                break;
                
                case 59:
                    if (c == '\"')
                        myString += '\"';
                    else if(c == 'n')
                        myString += '\n';
                    else if(c == 't')
                        myString += '\t';
                    else if (c == '\\')
                        myString += '\\';
                    
                    lexeme += c;
                    state = 43;
                break;

                default:
                return LexicalScannerStatus.INVALID_CHAR;
            }
        }

        return state;
    }
}

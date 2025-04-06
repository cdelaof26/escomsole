package modeling;

/**
 *
 * @author cristopher
 */
public class StrTokenType {
    public static final String [] STR_TOKEN = {
        "(", ")", "{", "}",
        ",", ".", "?", ":", "[", "]", ";",

        // Tokens de uno o dos caracteres
        "-", "+", "/", "*",
        "/=",
        "--", "-=",
        "*=",
        "++", "+=",
        "!", "!=",
        "=", "==",
        ">", ">=",
        "<", "<=",

        // Literales
        "identifier", "string", "number", "float number", "double number",

        // Palabras clave
        "and", "else", "false", "fun", "for", "if", "null", "or",
        "print", "return", "true", "var", "while",

        "$", "", "expression"
    };
}

package interpreter.core;

import interpreter.exception.RuntimeError;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    // Used to implement the concept of Lexical scopes
    private final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();

    public Environment() {
        enclosing = null;
    }

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    public Object get(String name) {
        if (values.containsKey(name))
            return values.get(name);

        // use of the enclosing to find variables declared
        // in a previous (higher) scope
        if(enclosing != null)
            return enclosing.get(name);

        throw new RuntimeError(name, "Undefined variable");
    }

    public void assign(String name, Object value) {
        if (values.containsKey(name)) {
            values.put(name, value);
            return;
        }

        // use of the enclosing to find variables declared
        // in a previous (higher) scope
        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name, "Undefined variable");
    }

    public void define(String name, Object value) {
        values.put(name, value);
    }

    @Override
    public String toString() {
        String result = values.toString();
        if (enclosing != null)
            result += " -> " + enclosing.toString();

        return result;
    }
}

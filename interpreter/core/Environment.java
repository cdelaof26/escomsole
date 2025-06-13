package interpreter.core;

import interpreter.exception.RuntimeError;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Environment {
    // Used to implement the concept of Lexical scopes
    private final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();
    private final Map<String, Object[]> identifiers = new HashMap<>();
    private final LinkedList<Object> returnValues = new LinkedList<>();
    private int breakStack = 0;
    
    public Environment() {
        enclosing = null;
    }

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    public Environment getEnclosing() {
        return enclosing;
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
    
    public Object [] getFunction(String name) {
        if (identifiers.containsKey(name))
            return identifiers.get(name);

        // use of the enclosing to find variables declared
        // in a previous (higher) scope
        if(enclosing != null)
            return enclosing.getFunction(name);

        throw new RuntimeError(name, "Undefined function");
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
        if (values.containsKey(name))
            throw new RuntimeError(name, "Redefinition of variable");
        
        values.put(name, value);
    }

    public Map<String, Object> getValues() {
        return values;
    }
    
    public void defineFunction(String name, Object [] value) {
        if (identifiers.containsKey(name))
            throw new RuntimeError(name, "Redefinition of variable");
        
        if (value.length != 2)
            throw new RuntimeError(name, "To define a function, a name, parameters and a body are required");
        
        identifiers.put(name, value);
    }
    
    public void addReturnValue(Object o) {
        returnValues.add(o);
    }
    
    public Object getReturnValue() {
        return returnValues.pollLast();
    }

    public LinkedList<Object> getReturnValues() {
        return returnValues;
    }

    public int getBreakStack() {
        return breakStack;
    }

    public boolean shouldBreakStack() {
        boolean b = breakStack != 0;
        if (breakStack > 0)
            breakStack--;
        return b;
    }

    public void incrBreakStack() {
        this.breakStack++;
    }
    
    @Override
    public String toString() {
        String result = values.toString();
        if (enclosing != null)
            result += " -> " + enclosing.toString();

        return result;
    }
}

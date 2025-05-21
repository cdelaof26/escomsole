# escomsole

REPL written in Java FTW

### Requirements
- Java 8 or newer

### Compile

```bash
javac -cp . escomsoleje/EscomsoleJE.java
### Para eliminar los .class y compilar luego luego
find . -name "*.ckass" -exec rm {} \; && javac -cp . escomsoleje/EscomsoleJE.java && java escomsoleje/EscomsoleJE
```

### Usage
```bash
# REPL mode
java escomsoleje/EscomsoleJE

# Execute file
java escomsoleje/EscomsoleJE path/to/file
```
